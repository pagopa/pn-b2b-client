"""Read issue links and statuses when a PST is regenerated."""

import json
import os
import re
from urllib import request


def issue_metadata(keys: list[str]) -> tuple[str, dict[str, str], str | None]:
    cloud_id = os.getenv('JIRA_CLOUD_ID', '')
    token = os.getenv('JIRA_API_TOKEN', '')
    base_url = os.getenv('JIRA_BASE_URL', '').rstrip('/')
    if not cloud_id or not token:
        return base_url, {}, 'Credenziali Jira non disponibili'
    api = f'https://api.atlassian.com/ex/jira/{cloud_id}'

    def get(path: str) -> dict:
        req = request.Request(api + path, headers={
            'Authorization': f'Bearer {token}', 'Accept': 'application/json'})
        with request.urlopen(req, timeout=15) as response:
            return json.load(response)

    errors = []
    if not base_url:
        try:
            base_url = get('/rest/api/3/serverInfo')['baseUrl'].rstrip('/')
        except (OSError, KeyError, TypeError, ValueError) as exc:
            errors.append(f'URL Jira: {exc}')
    statuses = {}
    for key in dict.fromkeys(keys):
        if not re.fullmatch(r'[A-Z][A-Z0-9]*-\d+', key):
            errors.append(f'Chiave Jira non valida: {key}')
            continue
        try:
            statuses[key] = get(f'/rest/api/3/issue/{key}?fields=status')['fields']['status']['name']
        except (OSError, KeyError, TypeError, ValueError) as exc:
            errors.append(f'{key}: {exc}')
    return base_url, statuses, ('Jira non disponibile: ' + '; '.join(errors)) if errors else None
