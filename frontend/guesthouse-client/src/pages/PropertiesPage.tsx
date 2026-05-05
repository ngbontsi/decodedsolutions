import { useState } from "react";
import { useBooking } from "../context/BookingContext";
import PropertyCard from "../components/shared/PropertyCard";

export default function PropertiesPage() {
  const { properties } = useBooking();
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState<"rating" | "price-low" | "price-high">(
    "rating",
  );

  const filtered = properties.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.location.toLowerCase().includes(search.toLowerCase()),
  );

  const sorted = [...filtered].sort((a, b) => {
    const minA = Math.min(...a.rooms.map((r) => r.price));
    const minB = Math.min(...b.rooms.map((r) => r.price));
    if (sort === "rating") return b.rating - a.rating;
    if (sort === "price-low") return minA - minB;
    return minB - minA;
  });

  return (
    <div className="page properties-page">
      <div className="page-header">
        <h1>All Properties</h1>
        <p>{sorted.length} properties available</p>
      </div>

      <div className="toolbar">
        <div className="search-box">
          <input
            type="text"
            placeholder="Search by name or location..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="filter-group">
          <select
            value={sort}
            onChange={(e) => setSort(e.target.value as typeof sort)}
          >
            <option value="rating">Highest Rated</option>
            <option value="price-low">Price: Low to High</option>
            <option value="price-high">Price: High to Low</option>
          </select>
        </div>
      </div>

      {sorted.length === 0 ? (
        <div className="empty-state">
          <h2>No properties found</h2>
          <p>Try a different search term</p>
        </div>
      ) : (
        <div className="property-grid">
          {sorted.map((property) => (
            <PropertyCard key={property.id} property={property} />
          ))}
        </div>
      )}
    </div>
  );
}
