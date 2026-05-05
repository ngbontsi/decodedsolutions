import { Link } from "react-router-dom";
import { ShoppingBag, Heart } from "lucide-react";
import { useShop } from "../context/ShopContext";

export default function WishlistPage() {
  const { products, toggleWishlist, isInWishlist, addToCart } = useShop();
  const wishlistProducts = products.filter((p) => isInWishlist(p.id));

  return (
    <div className="page wishlist-page">
      <div className="page-header">
        <h1>Wishlist</h1>
        <p>
          {wishlistProducts.length === 0
            ? "No saved items"
            : `${wishlistProducts.length} saved item${wishlistProducts.length > 1 ? "s" : ""}`}
        </p>
      </div>

      {wishlistProducts.length === 0 ? (
        <div className="empty-state">
          <Heart size={64} />
          <h2>Your wishlist is empty</h2>
          <p>Save items you love while you browse</p>
          <Link to="/shop" className="btn btn-primary">
            Start Shopping
          </Link>
        </div>
      ) : (
        <div className="product-grid">
          {wishlistProducts.map((product) => (
            <div key={product.id} className="product-card">
              <button
                className="wishlist-btn active"
                onClick={() => toggleWishlist(product.id)}
              >
                <Heart size={20} fill="currentColor" />
              </button>
              <div className="product-image">
                <span className="product-emoji">{product.image}</span>
              </div>
              <div className="product-info">
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
                    {product.inStock ? "Add to Cart" : "Sold Out"}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
