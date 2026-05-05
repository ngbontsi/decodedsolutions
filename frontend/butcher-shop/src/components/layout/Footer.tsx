export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-inner">
        <div className="footer-section">
          <h3>Fresh Cuts Butchery</h3>
          <p>Quality meats, fair prices, served with pride since 2010.</p>
        </div>
        <div className="footer-section">
          <h3>Hours</h3>
          <p>Mon-Fri: 7:00 - 18:00</p>
          <p>Saturday: 7:00 - 15:00</p>
          <p>Sunday: 8:00 - 13:00</p>
        </div>
        <div className="footer-section">
          <h3>Contact</h3>
          <p>📞 043 123 4567</p>
          <p>📍 42 Main Road, East London</p>
        </div>
      </div>
      <div className="footer-bottom">
        <p>&copy; {new Date().getFullYear()} Fresh Cuts Butchery. All rights reserved.</p>
      </div>
    </footer>
  );
}
