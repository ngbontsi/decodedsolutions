import type { Person, PersonName, Precision } from '../types'

export function preferredName(names: PersonName[] | undefined): string | null {
  if (!names || names.length === 0) return null
  const preferred = names.find((n) => n.preferred)
  return (preferred ?? names[0]).fullName
}

export function displayName(names: PersonName[] | undefined): string {
  return preferredName(names) ?? 'Unnamed person'
}

export function initials(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean)
  if (parts.length === 0) return '?'
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

export function yearOf(date: string | null): string | null {
  return date ? date.slice(0, 4) : null
}

export function lifeSpan(person: Person): string {
  const birth = yearOf(person.birthDate)
  const death = yearOf(person.deathDate)
  if (birth && death) return `${birth} – ${death}`
  if (birth) return `b. ${birth}`
  if (death) return `d. ${death}`
  return 'Dates unknown'
}

export function formatDate(date: string | null, precision?: Precision): string {
  if (!date) return ''
  const [year, month, day] = date.split('-')
  if (precision === 'YEAR' || (!month && year)) return year ?? date
  if (precision === 'MONTH') return `${month}/${year}`
  if (precision === 'DECADE') return `${year.slice(0, 3)}0s`
  return `${day}/${month}/${year}`
}

export function fullDate(iso: string | null): string {
  if (!iso) return '—'
  const date = new Date(iso)
  return date.toLocaleDateString('en-ZA', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

export function dateTime(iso: string | null): string {
  if (!iso) return '—'
  const date = new Date(iso)
  return date.toLocaleString('en-ZA', {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function timeAgo(iso: string): string {
  const then = new Date(iso).getTime()
  const seconds = Math.round((Date.now() - then) / 1000)
  if (seconds < 60) return 'just now'
  const minutes = Math.round(seconds / 60)
  if (minutes < 60) return `${minutes} min ago`
  const hours = Math.round(minutes / 60)
  if (hours < 24) return `${hours} hr ago`
  const days = Math.round(hours / 24)
  if (days < 30) return `${days} day${days === 1 ? '' : 's'} ago`
  return fullDate(iso)
}

export function titleCase(value: string): string {
  return value
    .toLowerCase()
    .split(/[_\s]+/)
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ')
}

export function toDateInput(value: string | null): string {
  return value ? value.slice(0, 10) : ''
}
