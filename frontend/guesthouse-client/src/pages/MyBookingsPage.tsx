import { useState } from "react";
import { Link } from "react-router-dom";
import { Calendar, Bed, AlertCircle, XCircle, Loader2 } from "lucide-react";
import { useBooking } from "../context/BookingContext";

export default function MyBookingsPage() {
  const { bookings, cancelBooking } = useBooking();
  const [cancellingId, setCancellingId] = useState<string | null>(null);

  const handleCancel = async (id: string) => {
    if (!window.confirm("Are you sure you want to cancel this booking?")) return;
    setCancellingId(id);
    await cancelBooking(id);
    setCancellingId(null);
  };

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
            {(booking.status === "confirmed" || booking.status === "pending") && (
              <div className="booking-card-actions">
                <button
                  className="btn btn-outline btn-danger"
                  disabled={cancellingId === booking.id}
                  onClick={() => handleCancel(booking.id)}
                >
                  {cancellingId === booking.id ? (
                    <><Loader2 size={14} className="spin" /> Cancelling...</>
                  ) : (
                    <><XCircle size={14} /> Cancel Booking</>
                  )}
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
