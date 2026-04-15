package Products;

public class Products {
    private int productId;
    private String productName;
    private String category;
    private String brand;
    private double price;
    private int stock;
    private double rating;
    private double discountPercentage;
    private boolean inStock;

    public Products(int productId, String productName, String category, String brand,
                   double price, int stock, double rating, double discountPercentage, boolean inStock) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.rating = rating;
        this.discountPercentage = discountPercentage;
        this.inStock = inStock;
    }

    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public String getCategory() {
        return category;
    }
    public String getBrand() {
        return brand;
    }
    public double getPrice() {
        return price;
    }
    public int getStock() {
        return stock;
    }
    public double getRating() {
        return rating;
    }
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    public boolean isInStock() {
        return inStock;
    }

    @Override
    public String toString(){
        return  " ProductName: " + productName + "Category: " + category + "Price: " + price +
                "rating: " + rating + "Stock: " + stock + "inStock: " + inStock;
     }
}
