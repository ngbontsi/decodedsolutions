# Family Tree — front end

A single-page React app for the `family-tree-service` archive. It is deliberately thin: no router,
no state library, no axios. One `loadArchive()` call assembles the whole archive, then each view
renders from that snapshot.

## Run it

The API must be on `http://localhost:8085` first.

```powershell
cd frontend/family-tree
npm run dev        # http://localhost:5174
```

The dev server proxies `/api` to `localhost:8085` (see `vite.config.ts`), so no CORS change is
needed. Set `VITE_API_BASE` to point at a different API.

```powershell
npm run build      # type-check + production bundle into dist/
```

## Why these choices

| Choice | Why | Rejected alternative |
| --- | --- | --- |
| Vite dev proxy | Zero backend changes, works on any port | Adding a permissive CORS config to the API |
| Plain `fetch` | One dependency fewer; `ApiError` already carries the API's error shape | axios (adds 13 kB for no gain here) |
| Local view state, no router | Five screens, no deep links needed in v1 | React Router (404s on static hosts, more moving parts) |
| One archive snapshot | Simple, consistent reads; the archive is small | Per-panel fetching (N+1, inconsistent screens) |
| `buildAncestorTree()` in `lib/graph.ts` | Pure function, easy to reason about and test | A graph library (heavy, wrong layout defaults) |

## Views

- **Overview** — archive health, counts, recent people, open claims, audit trail.
- **People** — searchable list; every person has an avatar that does not depend on a name.
- **Person detail** — names over time, parents (with identity status), children, unions, claims,
  family groups, organisations.
- **Tree** — ancestor chart with dashed "ghost" nodes for parents the archive knows about but has
  not identified. Honest gaps, not invented people.
- **Evidence** — claims with their evidence, claim-status transitions, and expandable sources.
- **Heritage** — events and organisations, both creatable.

## Notes

- `src/types.ts` mirrors the API responses exactly; `src/api.ts` is the only place that knows URLs.
- Dates are strings (`YYYY-MM-DD`) and precision is explicit, so a decade is never rendered as a day.
- The UI never writes a name, parent or claim in place. It closes and supersedes, matching the
  append-only backend.
