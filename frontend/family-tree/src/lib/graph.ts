import type { Parentage, Person, PersonName } from '../types'
import { displayName, lifeSpan } from './format'

export const NODE_W = 216
export const NODE_H = 78
export const GAP_X = 72
export const GAP_Y = 18

export interface TreePersonNode {
  kind: 'person'
  key: string
  personId: string
  title: string
  subtitle: string
  gender: Person['gender']
  depth: number
  x: number
  y: number
  children: TreeNode[]
  row: number
}

export interface TreeGhostNode {
  kind: 'ghost'
  key: string
  title: string
  subtitle: string
  depth: number
  x: number
  y: number
  children: TreeNode[]
  row: number
}

export type TreeNode = TreePersonNode | TreeGhostNode

export interface TreeEdge {
  key: string
  from: TreeNode
  to: TreeNode
  status: Parentage['identityStatus']
  active: boolean
}

export interface TreeResult {
  nodes: TreeNode[]
  edges: TreeEdge[]
  width: number
  height: number
}

function activeParentages(parentages: Parentage[]): Parentage[] {
  return parentages.filter((p) => p.status === 'ACTIVE')
}

export function parentagesByChild(parentages: Parentage[]): Record<string, Parentage[]> {
  const map: Record<string, Parentage[]> = {}
  for (const parentage of activeParentages(parentages)) {
    ;(map[parentage.childId] ??= []).push(parentage)
  }
  return map
}

export function childrenByParent(parentages: Parentage[]): Record<string, string[]> {
  const map: Record<string, string[]> = {}
  for (const parentage of activeParentages(parentages)) {
    if (parentage.parentId) {
      ;(map[parentage.parentId] ??= []).push(parentage.childId)
    }
  }
  return map
}

function ghostTitle(parentage: Parentage): string {
  if (parentage.identityStatus === 'UNRECORDED') {
    return parentage.reportedName ?? 'Name not written down'
  }
  if (parentage.identityStatus === 'UNIDENTIFIED') return 'Identity not found'
  return 'Parent not recorded'
}

function ghostSubtitle(parentage: Parentage): string {
  const role = parentage.role === 'UNKNOWN' ? 'Parent' : parentage.role.toLowerCase()
  if (parentage.identityStatus === 'UNRECORDED') return `${role} · known to the family`
  if (parentage.identityStatus === 'UNIDENTIFIED') return `${role} · not researched`
  return `${role} · not recorded`
}

export function buildAncestorTree(
  rootId: string,
  people: Person[],
  names: Record<string, PersonName[]>,
  parentages: Parentage[],
  generations: number,
): TreeResult {
  const byId = new Map(people.map((p) => [p.id, p]))
  const edgesByChild = parentagesByChild(parentages)
  const nodes: TreeNode[] = []
  const edges: TreeEdge[] = []
  let nextRow = 0

  const makePersonNode = (person: Person, depth: number): TreePersonNode => {
    const node: TreePersonNode = {
      kind: 'person',
      key: `${person.id}-${depth}`,
      personId: person.id,
      title: displayName(names[person.id]),
      subtitle: lifeSpan(person),
      gender: person.gender,
      depth,
      x: depth * (NODE_W + GAP_X),
      y: 0,
      children: [],
      row: 0,
    }
    return node
  }

  const expand = (node: TreeNode, path: Set<string>, depth: number) => {
    if (depth >= generations) return
    const childId = node.kind === 'person' ? node.personId : null
    if (!childId) return
    if (path.has(childId)) return

    const nextPath = new Set(path).add(childId)
    const parents = edgesByChild[childId] ?? []
    for (const parentage of parents) {
      const parent = parentage.parentId ? byId.get(parentage.parentId) : undefined
      if (parent && !nextPath.has(parent.id)) {
        const parentNode = makePersonNode(parent, depth + 1)
        node.children.push(parentNode)
        nodes.push(parentNode)
        edges.push({
          key: `${parentage.id}-edge`,
          from: parentNode,
          to: node,
          status: parentage.identityStatus,
          active: true,
        })
        expand(parentNode, nextPath, depth + 1)
      } else {
        const ghost: TreeGhostNode = {
          kind: 'ghost',
          key: `${parentage.id}-ghost`,
          title: ghostTitle(parentage),
          subtitle: ghostSubtitle(parentage),
          depth: depth + 1,
          x: (depth + 1) * (NODE_W + GAP_X),
          y: 0,
          children: [],
          row: 0,
        }
        node.children.push(ghost)
        nodes.push(ghost)
        edges.push({
          key: `${parentage.id}-edge`,
          from: ghost,
          to: node,
          status: parentage.identityStatus,
          active: true,
        })
      }
    }
  }

  const rootPerson = byId.get(rootId)
  if (!rootPerson) {
    return { nodes: [], edges: [], width: 0, height: 0 }
  }

  const root = makePersonNode(rootPerson, 0)
  nodes.push(root)
  expand(root, new Set(), 0)

  const assignRows = (node: TreeNode): number => {
    if (node.children.length === 0) {
      node.row = nextRow
      nextRow += 1
    } else {
      const rows = node.children.map(assignRows)
      node.row = (rows[0] + rows[rows.length - 1]) / 2
    }
    node.y = node.row * (NODE_H + GAP_Y)
    return node.row
  }
  assignRows(root)

  const maxDepth = nodes.reduce((max, node) => Math.max(max, node.depth), 0)
  return {
    nodes,
    edges,
    width: maxDepth * (NODE_W + GAP_X) + NODE_W + 48,
    height: Math.max(nextRow, 1) * (NODE_H + GAP_Y) + 48,
  }
}

export function edgePath(from: TreeNode, to: TreeNode): string {
  const startX = from.x + NODE_W
  const startY = from.y + NODE_H / 2
  const endX = to.x
  const endY = to.y + NODE_H / 2
  const midX = startX + (endX - startX) / 2
  return `M ${startX} ${startY} H ${midX} V ${endY} H ${endX}`
}
