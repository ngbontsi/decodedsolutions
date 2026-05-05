import { Link } from 'react-router-dom';
import { Star, MapPin, Bed } from 'lucide-react';
import type { Property } from '../../types';

export default function PropertyCard({ property }: { property: Property }) {
  const minPrice = Math.min(...property.rooms.map((r) => r.price));
  const availableRooms = property.rooms.filter((r) => r.available).length;

  return (
    <div className="property-card">
      <div className="property-image">
        <div className="property-visual">
          <span style={{ fontSize: 64 }}>🏡</span>
        </div>
        {property.featured && <div className="property-badge">Featured</div>}
      </div>
      <div className="property-info">
        <div className="property-header">
          <h3>{property.name}</h3>
          <div className="property-rating">
            <Star size={14} fill="currentColor" /> {property.rating}
            <span className="review-count">({property.reviews})</span>
          </div>
        </div>
        <div className="property-location">
          <MapPin size={14} />
          {property.location}
        </div>
        <p>{property.description.slice(0, 100)}...</p>
        <div className="property-tags">
          {property.tags.map((tag) => (
            <span className="tag" key={tag}>{tag}</span>
          ))}
        </div>
        <div className="property-footer">
          <div>
            <span className="property-price">R{minPrice.toLocaleString()}</span>
            <span className="property-price-sub">/night</span>
          </div>
          <div className="property-meta">
            <Bed size={14} />
            {availableRooms}/{property.rooms.length} rooms
          </div>
        </div>
        <Link to={`/properties/${property.id}`} className="btn btn-sm btn-primary">View Rooms</Link>
      </div>
    </div>
  );
}
