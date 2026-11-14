import {
  Users,
  UsersRound,
  HeartHandshake,
  BookOpen,
  ScrollText,
  Landmark,
  ArrowUpRight,
  Plus,
  Sparkles,
} from 'lucide-react'
import type { ArchiveData } from '../types'
import { displayName, fullDate, timeAgo, titleCase } from '../lib/format'
import { Panel, Badge } from '../components/ui'

export default function Overview({
  data,
  onOpenPerson,
  onAddPerson,
}: {
  data: ArchiveData
  onOpenPerson: (personId: string) => void
  onAddPerson: () => void
}) {
  const stats = [
    { label: 'People', value: data.people.length, icon: <Users /> },
    { label: 'Families', value: data.families.length, icon: <UsersRound /> },
    { label: 'Unions', value: data.unions.length, icon: <HeartHandshake /> },
    { label: 'Sources', value: data.sources.length, icon: <BookOpen /> },
    { label: 'Claims', value: data.claims.length, icon: <ScrollText /> },
    { label: 'Events', value: data.events.length, icon: <Landmark /> },
  ]

  const recentPeople = [...data.people]
    .sort((a, b) => b.createdAt.localeCompare(a.createdAt))
    .slice(0, 5)

  const openClaims = data.claims.filter((c) => c.status !== 'SUPPORTED' && c.status !== 'RETRACTED')

  return (
    <>
      <div className="hero">
        <div className="eyebrow">Family &amp; heritage archive</div>
        <h1 className="hero-title">{data.archive.name}</h1>
        <p className="hero-desc">
          {data.archive.description ??
            'A living record of the people, relationships and evidence that hold a family together.'}
        </p>
        <div className="hero-meta">
          <div>
            <strong>Opened</strong>
            {fullDate(data.archive.createdAt)}
          </div>
          <div>
            <strong>Status</strong>
            {titleCase(data.archive.status)}
          </div>
          <div>
            <strong>Contributors</strong>
            {data.contributors.length}
          </div>
          <div>
            <strong>Audit entries</strong>
            {data.audit.length} recent
          </div>
        </div>
      </div>

      <div className="stat-grid">
        {stats.map((stat) => (
          <div className="stat" key={stat.label}>
            <div className="stat-value">{stat.value}</div>
            <div className="stat-label">{stat.label}</div>
          </div>
        ))}
      </div>

      <div className="two-col">
        <div className="stack">
          <Panel
            title="Recently added"
            icon={<Users />}
            count={recentPeople.length}
            action={
              <button className="btn btn-sm" onClick={onAddPerson}>
                <Plus /> Add person
              </button>
            }
          >
            {recentPeople.length === 0 ? (
              <div className="empty">No people yet. Add the first one.</div>
            ) : (
              recentPeople.map((person) => (
                <button
                  key={person.id}
                  className="row"
                  style={{ width: '100%', background: 'none', border: 'none', textAlign: 'left' }}
                  onClick={() => onOpenPerson(person.id)}
                >
                  <div className="row-main">
                    <div className="row-title">{displayName(data.names[person.id])}</div>
                    <div className="row-sub">
                      {person.gender === 'UNKNOWN' ? 'Gender not recorded' : titleCase(person.gender)}
                      {' · added '}
                      {timeAgo(person.createdAt)}
                    </div>
                  </div>
                  <ArrowUpRight size={15} color="#8f877c" />
                </button>
              ))
            )}
          </Panel>

          <Panel title="Families" icon={<UsersRound />} count={data.families.length}>
            {data.families.length === 0 ? (
              <div className="empty">No family groups yet.</div>
            ) : (
              data.families.map((family) => {
                const members = data.memberships[family.id] ?? []
                return (
                  <div className="row" key={family.id}>
                    <div className="row-main">
                      <div className="row-title">{family.name}</div>
                      <div className="row-sub">
                        {members.length} member{members.length === 1 ? '' : 's'} ·{' '}
                        {titleCase(family.lineageType)}
                      </div>
                    </div>
                    <Badge tone="gold">{titleCase(family.lineageType)}</Badge>
                  </div>
                )
              })
            )}
          </Panel>
        </div>

        <div className="stack">
          <Panel title="Needs attention" icon={<ScrollText />} count={openClaims.length}>
            {openClaims.length === 0 ? (
              <div className="empty">Every claim is settled. Good.</div>
            ) : (
              openClaims.slice(0, 6).map((claim) => (
                <div className="row" key={claim.id}>
                  <div className="row-main">
                    <div className="row-title" style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>
                      {claim.statement}
                    </div>
                    <div className="row-sub">{timeAgo(claim.submittedAt)}</div>
                  </div>
                  <Badge tone={claim.status === 'DISPUTED' ? 'red' : 'amber'}>
                    {claim.status.replace(/_/g, ' ')}
                  </Badge>
                </div>
              ))
            )}
          </Panel>

          <Panel title="Recent activity" icon={<Sparkles />} count={data.audit.length}>
            {data.audit.length === 0 ? (
              <div className="empty">No activity recorded yet.</div>
            ) : (
              <div className="timeline">
                {data.audit.slice(0, 7).map((entry) => (
                  <div className="timeline-item" key={entry.id}>
                    <div className="timeline-title">
                      {titleCase(entry.action)} · {entry.entityType}
                    </div>
                    <div className="timeline-sub">{timeAgo(entry.occurredAt)}</div>
                  </div>
                ))}
              </div>
            )}
          </Panel>

          <div className="note">
            <strong>Nothing is ever overwritten.</strong> Names, parents and claims are closed and
            superseded, never deleted. Every write lands in an audit log that the database refuses to
            edit.
          </div>
        </div>
      </div>
    </>
  )
}
