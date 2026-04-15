package MobRecharge;

// Main Class
public class Main {
    public static void main(String[] args) {

        // Original Plan
        Mobile original = new Mobile(101, "Unlimited Pack", 28, 299);

        // Copy Plan
        Mobile copied = new Mobile(original);

        System.out.println("Original Plan:");
        original.displayPlanDetails();

        System.out.println("Copied Plan (Before Changes):");
        copied.displayPlanDetails();

        // Modify copied plan
        copied.setPrice(349);
        copied.setValidityDays(35);

        System.out.println("Copied Plan (After Changes):");
        copied.displayPlanDetails();
    }
}