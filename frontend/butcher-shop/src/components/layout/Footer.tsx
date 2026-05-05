export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-inner">
        <div className="footer-section">
          <h3>SC Socio Economic Growth Implementation Experts</h3>
          <p>Cutting poverty, serving quality</p>
        </div>
        <div className="footer-section">
          <h3>Hours</h3>
          <p>Mon-Fri: 7:00 - 18:00</p>
          <p>Saturday: 7:00 - 15:00</p>
          <p>Sunday: 8:00 - 13:00</p>
        </div>
        <div className="footer-section">
          <h3>Contact</h3>
          <p>📞 076 264 0842</p>
          <p>📍 2 Viola Street, Michausdal, Nxuba, 5880</p>
        </div>
      </div>
      <div className="footer-bottom">
        <p>
          &copy; {new Date().getFullYear()} SC Socio Economic Growth
          Implementation. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
