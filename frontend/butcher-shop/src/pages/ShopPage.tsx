import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import { ShoppingBag, Heart } from "lucide-react";
import { useShop } from "../context/ShopContext";
import type { Category } from "../types";

export default function ShopPage() {
  const [searchParams] = useSearchParams();
  const { products, addToCart, toggleWishlist, isInWishlist } = useShop();
  const [activeCategory, setActiveCategory] = useState<Category | "all">(
    (searchParams.get("category") as Category) || "all",
  );

  const categories: { key: Category | "all"; label: string }[] = [
    { key: "all", label: "All" },
    { key: "beef", label: "Beef" },
    { key: "lamb", label: "Lamb" },
    { key: "chicken", label: "Chicken" },
    { key: "pork", label: "Pork" },
    { key: "specials", label: "Specials" },
  ];

  const filtered =
    activeCategory === "all"
      ? products
      : products.filter((p) => p.category === activeCategory);

  return (
    <div className="page shop-page">
      <div className="page-header">
        <h1>Our Products</h1>
        <p>Fresh cuts, daily stock</p>
      </div>

      <div className="category-tabs">
        {categories.map((cat) => (
          <button
            key={cat.key}
            className={`cat-tab${activeCategory === cat.key ? " active" : ""}`}
            onClick={() => setActiveCategory(cat.key)}
          >
            {cat.label}
          </button>
        ))}
      </div>

      <div className="product-grid">
        {filtered.map((product) => {
          const wishlisted = isInWishlist(product.id);
          return (
            <div key={product.id} className="product-card">
              {!product.inStock && (
                <div className="product-badge sold-out">Sold Out</div>
              )}
              <button
                className="wishlist-btn"
                onClick={() => toggleWishlist(product.id)}
                aria-label="Toggle wishlist"
              >
                <Heart size={20} fill={wishlisted ? "currentColor" : "none"} />
              </button>
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
          );
        })}
      </div>
    </div>
  );
}
