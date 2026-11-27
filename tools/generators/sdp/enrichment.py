"""Read-only Jira enrichment and verified PST discovery for SDP publishing."""
import copy
import json
import os
from urllib import error, request
from uuid import UUID

from check_confluence import NoRedirect

PST_REPO = 'pagopa/pn-b2b-client'


def read_json(url, token, missing_ok=False):
    headers = {'Accept': 'application/json'}
    if token:
        headers['Authorization'] = f'Bearer {token}'
    try:
        with request.build_opener(NoRedirect()).open(request.Request(url, headers=headers), timeout=30) as response:
            return json.load(response)
    except error.HTTPError as exc:
        if missing_ok and exc.code == 404:
            return None
        raise ValueError(f'Lettura dati SDP: HTTP {exc.code}; verificare credenziali e permessi.') from None
    except (OSError, ValueError):
        raise ValueError('Lettura dati SDP: connessione o risposta non valida.') from None


def enrich(release, read=read_json):
    result = copy.deepcopy(release)
    items = result['work-items']
    keys = {i['id'] for i in items if not (i.get('title') or '').strip()}
    keys.update(i['qa_epic'] for i in items if i.get('qa_epic'))
    issues = {}
    if keys:
        token = os.getenv('JIRA_API_TOKEN', '')
        if not token:
            raise ValueError('Configurare JIRA_API_TOKEN per leggere titoli e stati QA.')
        try:
            cloud = str(UUID(os.getenv('JIRA_CLOUD_ID', '')))
        except ValueError:
            raise ValueError('Configurare JIRA_CLOUD_ID valido.') from None
        for key in sorted(keys):
            data = read(f'https://api.atlassian.com/ex/jira/{cloud}/rest/api/3/issue/{key}?fields=summary,status', token)
            fields = data.get('fields') if isinstance(data, dict) else None
            if not isinstance(fields, dict):
                raise ValueError(f'Risposta Jira non valida per {key}.')
            issues[key] = fields
    pst = {}
    for item in items:
        if not (item.get('title') or '').strip():
            title = issues[item['id']].get('summary')
            if not isinstance(title, str) or not title.strip():
                raise ValueError(f'Summary Jira assente per {item["id"]}.')
            item['title'] = title
        epic = item.get('qa_epic')
        if not epic:
            continue
        status = issues[epic].get('status') or {}
        if not isinstance(status, dict) or not isinstance(status.get('name'), str) or not status['name'].strip():
            raise ValueError(f'Stato Jira assente per {epic}.')
        item['_qa_status'] = status['name']
        category = status.get('statusCategory')
        item['_qa_category'] = category.get('key') if isinstance(category, dict) else None
        if epic not in pst:
            data = read(f'https://api.github.com/repos/{PST_REPO}/contents/pst_{epic}.md?ref=pst-output', os.getenv('GITHUB_TOKEN', ''), missing_ok=True)
            pst[epic] = bool(isinstance(data, dict) and data.get('type') == 'file')
        if pst[epic]:
            item['_pst_url'] = f'https://github.com/{PST_REPO}/blob/pst-output/pst_{epic}.md'
    result['_jira_synced'] = True
    return result
