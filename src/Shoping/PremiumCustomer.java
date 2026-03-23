package Shoping;

public class PremiumCustomer extends Customer {

    private String membership;
    private int rewardPoints;

    public PremiumCustomer(int customerId, String customerName, String email,
                           String membership, int rewardPoints) {
        super(customerId, customerName, email);
        this.membership = membership;
        this.rewardPoints = rewardPoints;
    }

    @Override
    public void displayCustomer() {
        super.displayCustomer();
        System.out.println("Membership: " + membership);
        System.out.println("Reward Points: " + rewardPoints);
    }

    public void applyPremiumBenefits() {
        System.out.println("Free Delivery");
    }
}