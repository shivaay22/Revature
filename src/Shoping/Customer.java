package Shoping;

public class Customer{
    private int customerId;
    private String customerName;
    private String email;

    public Customer(int customerId, String customerName, String email) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
    }

    public void displayCustomer(){
        System.out.println("CustomerID: " + customerId);
        System.out.println("CustomerName: " + customerName);
        System.out.println("Email: " + email);
    }
}
