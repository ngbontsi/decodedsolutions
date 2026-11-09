import { useMemo, useState } from 'react'
import { Plus, Search } from 'lucide-react'
import type { ArchiveData, Person } from '../types'
import { displayName, lifeSpan, titleCase } from '../lib/format'
import { Avatar, Badge } from '../components/ui'

function sortPeople(people: Person[]): Person[] {
  return [...people].sort((a, b) => {
    const aDate = a.birthDate ?? '9999'
    const bDate = b.birthDate ?? '9999'
    return aDate.localeCompare(bDate)
  })
}

export default function People({
  data,
  onOpenPerson,
  onAddPerson,
}: {
  data: ArchiveData
  onOpenPerson: (personId: string) => void
  onAddPerson: () => void
}) {
  const [query, setQuery] = useState('')

  const filtered = useMemo(() => {
    const needle = query.trim().toLowerCase()
    const people = sortPeople(data.people)
    if (!needle) return people
    return people.filter((person) => {
      const name = displayName(data.names[person.id]).toLowerCase()
      return name.includes(needle) || lifeSpan(person).toLowerCase().includes(needle)
    })
  }, [data, query])

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">People</div>
          <h1 className="page-title">Everyone in the archive</h1>
          <p className="page-sub">
            A person can exist before a name is known. Names are added as claims about identity, and
            kept as history when they change.
          </p>
        </div>
        <div className="actions">
          <button className="btn btn-primary" onClick={onAddPerson}>
            <Plus /> Add person
          </button>
        </div>
      </div>

      <div className="toolbar">
        <div className="search">
          <Search />
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by name or year…"
            aria-label="Search people"
          />
        </div>
        <span className="hint">
          {filtered.length} of {data.people.length}
        </span>
      </div>

      {filtered.length === 0 ? (
        <div className="card card-pad empty">Nobody matches that search.</div>
      ) : (
        <div className="people-grid">
          {filtered.map((person) => {
            const claims = data.claims.filter((c) => c.subjectPersonId === person.id)
            return (
              <button className="person-card" key={person.id} onClick={() => onOpenPerson(person.id)}>
                <Avatar
                  name={displayName(data.names[person.id])}
                  gender={person.gender}
                  size="lg"
                />
                <div className="person-card-main">
                  <div className="person-card-name">{displayName(data.names[person.id])}</div>
                  <div className="person-card-meta">{lifeSpan(person)}</div>
                  <div className="badge-row" style={{ marginTop: 7 }}>
                    <Badge tone={person.living ? 'green' : 'ghost'}>
                      {person.living ? 'Living' : 'Deceased'}
                    </Badge>
                    {claims.length > 0 && <Badge tone="blue">{claims.length} claim{claims.length === 1 ? '' : 's'}</Badge>}
                    {person.visibility !== 'FAMILY' && <Badge>{titleCase(person.visibility)}</Badge>}
                  </div>
                </div>
              </button>
            )
          })}
        </div>
      )}
    </>
  )
}
