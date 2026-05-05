import { useState } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingBag, Menu, X } from 'lucide-react';
import { useShop } from '../../context/ShopContext';

export default function Header() {
  const { cartCount, wishlist } = useShop();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="header">
      <div className="header-inner">
        <Link to="/" className="brand">
          <span className="brand-icon">🔪</span>
          <div>
            <div className="brand-name">Fresh Cuts</div>
            <div className="brand-tagline">Butchery & Deli</div>
          </div>
        </Link>

        <nav className={`nav${menuOpen ? ' open' : ''}`}>
          <Link to="/" onClick={() => setMenuOpen(false)}>Home</Link>
          <Link to="/shop" onClick={() => setMenuOpen(false)}>Shop</Link>
          <Link to="/wishlist" onClick={() => setMenuOpen(false)}>
            Wishlist <span className="badge-count">{wishlist.length || ''}</span>
          </Link>
          <Link to="/cart" onClick={() => setMenuOpen(false)}>
            <ShoppingBag size={18} />
            Cart <span className="badge-count">{cartCount || ''}</span>
          </Link>
        </nav>

        <button className="mobile-menu-btn" onClick={() => setMenuOpen(!menuOpen)}>
          {menuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>
    </header>
  );
}
