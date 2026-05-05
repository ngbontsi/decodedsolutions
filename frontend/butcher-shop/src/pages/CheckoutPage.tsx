import { useState } from "react";
import { Link } from "react-router-dom";
import { Check, ShoppingBag } from "lucide-react";
import { useShop } from "../context/ShopContext";

export default function CheckoutPage() {
  const { cart, cartTotal, clearCart } = useShop();
  const [step, setStep] = useState(1);
  const [orderPlaced, setOrderPlaced] = useState(false);

  if (cart.length === 0 && !orderPlaced) {
    return (
      <div className="page">
        <div className="empty-state">
          <ShoppingBag size={64} />
          <h2>Your cart is empty</h2>
          <Link to="/shop" className="btn btn-primary">
            Start Shopping
          </Link>
        </div>
      </div>
    );
  }

  const handlePlaceOrder = () => {
    setOrderPlaced(true);
    clearCart();
  };

  if (orderPlaced) {
    return (
      <div className="page">
        <div className="empty-state success">
          <div className="success-icon">
            <Check size={64} />
          </div>
          <h2>Order Placed!</h2>
          <p>
            Thank you for your order. We will contact you shortly to confirm
            delivery details.
          </p>
          <Link to="/shop" className="btn btn-primary">
            Continue Shopping
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page checkout-page">
      <div className="page-header">
        <h1>Checkout</h1>
        <p>Step {step} of 2</p>
      </div>

      <div className="checkout-layout">
        <div className="checkout-form">
          {step === 1 && (
            <>
              <h2>Delivery Details</h2>
              <div className="form-group">
                <label>Full Name</label>
                <input type="text" placeholder="John Doe" />
              </div>
              <div className="form-group">
                <label>Phone Number</label>
                <input type="tel" placeholder="064 123 4567" />
              </div>
              <div className="form-group">
                <label>Delivery Address</label>
                <input type="text" placeholder="123 Main Street, East London" />
              </div>
              <div className="form-group">
                <label>Delivery Preference</label>
                <select>
                  <option>Delivery (Free over R200)</option>
                  <option>Pickup in Store</option>
                </select>
              </div>
              <button
                className="btn btn-primary btn-block"
                onClick={() => setStep(2)}
              >
                Continue to Payment
              </button>
            </>
          )}

          {step === 2 && (
            <>
              <h2>Payment</h2>
              <div className="payment-methods">
                <label className="payment-option selected">
                  <input type="radio" name="payment" defaultChecked />
                  <div>
                    <strong>Cash on Delivery</strong>
                    <p>Pay when your order arrives</p>
                  </div>
                </label>
                <label className="payment-option">
                  <input type="radio" name="payment" />
                  <div>
                    <strong>EFT / Bank Transfer</strong>
                    <p>Pay before delivery</p>
                  </div>
                </label>
              </div>
              <div className="checkout-actions">
                <button className="btn btn-outline" onClick={() => setStep(1)}>
                  Back
                </button>
                <button className="btn btn-primary" onClick={handlePlaceOrder}>
                  Place Order
                </button>
              </div>
            </>
          )}
        </div>

        <div className="checkout-summary">
          <h2>Your Order</h2>
          {cart.map((item) => (
            <div key={item.product.id} className="checkout-item">
              <div className="checkout-item-info">
                <span>{item.product.image}</span>
                <div>
                  <h4>{item.product.name}</h4>
                  <span className="qty">x{item.quantity}</span>
                </div>
              </div>
              <span>R{(item.product.price * item.quantity).toFixed(2)}</span>
            </div>
          ))}
          <div className="checkout-total">
            <span>Total</span>
            <span>R{cartTotal.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
