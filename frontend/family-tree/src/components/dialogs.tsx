import { useState, type FormEvent } from 'react'
import { api, ApiError } from '../api'
import type { ArchiveData, Claim } from '../types'
import { displayName } from '../lib/format'
import { Banner, Field, Modal } from './ui'

interface DialogProps {
  onClose: () => void
  onSaved: (message: string) => void
}

function useSubmit(onSaved: (message: string) => void) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const run = async (action: () => Promise<string>) => {
    setBusy(true)
    setError(null)
    try {
      const message = await action()
      onSaved(message)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Something went wrong.')
    } finally {
      setBusy(false)
    }
  }

  return { busy, error, run }
}

function SubmitFooter({
  busy,
  label,
  formId,
  onClose,
}: {
  busy: boolean
  label: string
  formId: string
  onClose: () => void
}) {
  return (
    <>
      <button className="btn" onClick={onClose} type="button">
        Cancel
      </button>
      <button className="btn btn-primary" type="submit" form={formId} disabled={busy}>
        {busy ? 'Saving…' : label}
      </button>
    </>
  )
}

/* ------------------------------------------------------------------ */

export function PersonDialog({
  onClose,
  onSaved,
}: DialogProps) {
  const { busy, error, run } = useSubmit(onSaved)
  const [fullName, setFullName] = useState('')
  const [gender, setGender] = useState('UNKNOWN')
  const [birthDate, setBirthDate] = useState('')
  const [precision, setPrecision] = useState('YEAR')
  const [visibility, setVisibility] = useState('FAMILY')
  const [biography, setBiography] = useState('')

  const submit = (event: FormEvent) => {
    event.preventDefault()
    void run(async () => {
      const person = await api.createPerson({
        gender,
        birthDate: birthDate || null,
        birthDatePrecision: precision,
        visibility,
        biography: biography || null,
      })
      if (fullName.trim()) {
        await api.addName(person.id, { nameType: 'BIRTH', fullName: fullName.trim() })
      }
      return fullName.trim() ? `${fullName.trim()} added to the archive` : 'Person added'
    })
  }

  return (
    <Modal
      title="Add a person"
      subtitle="Names, dates and relationships are added separately. A person can exist unnamed."
      onClose={onClose}
      footer={<SubmitFooter busy={busy} label="Add person" formId="person-form" onClose={onClose} />}
    >
      <form id="person-form" onSubmit={submit} style={{ display: 'contents' }}>
        {error && <Banner>{error}</Banner>}
        <div className="form-grid">
          <Field label="Full name" span hint="Optional. You can record a name later.">
            <input
              autoFocus
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="e.g. Nomvula Nene"
            />
          </Field>
          <Field label="Gender">
            <select value={gender} onChange={(e) => setGender(e.target.value)}>
              <option value="UNKNOWN">Unknown</option>
              <option value="FEMALE">Female</option>
              <option value="MALE">Male</option>
              <option value="OTHER">Other</option>
            </select>
          </Field>
          <Field label="Visibility">
            <select value={visibility} onChange={(e) => setVisibility(e.target.value)}>
              <option value="FAMILY">Family</option>
              <option value="PUBLIC">Public</option>
              <option value="RESTRICTED">Restricted</option>
              <option value="PRIVATE">Private</option>
            </select>
          </Field>
          <Field label="Birth date">
            <input type="date" value={birthDate} onChange={(e) => setBirthDate(e.target.value)} />
          </Field>
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
          <Field label="Biography" span>
            <textarea
              value={biography}
              onChange={(e) => setBiography(e.target.value)}
              placeholder="What should the family know about this person?"
            />
          </Field>
        </div>
      </form>
    </Modal>
  )
}

/* ------------------------------------------------------------------ */

