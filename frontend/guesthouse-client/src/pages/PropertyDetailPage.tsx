import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  Star,
  MapPin,
  Bed,
  Users,
  Check,
  Wifi,
  Coffee,
  Tv,
  Droplets,
  Snowflake,
} from "lucide-react";
import { useBooking } from "../context/BookingContext";
import type { Room } from "../types";

const amenityIcons: Record<string, React.ReactNode> = {
  "King Bed": <Bed size={14} />,
  "Queen Bed": <Bed size={14} />,
  "Double Bed": <Bed size={14} />,
  "2 Double Beds": <Bed size={14} />,
  "2 Single Beds": <Bed size={14} />,
  "Single Bed": <Bed size={14} />,
  WiFi: <Wifi size={14} />,
  "Mini Bar": <Coffee size={14} />,
  TV: <Tv size={14} />,
  "Work Desk": <Check size={14} />,
  Fridge: <Snowflake size={14} />,
  "Tea Station": <Coffee size={14} />,
  Bathtub: <Droplets size={14} />,
};

export default function PropertyDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { properties } = useBooking();
  const property = properties.find((p) => p.id === id);

  const [selectedRoom, setSelectedRoom] = useState<Room | null>(null);
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [guests, setGuests] = useState(1);
  const [bookingStep, setBookingStep] = useState(0);

  if (!property) {
    return (
      <div className="page">
        <div className="empty-state">
          <h2>Property not found</h2>
          <Link to="/properties" className="btn btn-primary">
            Back to Properties
          </Link>
        </div>
      </div>
    );
  }

  const nights =
    checkIn && checkOut
      ? Math.max(
          1,
          Math.ceil(
            (new Date(checkOut).getTime() - new Date(checkIn).getTime()) /
              (1000 * 60 * 60 * 24),
          ),
        )
      : 0;

  const total = selectedRoom ? selectedRoom.price * nights : 0;

  const handleBooking = () => {
    setBookingStep(1);
  };

  const handleConfirm = () => {
    navigate("/my-bookings");
  };

  return (
    <div className="page property-detail">
      <Link to="/properties" className="back-link">
        <ArrowLeft size={16} /> Back to Properties
      </Link>

      <div className="property-detail-header">
        <div>
          <h1>{property.name}</h1>
          <div className="property-detail-location">
            <MapPin size={16} />
            {property.location}
            <span className="rating-badge">
              <Star size={14} fill="currentColor" /> {property.rating} (
              {property.reviews} reviews)
            </span>
          </div>
        </div>
        <div className="property-tags">
          {property.tags.map((tag) => (
            <span className="tag" key={tag}>
              {tag}
            </span>
          ))}
        </div>
      </div>

      <p className="property-detail-desc">{property.description}</p>

      <div className="rooms-section">
        <h2>Select a Room</h2>
        <div className="rooms-list">
          {property.rooms.map((room) => (
            <div
              key={room.id}
              className={`room-card${selectedRoom?.id === room.id ? " selected" : ""}`}
              onClick={() => room.available && setSelectedRoom(room)}
            >
              {!room.available && (
                <div className="room-unavailable-overlay">Unavailable</div>
              )}
              <div className="room-header">
                <h3>{room.name}</h3>
                <div className="room-price">
                  R{room.price.toLocaleString()}
                  <span>/night</span>
                </div>
              </div>
              <div className="room-meta">
                <span className="room-type">{room.type}</span>
                <span className="room-size">{room.size}</span>
                <span className="room-guests">
                  <Users size={14} /> Up to {room.capacity}
                </span>
                {room.available ? (
                  <span className="room-avail">Available</span>
                ) : (
                  <span className="room-soldout">Sold Out</span>
                )}
              </div>
              <div className="room-amenities">
                {room.amenities.map((a) => (
                  <span key={a} className="amenity">
                    {amenityIcons[a] || <Check size={14} />}
                    {a}
                  </span>
                ))}
              </div>
              {selectedRoom?.id === room.id && (
                <div className="booking-form-inline">
                  <div className="booking-row">
                    <div className="form-group">
                      <label>Check-in</label>
                      <input
                        type="date"
                        value={checkIn}
                        onChange={(e) => setCheckIn(e.target.value)}
                      />
                    </div>
                    <div className="form-group">
                      <label>Check-out</label>
                      <input
                        type="date"
                        value={checkOut}
                        onChange={(e) => setCheckOut(e.target.value)}
                      />
                    </div>
                    <div className="form-group">
                      <label>Guests</label>
                      <select
                        value={guests}
                        onChange={(e) => setGuests(Number(e.target.value))}
                      >
                        {Array.from(
                          { length: room.capacity },
                          (_, i) => i + 1,
                        ).map((n) => (
                          <option key={n} value={n}>
                            {n} guest{n > 1 ? "s" : ""}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                  {nights > 0 && (
                    <div className="booking-summary">
                      <span>
                        {nights} night{nights > 1 ? "s" : ""} × R
                        {room.price.toLocaleString()}
                      </span>
                      <span className="booking-total">
                        R{total.toLocaleString()}
                      </span>
                    </div>
                  )}
                  <button
                    className="btn btn-primary btn-block"
                    disabled={nights <= 0}
                    onClick={handleBooking}
                  >
                    Book This Room
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {bookingStep === 1 && (
        <div className="booking-modal">
          <div className="booking-modal-content">
            <h2>Booking Summary</h2>
            <div className="booking-details">
              <div className="booking-detail-row">
                <span>Property</span>
                <strong>{property.name}</strong>
              </div>
              <div className="booking-detail-row">
                <span>Room</span>
                <strong>{selectedRoom?.name}</strong>
              </div>
              <div className="booking-detail-row">
                <span>Check-in</span>
                <strong>{checkIn}</strong>
              </div>
              <div className="booking-detail-row">
                <span>Check-out</span>
                <strong>{checkOut}</strong>
              </div>
              <div className="booking-detail-row">
                <span>Guests</span>
                <strong>{guests}</strong>
              </div>
              <div className="booking-detail-row total">
                <span>Total ({nights} nights)</span>
                <strong>R{total.toLocaleString()}</strong>
              </div>
            </div>
            <div className="booking-modal-actions">
              <button
                className="btn btn-outline"
                onClick={() => setBookingStep(0)}
              >
                Back
              </button>
              <button className="btn btn-primary" onClick={handleConfirm}>
                Confirm Booking
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
