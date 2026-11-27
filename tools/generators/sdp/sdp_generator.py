"""Publish a release Service Delivery Plan; use --dry-run for an offline preview."""

import argparse
from datetime import date
import html
import math
from pathlib import Path
import re
import sys

import yaml


STATES = {'draft', 'planned', 'in-progress', 'execution', 'closed'}
STRATEGIES = {
    'NRT TA STANDARD', 'TA VERTICALE', 'NRT TA AD-HOC', 'NRT TM STANDARD',
    'TM VERTICALE', 'TM ESPLORATIVO', 'NRT TM AD-HOC', 'DESIGN ONLY',
    'TEST DI CARICO', 'TA FE VERTICALE',
}
ISSUE = re.compile(r'[A-Z][A-Z0-9]*-\d+')


class ManifestLoader(yaml.SafeLoader):
    """Reject duplicate mapping keys rather than silently losing data."""


def unique_mapping(loader, node):
    result = {}
    for key_node, value_node in node.value:
        key = loader.construct_object(key_node)
        if not isinstance(key, str):
            raise ValueError('Le chiavi YAML devono essere stringhe')
        if key in result:
            raise ValueError(f'Chiave YAML duplicata: {key}')
        result[key] = loader.construct_object(value_node)
    return result


ManifestLoader.add_constructor(yaml.resolver.BaseResolver.DEFAULT_MAPPING_TAG, unique_mapping)


def validate(release):
    """Validate the new release template, with field-specific errors."""
    def require(condition, message):
        if not condition:
            raise ValueError(message)

    def text(value):
        return isinstance(value, str) and bool(value.strip())

    def choice(value, choices):
        return isinstance(value, str) and value in choices

    require(isinstance(release, dict), 'release deve essere una mappa')
    for key in ('version', 'description', 'product', 'quarter'):
        require(text(release.get(key)), f'release.{key} obbligatorio e non vuoto')
    require(choice(release.get('status'), STATES), 'release.status non valido')
    require(choice(release['product'], {'SEND', 'INTEROP'}), 'release.product non valido')
    require(re.fullmatch(r'\d{4}-Q[1-4]', release['quarter']), 'release.quarter: usare AAAA-QN')
    delivery = release.get('delivery', {})
    require(isinstance(delivery, dict), 'delivery deve essere una mappa')
    for environment, value in delivery.items():
        require(environment in {'dev', 'test', 'qa', 'uat', 'prod'}, f'Ambiente sconosciuto: {environment}')
        if value is not None:
            require(isinstance(value, (str, date)), f'delivery.{environment}: data non valida')
            require(re.fullmatch(r'\d{4}-\d{2}-\d{2}', str(value)), f'delivery.{environment}: usare AAAA-MM-GG')
            try:
                date.fromisoformat(str(value))
            except ValueError:
                raise ValueError(f'delivery.{environment}: data non valida') from None

    items = release.get('work-items')
    require(isinstance(items, list), 'work-items deve essere una lista (anche vuota)')
    identities, epics, included = set(), set(), set()
    for index, item in enumerate(items, 1):
        where = f'work-items[{index}]'
        require(isinstance(item, dict), f'{where} deve essere una mappa')
        key = item.get('id')
        require(text(key) and ISSUE.fullmatch(key), f'{where}.id: chiave Jira non valida')
        included.add(key)
        sequence = item.get('sequence')
        require(type(sequence) is int and sequence > 0, f'{where}.sequence: intero positivo richiesto')
        require((key, sequence) not in identities, f'{where}: coppia (id, sequence) duplicata')
        identities.add((key, sequence))
        require(choice(item.get('repo'), {'pn-b2b-client', 'qe_suit'}), f'{where}.repo non valido')
        for field in ('qa_epic', 'branch'):
            require(field in item, f'{where}.{field} deve essere presente, anche vuoto')
            require(item[field] is None or isinstance(item[field], str), f'{where}.{field}: testo richiesto')
        epic = item['qa_epic']
        if epic:
            require(re.fullmatch(r'QA-\d+', epic), f'{where}.qa_epic non valida')
            require((key, epic) not in epics, f'{where}: coppia (id, qa_epic) duplicata')
            epics.add((key, epic))
        require('status' not in item, f'{where}.status: lo stato QA proviene da Jira, rimuovere il campo')
        strategies = item.get('strategies')
        require(isinstance(strategies, list) and bool(strategies), f'{where}.strategies: lista non vuota richiesta')
        require(all(choice(s, STRATEGIES) for s in strategies), f'{where}.strategies: strategia non valida')
        require(choice(item.get('priority'), {'Critical', 'High', 'Medium', 'Low'}), f'{where}.priority non valida')
        size = item.get('size')
        require(size is None or (type(size) in (int, float) and math.isfinite(size) and size > 0), f'{where}.size: numero positivo richiesto')
        for field in ('title', 'notes', 'risks'):
            require(item.get(field) is None or isinstance(item[field], str), f'{where}.{field}: testo richiesto')
        resources = item.get('resources', [])
        require(isinstance(resources, list), f'{where}.resources: lista richiesta')
        for person in resources:
            require(isinstance(person, dict) and text(person.get('name')) and choice(person.get('role'), {'TA', 'TM'}), f'{where}.resources: name e role TA/TM obbligatori')
        dependencies = item.get('dependencies', [])
        if dependencies is None:
            dependencies = []
        require(isinstance(dependencies, list) and all(text(d) for d in dependencies), f'{where}.dependencies: lista di testi richiesta')

    exclusions = release.get('out_of_scope', [])
    require(isinstance(exclusions, list), 'out_of_scope deve essere una lista')
    excluded = set()
    for item in exclusions:
        require(isinstance(item, dict), 'out_of_scope: ogni voce deve essere una mappa')
        key = item.get('id')
        require(text(key) and ISSUE.fullmatch(key), 'out_of_scope.id: chiave Jira non valida')
        require(text(item.get('reason')), f'{key}: reason obbligatoria e non vuota')
        require(key not in excluded and key not in included, f'{key}: esclusione duplicata o presente anche in work-items')
        excluded.add(key)


