"""Read the configured Confluence parent content without writing any content."""

import json
from http.client import HTTPException
import os
from pathlib import Path
import re
import sys
from urllib import error, request
from uuid import UUID

import yaml


class NoRedirect(request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def main():
    cloud_id = os.getenv('JIRA_CLOUD_ID', '')
    token = os.getenv('CONFLUENCE_API_TOKEN', '')
    if not cloud_id or not token:
        print('Configurare vars.JIRA_CLOUD_ID e secrets.CONFLUENCE_API_TOKEN.', file=sys.stderr)
        return 1
    try:
        cloud_id = str(UUID(cloud_id))
        config = yaml.safe_load(Path(__file__).with_name('config.yaml').read_text())
        override = os.getenv('CONFLUENCE_CHECK_PAGE_ID', '').strip()
        parent_type = 'page' if override else config['confluence']['parent_type']
        if parent_type not in ('page', 'folder'):
            raise ValueError('Invalid parent type')
        page_id = override or str(config['confluence']['parent_id'])
        if not re.fullmatch(r'\d+', page_id):
            raise ValueError('Invalid page ID')
    except (OSError, ValueError, KeyError, TypeError, yaml.YAMLError):
        print('Cloud ID o configurazione del contenitore padre non validi.', file=sys.stderr)
        return 1
    url = f'https://api.atlassian.com/ex/confluence/{cloud_id}/wiki/api/v2/{parent_type}s/{page_id}'
    req = request.Request(url, method='GET', headers={
        'Authorization': f'Bearer {token}', 'Accept': 'application/json'})
    try:
        with request.build_opener(NoRedirect()).open(req, timeout=30) as response:
            page = json.load(response)
        if not isinstance(page, dict) or str(page.get('id')) != page_id or not page.get('spaceId'):
            raise ValueError('Unexpected response')
    except error.HTTPError as exc:
        messages = {
            401: 'Autenticazione non accettata: verificare tipo, validità e scope Confluence del token.',
            403: 'Accesso negato: verificare scope del token, accesso Confluence e permessi sullo spazio.',
            404: 'Contenuto non trovato o non visibile: verificare Cloud ID, tipo, ID e permessi.',
            429: 'Limite richieste raggiunto; riprovare più tardi.',
        }
        print(f'HTTP {exc.code}: ' + messages.get(exc.code, 'Richiesta Confluence non riuscita.'), file=sys.stderr)
        return 1
    except (OSError, ValueError, HTTPException):
        print('Connessione Confluence o risposta JSON non valida.', file=sys.stderr)
        return 1
    # No response body, headers or token are emitted to the workflow log.
    print(f'OK: {parent_type} {page_id} leggibile con le credenziali configurate.')
    print('Nessuna pagina creata o modificata. Permessi di scrittura non verificati.')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
