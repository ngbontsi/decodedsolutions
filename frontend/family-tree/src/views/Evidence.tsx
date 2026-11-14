import { useState } from 'react'
import { BookOpen, ChevronDown, ChevronRight, FileText, Paperclip, Plus, ScrollText } from 'lucide-react'
import { api, ApiError } from '../api'
import type { ArchiveData, Claim } from '../types'
import { displayName, fullDate, titleCase } from '../lib/format'
import { Badge, Empty, Panel, StatusBadge } from '../components/ui'

const NEXT_STATUS: Record<string, string[]> = {
  UNVERIFIED: ['UNDER_REVIEW'],
  UNDER_REVIEW: ['SUPPORTED', 'DISPUTED', 'RETRACTED'],
  SUPPORTED: ['DISPUTED', 'RETRACTED'],
  DISPUTED: ['UNDER_REVIEW', 'SUPPORTED', 'RETRACTED'],
  RETRACTED: [],
}

export default function Evidence({
  data,
  onAddSource,
  onAddClaim,
  onAttachEvidence,
  onChanged,
  onOpenPerson,
}: {
  data: ArchiveData
  onAddSource: () => void
  onAddClaim: () => void
  onAttachEvidence: (claim: Claim) => void
  onChanged: (message: string) => void
  onOpenPerson: (personId: string) => void
}) {
  const [filter, setFilter] = useState<'all' | 'open' | 'settled'>('all')
  const [expanded, setExpanded] = useState<Record<string, boolean>>({})
  const [busy, setBusy] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  const claims = data.claims.filter((claim) => {
    if (filter === 'open') return claim.status === 'UNDER_REVIEW' || claim.status === 'UNVERIFIED'
    if (filter === 'settled') return claim.status === 'SUPPORTED' || claim.status === 'RETRACTED'
    return true
  })

  const move = async (claim: Claim, status: string) => {
    setBusy(claim.id)
    setError(null)
    try {
      await api.transitionClaim(claim.id, status)
      onChanged(`Claim moved to ${status.replace(/_/g, ' ').toLowerCase()}`)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Could not change the claim status.')
    } finally {
      setBusy(null)
    }
  }

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Evidence</div>
          <h1 className="page-title">Claims and what supports them</h1>
          <p className="page-sub">
            Evidence never attaches to a person. It attaches to a specific claim, with a stance and a
            weight, so disagreement stays visible.
          </p>
        </div>
        <div className="actions">
          <button className="btn" onClick={onAddSource}>
            <BookOpen /> Add source
          </button>
          <button className="btn btn-primary" onClick={onAddClaim}>
            <Plus /> Submit claim
          </button>
        </div>
      </div>

      {error && <div className="banner">{error}</div>}

      <div className="toolbar">
        <div className="actions">
          {(['all', 'open', 'settled'] as const).map((option) => (
            <button
              key={option}
              className={`btn btn-sm ${filter === option ? 'btn-primary' : ''}`}
              onClick={() => setFilter(option)}
            >
              {titleCase(option)}
            </button>
          ))}
        </div>
        <span className="hint">{claims.length} claims shown</span>
      </div>

      <div className="detail-grid">
        <div className="stack">
          <Panel title="Claims" icon={<ScrollText />} count={claims.length}>
            {claims.length === 0 ? (
              <Empty>No claims in this view.</Empty>
            ) : (
              claims.map((claim) => {
                const evidence = data.claimEvidence[claim.id] ?? []
                const subject = claim.subjectPersonId
                  ? displayName(data.names[claim.subjectPersonId])
                  : 'Archive'
                return (
                  <div className="claim" key={claim.id} style={{ marginBottom: 10 }}>
                    <div className="claim-text">{claim.statement}</div>
                    <div className="claim-meta">
                      <StatusBadge status={claim.status} />
                      <button
                        className="btn btn-ghost btn-sm"
                        onClick={() => onOpenPerson(claim.subjectPersonId ?? '')}
                      >
                        {subject}
                      </button>
                      <span>·</span>
                      <span>{fullDate(claim.submittedAt)}</span>
                      <span>·</span>
                      <span>{evidence.length} evidence</span>
                    </div>

                    {evidence.length > 0 && (
                      <div className="evidence-list">
                        {evidence.map((link) => {
                          const doc = Object.values(data.documents)
                            .flat()
                            .find((d) => d.id === link.id.evidenceId)
                          const source = doc
                            ? data.sources.find((s) => s.id === doc.sourceId)
                            : undefined
                          return (
                            <div
                              className="evidence-item"
                              key={`${link.id.claimId}-${link.id.evidenceId}`}
                            >
                              <span
                                className={`evidence-dot ${
                                  link.stance === 'CONTRADICTS'
                                    ? 'contradicts'
                                    : link.stance === 'CONTEXT'
                                      ? 'context'
                                      : ''
                                }`}
                              />
                              <span>
                                <strong style={{ color: 'var(--text-dim)' }}>
                                  {source?.title ?? 'Document'}
                                </strong>{' '}
                                — {link.stance.toLowerCase()}, {link.weight.toLowerCase()}
                                {link.note ? ` · ${link.note}` : ''}
                              </span>
                            </div>
                          )
                        })}
                      </div>
                    )}

                    <div className="actions" style={{ marginTop: 12 }}>
                      <button className="btn btn-sm" onClick={() => onAttachEvidence(claim)}>
                        <Paperclip /> Attach evidence
                      </button>
                      {NEXT_STATUS[claim.status]?.map((status) => (
                        <button
                          key={status}
                          className="btn btn-sm"
                          disabled={busy === claim.id}
                          onClick={() => void move(claim, status)}
                        >
                          {busy === claim.id ? '…' : `Mark ${status.replace(/_/g, ' ').toLowerCase()}`}
                        </button>
                      ))}
                    </div>
                  </div>
                )
              })
            )}
          </Panel>
        </div>

        <div className="stack">
          <Panel
            title="Sources"
            icon={<BookOpen />}
            count={data.sources.length}
            action={
              <button className="btn btn-sm" onClick={onAddSource}>
                <Plus /> Add
              </button>
            }
          >
            {data.sources.length === 0 ? (
              <Empty>No sources yet. Start with a person who remembers.</Empty>
            ) : (
              data.sources.map((source) => {
                const documents = data.documents[source.id] ?? []
                const isOpen = expanded[source.id] ?? false
                return (
                  <div key={source.id}>
                    <button
                      className="source-head"
                      style={{ width: '100%', background: 'none', border: 'none', textAlign: 'left' }}
                      onClick={() => setExpanded((prev) => ({ ...prev, [source.id]: !isOpen }))}
                    >
                      {isOpen ? <ChevronDown size={15} /> : <ChevronRight size={15} />}
                      <div className="row-main">
                        <div className="source-title">{source.title}</div>
                        <div className="source-meta">
                          {titleCase(source.type)}
                          {source.authorOrSpeaker ? ` · ${source.authorOrSpeaker}` : ''} ·{' '}
                          {documents.length} document{documents.length === 1 ? '' : 's'}
                        </div>
                      </div>
                      <Badge>{titleCase(source.accessLevel)}</Badge>
                    </button>
                    {isOpen && (
                      <div style={{ padding: '4px 0 12px 24px' }}>
                        {documents.length === 0 ? (
                          <div className="hint">No document attached yet.</div>
                        ) : (
                          documents.map((document) => (
                            <div className="evidence-item" key={document.id} style={{ marginBottom: 6 }}>
                              <FileText size={13} color="#8f877c" style={{ marginTop: 3 }} />
                              <span>
                                {document.mediaType === 'TESTIMONY'
                                  ? document.description ?? 'Written testimony'
                                  : document.originalFilename ?? document.mediaType}
                                <span className="mono" style={{ display: 'block' }}>
                                  {fullDate(document.createdAt)}
                                </span>
                              </span>
                            </div>
                          ))
                        )}
                      </div>
                    )}
                  </div>
                )
              })
            )}
          </Panel>

          <div className="note">
            <strong>Claim states are a one-way safety rail.</strong> A claim moves from unverified to
            review, then to supported or disputed. Nothing jumps straight to supported, and a
            retracted claim cannot be reopened.
          </div>
        </div>
      </div>
    </>
  )
}
