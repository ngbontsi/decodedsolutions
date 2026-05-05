import { useState } from 'react';
import type { AnalyticsDataPoint } from '../../types';

const mockData: AnalyticsDataPoint[] = [
  { date: '2024-04-01', users: 150, orders: 85, revenue: 4200 },
  { date: '2024-04-05', users: 180, orders: 95, revenue: 5100 },
  { date: '2024-04-10', users: 210, orders: 110, revenue: 5800 },
  { date: '2024-04-15', users: 195, orders: 102, revenue: 5400 },
  { date: '2024-04-20', users: 230, orders: 120, revenue: 6200 },
  { date: '2024-04-25', users: 250, orders: 135, revenue: 7100 },
  { date: '2024-05-01', users: 275, orders: 148, revenue: 7800 },
];

type Metric = 'users' | 'orders' | 'revenue';

export default function AnalyticsPage() {
  const [metric, setMetric] = useState<Metric>('users');

  const labels: Record<Metric, string> = { users: 'New Users', orders: 'Orders', revenue: 'Revenue ($)' };
  const maxVal = Math.max(...mockData.map((d) => d[metric]));

  return (
    <div className="page">
      <div className="page-header">
        <h1>Analytics</h1>
        <p>Platform usage and performance trends</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-header"><span className="stat-title">Total Revenue</span></div>
          <div className="stat-value">$41,600</div>
          <div className="stat-change positive">+18% this month</div>
        </div>
        <div className="stat-card">
          <div className="stat-header"><span className="stat-title">Avg Order Value</span></div>
          <div className="stat-value">$52.40</div>
          <div className="stat-change positive">+3% this month</div>
        </div>
        <div className="stat-card">
          <div className="stat-header"><span className="stat-title">User Growth</span></div>
          <div className="stat-value">83%</div>
          <div className="stat-change positive">+12% this month</div>
        </div>
      </div>

      <div className="card chart-card">
        <div className="chart-header">
          <h2>{labels[metric]} Trend</h2>
          <div className="chart-controls">
            {(['users', 'orders', 'revenue'] as const).map((m) => (
              <button
                key={m}
                className={`chart-btn ${metric === m ? 'active' : ''}`}
                onClick={() => setMetric(m)}
              >
                {labels[m]}
              </button>
            ))}
          </div>
        </div>

        <div className="chart-container">
          <div className="chart-y-axis">
            {Array.from({ length: 5 }, (_, i) => (
              <span key={i} className="y-label">{Math.round((maxVal / 4) * (4 - i)).toLocaleString()}</span>
            ))}
          </div>
          <div className="chart-bars">
            {mockData.map((d) => {
              const val = d[metric];
              const height = (val / maxVal) * 100;
              return (
                <div key={d.date} className="bar-group">
                  <div className="bar" style={{ height: `${height}%` }}>
                    <span className="bar-value">
                      {metric === 'revenue' ? `$${val.toLocaleString()}` : val}
                    </span>
                  </div>
                  <span className="bar-label">{d.date.slice(5)}</span>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
