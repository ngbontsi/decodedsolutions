export type Gender = 'MALE' | 'FEMALE' | 'OTHER' | 'UNKNOWN'

export type Precision =
  | 'DAY'
  | 'MONTH'
  | 'YEAR'
  | 'DECADE'
  | 'APPROXIMATE'
  | 'UNKNOWN'

export type Visibility = 'PUBLIC' | 'FAMILY' | 'RESTRICTED' | 'PRIVATE'

export type ParentageType = 'BIOLOGICAL' | 'ADOPTIVE' | 'LEGAL' | 'OTHER_RECOGNISED'

export type IdentityStatus = 'IDENTIFIED' | 'UNIDENTIFIED' | 'UNRECORDED' | 'UNKNOWN'

export type ParentRole = 'FATHER' | 'MOTHER' | 'PARENT' | 'UNKNOWN'

export type NameType =
  | 'BIRTH'
  | 'MAIDEN'
  | 'MARRIED'
  | 'CLAN'
  | 'NICKNAME'
  | 'ALIAS'
  | 'RELIGIOUS'
  | 'OTHER'

export type ClaimStatus = 'UNVERIFIED' | 'UNDER_REVIEW' | 'SUPPORTED' | 'DISPUTED' | 'RETRACTED'

export interface Archive {
  id: string
  name: string
  description: string | null
  status: string
  createdAt: string
}

export interface Person {
  id: string
  archiveId: string
  gender: Gender
  birthDate: string | null
  birthDatePrecision: Precision
  deathDate: string | null
  deathDatePrecision: Precision
  biography: string | null
  living: boolean
  visibility: Visibility
  status: string
  createdAt: string
  updatedAt: string
}

export interface PersonName {
  id: string
  personId: string
  nameType: NameType | null
  givenNames: string | null
  familyName: string | null
  fullName: string
  script: string
  language: string | null
  preferred: boolean
  validFrom: string | null
  validTo: string | null
  sourceId: string | null
  createdAt: string
}

export interface Parentage {
  id: string
  childId: string
  parentId: string | null
  type: ParentageType
  identityStatus: IdentityStatus
  role: ParentRole
  reportedName: string | null
  validFrom: string | null
  validTo: string | null
  assertedBy: string
  sourceId: string | null
  status: 'ACTIVE' | 'SUPERSEDED' | 'ENTERED_IN_ERROR'
  supersededById: string | null
  createdAt: string
}

export interface Claim {
  id: string
  archiveId: string
  subjectPersonId: string | null
  subjectOrganisationId: string | null
  subjectEventId: string | null
  statement: string
  status: ClaimStatus
  confidence: 'LOW' | 'MEDIUM' | 'HIGH'
  submittedBy: string
  submittedAt: string
}

export interface ClaimEvidence {
  id: { claimId: string; evidenceId: string }
  stance: 'SUPPORTS' | 'CONTRADICTS' | 'CONTEXT'
  weight: 'WEAK' | 'MODERATE' | 'STRONG'
  note: string | null
  addedBy: string
  addedAt: string
}

export interface Source {
  id: string
  archiveId: string
  type: string
  title: string
  authorOrSpeaker: string | null
  institution: string | null
  referenceCode: string | null
  recordedDate: string | null
  locationId: string | null
  accessLevel: Visibility
  notes: string | null
  createdAt: string
}

export interface Document {
  id: string
  sourceId: string
  mediaType: string
  storageKey: string | null
  originalFilename: string | null
  checksumSha256: string | null
  capturedDate: string | null
  description: string | null
  createdAt: string
}

export interface FamilyGroup {
  id: string
  archiveId: string
  name: string
  description: string | null
  lineageType: 'PATRILINEAL' | 'MATRILINEAL' | 'BILATERAL' | 'UNKNOWN'
  status: string
  createdAt: string
}

export interface FamilyMembership {
  id: string
  personId: string
  familyGroupId: string
  connectionType: string
  basis: string
  validFrom: string | null
  validTo: string | null
  assertedBy: string
  sourceId: string | null
  status: string
  createdAt: string
}

export interface FamilyUnion {
  id: string
  archiveId: string
  type: 'MARRIAGE' | 'CIVIL_UNION' | 'CUSTOMARY' | 'COHABITATION' | 'PARTNERSHIP' | 'UNKNOWN'
  startDate: string | null
  endDate: string | null
  status: string
  assertedBy: string
  sourceId: string | null
  createdAt: string
}

export interface UnionParticipant {
  id: { unionId: string; personId: string }
  role: 'SPOUSE' | 'PARTNER'
}

export interface HistoricalEvent {
  id: string
  archiveId: string
  title: string
  description: string | null
  eventDate: string | null
  datePrecision: Precision
  locationId: string | null
  type: string
  assertedBy: string
  createdAt: string
}

export interface Organisation {
  id: string
  archiveId: string
  name: string
  type: string
  foundedDate: string | null
  dissolvedDate: string | null
  description: string | null
  createdAt: string
}

export interface OrganisationMembership {
  id: string
  personId: string
  organisationId: string
  role: string | null
  startDate: string | null
  endDate: string | null
  assertedBy: string
}

export interface Contributor {
  id: string
  archiveId: string
  displayName: string
  email: string
  status: string
  joinedAt: string
}

export interface AuditLog {
  id: string
  archiveId: string
  actorContributorId: string | null
  entityType: string
  entityId: string
  action: string
  beforeState: string | null
  afterState: string | null
  reason: string | null
  occurredAt: string
  requestId: string | null
}

export interface Page<T> {
  content: T[]
  totalElements: number
  number: number
  size: number
}

export interface ArchiveData {
  archive: Archive
  people: Person[]
  names: Record<string, PersonName[]>
  parentages: Parentage[]
  claims: Claim[]
  claimEvidence: Record<string, ClaimEvidence[]>
  sources: Source[]
  documents: Record<string, Document[]>
  families: FamilyGroup[]
  memberships: Record<string, FamilyMembership[]>
  unions: FamilyUnion[]
  unionParticipants: Record<string, UnionParticipant[]>
  events: HistoricalEvent[]
  organisations: Organisation[]
  orgMemberships: Record<string, OrganisationMembership[]>
  contributors: Contributor[]
  audit: AuditLog[]
}
