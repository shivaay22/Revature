package Shoping;

public class Main {
    public static void main(String[] args) {

        PremiumCustomer p = new PremiumCustomer(
                101, "Bhardwaj", "Bhardwaj@gmail.com", "headphone", 500
        );

        p.applyPremiumBenefits();
        System.out.println();
        p.displayCustomer();
    }
}