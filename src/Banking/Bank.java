package Banking;

public class Bank {
    String name;
    int accountnumber;
    int balance;
    void deposit(int amount)
    {
        balance = balance + amount;
        System.out.println("Deposited succesfuuly: " + balance);
    }
    void withdrwan(int amount)
    {
        if(amount<=balance)
        {
            balance = balance - amount;
            System.out.println("Amount withdrwan successfully: " + balance);
        }
        else
        {
            System.out.println("Insufficient amount");
        }
    }
}
