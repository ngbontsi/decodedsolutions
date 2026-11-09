import {
  ArrowLeft,
  BookOpen,
  Building2,
  FileText,
  GitBranch,
  HeartHandshake,
  Link2,
  Plus,
  ScrollText,
  UsersRound,
} from 'lucide-react'
import type { ArchiveData, Claim, Parentage } from '../types'
import {
  displayName,
  formatDate,
  fullDate,
  lifeSpan,
  titleCase,
} from '../lib/format'
import { Avatar, Badge, Empty, IdentityBadge, Panel, StatusBadge } from '../components/ui'

function roleLabel(role: string): string {
  if (role === 'UNKNOWN') return 'Parent'
  return titleCase(role)
}

export default function PersonDetail({
  data,
  personId,
  onBack,
  onOpenPerson,
  onAddName,
  onAddParentage,
  onAddClaim,
}: {
  data: ArchiveData
  personId: string
  onBack: () => void
  onOpenPerson: (personId: string) => void
  onAddName: () => void
  onAddParentage: () => void
  onAddClaim: () => void
}) {
  const person = data.people.find((p) => p.id === personId)
  if (!person) {
    return (
      <div className="card card-pad empty">
        That person is no longer in the archive.{' '}
        <button className="btn btn-sm" onClick={onBack}>
          Go back
        </button>
      </div>
    )
  }

  const names = data.names[person.id] ?? []
  const parentLinks = data.parentages
    .filter((p) => p.childId === person.id && p.status === 'ACTIVE')
    .sort((a, b) => a.createdAt.localeCompare(b.createdAt))
  const childLinks = data.parentages
    .filter((p) => p.parentId === person.id && p.status === 'ACTIVE')
    .sort((a, b) => a.createdAt.localeCompare(b.createdAt))
  const unions = data.unions.filter((union) =>
    (data.unionParticipants[union.id] ?? []).some((p) => p.id.personId === person.id),
  )
  const memberships = Object.entries(data.memberships).flatMap(([familyId, list]) =>
    list
      .filter((m) => m.personId === person.id && m.status === 'ACTIVE')
      .map((m) => ({ family: data.families.find((f) => f.id === familyId), membership: m })),
  )
  const orgs = data.orgMemberships[person.id] ?? []
  const claims = data.claims.filter((c) => c.subjectPersonId === person.id)
  const name = displayName(names)

  return (
    <>
      <button className="btn btn-ghost" onClick={onBack} style={{ marginBottom: 14 }}>
        <ArrowLeft /> Back
      </button>

      <div className="detail-head">
        <Avatar name={name} gender={person.gender} size="lg" />
        <div className="detail-id">
          <h1 className="detail-name">{name}</h1>
          <div className="detail-meta">
            {lifeSpan(person)} · {person.gender === 'UNKNOWN' ? 'Gender not recorded' : titleCase(person.gender)}
          </div>
          <div className="badge-row" style={{ marginTop: 10 }}>
            <Badge tone={person.living ? 'green' : 'ghost'}>
              {person.living ? 'Living' : 'Deceased'}
            </Badge>
            <Badge tone={person.visibility === 'PUBLIC' ? 'gold' : undefined}>
              {titleCase(person.visibility)}
            </Badge>
            <Badge>{titleCase(person.status)}</Badge>
          </div>
        </div>
        <div className="actions">
          <button className="btn" onClick={onAddName}>
            <Plus /> Name
          </button>
          <button className="btn" onClick={onAddParentage}>
            <GitBranch /> Parent
          </button>
          <button className="btn btn-primary" onClick={onAddClaim}>
            <ScrollText /> Claim
          </button>
        </div>
      </div>

      <div className="detail-grid">
        <div className="stack">
          <Panel title="Names" icon={<FileText />} count={names.length} action={<button className="btn btn-sm" onClick={onAddName}><Plus /> Add</button>}>
            {names.length === 0 ? (
              <Empty>The name is not known yet.</Empty>
            ) : (
              <div className="timeline">
                {names.map((entry) => (
                  <div className="timeline-item" key={entry.id}>
                    <div className="timeline-title" style={{ fontFamily: 'var(--serif)', fontSize: 15, color: 'var(--text)' }}>
                      {entry.fullName}
                      {entry.preferred && (
                        <span style={{ marginLeft: 8 }}>
                          <Badge tone="gold">Preferred</Badge>
                        </span>
                      )}
                    </div>
                    <div className="timeline-sub">
                      {titleCase(entry.nameType ?? 'OTHER')} name · added {fullDate(entry.createdAt)}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Panel>

          <Panel
            title="Parents"
            icon={<Link2 />}
            count={parentLinks.length}
            action={<button className="btn btn-sm" onClick={onAddParentage}><Plus /> Add</button>}
          >
            {parentLinks.length === 0 ? (
              <Empty>No parents recorded. This is not the same as having no parents.</Empty>
            ) : (
              parentLinks.map((link) => <ParentRow key={link.id} link={link} data={data} onOpenPerson={onOpenPerson} />)
            )}
          </Panel>

          <Panel title="Children" icon={<GitBranch />} count={childLinks.length}>
            {childLinks.length === 0 ? (
              <Empty>No children recorded against this person.</Empty>
            ) : (
              childLinks.map((link) => {
                const child = data.people.find((p) => p.id === link.childId)
                if (!child) return null
                return (
                  <button
                    key={link.id}
                    className="row"
                    style={{ width: '100%', background: 'none', border: 'none', textAlign: 'left', borderBottom: '1px solid var(--line-soft)' }}
                    onClick={() => onOpenPerson(child.id)}
                  >
                    <Avatar name={displayName(data.names[child.id])} gender={child.gender} size="sm" />
                    <div className="row-main">
                      <div className="row-title">{displayName(data.names[child.id])}</div>
                      <div className="row-sub">
                        {lifeSpan(child)} · {titleCase(link.type)}
                      </div>
                    </div>
                    <Badge tone="ghost">{roleLabel(link.role)} link</Badge>
                  </button>
                )
              })
            )}
          </Panel>

          <Panel title="Unions" icon={<HeartHandshake />} count={unions.length}>
            {unions.length === 0 ? (
              <Empty>No unions recorded.</Empty>
            ) : (
              unions.map((union) => {
                const participants = data.unionParticipants[union.id] ?? []
                const others = participants.filter((p) => p.id.personId !== person.id)
                return (
                  <div className="row" key={union.id}>
                    <div className="row-main">
                      <div className="row-title">
                        {titleCase(union.type)}
                        {others.length > 0 && (
                          <span style={{ color: 'var(--muted)', fontWeight: 400 }}>
                            {' '}
                            with{' '}
                            {others
                              .map((other) => displayName(data.names[other.id.personId]))
                              .join(', ')}
                          </span>
                        )}
                      </div>
                      <div className="row-sub">
                        {union.startDate ? formatDate(union.startDate) : 'Start date unknown'} ·{' '}
                        {titleCase(union.status)}
                      </div>
                    </div>
                  </div>
                )
              })
            )}
          </Panel>
        </div>

        <div className="stack">
          {person.biography && (
            <Panel title="Life" icon={<BookOpen />}>
              <p className="bio">{person.biography}</p>
            </Panel>
          )}

          <Panel title="Claims" icon={<ScrollText />} count={claims.length} action={<button className="btn btn-sm" onClick={onAddClaim}><Plus /> Claim</button>}>
            {claims.length === 0 ? (
              <Empty>No claims recorded about this person.</Empty>
            ) : (
              claims.map((claim) => <ClaimCard key={claim.id} claim={claim} data={data} />)
            )}
          </Panel>

          <Panel title="Family groups" icon={<UsersRound />} count={memberships.length}>
            {memberships.length === 0 ? (
              <Empty>Not asserted as a member of any family group.</Empty>
            ) : (
              memberships.map(({ family, membership }) => (
                <div className="row" key={membership.id}>
                  <div className="row-main">
                    <div className="row-title">{family?.name ?? 'Family'}</div>
                    <div className="row-sub">
                      {titleCase(membership.connectionType)} · {titleCase(membership.basis)}
                    </div>
                  </div>
                  <Badge tone="gold">{titleCase(membership.connectionType)}</Badge>
                </div>
              ))
            )}
          </Panel>

          <Panel title="Organisations" icon={<Building2 />} count={orgs.length}>
            {orgs.length === 0 ? (
              <Empty>No organisational memberships.</Empty>
            ) : (
              orgs.map((membership) => {
                const org = data.organisations.find((o) => o.id === membership.organisationId)
                return (
                  <div className="row" key={membership.id}>
                    <div className="row-main">
                      <div className="row-title">{org?.name ?? 'Organisation'}</div>
                      <div className="row-sub">{membership.role ?? 'Member'}</div>
                    </div>
                  </div>
                )
              })
            )}
          </Panel>

          <div className="note">
            <strong>Identity is a claim, not a fact.</strong> Every parent link carries an identity
            status. A parent the family remembers but has not researched stays visible without
            inventing a person.
          </div>
        </div>
      </div>
    </>
  )
}

function ParentRow({
  link,
  data,
  onOpenPerson,
}: {
  link: Parentage
  data: ArchiveData
  onOpenPerson: (personId: string) => void
}) {
  const parent = link.parentId ? data.people.find((p) => p.id === link.parentId) : undefined

  if (parent) {
    return (
      <button
        className="row"
        style={{ width: '100%', background: 'none', border: 'none', textAlign: 'left', borderBottom: '1px solid var(--line-soft)' }}
        onClick={() => onOpenPerson(parent.id)}
      >
        <Avatar name={displayName(data.names[parent.id])} gender={parent.gender} size="sm" />
        <div className="row-main">
          <div className="row-title">{displayName(data.names[parent.id])}</div>
          <div className="row-sub">
            {roleLabel(link.role)} · {titleCase(link.type)}
          </div>
        </div>
        <IdentityBadge status={link.identityStatus} />
      </button>
    )
  }

  return (
    <div className="ghost-row" style={{ marginBottom: 8 }}>
      <div className="row-title">
        {link.identityStatus === 'UNRECORDED'
          ? link.reportedName ?? 'Name not written down'
          : link.identityStatus === 'UNIDENTIFIED'
            ? 'Identity not yet found'
            : 'Unknown'}
      </div>
      <div className="row-sub">
        {roleLabel(link.role)} · {titleCase(link.type)} ·{" "}
        {link.identityStatus === 'UNRECORDED'
          ? 'known to the family, not yet researched'
          : link.identityStatus === 'UNIDENTIFIED'
            ? 'a parent exists, name unknown'
            : 'not recorded'}
      </div>
      <div style={{ marginTop: 7 }}>
        <IdentityBadge status={link.identityStatus} />
      </div>
    </div>
  )
}

function ClaimCard({ claim, data }: { claim: Claim; data: ArchiveData }) {
  const evidence = data.claimEvidence[claim.id] ?? []
  return (
    <div className="claim" style={{ marginBottom: 10 }}>
      <div className="claim-text">{claim.statement}</div>
      <div className="claim-meta">
        <StatusBadge status={claim.status} />
        <span>Confidence {claim.confidence.toLowerCase()}</span>
        <span>·</span>
        <span>{fullDate(claim.submittedAt)}</span>
      </div>
      {evidence.length > 0 && (
        <div className="evidence-list">
          {evidence.map((link) => (
            <div className="evidence-item" key={`${link.id.claimId}-${link.id.evidenceId}`}>
              <span
                className={`evidence-dot ${
                  link.stance === 'CONTRADICTS' ? 'contradicts' : link.stance === 'CONTEXT' ? 'context' : ''
                }`}
              />
              <span>
                {titleCase(link.stance)} · {link.weight.toLowerCase()} evidence
                {link.note ? ` — ${link.note}` : ''}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
