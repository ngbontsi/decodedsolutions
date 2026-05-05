import { Activity, Clock, AlertTriangle, CheckCircle } from 'lucide-react';
import type { ServiceStatus } from '../../types';

const mockServices: ServiceStatus[] = [
  { name: 'API Gateway', port: 8080, status: 'healthy', uptime: '99.98%', lastCheck: '30s ago', requestsPerMinute: 1240, avgResponseTime: 45, errorRate: 0.1 },
  { name: 'Auth Service', port: 8081, status: 'healthy', uptime: '99.95%', lastCheck: '30s ago', requestsPerMinute: 320, avgResponseTime: 120, errorRate: 0.3 },
  { name: 'Restaurant Service', port: 8082, status: 'healthy', uptime: '99.90%', lastCheck: '30s ago', requestsPerMinute: 180, avgResponseTime: 85, errorRate: 0.2 },
  { name: 'Guesthouse Service', port: 8083, status: 'degraded', uptime: '97.50%', lastCheck: '30s ago', requestsPerMinute: 95, avgResponseTime: 340, errorRate: 2.1 },
  { name: 'Marketplace Service', port: 8084, status: 'healthy', uptime: '99.92%', lastCheck: '30s ago', requestsPerMinute: 450, avgResponseTime: 110, errorRate: 0.4 },
  { name: 'Task API', port: 8085, status: 'healthy', uptime: '99.99%', lastCheck: '30s ago', requestsPerMinute: 210, avgResponseTime: 65, errorRate: 0.05 },
];

const statusConfig: Record<string, { icon: React.ElementType; color: string }> = {
  healthy: { icon: CheckCircle, color: 'text-green' },
  unhealthy: { icon: AlertTriangle, color: 'text-red' },
  degraded: { icon: AlertTriangle, color: 'text-yellow' },
  unknown: { icon: Activity, color: 'text-gray' },
};

export default function MonitoringPage() {
  return (
    <div className="page">
      <div className="page-header">
        <h1>Service Monitoring</h1>
        <p>Real-time health and performance metrics</p>
      </div>

      <div className="monitoring-grid">
        {mockServices.map((svc) => {
          const cfg = statusConfig[svc.status];
          const StatusIcon = cfg.icon;
          return (
            <div key={svc.name} className="card service-card">
              <div className="service-card-header">
                <div className="service-title">
                  <StatusIcon size={20} className={cfg.color} />
                  <h3>{svc.name}</h3>
                </div>
                <span className={`status-badge ${svc.status}`}>
                  {svc.status.charAt(0).toUpperCase() + svc.status.slice(1)}
                </span>
              </div>
              <div className="service-metrics">
                <div className="metric">
                  <span className="metric-label">Port</span>
                  <span className="metric-value">{svc.port}</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Uptime</span>
                  <span className="metric-value">{svc.uptime}</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Req/min</span>
                  <span className="metric-value">{svc.requestsPerMinute}</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Avg Response</span>
                  <span className="metric-value">{svc.avgResponseTime}ms</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Error Rate</span>
                  <span className={`metric-value ${svc.errorRate > 1 ? 'text-red' : ''}`}>{svc.errorRate}%</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Last Check</span>
                  <span className="metric-value"><Clock size={12} /> {svc.lastCheck}</span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
