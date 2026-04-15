// src/pages/Cart.jsx
import { useState, useEffect } from 'react';
import { products } from '../data/products';
import './Cart.css';

function Cart() {
  const [cartItems, setCartItems] = useState([]);

  useEffect(() => {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      setCartItems(JSON.parse(savedCart));
    }
  }, []);

  const updateCart = (newCart) => {
    setCartItems(newCart);
    localStorage.setItem('cart', JSON.stringify(newCart));
  };

  const removeFromCart = (productId) => {
    const newCart = cartItems.filter(item => item.id !== productId);
    updateCart(newCart);
  };

  const updateQuantity = (productId, newQuantity) => {
    if (newQuantity < 1) return;
    const product = products.find(p => p.id === productId);
    if (newQuantity > product.stock) return;
    
    const newCart = cartItems.map(item =>
      item.id === productId ? { ...item, quantity: newQuantity } : item
    );
    updateCart(newCart);
  };

  const totalAmount = cartItems.reduce((total, item) => {
    const product = products.find(p => p.id === item.id);
    return total + (product.price * item.quantity);
  }, 0);

  if (cartItems.length === 0) {
    return (
      <div className="cart-empty">
        <h2>Your Cart is Empty</h2>
        <p>Add some products to your cart!</p>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <h2>Shopping Cart</h2>
      <div className="cart-items">
        {cartItems.map(item => {
          const product = products.find(p => p.id === item.id);
          return (
            <div key={item.id} className="cart-item">
              <img src={product.image} alt={product.name} className="cart-item-image" />
              <div className="cart-item-details">
                <h3>{product.name}</h3>
                <p>₹{product.price}</p>
                <p>Stock: {product.stock}</p>
              </div>
              <div className="cart-item-actions">
                <button 
                  onClick={() => updateQuantity(item.id, item.quantity - 1)}
                  className="qty-btn"
                >
                  -
                </button>
                <span className="qty">{item.quantity}</span>
                <button 
                  onClick={() => updateQuantity(item.id, item.quantity + 1)}
                  className="qty-btn"
                  disabled={item.quantity >= product.stock}
                >
                  +
                </button>
                <button 
                  onClick={() => removeFromCart(item.id)}
                  className="remove-btn"
                >
                  Remove
                </button>
              </div>
            </div>
          );
        })}
      </div>
      <div className="cart-summary">
        <h3>Total: ₹{totalAmount}</h3>
        <button className="checkout-btn">Proceed to Checkout</button>
      </div>
    </div>
  );
}

export default Cart;