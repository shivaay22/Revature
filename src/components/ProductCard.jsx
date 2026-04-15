// src/components/ProductCard.jsx
import { useState, useEffect } from 'react';
import './ProductCard.css';

function ProductCard({ product }) {
  const [isInCart, setIsInCart] = useState(false);
  
  const isLowStock = product.stock < 5 && product.stock > 0;
  const isOutOfStock = product.stock === 0;
  
  const cardClasses = `product-card ${isLowStock ? 'low-stock' : ''} ${isOutOfStock ? 'out-of-stock-card' : ''}`;

  useEffect(() => {
    const cart = localStorage.getItem('cart');
    if (cart) {
      const items = JSON.parse(cart);
      const exists = items.some(item => item.id === product.id);
      setIsInCart(exists);
    }
  }, [product.id]);

  const handleAddToCart = () => {
    const cart = localStorage.getItem('cart');
    let cartItems = cart ? JSON.parse(cart) : [];
    
    const existingItem = cartItems.find(item => item.id === product.id);
    
    if (existingItem) {
      if (existingItem.quantity < product.stock) {
        existingItem.quantity++;
      } else {
        alert(`Only ${product.stock} items available in stock!`);
        return;
      }
    } else {
      cartItems.push({ id: product.id, quantity: 1 });
    }
    
    localStorage.setItem('cart', JSON.stringify(cartItems));
    setIsInCart(true);
    window.dispatchEvent(new Event('storage'));
  };

  return (
    <div className={cardClasses}>
      <div className="product-image">
        <img src={product.image} alt={product.name} />
      </div>
      <div className="product-info">
        <h3 className="product-name">{product.name}</h3>
        <p className="product-category">{product.category}</p>
        <p className="product-price">₹{product.price}</p>
        <p className="product-stock">Stock: {product.stock}</p>
        <p className={`stock-status ${isOutOfStock ? 'out-of-stock' : 'in-stock'}`}>
          Status: {isOutOfStock ? 'Out of Stock' : 'In Stock'}
        </p>
        {isLowStock && !isOutOfStock && (
          <p className="low-stock-warning">⚠️ Hurry! Only few left</p>
        )}
        <button 
          className={`add-to-cart-btn ${isOutOfStock ? 'disabled' : ''}`}
          onClick={handleAddToCart}
          disabled={isOutOfStock}
        >
          {isOutOfStock ? 'Out of Stock' : (isInCart ? 'Added to Cart' : 'Add to Cart')}
        </button>
      </div>
    </div>
  );
}

export default ProductCard;