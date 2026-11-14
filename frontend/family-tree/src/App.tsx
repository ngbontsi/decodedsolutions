import { useCallback, useEffect, useState, type FormEvent } from 'react'
import {
  GitBranch,
  Home,
  Landmark,
  RefreshCw,
  ScrollText,
  Users,
} from 'lucide-react'
import { ApiError, getPasscode, loadArchive, rememberPasscode } from './api'
import type { ArchiveData, Claim } from './types'
import { Banner } from './components/ui'
import {
  ClaimDialog,
  EvidenceDialog,
  NameDialog,
  ParentageDialog,
  PersonDialog,
  SourceDialog,
} from './components/dialogs'
import Overview from './views/Overview'
import People from './views/People'
import PersonDetail from './views/PersonDetail'
import TreePage from './views/TreePage'
import Evidence from './views/Evidence'
import Heritage from './views/Heritage'

type View = 'overview' | 'people' | 'person' | 'tree' | 'evidence' | 'heritage'

type DialogState =
  | { kind: 'person' }
  | { kind: 'name'; personId: string }
  | { kind: 'parentage'; childId?: string }
  | { kind: 'claim'; personId?: string }
  | { kind: 'source' }
  | { kind: 'evidence'; claim: Claim }
  | null

export default function App() {
  const [data, setData] = useState<ArchiveData | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [refreshing, setRefreshing] = useState(true)
  const [view, setView] = useState<View>('overview')
  const [personId, setPersonId] = useState<string | null>(null)
  const [dialog, setDialog] = useState<DialogState>(null)
  const [toast, setToast] = useState<string | null>(null)
  const [locked, setLocked] = useState(false)
  const [gateError, setGateError] = useState<string | null>(null)
  const [passcodeDraft, setPasscodeDraft] = useState('')

  const load = useCallback(async () => {
    setRefreshing(true)
    try {
      const next = await loadArchive()
      setData(next)
      setError(null)
      setLocked(false)
    } catch (err) {
      if (err instanceof ApiError && (err.status === 401 || err.code === 'BETA_REQUIRED')) {
        setLocked(true)
        setGateError(getPasscode() ? 'That passcode was not accepted. Try again.' : null)
        setError(null)
      } else {
        setError(err instanceof ApiError ? err.message : 'Could not load the archive.')
      }
    } finally {
      setRefreshing(false)
    }
  }, [])

  useEffect(() => {
    void load()
  }, [load])

  useEffect(() => {
    if (!toast) return
    const timer = window.setTimeout(() => setToast(null), 4500)
    return () => window.clearTimeout(timer)
  }, [toast])

  const openPerson = (id: string) => {
    setPersonId(id)
    setView('person')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const go = (next: View) => {
    setView(next)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const onSaved = (message: string) => {
    setDialog(null)
    setToast(message)
    void load()
  }

  const defaultPersonId = personId ?? data?.people[0]?.id ?? ''

  const submitPasscode = async (event: FormEvent) => {
    event.preventDefault()
    const candidate = passcodeDraft.trim()
    if (!candidate) return
    rememberPasscode(candidate)
    setGateError(null)
    await load()
  }

  if (locked) {
    return (
      <div className="gate">
        <form className="gate-card" onSubmit={submitPasscode}>
          <div className="brand-mark">D</div>
          <h1>Family archive</h1>
          <p>This archive is private. Enter the passcode the family shared with you.</p>
          {gateError && <Banner>{gateError}</Banner>}
          <input
            type="password"
            value={passcodeDraft}
            onChange={(e) => setPasscodeDraft(e.target.value)}
            placeholder="Family passcode"
            autoFocus
            aria-label="Family passcode"
          />
          <button
            className="btn btn-primary"
            type="submit"
            disabled={refreshing || !passcodeDraft.trim()}
          >
            {refreshing ? 'Checking...' : 'Open archive'}
          </button>
        </form>
      </div>
    )
  }

  if (!data) {
    return (
      <div className="loading">
        {error ? (
          <>
            <Banner>{error}</Banner>
            <button className="btn btn-primary" onClick={() => void load()} disabled={refreshing}>
              <RefreshCw /> Try again
            </button>
          </>
        ) : (
          <>
            <div className="spinner" />
            <span>Opening the archive…</span>
          </>
        )}
      </div>
    )
  }

  const navItems: {
    id: View
    label: string
    icon: typeof Home
    count?: number
  }[] = [
    { id: 'overview', label: 'Overview', icon: Home },
    { id: 'people', label: 'People', icon: Users, count: data.people.length },
    { id: 'tree', label: 'Tree', icon: GitBranch },
    { id: 'evidence', label: 'Evidence', icon: ScrollText, count: data.claims.length },
    { id: 'heritage', label: 'Heritage', icon: Landmark, count: data.events.length },
  ]

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">D</div>
          <div className="brand-text">
            <div className="brand-name">{data.archive.name}</div>
            <div className="brand-sub">Family archive</div>
          </div>
        </div>

        <nav className="nav">
          <span className="nav-label">Archive</span>
          {navItems.map((item) => {
            const Icon = item.icon
            const active =
              item.id === view || (item.id === 'people' && view === 'person')
            return (
              <button
                key={item.id}
                className={`nav-item ${active ? 'active' : ''}`}
                onClick={() => go(item.id)}
              >
                <Icon />
                {item.label}
                {item.count !== undefined && item.count > 0 && (
                  <span className="nav-count">{item.count}</span>
                )}
              </button>
            )
          })}
        </nav>

        <div className="sidebar-footer">
          <span>
            {data.people.length} people · {data.parentages.length} parent links
          </span>
          <span>
            {data.contributors.length} contributor
            {data.contributors.length === 1 ? '' : 's'} · Phase 1
          </span>
        </div>
      </aside>

      <main className="main">
        <div className="topbar">
          <span className="topbar-state">
            <span className={`status-dot ${refreshing ? 'busy' : ''}`} />
            {refreshing ? 'Syncing with the archive…' : 'Live · connected to the family-tree service'}
          </span>
          <button className="btn btn-sm" onClick={() => void load()} disabled={refreshing}>
            <RefreshCw className={refreshing ? 'spin' : undefined} /> Refresh
          </button>
        </div>

        {error && <Banner>{error}</Banner>}

        {view === 'overview' && (
          <Overview
            data={data}
            onOpenPerson={openPerson}
            onAddPerson={() => setDialog({ kind: 'person' })}
          />
        )}

        {view === 'people' && (
          <People
            data={data}
            onOpenPerson={openPerson}
            onAddPerson={() => setDialog({ kind: 'person' })}
          />
        )}

        {view === 'person' && personId && (
          <PersonDetail
            data={data}
            personId={personId}
            onBack={() => go('people')}
            onOpenPerson={openPerson}
            onAddName={() => setDialog({ kind: 'name', personId })}
            onAddParentage={() => setDialog({ kind: 'parentage', childId: personId })}
            onAddClaim={() => setDialog({ kind: 'claim', personId: personId ?? undefined })}
          />
        )}

        {view === 'person' && !personId && (
          <div className="card card-pad empty">Choose a person from the list first.</div>
        )}

        {view === 'tree' && <TreePage data={data} onOpenPerson={openPerson} />}

        {view === 'evidence' && (
          <Evidence
            data={data}
            onAddSource={() => setDialog({ kind: 'source' })}
            onAddClaim={() => setDialog({ kind: 'claim', personId: personId ?? undefined })}
            onAttachEvidence={(claim) => setDialog({ kind: 'evidence', claim })}
            onChanged={onSaved}
            onOpenPerson={openPerson}
          />
        )}

        {view === 'heritage' && <Heritage data={data} onChanged={onSaved} />}

        {dialog?.kind === 'person' && (
          <PersonDialog onClose={() => setDialog(null)} onSaved={onSaved} />
        )}

        {dialog?.kind === 'name' && (
          <NameDialog
            personId={dialog.personId}
            onClose={() => setDialog(null)}
            onSaved={onSaved}
          />
        )}

        {dialog?.kind === 'parentage' && (
          <ParentageDialog
            data={data}
            defaultChildId={dialog.childId}
            onClose={() => setDialog(null)}
            onSaved={onSaved}
          />
        )}

        {dialog?.kind === 'claim' && (
          <ClaimDialog
            data={data}
            defaultPersonId={dialog.personId ?? defaultPersonId}
            onClose={() => setDialog(null)}
            onSaved={onSaved}
          />
        )}

        {dialog?.kind === 'source' && (
          <SourceDialog onClose={() => setDialog(null)} onSaved={onSaved} />
        )}

        {dialog?.kind === 'evidence' && (
          <EvidenceDialog
            data={data}
            claim={dialog.claim}
            onClose={() => setDialog(null)}
            onSaved={onSaved}
          />
        )}

        {toast && (
          <div className="toast" role="status">
            {toast}
          </div>
        )}
      </main>
    </div>
  )
}
