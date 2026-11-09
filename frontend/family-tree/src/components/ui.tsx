import type { ReactNode } from 'react'
import { X, AlertTriangle } from 'lucide-react'
import { initials } from '../lib/format'

export function Avatar({
  name,
  gender,
  size,
}: {
  name: string
  gender?: string
  size?: 'sm' | 'lg'
}) {
  return (
    <div
      className={`avatar ${size ?? ''}`}
      data-gender={gender ?? 'UNKNOWN'}
      aria-hidden="true"
      title={name}
    >
      {initials(name)}
    </div>
  )
}

export function Badge({
  children,
  tone,
}: {
  children: ReactNode
  tone?: 'gold' | 'blue' | 'green' | 'red' | 'amber' | 'ghost'
}) {
  return <span className={`badge ${tone ?? ''}`}>{children}</span>
}

export function StatusBadge({ status }: { status: string }) {
  const tone =
    status === 'SUPPORTED'
      ? 'green'
      : status === 'DISPUTED'
        ? 'red'
        : status === 'UNDER_REVIEW'
          ? 'amber'
          : status === 'RETRACTED'
            ? 'ghost'
            : 'blue'
  return <Badge tone={tone as 'green'}>{status.replace(/_/g, ' ')}</Badge>
}

export function IdentityBadge({ status }: { status: string }) {
  const tone =
    status === 'IDENTIFIED'
      ? 'green'
      : status === 'UNRECORDED'
        ? 'amber'
        : status === 'UNIDENTIFIED'
          ? 'blue'
          : 'ghost'
  const label =
    status === 'IDENTIFIED'
      ? 'Identified'
      : status === 'UNRECORDED'
        ? 'Not written down'
        : status === 'UNIDENTIFIED'
          ? 'Not researched'
          : 'Unknown'
  return <Badge tone={tone as 'green'}>{label}</Badge>
}

export function Panel({
  title,
  icon,
  count,
  action,
  children,
}: {
  title: string
  icon?: ReactNode
  count?: number
  action?: ReactNode
  children: ReactNode
}) {
  return (
    <section className="panel">
      <div className="panel-head">
        <h3 className="panel-title">
          {icon}
          {title}
          {count !== undefined && <span className="panel-count">{count}</span>}
        </h3>
        {action}
      </div>
      <div className="panel-body">{children}</div>
    </section>
  )
}

export function Modal({
  title,
  subtitle,
  onClose,
  children,
  footer,
  wide,
}: {
  title: string
  subtitle?: string
  onClose: () => void
  children: ReactNode
  footer?: ReactNode
  wide?: boolean
}) {
  return (
    <div className="overlay" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className={`modal ${wide ? 'wide' : ''}`} role="dialog" aria-modal="true">
        <div className="modal-head">
          <div>
            <h2 className="modal-title">{title}</h2>
            {subtitle && <p className="modal-sub">{subtitle}</p>}
          </div>
          <button className="icon-btn" onClick={onClose} aria-label="Close">
            <X />
          </button>
        </div>
        <div className="modal-body">{children}</div>
        {footer && <div className="modal-foot">{footer}</div>}
      </div>
    </div>
  )
}

export function Banner({
  children,
  tone,
}: {
  children: ReactNode
  tone?: 'error' | 'success'
}) {
  return (
    <div className={`banner ${tone === 'success' ? 'success' : ''}`}>
      <AlertTriangle />
      <span>{children}</span>
    </div>
  )
}

export function Empty({ children }: { children: string }) {
  return <div className="empty">{children}</div>
}

export function Field({
  label,
  hint,
  children,
  span,
}: {
  label: string
  hint?: string
  children: ReactNode
  span?: boolean
}) {
  return (
    <label className={`field ${span ? 'span-2' : ''}`}>
      <span className="field-label">{label}</span>
      {children}
      {hint && <span className="hint">{hint}</span>}
    </label>
  )
}