export function NameDialog({
  personId,
  onClose,
  onSaved,
}: DialogProps & { personId: string }) {
  const { busy, error, run } = useSubmit(onSaved)
  const [fullName, setFullName] = useState('')
  const [nameType, setNameType] = useState('BIRTH')
  const [givenNames, setGivenNames] = useState('')
  const [familyName, setFamilyName] = useState('')
  const [preferred, setPreferred] = useState(true)

  const submit = (event: FormEvent) => {
    event.preventDefault()
    void run(async () => {
      await api.addName(personId, {
        nameType,
        fullName: fullName.trim(),
        givenNames: givenNames || null,
        familyName: familyName || null,
        preferred,
      })
      return 'Name recorded'
    })
  }

  return (
    <Modal
      title="Add a name"
      subtitle="Nothing is overwritten. A new preferred name closes the old one instead."
      onClose={onClose}
      footer={<SubmitFooter busy={busy} label="Add name" formId="name-form" onClose={onClose} />}
    >
      <form id="name-form" onSubmit={submit} style={{ display: 'contents' }}>
        {error && <Banner>{error}</Banner>}
        <div className="form-grid">
          <Field label="Full name" span>
            <input
              autoFocus
              required
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="e.g. Nomvula Grace Nene"
            />
          </Field>
          <Field label="Name type">
            <select value={nameType} onChange={(e) => setNameType(e.target.value)}>
              <option value="BIRTH">Birth</option>
              <option value="MAIDEN">Maiden</option>
              <option value="MARRIED">Married</option>
              <option value="CLAN">Clan</option>
              <option value="NICKNAME">Nickname</option>
              <option value="ALIAS">Alias</option>
              <option value="RELIGIOUS">Religious</option>
              <option value="OTHER">Other</option>
            </select>
          </Field>
          <Field label="Preferred">
            <select value={preferred ? 'yes' : 'no'} onChange={(e) => setPreferred(e.target.value === 'yes')}>
              <option value="yes">Yes, display this name</option>
              <option value="no">No, keep as history</option>
            </select>
          </Field>
          <Field label="Given names">
            <input value={givenNames} onChange={(e) => setGivenNames(e.target.value)} />
          </Field>
          <Field label="Family name">
            <input value={familyName} onChange={(e) => setFamilyName(e.target.value)} />
          </Field>
        </div>
      </form>
    </Modal>
  )
}

/* ------------------------------------------------------------------ */

const IDENTITY_HELP: Record<string, string> = {
  IDENTIFIED: 'You know who the parent is and they are in the archive.',
  UNRECORDED: 'The family knows the name, but nobody has researched the person yet.',
  UNIDENTIFIED: 'A parent exists, but their identity has not been established.',
  UNKNOWN: 'Nothing is known about whether a parent exists.',
}

