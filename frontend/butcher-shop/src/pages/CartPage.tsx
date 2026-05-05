import { Link } from "react-router-dom";
import { Heart, ShoppingBag, Minus, Plus, Trash2 } from "lucide-react";
import { useShop } from "../context/ShopContext";

export default function CartPage() {
  const {
    cart,
    updateCartQty,
    removeFromCart,
    cartTotal,
    addToCart,
    products,
  } = useShop();

  const wishlistProducts = products
    .filter((p) => {
      const inCart = cart.some((item) => item.product.id === p.id);
      return !inCart;
    })
    .slice(0, 3);

  return (
    <div className="page cart-page">
      <div className="page-header">
        <h1>Shopping Cart</h1>
        <p>
          {cart.length === 0
            ? "Your cart is empty"
            : `${cart.length} item${cart.length > 1 ? "s" : ""} in your cart`}
        </p>
      </div>

      {cart.length === 0 ? (
        <div className="empty-state">
          <ShoppingBag size={64} />
          <h2>Your cart is empty</h2>
          <p>Start shopping to add items to your cart</p>
          <Link to="/shop" className="btn btn-primary">
            Browse Products
          </Link>
        </div>
      ) : (
        <div className="cart-layout">
          <div className="cart-items">
            {cart.map((item) => (
              <div key={item.product.id} className="cart-item">
                <div className="cart-item-image">
                  <span className="product-emoji">{item.product.image}</span>
                </div>
                <div className="cart-item-details">
                  <h3>{item.product.name}</h3>
                  <p>{item.product.description}</p>
                  <span className="product-price">
                    R{item.product.price.toFixed(2)}/{item.product.unit}
                  </span>
                </div>
                <div className="cart-item-actions">
                  <div className="qty-control">
                    <button
                      onClick={() =>
                        updateCartQty(item.product.id, item.quantity - 1)
                      }
                    >
                      <Minus size={16} />
                    </button>
                    <span>{item.quantity}</span>
                    <button
                      onClick={() =>
                        updateCartQty(item.product.id, item.quantity + 1)
                      }
                    >
                      <Plus size={16} />
                    </button>
                  </div>
                  <div className="cart-item-subtotal">
                    R{(item.product.price * item.quantity).toFixed(2)}
                  </div>
                  <button
                    className="btn-remove"
                    onClick={() => removeFromCart(item.product.id)}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="cart-summary">
            <h2>Order Summary</h2>
            <div className="summary-row">
              <span>Subtotal</span>
              <span>R{cartTotal.toFixed(2)}</span>
            </div>
            <div className="summary-row">
              <span>Delivery</span>
              <span>Free</span>
            </div>
            <div className="summary-total">
              <span>Total</span>
              <span>R{cartTotal.toFixed(2)}</span>
            </div>
            <Link to="/checkout" className="btn btn-primary btn-block">
              Proceed to Checkout
            </Link>
          </div>
        </div>
      )}

      {cart.length > 0 && wishlistProducts.length > 0 && (
        <div className="section" style={{ marginTop: "3rem" }}>
          <h2>You May Also Like</h2>
          <div
            className="product-grid"
            style={{
              gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
            }}
          >
            {wishlistProducts.map((product) => (
              <div key={product.id} className="product-card">
                <button className="wishlist-btn" onClick={() => {}}>
                  <Heart size={20} fill="none" />
                </button>
                <div className="product-image">
                  <span className="product-emoji">{product.image}</span>
                </div>
                <div className="product-info">
                  <h3 style={{ fontSize: "14px" }}>{product.name}</h3>
                  <div className="product-price" style={{ fontSize: "16px" }}>
                    R{product.price.toFixed(2)}
                    <span>/{product.unit}</span>
                  </div>
                  <button
                    className="btn btn-sm btn-primary"
                    onClick={() => addToCart(product)}
                  >
                    <ShoppingBag size={16} /> Add
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
