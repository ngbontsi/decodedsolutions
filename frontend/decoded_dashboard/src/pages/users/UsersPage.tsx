import { useState } from "react";
import { Search, Filter, UserCheck, UserX, Eye, Edit2 } from "lucide-react";
import type { User, UserRole } from "../../types";

const mockUsers: User[] = [
  {
    id: "1",
    email: "admin@decoded.com",
    firstName: "Admin",
    lastName: "User",
    role: "ADMIN",
    enabled: true,
    createdAt: "2024-01-15",
    updatedAt: "2024-01-15",
  },
  {
    id: "2",
    email: "ngbontsi@gmail.com",
    firstName: "Ndimphiwe",
    lastName: "Bontsi",
    role: "USER",
    enabled: true,
    createdAt: "2024-02-10",
    updatedAt: "2024-03-05",
  },
  {
    id: "3",
    email: "nokuthula@gmail.com",
    firstName: "Nokuthula",
    lastName: "Mtshiselwa",
    role: "RESTAURANT_OWNER",
    enabled: true,
    createdAt: "2024-03-01",
    updatedAt: "2024-03-01",
  },
  {
    id: "4",
    email: "zoleka@gmail.com",
    firstName: "Zoleka",
    lastName: "Bontsi",
    role: "GUESTHOUSE_OWNER",
    enabled: false,
    createdAt: "2024-03-12",
    updatedAt: "2024-04-20",
  },
  {
    id: "5",
    email: "rasmeni@example.com",
    firstName: "Rasmeni",
    lastName: "Alex",
    role: "USER",
    enabled: true,
    createdAt: "2024-04-01",
    updatedAt: "2024-04-01",
  },
];

const roleBadgeClass: Record<UserRole, string> = {
  ADMIN: "badge-admin",
  USER: "badge-user",
  RESTAURANT_OWNER: "badge-restaurant",
  GUESTHOUSE_OWNER: "badge-guesthouse",
};

function RoleBadge({ role }: { role: UserRole }) {
  return (
    <span className={`badge ${roleBadgeClass[role]}`}>
      {role.replace("_", " ")}
    </span>
  );
}

export default function UsersPage() {
  const [search, setSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState<UserRole | "ALL">("ALL");

  const filtered = mockUsers.filter((u) => {
    const matchSearch =
      u.email.toLowerCase().includes(search.toLowerCase()) ||
      `${u.firstName} ${u.lastName}`
        .toLowerCase()
        .includes(search.toLowerCase());
    const matchRole = roleFilter === "ALL" || u.role === roleFilter;
    return matchSearch && matchRole;
  });

  return (
    <div className="page">
      <div className="page-header">
        <h1>User Management</h1>
        <p>Manage platform users and permissions</p>
      </div>

      <div className="card">
        <div className="toolbar">
          <div className="search-box">
            <Search size={18} />
            <input
              type="text"
              placeholder="Search users..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <div className="filter-group">
            <Filter size={18} />
            <select
              value={roleFilter}
              onChange={(e) =>
                setRoleFilter(e.target.value as UserRole | "ALL")
              }
            >
              <option value="ALL">All Roles</option>
              <option value="ADMIN">Admin</option>
              <option value="USER">User</option>
              <option value="RESTAURANT_OWNER">Restaurant Owner</option>
              <option value="GUESTHOUSE_OWNER">Guesthouse Owner</option>
            </select>
          </div>
        </div>

        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((user) => (
                <tr key={user.id}>
                  <td>
                    {user.firstName} {user.lastName}
                  </td>
                  <td>{user.email}</td>
                  <td>
                    <RoleBadge role={user.role} />
                  </td>
                  <td>
                    <span
                      className={`status-indicator ${user.enabled ? "enabled" : "disabled"}`}
                    >
                      {user.enabled ? (
                        <UserCheck size={14} />
                      ) : (
                        <UserX size={14} />
                      )}
                      {user.enabled ? "Active" : "Disabled"}
                    </span>
                  </td>
                  <td>{user.createdAt}</td>
                  <td className="actions">
                    <button className="btn-icon" title="View">
                      <Eye size={16} />
                    </button>
                    <button className="btn-icon" title="Edit">
                      <Edit2 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
