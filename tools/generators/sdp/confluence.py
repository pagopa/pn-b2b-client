"""Versioned SDP publishing through Confluence REST v2."""

import hashlib
from http.client import HTTPException
import json
import os
from pathlib import Path
import re
from urllib import error, parse, request
from uuid import UUID

import yaml

from check_confluence import NoRedirect


def identifier(value):
    value = str(value)
    if not re.fullmatch(r'[0-9]+', value):
        raise ValueError('ID Confluence non valido.')
    return value


class Client:
    def __init__(self):
        token = os.getenv('CONFLUENCE_API_TOKEN', '')
        try:
            cloud = str(UUID(os.getenv('JIRA_CLOUD_ID', '')))
        except ValueError:
            raise ValueError('Configurare JIRA_CLOUD_ID con il Cloud ID valido.') from None
        if not token:
            raise ValueError('Configurare CONFLUENCE_API_TOKEN.')
        self.base = f'https://api.atlassian.com/ex/confluence/{cloud}/wiki/api/v2'
        self.headers = {'Authorization': f'Bearer {token}', 'Accept': 'application/json',
                        'Content-Type': 'application/json'}
        self.opener = request.build_opener(NoRedirect())

    def call(self, method, path, payload=None):
        if not path.startswith('/') or path.startswith('//'):
            raise ValueError('Percorso API non valido.')
        req = request.Request(self.base + path, method=method, headers=self.headers,
                              data=None if payload is None else json.dumps(payload).encode('utf-8'))
        try:
            with self.opener.open(req, timeout=30) as response:
                result = json.load(response)
            if not isinstance(result, dict):
                raise ValueError('Risposta non valida')
            return result
        except error.HTTPError as exc:
            raise ValueError(f'Confluence HTTP {exc.code}: verificare accesso, scope e contenuti; nessun retry automatico.') from None
        except (OSError, ValueError, HTTPException):
            raise ValueError('Connessione o risposta Confluence non valida. Esito della scrittura eventualmente incerto: verificare prima di riprovare.') from None

    def pages(self, space):
        # Never follow a server-supplied URL with credentials: extract only its cursor.
        query = {'space-id': space, 'status': ['current', 'archived'], 'body-format': 'storage', 'limit': 100}
        seen = set()
        while True:
            batch = self.call('GET', '/pages?' + parse.urlencode(query, doseq=True))
            results = batch.get('results')
            if not isinstance(results, list) or not all(isinstance(p, dict) for p in results):
                raise ValueError('Elenco pagine Confluence non valido.')
            yield from results
            next_url = batch.get('_links', {}).get('next')
            if not next_url:
                break
            cursors = parse.parse_qs(parse.urlsplit(next_url).query).get('cursor', [])
            if len(cursors) != 1 or cursors[0] in seen:
                raise ValueError('Paginazione Confluence non valida.')
            query['cursor'] = cursors[0]
            seen.add(cursors[0])


def identity(release):
    key = json.dumps([release['product'], release['version']], ensure_ascii=True)
    return 'rac-sdp-' + hashlib.sha256(key.encode()).hexdigest()


def anchor(name):
    return '<ac:structured-macro ac:name="anchor"><ac:parameter ac:name="">' + name + '</ac:parameter></ac:structured-macro>'


def markers(page):
    try:
        body = page['body']['storage']['value']
        if not isinstance(body, str):
            raise ValueError('Corpo pagina non valido.')
        return set(re.findall(r'>\s*(rac-sdp-[a-f0-9]{64}(?:-closed)?)\s*</ac:parameter>', body))
    except (KeyError, TypeError):
        raise ValueError('Corpo storage mancante: ricerca SDP interrotta per evitare duplicati.') from None


def publish(release, body, client=None, config=None):
    if config is None:
        config = yaml.safe_load(Path(__file__).with_name('config.yaml').read_text(encoding='utf-8'))
    try:
        settings = config['confluence']
        parent_type = settings['parent_type']
        parent_id = identifier(settings['parent_id'])
        title = settings['title_template'].format(product=release['product'], version=release['version'])
        if parent_type not in ('folder', 'page') or settings['identity_fields'] != ['product', 'version'] or not title.strip():
            raise ValueError('Configurazione Confluence non valida.')
    except (KeyError, TypeError, AttributeError):
        raise ValueError('Configurazione Confluence non valida.') from None
    policy = config.get('publication', {})
    if release['status'] in policy.get('excluded_statuses', ['draft']):
        return f'Release {release["status"]}: pubblicazione esclusa.'
    final_status = policy.get('final_status', 'closed')
    if release['status'] not in policy.get('active_statuses', ['planned', 'in-progress', 'execution']) + [final_status]:
        raise ValueError('Stato release non ammesso dalla configurazione.')
    client = client or Client()
    parent = client.call('GET', f'/{parent_type}s/{parent_id}')
    if str(parent.get('id')) != parent_id:
        raise ValueError('Contenitore Confluence inatteso.')
    space = identifier(parent.get('spaceId'))
    key = identity(release)
    matches = []
    title_collision = False
    for page in client.pages(space):
        if key in markers(page):
            matches.append(page)
        elif page.get('title') == title:
            title_collision = True
    if len(matches) > 1:
        raise ValueError('Più pagine SDP con la stessa identità: risolvere i duplicati prima di pubblicare.')
    if title_collision:
        raise ValueError('Titolo già usato da una pagina non riconosciuta: nessuna sovrascrittura.')
    existing = None
    if matches:
        page_id = identifier(matches[0].get('id'))
        existing = client.call('GET', f'/pages/{page_id}?body-format=storage')
        if str(existing.get('id')) != page_id or str(existing.get('spaceId')) != space or key not in markers(existing):
            raise ValueError('Identità della pagina modificata durante la pubblicazione.')
        if existing.get('status') != 'current':
            raise ValueError('Pagina SDP non attiva: ripristinarla prima di pubblicare.')
        if str(existing.get('parentId')) != parent_id:
            raise ValueError('La pagina SDP è stata spostata: verificare il contenitore prima di aggiornare.')
        if release['status'] == final_status and policy.get('publish_closed_once', True) and key + '-closed' in markers(existing):
            return f'SDP {page_id}: chiusura già pubblicata, nessun aggiornamento.'
    content = anchor(key) + (anchor(key + '-closed') if release['status'] == final_status else '') + body
    payload = {'status': 'current', 'title': title,
               'body': {'representation': 'storage', 'value': content}}
    if existing:
        version = existing.get('version', {}).get('number')
        if type(version) is not int or version < 1:
            raise ValueError('Versione Confluence non valida.')
        payload.update(id=page_id, version={'number': version + 1,
                       'message': 'Generated by Release as Code'})
        result = client.call('PUT', f'/pages/{page_id}', payload)
        if str(result.get('id')) != page_id:
            raise ValueError('Conferma aggiornamento non valida: verificare la pagina.')
        action = 'aggiornata'
    else:
        payload.update(spaceId=space, parentId=parent_id)
        result = client.call('POST', '/pages', payload)
        page_id = identifier(result.get('id'))
        action = 'creata'
    return f'SDP: pagina {page_id} {action} su Confluence.'
