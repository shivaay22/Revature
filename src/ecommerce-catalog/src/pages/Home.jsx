
import { Link } from 'react-router-dom';
import './Home.css';

function Home() {
  return (
    <div className="home">
      <div className="hero">
        <h1>Welcome to ShopHub</h1>
        <p>Discover amazing products at unbeatable prices</p>
        <Link to="/products" className="shop-now-btn">Shop Now</Link>
      </div>
      <div className="features">
        <div className="feature">
          <h3>Free Shipping</h3>
          <p>On orders over ₹999</p>
        </div>
        <div className="feature">
          <h3>Secure Payment</h3>
          <p>100% secure transactions</p>
        </div>
        <div className="feature">
          <h3>24/7 Support</h3>
          <p>Always here to help you</p>
        </div>
      </div>
    </div>
  );
}

export default Home;