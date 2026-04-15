package MobRecharge;

public class Mobile {
    private int planId;
    private String planName;
    private int validityDays;
    private double price;

    public Mobile(int planId, String planName, int validityDays, double price){
        this.planId = planId;
        this.planName = planName;
        this.validityDays = validityDays;
        this.price = price;
    }

    public Mobile(Mobile cp){
        this.planId = cp.planId;
        this.planName = cp.planName;
        this.validityDays = cp.validityDays;
        this.price = cp.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setValidityDays(int validityDays) {
        this.validityDays = validityDays;
    }

    public void displayPlanDetails() {
        System.out.println("Plan ID: " + planId);
        System.out.println("Plan Name: " + planName);
        System.out.println("Validity: " + validityDays + " days");
        System.out.println("Price: " + price);
    }

}