def load_manifest(path):
    data = yaml.load(path.read_text(encoding='utf-8'), Loader=ManifestLoader)
    if not isinstance(data, dict) or 'release' not in data:
        raise ValueError('Il manifest deve contenere release')
    release = data['release']
    validate(release)
    return release


def cell(value):
    if value is None or value == '':
        return '—'
    escaped = html.escape(str(value).strip(), quote=False)
    escaped = re.sub(r'([\\`*_\[\]])', r'\\\1', escaped)
    return escaped.replace('|', '&#124;').replace('\r\n', '\n').replace('\r', '\n').replace('\n', '<br>')


def table(headers, rows):
    lines = ['| ' + ' | '.join(headers) + ' |', '| ' + ' | '.join('---' for _ in headers) + ' |']
    lines.extend('| ' + ' | '.join(cell(v) for v in row) + ' |' for row in rows)
    return '\n'.join(lines)


def storage_cell(value):
    if value is None or value == '':
        return '—'
    return html.escape(str(value).strip()).replace('\r\n', '\n').replace('\r', '\n').replace('\n', '<br />')


def storage_table(headers, rows):
    return '<table><tbody><tr>' + ''.join('<th>' + storage_cell(h) + '</th>' for h in headers) + '</tr>' + ''.join(
        '<tr>' + ''.join('<td>' + storage_cell(v) + '</td>' for v in row) + '</tr>' for row in rows) + '</tbody></table>'


def render(release, storage=False):
    """Render the same data as Markdown or Confluence storage XHTML."""
    escape = storage_cell if storage else cell
    make_table = storage_table if storage else table
    def paragraph(text):
        return '<p>' + text + '</p>' if storage else text
    def heading(text, level=2):
        return f'<h{level}>{text}</h{level}>' if storage else '#' * level + ' ' + text
    lines = [heading(f'Service Delivery Plan — {escape(release["product"])} {escape(release["version"])}', 1),
             '', paragraph(f'Trimestre: {escape(release["quarter"])} · Stato release: {escape(release["status"])}'),
             '', paragraph(escape(release['description'])), '',
             paragraph('Jira non interrogato. Gli stati QA non sono sincronizzati.'), '']
    if release.get('delivery'):
        lines += [heading('Calendario della release'), '', make_table(['Ambiente', 'Data prevista'],
                  [(key.upper(), value or 'Da definire') for key, value in release['delivery'].items()]), '']
    lines += [heading('Piano di delivery QA'), '']
    rows = []
    for item in release['work-items']:
        resources = '\n'.join(f'{p["name"]} ({p["role"]})' for p in item.get('resources', []))
        details = []
        for field, label in [('notes', 'Note'), ('risks', 'Rischi')]:
            if item.get(field):
                details.append(f'{label}: {item[field].strip()}')
        if item.get('dependencies'):
            details.append('Dipendenze: ' + '; '.join(item['dependencies']))
        rows.append([item['id'], item['sequence'], item.get('title') or 'Titolo da recuperare da Jira',
                     item.get('qa_epic') or 'Da creare', '\n'.join(item['strategies']),
                     item.get('size'), resources or 'Da assegnare', item['priority'],
                     'Non disponibile (Jira non interrogato)' if item.get('qa_epic') else 'QA Epic da creare',
                     '\n'.join(details)])
    if rows:
        lines += [make_table(['Work item', 'Seq.', 'Attività', 'QA Epic', 'Strategia', 'Effort (gg/uomo)',
                         'Risorse', 'Priorità', 'Stato QA', 'Note / Rischi / Dipendenze'], rows)]
    else:
        lines += [paragraph('Nessuna attività QA pianificata.')]
    lines += ['', heading('Work item esclusi dal perimetro QA'), '']
    exclusions = release.get('out_of_scope', [])
    lines += [make_table(['Work item', "Motivo dell'esclusione"], [(x['id'], x['reason']) for x in exclusions])
              if exclusions else paragraph('Nessun work item escluso.')]
    return '\n'.join(lines) + '\n'


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('manifest', type=Path, help='Manifest YAML della release')
    parser.add_argument('--dry-run', action='store_true', help='Anteprima Markdown offline, senza scritture')
    args = parser.parse_args(argv)
    try:
        release = load_manifest(args.manifest)
        if args.dry_run:
            output = render(release)
        else:
            from confluence import publish
            output = publish(release, render(release, storage=True)) + '\n'
    except (OSError, ValueError, yaml.YAMLError) as exc:
        print(f'Errore SDP: {exc}', file=sys.stderr)
        return 1
    print(output, end='')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
