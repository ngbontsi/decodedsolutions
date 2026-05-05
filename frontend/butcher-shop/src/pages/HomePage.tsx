import { Link } from "react-router-dom";
import { ShoppingBag, Star, ChevronRight } from "lucide-react";
import { useShop } from "../context/ShopContext";

export default function HomePage() {
  const { products, addToCart } = useShop();
  const featured = products.filter((p) => p.featured).slice(0, 4);

  return (
    <div className="home">
      <section className="hero">
        <div className="hero-content">
          <h1>
            Premium Quality
            <br />
            <span className="accent">Fresh Cuts</span> Daily
          </h1>
          <p>
            From farm to your braai. Hand-selected cuts, traditional recipes,
            and the freshest meats in town.
          </p>
          <div className="hero-actions">
            <Link to="/shop" className="btn btn-primary">
              <ShoppingBag size={18} />
              Shop Now
            </Link>
            <Link to="/shop" className="btn btn-outline">
              View Specials
            </Link>
          </div>
        </div>
        <div className="hero-stats">
          <div className="stat">
            <div className="stat-num">13+</div>
            <div className="stat-label">Years</div>
          </div>
          <div className="stat">
            <div className="stat-num">500+</div>
            <div className="stat-label">Happy Customers</div>
          </div>
          <div className="stat">
            <div className="stat-num">100%</div>
            <div className="stat-label">Fresh Daily</div>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="section-header">
          <h2>Featured Cuts</h2>
          <Link to="/shop" className="link-arrow">
            View All <ChevronRight size={16} />
          </Link>
        </div>
        <div className="product-grid">
          {featured.map((product) => (
            <div key={product.id} className="product-card">
              <div className="product-badge">⭐ Featured</div>
              <div className="product-image">
                <span className="product-emoji">{product.image}</span>
              </div>
              <div className="product-info">
                <span className="product-category">{product.category}</span>
                <h3>{product.name}</h3>
                <p>{product.description}</p>
                <div className="product-footer">
                  <div className="product-price">
                    R{product.price.toFixed(2)}
                    <span>/{product.unit}</span>
                  </div>
                  <button
                    className="btn btn-sm btn-primary"
                    onClick={() => addToCart(product)}
                    disabled={!product.inStock}
                  >
                    <ShoppingBag size={16} />
                    {product.inStock ? "Add" : "Sold Out"}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="section section-dark">
        <div className="categories-grid">
          {[
            { key: "beef", label: "Beef", emoji: "🥩" },
            { key: "lamb", label: "Lamb", emoji: "🍖" },
            { key: "chicken", label: "Chicken", emoji: "🍗" },
            { key: "pork", label: "Pork", emoji: "🥓" },
            { key: "specials", label: "Specials", emoji: "⭐" },
          ].map((cat) => (
            <Link
              key={cat.key}
              to={`/shop?category=${cat.key}`}
              className="category-card"
            >
              <span className="category-emoji">{cat.emoji}</span>
              <span className="category-name">{cat.label}</span>
            </Link>
          ))}
        </div>
      </section>

      <section className="section">
        <div className="trust-grid">
          <div className="trust-item">
            <div className="trust-icon">
              <Star size={24} />
            </div>
            <h3>Quality Guaranteed</h3>
            <p>Hand-selected cuts, inspected and graded for quality.</p>
          </div>
          <div className="trust-item">
            <div className="trust-icon">
              <ShoppingBag size={24} />
            </div>
            <h3>Fresh Daily</h3>
            <p>New stock delivered every morning, straight from the farm.</p>
          </div>
          <div className="trust-item">
            <div className="trust-icon">
              <span style={{ fontSize: 24 }}>💳</span>
            </div>
            <h3>Easy Checkout</h3>
            <p>Simple ordering with delivery or pickup options.</p>
          </div>
        </div>
      </section>
    </div>
  );
}
