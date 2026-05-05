import { HashRouter, Routes, Route } from 'react-router-dom';
import { BookingProvider } from './context/BookingContext';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import HomePage from './pages/HomePage';
import PropertiesPage from './pages/PropertiesPage';
import PropertyDetailPage from './pages/PropertyDetailPage';
import MyBookingsPage from './pages/MyBookingsPage';
import './styles/app.css';

export default function App() {
  return (
    <HashRouter>
      <BookingProvider>
        <Header />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/properties" element={<PropertiesPage />} />
          <Route path="/properties/:id" element={<PropertyDetailPage />} />
          <Route path="/my-bookings" element={<MyBookingsPage />} />
        </Routes>
        <Footer />
      </BookingProvider>
    </HashRouter>
  );
}
