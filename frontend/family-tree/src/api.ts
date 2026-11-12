import type {
  Archive,
  ArchiveData,
  AuditLog,
  Claim,
  ClaimEvidence,
  Contributor,
  Document,
  FamilyGroup,
  FamilyMembership,
  FamilyUnion,
  HistoricalEvent,
  Organisation,
  OrganisationMembership,
  Page,
  Parentage,
  Person,
  PersonName,
  Source,
  UnionParticipant,
} from './types'

const BASE = (import.meta.env.VITE_API_BASE as string | undefined) ?? '/api/v1'

const PASSCODE_KEY = 'familytree.passcode'

export function getPasscode(): string {
  return window.localStorage.getItem(PASSCODE_KEY) ?? ''
}

export function rememberPasscode(passcode: string): void {
  window.localStorage.setItem(PASSCODE_KEY, passcode)
}

export class ApiError extends Error {
  readonly status: number
  readonly code: string | null

  constructor(status: number, message: string, code: string | null = null) {
    super(message)
    this.status = status
    this.code = code
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const passcode = getPasscode()
  let response: Response
  try {
    response = await fetch(`${BASE}${path}`, {
      ...init,
      headers: {
        'Content-Type': 'application/json',
        ...(passcode ? { 'X-Beta-Passcode': passcode } : {}),
        ...(init?.headers ?? {}),
      },
    })
  } catch {
    throw new ApiError(
      0,
      'Cannot reach the archive service. Is it running on port 8085?',
    )
  }

  if (!response.ok) {
    let message = `${response.status} ${response.statusText}`
    let code: string | null = null
    try {
      const body = (await response.json()) as { message?: string; code?: string }
      if (body.message) message = body.message
      if (body.code) code = body.code
    } catch {
      // non-JSON error body; keep the status text
    }
    throw new ApiError(response.status, message, code)
  }

  if (response.status === 204) return undefined as T
  return (await response.json()) as T
}

const get = <T>(path: string) => request<T>(path)
const post = <T>(path: string, body: unknown) =>
  request<T>(path, { method: 'POST', body: JSON.stringify(body) })
const patch = <T>(path: string, body: unknown) =>
  request<T>(path, { method: 'PATCH', body: JSON.stringify(body) })

export const api = {
  archive: () => get<Archive>('/archive'),

  people: () => get<Person[]>('/persons'),
  person: (id: string) => get<Person>(`/persons/${id}`),
  createPerson: (body: {
    gender: string
    birthDate?: string | null
    birthDatePrecision?: string
    biography?: string | null
    visibility?: string
  }) => post<Person>('/persons', body),
  names: (personId: string) => get<PersonName[]>(`/persons/${personId}/names`),
  addName: (
    personId: string,
    body: {
      nameType: string
      fullName: string
      givenNames?: string | null
      familyName?: string | null
      validFrom?: string | null
      preferred?: boolean
    },
  ) => post<PersonName>(`/persons/${personId}/names`, body),
  recordDeath: (personId: string, body: { deathDate: string; precision?: string }) =>
    post<Person>(`/persons/${personId}/death`, body),

  parentages: () => get<Parentage[]>('/parentages'),
  recordParentage: (body: {
    childId: string
    parentId?: string | null
    parentageType?: string
    parentRole?: string
    identityStatus: string
    reportedName?: string | null
    validFrom?: string | null
  }) => post<Parentage>('/parentages', body),

  claimsForPerson: (personId: string) => get<Claim[]>(`/claims/person/${personId}`),
  createClaim: (body: {
    subjectPersonId: string
    statement: string
  }) => post<Claim>('/claims', body),
  claimEvidence: (claimId: string) => get<ClaimEvidence[]>(`/claims/${claimId}/evidence`),
  attachEvidence: (
    claimId: string,
    body: {
      documentId: string
      stance: string
      weight: string
      note?: string | null
    },
  ) => post<ClaimEvidence>(`/claims/${claimId}/evidence`, body),
  transitionClaim: (claimId: string, status: string) =>
    patch<Claim>(`/claims/${claimId}/status`, { status }),

  sources: () => get<Source[]>('/evidence/sources'),
  createSource: (body: {
    sourceType: string
    title: string
    authorOrSpeaker?: string | null
  }) => post<Source>('/evidence/sources', body),
  documents: (sourceId: string) => get<Document[]>(`/evidence/sources/${sourceId}/documents`),
  addTestimony: (sourceId: string, description: string) =>
    post<Document>(`/evidence/sources/${sourceId}/documents`, { description }),

  families: () => get<FamilyGroup[]>('/families'),
  members: (familyId: string) => get<FamilyMembership[]>(`/families/${familyId}/members`),
  createFamily: (body: { name: string; description?: string; lineageType?: string }) =>
    post<FamilyGroup>('/families', body),
  addMember: (
    familyId: string,
    body: {
      personId: string
      connectionType: string
      basis: string
      validFrom?: string | null
    },
  ) => post<FamilyMembership>(`/families/${familyId}/members`, body),

  unions: () => get<FamilyUnion[]>('/unions'),
  unionParticipants: (unionId: string) =>
    get<UnionParticipant[]>(`/unions/${unionId}/participants`),
  createUnion: (body: {
    unionType: string
    startDate?: string | null
    participants: { personId: string; role: string }[]
  }) => post<FamilyUnion>('/unions', body),

  events: () => get<HistoricalEvent[]>('/heritage/events'),
  createEvent: (body: {
    title: string
    description?: string | null
    eventDate?: string | null
    datePrecision?: string
    eventType: string
  }) => post<HistoricalEvent>('/heritage/events', body),
  organisations: () => get<Organisation[]>('/heritage/organisations'),
  createOrganisation: (body: {
    name: string
    organisationType: string
    foundedDate?: string | null
    description?: string | null
  }) => post<Organisation>('/heritage/organisations', body),
  orgMemberships: (personId: string) =>
    get<OrganisationMembership[]>(`/heritage/persons/${personId}/memberships`),

  contributors: () => get<Contributor[]>('/contributors'),
  audit: (size = 8) => get<Page<AuditLog>>(`/audit?size=${size}&sort=occurredAt,desc`),
}

export async function loadArchive(): Promise<ArchiveData> {
  const [archive, people, parentages, sources, families, unions, events, organisations, contributors, auditPage] =
    await Promise.all([
      api.archive(),
      api.people(),
      api.parentages(),
      api.sources(),
      api.families(),
      api.unions(),
      api.events(),
      api.organisations(),
      api.contributors(),
      api.audit(10),
    ])

  const [names, documents, memberships, unionParticipants, perPersonClaims] = await Promise.all([
    Promise.all(people.map((p) => api.names(p.id).catch(() => [] as PersonName[]))),
    Promise.all(sources.map((s) => api.documents(s.id).catch(() => [] as Document[]))),
    Promise.all(families.map((f) => api.members(f.id).catch(() => [] as FamilyMembership[]))),
    Promise.all(
      unions.map((u) => api.unionParticipants(u.id).catch(() => [] as UnionParticipant[])),
    ),
    Promise.all(people.map((p) => api.claimsForPerson(p.id).catch(() => [] as Claim[]))),
  ])

  const allClaims = perPersonClaims.flat().sort((a, b) => b.submittedAt.localeCompare(a.submittedAt))
  const claimEvidence = await Promise.all(
    allClaims.map((c) => api.claimEvidence(c.id).catch(() => [] as ClaimEvidence[])),
  )

  const byPerson = (list: { personId: string }[]) => {
    const map: Record<string, typeof list> = {}
    for (const item of list) {
      ;(map[item.personId] ??= []).push(item)
    }
    return map
  }

  const orgMemberships: Record<string, OrganisationMembership[]> = {}
  await Promise.all(
    people.map(async (p) => {
      orgMemberships[p.id] = await api.orgMemberships(p.id).catch(() => [])
    }),
  )

  return {
    archive,
    people,
    names: byPerson(names.flat()) as Record<string, PersonName[]>,
    parentages,
    claims: allClaims,
    claimEvidence: Object.fromEntries(allClaims.map((c, i) => [c.id, claimEvidence[i]])),
    sources,
    documents: Object.fromEntries(sources.map((s, i) => [s.id, documents[i]])),
    families,
    memberships: Object.fromEntries(families.map((f, i) => [f.id, memberships[i]])),
    unions,
    unionParticipants: Object.fromEntries(unions.map((u, i) => [u.id, unionParticipants[i]])),
    events,
    organisations,
    orgMemberships,
    contributors,
    audit: auditPage.content,
  }
}
