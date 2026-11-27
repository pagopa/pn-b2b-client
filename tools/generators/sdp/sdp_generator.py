"""Publish a release Service Delivery Plan; use --dry-run for an offline preview."""

import argparse
from datetime import date, datetime
from zoneinfo import ZoneInfo
from dataclasses import dataclass
from urllib.parse import quote
import os
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


@dataclass(frozen=True)
class Link:
    label: str
    url: str
    inline: bool = False


# Fixed palette, shared by the legend and the delivery table.
PALETTE = {
    'grey': ('#F1F2F4', '#B3B9C4', '#292A2E'),
    'blue': ('#CCE0FF', '#85B8FF', '#09326C'),
    'green': ('#D3F1A7', '#94C748', '#37471F'),
    'red': ('#FFD5D2', '#FF9C8F', '#5D1F1A'),
    'purple': ('#EED7FC', '#D8A0F7', '#48245D'),
    'yellow': ('#FEEDB9', '#F5CD47', '#533F04'),
}
STRATEGY_COLORS = {
    'NRT TA STANDARD': 'red', 'TA VERTICALE': 'red',
    'NRT TA AD-HOC': 'yellow', 'NRT TM STANDARD': 'purple',
    'TM VERTICALE': 'purple', 'TM ESPLORATIVO': 'purple',
    'NRT TM AD-HOC': 'blue', 'OUT OF QA SCOPE': 'grey',
    'DESIGN ONLY': 'green', 'TEST DI CARICO': 'yellow',
    'TA FE VERTICALE': 'blue',
}
ROLE_COLORS = {'TA': 'blue', 'TM': 'green'}
EFFORT_BANDS = [(0, 'XSS', 'grey'), (1, 'XS', 'grey'), (3, 'S', 'blue'),
                (5, 'M', 'green'), (7, 'L', 'red'), (10, 'XL', 'purple')]


@dataclass(frozen=True)
class Badge:
    label: str
    color: str


@dataclass(frozen=True)
class InlineGroup:
    parts: tuple
    separator: str = ' '


def effort_badge(size):
    if size is None:
        return Badge('Da stimare', 'grey')
    for limit, label, color in EFFORT_BANDS:
        if size <= limit:
            return Badge(f'{label} · {size:g}', color)
    return Badge(f'XXL · {size:g}', 'purple')


def cell(value):
    if isinstance(value, Badge):
        return cell(value.label)
    if isinstance(value, InlineGroup):
        separator = '<br>' if value.separator == '\n' else cell(value.separator) if value.separator.strip() else ' '
        return separator.join(cell(part) for part in value.parts)
    if isinstance(value, Link):
        return f"[{cell(value.label)}]({value.url})"
    if value is None or value == '':
        return '-'
    escaped = html.escape(str(value).strip(), quote=False)
    escaped = re.sub(r'([\\`*_\[\]])', r'\\\1', escaped)
    return escaped.replace('|', '&#124;').replace('\r\n', '\n').replace('\r', '\n').replace('\n', '<br>')


def table(headers, rows):
    lines = ['| ' + ' | '.join(headers) + ' |', '| ' + ' | '.join('---' for _ in headers) + ' |']
    lines.extend('| ' + ' | '.join(cell(v) for v in row) + ' |' for row in rows)
    return '\n'.join(lines)


def storage_cell(value):
    if isinstance(value, Badge):
        background, border, foreground = PALETTE[value.color]
        style = (f'background-color: {background}; color: {foreground}; '
                 f'border: 1px solid {border}; border-radius: 4px; '
                 'padding: 1px 4px; font-size: inherit; font-weight: normal;')
        return f'<span style="{style}">{storage_cell(value.label)}</span>'
    if isinstance(value, InlineGroup):
        separator = '<br />' if value.separator == '\n' else html.escape(value.separator)
        return separator.join(storage_cell(part) for part in value.parts)
    if isinstance(value, Link):
        if value.inline:
            url = html.escape(value.url, quote=True)
            return f'<a href="{url}" data-card-appearance="inline">{url}</a>'
        return f'<a href="{html.escape(value.url, quote=True)}">{storage_cell(value.label)}</a>'
    if value is None or value == '':
        return '-'
    return html.escape(str(value).strip()).replace('\r\n', '\n').replace('\r', '\n').replace('\n', '<br />')


