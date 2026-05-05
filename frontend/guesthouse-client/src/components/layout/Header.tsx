import { useState } from "react";
import { Link } from "react-router-dom";
import { Menu, X, Calendar } from "lucide-react";
import { useBooking } from "../../context/BookingContext";

export default function Header() {
  const [menuOpen, setMenuOpen] = useState(false);
  const { bookings } = useBooking();
  const activeBookings = bookings.filter(
    (b) => b.status !== "cancelled",
  ).length;

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="brand">
          <span className="brand-icon">🏡</span>
          <div>
            <div className="brand-name">Rasmeni & Sons</div>
            <div className="brand-tagline">Guesthouse Collection</div>
          </div>
        </Link>

        <nav className={`nav${menuOpen ? " open" : ""}`}>
          <Link to="/" onClick={() => setMenuOpen(false)}>
            Home
          </Link>
          <Link to="/properties" onClick={() => setMenuOpen(false)}>
            Properties
          </Link>
          <Link to="/my-bookings" onClick={() => setMenuOpen(false)}>
            <Calendar size={18} />
            My Bookings{" "}
            {activeBookings > 0 && (
              <span className="badge-count">{activeBookings}</span>
            )}
          </Link>
        </nav>

        <button
          className="mobile-menu-btn"
          onClick={() => setMenuOpen(!menuOpen)}
        >
          {menuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>
    </header>
  );
}
