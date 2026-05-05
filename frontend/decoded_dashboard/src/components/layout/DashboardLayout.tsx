import { NavLink, Outlet, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  Users,
  Activity,
  Store,
  BarChart3,
  LogOut,
} from "lucide-react";

const navItems = [
  { path: "/", label: "Dashboard", icon: LayoutDashboard },
  { path: "/users", label: "Users", icon: Users },
  { path: "/monitoring", label: "Monitoring", icon: Activity },
  { path: "/business", label: "Business", icon: Store },
  { path: "/analytics", label: "Analytics", icon: BarChart3 },
];

export default function DashboardLayout() {
  const navigate = useNavigate();

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <span className="logo-mark">DS</span>
          <span className="logo-text">Decoded Solutions</span>
        </div>

        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `nav-item${isActive ? " active" : ""}`
              }
            >
              <item.icon size={20} />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <button
            className="nav-item logout"
            onClick={() => navigate("/login")}
          >
            <LogOut size={20} />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
