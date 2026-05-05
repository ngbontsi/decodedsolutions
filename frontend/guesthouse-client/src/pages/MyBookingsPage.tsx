import { Link } from "react-router-dom";
import { Calendar, Bed, AlertCircle, XCircle } from "lucide-react";
import { useBooking } from "../context/BookingContext";

export default function MyBookingsPage() {
  const { bookings } = useBooking();

  if (bookings.length === 0) {
    return (
      <div className="page">
        <div className="empty-state">
          <Calendar size={64} />
          <h2>No bookings yet</h2>
          <p>Book your first stay and it will appear here</p>
          <Link to="/properties" className="btn btn-primary">
            Browse Properties
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page bookings-page">
      <div className="page-header">
        <h1>My Bookings</h1>
        <p>
          {bookings.length} booking{bookings.length > 1 ? "s" : ""}
        </p>
      </div>

      <div className="bookings-list">
        {bookings.map((booking, idx) => (
          <div key={idx} className={`booking-card ${booking.status}`}>
            <div className="booking-card-header">
              <div>
                <h3>{booking.propertyName}</h3>
                <div className="booking-card-sub">
                  <Bed size={14} /> {booking.roomName}
                </div>
              </div>
              <span className={`booking-status ${booking.status}`}>
                {booking.status === "confirmed" ? (
                  <>
                    <Calendar size={12} /> Confirmed
                  </>
                ) : booking.status === "pending" ? (
                  <>
                    <AlertCircle size={12} /> Pending
                  </>
                ) : (
                  <>
                    <XCircle size={12} /> Cancelled
                  </>
                )}
              </span>
            </div>
            <div className="booking-card-dates">
              <div className="booking-date">
                <span className="date-label">Check-in</span>
                <span className="date-value">{booking.checkIn}</span>
              </div>
              <div className="booking-date-arrow">→</div>
              <div className="booking-date">
                <span className="date-label">Check-out</span>
                <span className="date-value">{booking.checkOut}</span>
              </div>
              <div className="booking-date">
                <span className="date-label">Guests</span>
                <span className="date-value">{booking.guests}</span>
              </div>
              <div className="booking-date">
                <span className="date-label">Total</span>
                <span className="date-value total">
                  R{booking.total.toLocaleString()}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
