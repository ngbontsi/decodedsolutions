import { useMemo, useState } from 'react'
import { GitBranch, Users } from 'lucide-react'
import type { ArchiveData } from '../types'
import { displayName } from '../lib/format'
import { buildAncestorTree, edgePath, NODE_H, NODE_W } from '../lib/graph'
import { Avatar, Badge, Empty } from '../components/ui'

function clip(value: string, max: number): string {
  return value.length > max ? `${value.slice(0, max - 1)}…` : value
}

export default function TreePage({
  data,
  onOpenPerson,
}: {
  data: ArchiveData
  onOpenPerson: (personId: string) => void
}) {
  const peopleWithParents = useMemo(() => {
    const ids = new Set(
      data.parentages.filter((p) => p.status === 'ACTIVE' && p.childId).map((p) => p.childId),
    )
    return data.people.filter((p) => ids.has(p.id))
  }, [data])

  const [focusId, setFocusId] = useState(
    peopleWithParents[0]?.id ?? data.people[0]?.id ?? '',
  )
  const [generations, setGenerations] = useState(4)

  const tree = useMemo(
    () => buildAncestorTree(focusId, data.people, data.names, data.parentages, generations),
    [focusId, data, generations],
  )

  if (data.people.length === 0) {
    return (
      <div className="card card-pad">
        <Empty>The archive is empty. Add a person to start a tree.</Empty>
      </div>
    )
  }

  const confirmed = tree.nodes.filter((n) => n.kind === 'person').length
  const gaps = tree.nodes.filter((n) => n.kind === 'ghost').length

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Tree</div>
          <h1 className="page-title">Ancestors of {displayName(data.names[focusId])}</h1>
          <p className="page-sub">
            Each line is a recorded parent link. Dashed nodes are parents the archive cannot name
            yet — shown honestly instead of invented.
          </p>
        </div>
        <div className="actions">
          <select
            className="btn"
            value={focusId}
            onChange={(e) => setFocusId(e.target.value)}
            aria-label="Focus person"
            style={{ background: 'var(--panel)' }}
          >
            {[...data.people]
              .sort((a, b) => displayName(data.names[a.id]).localeCompare(displayName(data.names[b.id])))
              .map((person) => (
                <option key={person.id} value={person.id}>
                  {displayName(data.names[person.id])}
                </option>
              ))}
          </select>
          <select
            className="btn"
            value={generations}
            onChange={(e) => setGenerations(Number(e.target.value))}
            aria-label="Generations"
            style={{ background: 'var(--panel)' }}
          >
            {[2, 3, 4, 5, 6].map((count) => (
              <option key={count} value={count}>
                {count} generations
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="badge-row" style={{ marginBottom: 14 }}>
        <Badge tone="gold">
          <GitBranch size={11} /> {confirmed} recorded {confirmed === 1 ? 'person' : 'people'}
        </Badge>
        <Badge tone="ghost">{gaps} unresolved {gaps === 1 ? 'link' : 'links'}</Badge>
        <Badge tone="blue">
          <Users size={11} /> {data.people.length} in archive
        </Badge>
      </div>

      <div className="tree-shell">
        <svg
          className="tree-svg"
          width={tree.width + 48}
          height={tree.height + 48}
          role="img"
          aria-label="Ancestor tree"
        >
          <g transform="translate(24,24)">
            {tree.edges.map((edge) => (
              <path
                key={edge.key}
                className={`tree-edge ${edge.status !== 'IDENTIFIED' ? 'ghost' : ''}`}
                d={edgePath(edge.from, edge.to)}
              />
            ))}
            {tree.nodes.map((node) => {
              const isPerson = node.kind === 'person'
              return (
                <g
                  key={node.key}
                  className={`tree-node ${isPerson ? '' : 'ghost'}`}
                  transform={`translate(${node.x},${node.y})`}
                  onClick={() => isPerson && onOpenPerson(node.personId)}
                  role={isPerson ? 'button' : undefined}
                  tabIndex={isPerson ? 0 : undefined}
                  onKeyDown={(e) => {
                    if (isPerson && (e.key === 'Enter' || e.key === ' ')) onOpenPerson(node.personId)
                  }}
                >
                  <rect className="node-bg" width={NODE_W} height={NODE_H} rx={12} />
                  {isPerson ? (
                    <foreignObject x={12} y={20} width={44} height={44}>
                      <div>
                        <Avatar name={node.title} gender={node.gender} size="sm" />
                      </div>
                    </foreignObject>
                  ) : (
                    <>
                      <circle cx={30} cy={39} r={16} fill="none" stroke="#3a3a3a" strokeDasharray="4 3" />
                      <text x={30} y={44} textAnchor="middle" className="node-sub">
                        ?
                      </text>
                    </>
                  )}
                  <text x={56} y={34} className="node-name">
                    {clip(node.title, 20)}
                  </text>
                  <text x={56} y={53} className="node-sub">
                    {clip(node.subtitle, 24)}
                  </text>
                </g>
              )
            })}
          </g>
        </svg>
      </div>

      <div className="tree-legend">
        <span className="legend-item">
          <span className="legend-swatch" /> identified parent
        </span>
        <span className="legend-item">
          <span className="legend-swatch dashed" /> known but not researched
        </span>
        <span className="legend-item">
          <span
            style={{
              width: 14,
              height: 14,
              borderRadius: 4,
              border: '1px dashed #3a3a3a',
              display: 'inline-block',
            }}
          />
          unresolved parent
        </span>
        <span className="legend-item">Click a person to open their record</span>
      </div>
    </>
  )
}
