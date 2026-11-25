import { Parser, AstBuilder, GherkinClassicTokenMatcher } from '@cucumber/gherkin';
import { IdGenerator } from '@cucumber/messages';
import { createHash } from 'node:crypto';
export const stableId = value => createHash('sha256').update(value).digest('hex').slice(0, 20);
const tags = node => (node.tags || []).map(t => t.name);
const unique = values => [...new Set(values)];
function steps(node) {
  return (node.steps || []).map(s => ({ keyword: s.keyword.trim(), text: s.text,
    table: s.dataTable?.rows.map(r => r.cells.map(c => c.value)), docString: s.docString?.content }));
}
export function parseFeature(source, path) {
  const document = new Parser(new AstBuilder(IdGenerator.incrementing()), new GherkinClassicTokenMatcher()).parse(source);
  if (!document.feature) return null;
  const f = document.feature;
  const result = { id: stableId(path), path, name: f.name, description: f.description, tags: tags(f), scenarios: [] };
  const identities = new Map();
  function walk(children, inheritedTags, backgrounds, rule) {
    const common = [...backgrounds, ...children.filter(c => c.background).map(c => ({name:c.background.name, description:c.background.description, steps:steps(c.background)}))];
    for (const c of children) {
      if (c.rule) walk(c.rule.children, unique([...inheritedTags, ...tags(c.rule)]), common, {name:c.rule.name, description:c.rule.description});
      if (!c.scenario) continue;
      const s = c.scenario;
      const effective = unique([...inheritedTags, ...tags(s)]);
      const identity = s.name.match(/^\[([^\]]+)\]/)?.[1] || `${rule?.name || ''}:${s.name}`;
      const occurrence = (identities.get(identity) || 0) + 1;
      identities.set(identity, occurrence);
      result.scenarios.push({ id: stableId(`${path}:${identity}:${occurrence}`), legacyId: stableId(`${path}:${s.location.line}`), name:s.name, description:s.description,
        line:s.location.line, keyword:s.keyword, rule, tags:effective, backgrounds:common, steps:steps(s),
        examples:s.examples.map(e => ({name:e.name, description:e.description, tags:unique([...effective, ...tags(e)]),
          header:e.tableHeader?.cells.map(c => c.value) || [], rows:e.tableBody.map(r => r.cells.map(c => c.value))})) });
    }
  }
  walk(f.children, tags(f), []);
  return result;
}
export function classify(path, scenario, config) {
  return config.rules.find(r => (r.tags?.some(t => scenario.tags.includes(t))) || (r.path && new RegExp(r.path).test(path)))?.area || 'Da classificare';
}
export function safeJson(value) { return JSON.stringify(value).replace(/</g, '\\u003c').replace(/\u2028/g, '\\u2028').replace(/\u2029/g, '\\u2029'); }
