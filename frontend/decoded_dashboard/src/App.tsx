import { HashRouter, Routes, Route } from 'react-router-dom';
import DashboardLayout from './components/layout/DashboardLayout';
import DashboardPage from './pages/DashboardPage';
import UsersPage from './pages/users/UsersPage';
import MonitoringPage from './pages/monitoring/MonitoringPage';
import BusinessPage from './pages/business/BusinessPage';
import AnalyticsPage from './pages/analytics/AnalyticsPage';
import './styles/dashboard.css';

export default function App() {
  return (
    <HashRouter>
      <Routes>
        <Route path="/" element={<DashboardLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path="users" element={<UsersPage />} />
          <Route path="monitoring" element={<MonitoringPage />} />
          <Route path="business" element={<BusinessPage />} />
          <Route path="analytics" element={<AnalyticsPage />} />
        </Route>
      </Routes>
    </HashRouter>
  );
}
