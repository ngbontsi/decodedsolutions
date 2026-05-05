import { Users, Activity, Store, BarChart3 } from "lucide-react";
import StatCard from "../components/shared/StatCard";

export default function DashboardPage() {
  return (
    <div className="page">
      <div className="page-header">
        <h1>Dashboard</h1>
        <p>Decoded Platform at a glance</p>
      </div>

      <div className="stats-grid">
        <StatCard
          title="Total Users"
          value="1,247"
          icon={Users}
          change={{ value: 12, positive: true }}
        />
        <StatCard
          title="Active Users"
          value="892"
          icon={Users}
          change={{ value: 5, positive: true }}
        />
        <StatCard
          title="Restaurants"
          value="54"
          icon={Store}
          change={{ value: 3, positive: true }}
        />
        <StatCard
          title="Guesthouses"
          value="28"
          icon={Store}
          change={{ value: -1, positive: false }}
        />
        <StatCard
          title="Service Health"
          value="99.8%"
          icon={Activity}
          change={{ value: 0.2, positive: true }}
        />
        <StatCard
          title="Pending Tasks"
          value="47"
          icon={BarChart3}
          change={{ value: 8, positive: false }}
        />
      </div>

      <div className="dashboard-grid">
        <div className="card">
          <h2>Recent Activity</h2>
          <ul className="activity-list">
            <li className="activity-item">
              <span className="activity-dot green" />
              <span className="activity-text">
                New user registered: ngbontsi@gmail.com
              </span>
              <span className="activity-time">2m ago</span>
            </li>
            <li className="activity-item">
              <span className="activity-dot blue" />
              <span className="activity-text">
                Restaurant "Zozo's Restaurant" updated menu
              </span>
              <span className="activity-time">15m ago</span>
            </li>
            <li className="activity-item">
              <span className="activity-dot yellow" />
              <span className="activity-text">
                Guesthouse "Rasmeni an son's" booking confirmed
              </span>
              <span className="activity-time">32m ago</span>
            </li>
            <li className="activity-item">
              <span className="activity-dot red" />
              <span className="activity-text">
                Marketplace order #3421 cancelled
              </span>
              <span className="activity-time">1h ago</span>
            </li>
            <li className="activity-item">
              <span className="activity-dot green" />
              <span className="activity-text">Service health check passed</span>
              <span className="activity-time">1h ago</span>
            </li>
          </ul>
        </div>

        <div className="card">
          <h2>Service Status</h2>
          <div className="service-status-list">
            {[
              "API Gateway",
              "Auth Service",
              "Restaurant Service",
              "Guesthouse Service",
              "Marketplace Service",
              "Task API",
            ].map((s) => (
              <div key={s} className="service-status-item">
                <span className="status-dot green" />
                <span className="service-name">{s}</span>
                <span className="status-badge healthy">Healthy</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