export function ParentageDialog({
  data,
  defaultChildId,
  onClose,
  onSaved,
}: DialogProps & { data: ArchiveData; defaultChildId?: string }) {
  const { busy, error, run } = useSubmit(onSaved)
  const [childId, setChildId] = useState(defaultChildId ?? data.people[0]?.id ?? '')
  const [identity, setIdentity] = useState('IDENTIFIED')
  const [role, setRole] = useState('FATHER')
  const [type, setType] = useState('BIOLOGICAL')
  const [parentId, setParentId] = useState('')
  const [reportedName, setReportedName] = useState('')
  const [validFrom, setValidFrom] = useState('')

  const submit = (event: FormEvent) => {
    event.preventDefault()
    void run(async () => {
      await api.recordParentage({
        childId,
        parentageType: type,
        parentRole: role,
        identityStatus: identity,
        parentId: identity === 'IDENTIFIED' ? parentId : null,
        reportedName: identity === 'UNRECORDED' ? reportedName : null,
        validFrom: validFrom || null,
      })
      return 'Parent link recorded'
    })
  }

  return (
    <Modal
      title="Record a parent"
      subtitle="Four states, because 'we do not know' and 'not written down' are different problems."
      onClose={onClose}
      footer={<SubmitFooter busy={busy} label="Record parent" formId="parentage-form" onClose={onClose} />}
    >
      <form id="parentage-form" onSubmit={submit} style={{ display: 'contents' }}>
        {error && <Banner>{error}</Banner>}
        <div className="form-grid">
          <Field label="Child" span>
            <select value={childId} onChange={(e) => setChildId(e.target.value)} required>
              {data.people.map((person) => (
                <option key={person.id} value={person.id}>
                  {displayName(data.names[person.id])}
                </option>
              ))}
            </select>
          </Field>
          <Field label="What do we know?" span hint={IDENTITY_HELP[identity]}>
            <select value={identity} onChange={(e) => setIdentity(e.target.value)}>
              <option value="IDENTIFIED">Identified — the person is in the archive</option>
              <option value="UNRECORDED">Unrecorded — name known, person not researched</option>
              <option value="UNIDENTIFIED">Unidentified — parent exists, name unknown</option>
              <option value="UNKNOWN">Unknown — nothing is known</option>
            </select>
          </Field>
          {identity === 'IDENTIFIED' && (
            <Field label="Parent" span>
              <select value={parentId} onChange={(e) => setParentId(e.target.value)} required>
                <option value="">Choose a person…</option>
                {data.people
                  .filter((p) => p.id !== childId)
                  .map((person) => (
                    <option key={person.id} value={person.id}>
                      {displayName(data.names[person.id])}
                    </option>
                  ))}
              </select>
            </Field>
          )}
          {identity === 'UNRECORDED' && (
            <Field label="Reported name" span hint="The name the family uses, even if unresearched.">
              <input
                value={reportedName}
                onChange={(e) => setReportedName(e.target.value)}
                placeholder="e.g. Mama Thandi"
                required
              />
            </Field>
          )}
          <Field label="Role">
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="MOTHER">Mother</option>
              <option value="FATHER">Father</option>
              <option value="PARENT">Parent</option>
              <option value="UNKNOWN">Unknown</option>
            </select>
          </Field>
          <Field label="Parentage type">
            <select value={type} onChange={(e) => setType(e.target.value)}>
              <option value="BIOLOGICAL">Biological</option>
              <option value="ADOPTIVE">Adoptive</option>
              <option value="LEGAL">Legal</option>
              <option value="OTHER_RECOGNISED">Other recognised</option>
            </select>
          </Field>
          <Field label="Known from" span>
            <input type="date" value={validFrom} onChange={(e) => setValidFrom(e.target.value)} />
          </Field>
        </div>
      </form>
    </Modal>
  )
}

/* ------------------------------------------------------------------ */

