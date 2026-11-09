import { useMemo, useState, type FormEvent } from 'react'
import { Building2, Landmark, Plus, Users } from 'lucide-react'
import { api, ApiError } from '../api'
import type { ArchiveData } from '../types'
import { formatDate, titleCase } from '../lib/format'
import { Badge, Banner, Empty, Field, Modal, Panel } from '../components/ui'

type Draft = 'event' | 'organisation' | null

export default function Heritage({
  data,
  onChanged,
}: {
  data: ArchiveData
  onChanged: (message: string) => void
}) {
  const [draft, setDraft] = useState<Draft>(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [title, setTitle] = useState('')
  const [eventDate, setEventDate] = useState('')
  const [precision, setPrecision] = useState('YEAR')
  const [type, setType] = useState('PERSONAL')
  const [description, setDescription] = useState('')

  const orgMemberCounts = useMemo(() => {
    const counts: Record<string, number> = {}
    for (const list of Object.values(data.orgMemberships)) {
      for (const membership of list) {
        counts[membership.organisationId] = (counts[membership.organisationId] ?? 0) + 1
      }
    }
    return counts
  }, [data.orgMemberships])

  const close = () => {
    setDraft(null)
    setError(null)
    setTitle('')
    setDescription('')
    setEventDate('')
    setType('PERSONAL')
  }

  const submit = (event: FormEvent) => {
    event.preventDefault()
    setBusy(true)
    setError(null)
    void (async () => {
      try {
        if (draft === 'event') {
          await api.createEvent({
            title: title.trim(),
            description: description || null,
            eventDate: eventDate || null,
            datePrecision: precision,
            eventType: type,
          })
          onChanged('Event added to the record')
        } else {
          await api.createOrganisation({
            name: title.trim(),
            organisationType: type,
            foundedDate: eventDate || null,
            description: description || null,
          })
          onChanged('Organisation added')
        }
        close()
      } catch (err) {
        setError(err instanceof ApiError ? err.message : 'Could not save that.')
      } finally {
        setBusy(false)
      }
    })()
  }

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Heritage</div>
          <h1 className="page-title">The world around the family</h1>
          <p className="page-sub">
            Events and organisations give a family its context — migrations, churches, schools,
            employers. They are recorded separately from the people they touch.
          </p>
        </div>
        <div className="actions">
          <button className="btn" onClick={() => { setType('CIVIC'); setDraft('organisation') }}>
            <Building2 /> Add organisation
          </button>
          <button className="btn btn-primary" onClick={() => { setType('PERSONAL'); setDraft('event') }}>
            <Plus /> Add event
          </button>
        </div>
      </div>

      <div className="detail-grid">
        <div className="stack">
          <Panel title="Historical events" icon={<Landmark />} count={data.events.length}>
            {data.events.length === 0 ? (
              <Empty>No events yet. A family history is more than names and dates.</Empty>
            ) : (
              <div className="timeline">
                {data.events.map((event) => (
                  <div className="timeline-item" key={event.id}>
                    <div className="timeline-title" style={{ fontFamily: 'var(--serif)', fontSize: 15 }}>
                      {event.title}
                    </div>
                    <div className="timeline-sub">
                      <Badge tone="gold">{titleCase(event.type)}</Badge>{' '}
                      {event.eventDate
                        ? `· ${formatDate(event.eventDate, event.datePrecision)}`
                        : '· date unknown'}
                    </div>
                    {event.description && <p className="bio">{event.description}</p>}
                  </div>
                ))}
              </div>
            )}
          </Panel>
        </div>

        <div className="stack">
          <Panel title="Organisations" icon={<Building2 />} count={data.organisations.length}>
            {data.organisations.length === 0 ? (
              <Empty>No organisations recorded.</Empty>
            ) : (
              data.organisations.map((org) => (
                <div className="row" key={org.id}>
                  <div className="row-main">
                    <div className="row-title">{org.name}</div>
                    <div className="row-sub">
                      {titleCase(org.type)}
                      {org.foundedDate ? ` · from ${formatDate(org.foundedDate, 'YEAR')}` : ''}
                    </div>
                  </div>
                  <Badge tone="blue">
                    <Users size={11} /> {orgMemberCounts[org.id] ?? 0}
                  </Badge>
                </div>
              ))
            )}
          </Panel>

          <div className="note">
            <strong>Context is evidence too.</strong> An event or organisation can be cited by a
            claim, so a migration story or a church record supports the facts it touches.
          </div>
        </div>
      </div>

      {draft && (
        <Modal
          title={draft === 'event' ? 'Add an event' : 'Add an organisation'}
          subtitle={
            draft === 'event'
              ? 'Record what happened and when. Precision is explicit, so a decade is never mistaken for a year.'
              : 'Churches, schools, employers, regiments — the institutions a family belonged to.'
          }
          onClose={close}
          footer={
            <>
              <button className="btn" type="button" onClick={close}>
                Cancel
              </button>
              <button
                className="btn btn-primary"
                type="submit"
                form="heritage-form"
                disabled={busy}
              >
                {busy ? 'Saving…' : draft === 'event' ? 'Add event' : 'Add organisation'}
              </button>
            </>
          }
        >
          <form id="heritage-form" onSubmit={submit} style={{ display: 'contents' }}>
            {error && <Banner>{error}</Banner>}
            <div className="form-grid">
              <Field label={draft === 'event' ? 'Title' : 'Name'} span>
                <input
                  autoFocus
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder={
                    draft === 'event' ? 'e.g. The family moves to Cape Town' : 'e.g. Ngcobo Methodist Church'
                  }
                />
              </Field>
              <Field label={draft === 'event' ? 'Type' : 'Organisation type'}>
                <select value={type} onChange={(e) => setType(e.target.value)}>
                  {draft === 'event' ? (
                    <>
                      <option value="PERSONAL">Personal</option>
                      <option value="SOCIAL">Social</option>
                      <option value="CULTURAL">Cultural</option>
                      <option value="POLITICAL">Political</option>
                      <option value="LEGAL">Legal</option>
                      <option value="OTHER">Other</option>
                    </>
                  ) : (
                    <>
                      <option value="RELIGIOUS">Religious</option>
                      <option value="EDUCATION">Education</option>
                      <option value="CIVIC">Civic</option>
                      <option value="BUSINESS">Business</option>
                      <option value="MILITARY">Military</option>
                      <option value="POLITICAL">Political</option>
                      <option value="OTHER">Other</option>
                    </>
                  )}
                </select>
              </Field>
              <Field label={draft === 'event' ? 'Date' : 'Founded'}>
                <input type="date" value={eventDate} onChange={(e) => setEventDate(e.target.value)} />
              </Field>
              {draft === 'event' && (
                <Field label="Date precision">
                  <select value={precision} onChange={(e) => setPrecision(e.target.value)}>
                    <option value="DAY">Exact day</option>
                    <option value="MONTH">Month</option>
                    <option value="YEAR">Year</option>
                    <option value="DECADE">Decade</option>
                    <option value="APPROXIMATE">Approximate</option>
                    <option value="UNKNOWN">Unknown</option>
                  </select>
                </Field>
              )}
              <Field label="Description" span>
                <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
              </Field>
            </div>
          </form>
        </Modal>
      )}
    </>
  )
}
