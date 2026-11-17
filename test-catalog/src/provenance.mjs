// Git metadata is taken from the same revision as the feature content.
export function fileHistory(git, revision, path, repositoryUrl) {
  const output = git(['log', '--follow', '--format=%H%x1f%an%x1f%cI%x1f%s%x1f%b%x1e', revision, '--', path]);
  return output.split('\x1e').filter(s => s.trim()).map(record => {
    const [commit, author, date, subject, body = ''] = record.trim().split('\x1f');
    const coauthors = [...new Set([...body.matchAll(/^Co-authored-by:[ \t]*(.*?)[ \t]*<[^>]+>[ \t]*$/gim)].map(m => m[1].trim()))].filter(name => name && name !== author);
    return { commit, author, date, subject, coauthors, url: `${repositoryUrl}/commit/${commit}` };
  });
}
export function latestChange(features) {
  return features.map(f => f.history?.[0]).filter(Boolean).sort((a,b) => Date.parse(b.date)-Date.parse(a.date))[0] || null;
}
