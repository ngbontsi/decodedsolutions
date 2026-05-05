export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-inner">
        <div className="footer-section">
          <h3>Rasmeni & Sons</h3>
          <p>
            A curated collection of guesthouses across Cradock. From Lingelihle
            to Town.
          </p>
        </div>
        <div className="footer-section">
          <h3>Contact</h3>
          <p>📞 0800 STAY REST</p>
          <p>✉️ rasmeni@gmail.com</p>
        </div>
        <div className="footer-section">
          <h3>Properties</h3>
          <p>5 unique locations</p>
          <p>50+ rooms available</p>
        </div>
      </div>
      <div className="footer-bottom">
        <p>
          &copy; {new Date().getFullYear()} Rasmeni & Sons Guesthouse
          Collection. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
