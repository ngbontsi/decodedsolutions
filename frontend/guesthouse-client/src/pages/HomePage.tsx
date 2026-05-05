import { Link } from "react-router-dom";
import { Search, Star, MapPin, ChevronRight } from "lucide-react";
import { useBooking } from "../context/BookingContext";
import PropertyCard from "../components/shared/PropertyCard";

export default function HomePage() {
  const { properties } = useBooking();
  const featured = properties.filter((p) => p.featured);

  return (
    <div className="home">
      <section className="hero">
        <div className="hero-content">
          <h1>
            Find Your Perfect
            <br />
            <span className="accent">Getaway</span>
          </h1>
          <p>Book your weekend away with us and regret nothing.</p>
          <div className="hero-actions">
            <Link to="/properties" className="btn btn-primary">
              <Search size={18} />
              Browse Properties
            </Link>
            <Link to="/properties" className="btn btn-outline">
              View All Locations
            </Link>
          </div>
        </div>
        <div className="hero-stats">
          <div className="stat">
            <div className="stat-num">{properties.length}</div>
            <div className="stat-label">Properties</div>
          </div>
          <div className="stat">
            <div className="stat-num">
              {properties.reduce((s, p) => s + p.rooms.length, 0)}+
            </div>
            <div className="stat-label">Rooms</div>
          </div>
          <div className="stat">
            <div className="stat-num">
              {(
                properties.reduce((s, p) => s + p.rating, 0) / properties.length
              ).toFixed(1)}
            </div>
            <div className="stat-label">Avg Rating</div>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="section-header">
          <h2>Featured Properties</h2>
          <Link to="/properties" className="link-arrow">
            View All <ChevronRight size={16} />
          </Link>
        </div>
        <div className="property-grid">
          {featured.map((property) => (
            <PropertyCard key={property.id} property={property} />
          ))}
        </div>
      </section>

      <section className="section section-dark">
        <div className="section-header">
          <h2>Explore by Location</h2>
        </div>
        <div className="locations-grid">
          {properties.map((property) => (
            <Link
              key={property.id}
              to={`/properties/${property.id}`}
              className="location-card"
            >
              <MapPin size={24} />
              <div>
                <div className="location-name">{property.name}</div>
                <div className="location-sub">{property.location}</div>
              </div>
              <div className="location-rating">
                <Star size={14} fill="currentColor" /> {property.rating}
              </div>
            </Link>
          ))}
        </div>
      </section>
    </div>
  );
}