export function ClaimDialog({
  data,
  defaultPersonId,
  onClose,
  onSaved,
}: DialogProps & { data: ArchiveData; defaultPersonId?: string }) {
  const { busy, error, run } = useSubmit(onSaved)
  const [personId, setPersonId] = useState(defaultPersonId ?? data.people[0]?.id ?? '')
  const [statement, setStatement] = useState('')

  const submit = (event: FormEvent) => {
    event.preventDefault()
    void run(async () => {
      await api.createClaim({ subjectPersonId: personId, statement: statement.trim() })
      return 'Claim submitted for review'
    })
  }

  return (
    <Modal
      title="Submit a claim"
      subtitle="Claims belong to one subject and carry their own evidence. People never carry evidence directly."
      onClose={onClose}
      footer={<SubmitFooter busy={busy} label="Submit claim" formId="claim-form" onClose={onClose} />}
    >
      <form id="claim-form" onSubmit={submit} style={{ display: 'contents' }}>
        {error && <Banner>{error}</Banner>}
        <div className="form-grid">
          <Field label="About" span>
            <select value={personId} onChange={(e) => setPersonId(e.target.value)} required>
              {data.people.map((person) => (
                <option key={person.id} value={person.id}>
                  {displayName(data.names[person.id])}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Statement" span hint="One factual statement. Keep it specific enough to verify.">
            <textarea
              autoFocus
              required
              value={statement}
              onChange={(e) => setStatement(e.target.value)}
              placeholder="e.g. Nomvula was born in Ngcobo in 1928"
            />
          </Field>
        </div>
      </form>
    </Modal>
  )
}

/* ------------------------------------------------------------------ */

export function SourceDialog({ onClose, onSaved }: DialogProps) {
  const { busy, error, run } = useSubmit(onSaved)
  const [sourceType, setSourceType] = useState('ORAL_HISTORY')
  const [title, setTitle] = useState('')
  const [speaker, setSpeaker] = useState('')
  const [description, setDescription] = useState('')

  const submit = (event: FormEvent) => {
    event.preventDefault()
    void run(async () => {
      const source = await api.createSource({
        sourceType,
        title: title.trim(),
        authorOrSpeaker: speaker || null,
      })
      if (description.trim()) {
        await api.addTestimony(source.id, description.trim())
      }
      return 'Source recorded'
    })
  }

  return (
    <Modal
      title="Add a source"
      subtitle="A source is where a claim comes from. A document is the testimony or file itself."
      onClose={onClose}
      footer={<SubmitFooter busy={busy} label="Add source" formId="source-form" onClose={onClose} />}
    >
      <form id="source-form" onSubmit={submit} style={{ display: 'contents' }}>
        {error && <Banner>{error}</Banner>}
        <div className="form-grid">
          <Field label="Title" span>
            <input
              autoFocus
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Interview with Gogo Nene, 2019"
            />
          </Field>
          <Field label="Type">
            <select value={sourceType} onChange={(e) => setSourceType(e.target.value)}>
              <option value="ORAL_HISTORY">Oral history</option>
              <option value="FAMILY_TESTIMONY">Family testimony</option>
              <option value="DOCUMENT">Document</option>
              <option value="PHOTOGRAPH">Photograph</option>
              <option value="AUDIO">Audio</option>
              <option value="VIDEO">Video</option>
              <option value="PUBLICATION">Publication</option>
              <option value="ARCHIVE_RECORD">Archive record</option>
              <option value="GOVERNMENT_RECORD">Government record</option>
              <option value="OTHER">Other</option>
            </select>
          </Field>
          <Field label="Speaker or author">
            <input value={speaker} onChange={(e) => setSpeaker(e.target.value)} />
          </Field>
          <Field label="Testimony" span hint="What this source says. Leave blank to attach a file later.">
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
          </Field>
        </div>
      </form>
    </Modal>
  )
}

/* ------------------------------------------------------------------ */

export function EvidenceDialog({
  data,
  claim,
  onClose,
  onSaved,
}: DialogProps & { data: ArchiveData; claim: Claim }) {
  const { busy, error, run } = useSubmit(onSaved)
  const options = data.sources.flatMap((source) =>
    (data.documents[source.id] ?? []).map((document) => ({
      id: document.id,
      label: `${source.title} · ${document.mediaType === 'TESTIMONY' ? 'testimony' : document.mediaType}`,
    })),
  )
  const [documentId, setDocumentId] = useState(options[0]?.id ?? '')
  const [stance, setStance] = useState('SUPPORTS')
  const [weight, setWeight] = useState('MODERATE')
  const [note, setNote] = useState('')

  const submit = (event: FormEvent) => {
    event.preventDefault()
    void run(async () => {
      await api.attachEvidence(claim.id, {
        documentId,
        stance,
        weight,
        note: note || null,
      })
      return 'Evidence attached'
    })
  }

  return (
    <Modal
      title="Attach evidence"
      subtitle="Evidence attaches to a claim, never to a person."
      onClose={onClose}
      footer={<SubmitFooter busy={busy} label="Attach" formId="evidence-form" onClose={onClose} />}
    >
      <form id="evidence-form" onSubmit={submit} style={{ display: 'contents' }}>
        {error && <Banner>{error}</Banner>}
        {options.length === 0 ? (
          <Banner>
            There are no documents yet. Add a source and a testimony first, then attach it here.
          </Banner>
        ) : (
          <div className="form-grid">
            <Field label="Document" span>
              <select value={documentId} onChange={(e) => setDocumentId(e.target.value)}>
                {options.map((option) => (
                  <option key={option.id} value={option.id}>
                    {option.label}
                  </option>
                ))}
              </select>
            </Field>
            <Field label="Stance">
              <select value={stance} onChange={(e) => setStance(e.target.value)}>
                <option value="SUPPORTS">Supports</option>
                <option value="CONTRADICTS">Contradicts</option>
                <option value="CONTEXT">Context</option>
              </select>
            </Field>
            <Field label="Weight">
              <select value={weight} onChange={(e) => setWeight(e.target.value)}>
                <option value="WEAK">Weak</option>
                <option value="MODERATE">Moderate</option>
                <option value="STRONG">Strong</option>
              </select>
            </Field>
            <Field label="Note" span>
              <input value={note} onChange={(e) => setNote(e.target.value)} />
            </Field>
          </div>
        )}
      </form>
    </Modal>
  )
}
