package FoodDelivery;

public class Customers{
    private int customerId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String address;

    public Customers(int customerId, String customerName, String email, String phoneNumber, String address){
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getCustomerId(){
        return customerId;
    }

    public void setName(String name){
        this.customerName = name;
    }

    public String getName(){
        return customerName;
    }

    public String getEmail(){
        return email;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setAddress(String address){
        this.address = address;
    }

}
