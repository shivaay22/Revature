package AllPro;

public class Main {

    public  static  void main(String args[]){
        Products p1 = new Products(22,"Laptop",54521,25,5);
        p1.productDetails();
        p1.sellPro(1);
        p1.setDiscount(23);
        p1.productDetails();

    }
}
