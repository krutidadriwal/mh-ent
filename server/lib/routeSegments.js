// Sub-path segments matched by a [...path].js catch-all route, parsed from
// req.url rather than req.query.path -- Vercel's Node runtime doesn't reliably
// populate the dynamic segment into req.query for bracket-named files.
//
// prefixSegmentCount is how many segments to drop from the front (e.g. 2 for
// "/api/app/today" -> drop "api","app" -> ["today"]).
function routeSegmentsAfter(req, prefixSegmentCount) {
  const pathname = req.url.split('?')[0];
  return pathname.split('/').filter(Boolean).slice(prefixSegmentCount);
}

module.exports = { routeSegmentsAfter };
