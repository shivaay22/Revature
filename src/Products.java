package AllPro;

public class Products {
    private int productId;
    private String name;
    private double price;
    private double percetage;
    private int stock;

    public Products(int productId,String name, double price, double percetage,int stock){
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.percetage = percetage;
        this.stock = stock;
    }

    public int getproductId(){
        return productId;
    }

    public void setProName(String name){
        if(name == null){
            System.out.println("Invalid Name");
        }
        else{
            this.name = name;
        }
    }

    public void setPrice(double price){
        if(price < 0){
            System.out.println("Invalid Price");
        }
        else{
            this.price = price;
        }
    }

    public void setDiscount(double price){
        if(price > 0 && price <= 60){
            this.price = price;
        }
        else{
            this.price = price;
        }
    }

    public double calcDiscount(){
        return price * (percetage / 100);
    }

    public double calcFinalPrice(){
        return price - calcDiscount();
    }

    public void sellPro(int qty){
        if(qty <= 0){
            System.out.println("Invalid Qnatity");
        }
        else if(qty > stock){
            System.out.println("Insufficient");
        }
        else{
             stock -= qty;
            System.out.println("Product sold: " + qty);
        }
    }

    public void productDetails(){
        System.out.println("Product Id: " + productId);
        System.out.println("Product Name: " + name);
        System.out.println("initialPrice: " + price);
        System.out.println("DIscount: " + percetage);
        System.out.println("Discount amount: " + calcDiscount());
        System.out.println("finalPrice: " + calcFinalPrice());
    }

}