def storage_table(headers, rows):
    columns = ''
    if 'Effort (gg/uomo)' in headers:
        widths = [240, 260, 150, 90, 160, 100, 110]
        columns = '<colgroup>' + ''.join(f'<col style="width: {width}px;" />' for width in widths) + '</colgroup>'
    return '<table>' + columns + '<tbody><tr>' + ''.join('<th><h6>' + storage_cell(h) + '</h6></th>' for h in headers) + '</tr>' + ''.join(
        '<tr>' + ''.join('<td><h6>' + storage_cell(v) + '</h6></td>' for v in row) + '</tr>' for row in rows) + '</tbody></table>'


def render(release, storage=False, manifest_url=None, generated_at=None):
    """Render the same data as Markdown or Confluence storage XHTML."""
    escape = storage_cell if storage else cell
    make_table = storage_table if storage else table
    def paragraph(text):
        return '<p>' + text + '</p>' if storage else text
    def heading(text, level=2):
        return f'<h{level}>{text}</h{level}>' if storage else '#' * level + ' ' + text
    def field(name, value):
        label = '<strong>' + escape(name) + ':</strong>' if storage else '**' + escape(name) + ':**'
        return label + ' ' + escape(value)
    def issue(key):
        return Link(key, f'https://pagopa.atlassian.net/browse/{key}', inline=True)
    timestamp = (generated_at or datetime.now(ZoneInfo('Europe/Rome'))).astimezone(ZoneInfo('Europe/Rome'))
    lines = [paragraph(field('Trimestre', release['quarter']) + ' · ' + field('Stato release', release['status'])),
             '', paragraph(escape(release['description'])), '',
             paragraph('Titoli e stati QA aggiornati da Jira.' if release.get('_jira_synced') else 'Jira non interrogato. Gli stati QA non sono sincronizzati.'),
             paragraph(field('Ultimo aggiornamento', timestamp.strftime('%d/%m/%Y %H:%M %Z') + ' - Europe/Rome')), '']
    if release.get('delivery'):
        lines += [heading('Calendario della release'), '', make_table(['Ambiente', 'Data prevista'],
                  [(key.upper(), value or 'Da definire') for key, value in release['delivery'].items()]), '']
    items = release['work-items']
    categories = {'new': 0, 'indeterminate': 0, 'done': 0, 'unknown': 0}
    for item in items:
        category = item.get('_qa_category') if item.get('qa_epic') else 'new'
        categories[category if category in categories else 'unknown'] += 1
    estimates = [i['size'] for i in items if i.get('size') is not None]
    effort = f'{sum(estimates):g} gg/uomo su {len(estimates)} attività; {len(items) - len(estimates)} da stimare'
    lines += [heading('Quadro di sintesi'), '', make_table(
        ['Attività QA', 'Da avviare', 'In corso', 'Completate', 'Stato non disponibile', 'Effort stimato'],
        [[len(items), categories['new'], categories['indeterminate'], categories['done'], categories['unknown'], effort]]), '',
        paragraph('Effort di analisi, progettazione e implementazione dei test; esclude esecuzione e analisi degli esiti. Il completamento delle attività QA non indica che tutti i test siano passati.'),
        paragraph('Conteggi per attività del manifest, anche quando condividono una QA Epic. Raggruppamento basato sulle categorie Jira; le attività senza Epic sono da avviare.'), '',
        heading('Piano di delivery QA'), '']
    rows = []
    for item in release['work-items']:
        resources = InlineGroup(tuple(InlineGroup((person['name'], Badge(person['role'], ROLE_COLORS[person['role']])))
                                      for person in item.get('resources', [])), '\n')
        rows.append([issue(item['id']),
                     issue(item['qa_epic']) if item.get('qa_epic') else 'QA Epic da creare', InlineGroup(tuple(Badge(strategy, STRATEGY_COLORS[strategy]) for strategy in item['strategies']), '\n'),
                     effort_badge(item.get('size')), resources if resources.parts else 'Da assegnare', item['priority'],
                     Link('Apri PST', item['_pst_url']) if item.get('_pst_url') else ('Non disponibile' if release.get('_jira_synced') else 'Non verificato (anteprima offline)')])
    if rows:
        lines += [make_table(['Work item Prodotto', 'QA Epic', 'Strategia', 'Effort (gg/uomo)',
                         'Risorse', 'Priorità', 'PST'], rows)]
    else:
        lines += [paragraph('Nessuna attività QA pianificata.')]
    details = []
    for item in items:
        entries = [(label, item.get(field)) for field, label in [('notes', 'Note'), ('risks', 'Rischi')]]
        entries.append(('Dipendenze', '; '.join(item.get('dependencies') or [])))
        entries = [(label, value) for label, value in entries if value and value.strip()]
        if entries:
            label = escape(issue(item['id'])) + ' - ' + escape(item.get('title') or 'Titolo da recuperare da Jira')
            if item.get('qa_epic'):
                label += ' (' + escape(issue(item['qa_epic'])) + ')'
            details += [heading(label, 3)]
            details += [paragraph(field(name, value)) for name, value in entries]
    if details:
        lines += ['', heading('Note, rischi e dipendenze'), ''] + details
    lines += ['', heading('Work item esclusi dal perimetro QA'), '']
    exclusions = release.get('out_of_scope', [])
    lines += [make_table(['Work item Prodotto', "Motivo dell'esclusione"], [(issue(x['id']), x['reason']) for x in exclusions])
              if exclusions else paragraph('Nessun work item escluso.')]
    legend_effort = InlineGroup(tuple(Badge(label, color) for label, color in (
        ('XSS (=0)', 'grey'), ('XS (>0-1)', 'grey'), ('S (>1-3)', 'blue'),
        ('M (>3-5)', 'green'), ('L (>5-7)', 'red'), ('XL (>7-10)', 'purple'),
        ('XXL (>10)', 'purple'))))
    legend_roles = InlineGroup((Badge('TA', 'blue'), Badge('TM', 'green')))
    legend_strategies = InlineGroup(tuple(Badge(name, color) for name, color in STRATEGY_COLORS.items()))
    compact = lambda content: '<h6>' + content + '</h6>' if storage else content
    explanation = ('Effort size: effort totale attribuito al work item per analisi/progettazione '
                   'e implementazione, espresso in giorni uomo; non è una stima per ogni singola persona. '
                   'Esclude esecuzione e analisi esiti su TEST/UAT. XSS (=0) indica nessun effort '
                   'aggiuntivo attribuito al work item, ad esempio copertura NRT condivisa con un altro '
                   'work item; non significa assenza di lavoro QA. Un valore vuoto significa Da stimare. '
                   '')
    lines += ['', heading('LEGENDA'), compact(field('EFFORT SIZE', legend_effort) + ' | ' + escape(legend_roles))]
    if storage:
        lines += ['<div style="background-color: #E9F2FF; padding: 12px; border-radius: 6px;">'
                  + compact(field('Effort size', explanation.removeprefix('Effort size: '))) + '</div>']
    else:
        lines += [paragraph(field('Effort size', explanation.removeprefix('Effort size: ')))]
    lines += [compact(field('STRATEGIA', legend_strategies))]
    lines += ['', paragraph('Pagina generata automaticamente. Le modifiche manuali vengono sostituite alla generazione successiva.')]
    if manifest_url:
        lines += [paragraph(escape(Link('Manifest sorgente', manifest_url)))]
    return '\n'.join(lines) + '\n'


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('manifest', type=Path, help='Manifest YAML della release')
    parser.add_argument('--dry-run', action='store_true', help='Anteprima Markdown offline, senza scritture')
    args = parser.parse_args(argv)
    try:
        release = load_manifest(args.manifest)
        manifest_url = None
        root = Path(__file__).resolve().parents[3]
        try:
            relative = args.manifest.resolve().relative_to(root)
            revision = os.getenv('GITHUB_SHA') or os.getenv('GITHUB_REF_NAME', 'develop')
            manifest_url = 'https://github.com/pagopa/pn-b2b-client/blob/' + quote(revision, safe='') + '/' + quote(relative.as_posix(), safe='/')
        except ValueError:
            pass
        if args.dry_run:
            output = render(release, manifest_url=manifest_url)
        else:
            from confluence import publish
            from enrichment import enrich
            if release['status'] != 'draft':
                release = enrich(release)
            output = publish(release, render(release, storage=True, manifest_url=manifest_url)) + '\n'
    except (OSError, ValueError, yaml.YAMLError) as exc:
        print(f'Errore SDP: {exc}', file=sys.stderr)
        return 1
    print(output, end='')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
