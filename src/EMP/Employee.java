package EMP;

public class Employee {
    private int employeed;
    private String employeeName;
    private double basicSalary;
    private double allowance;
    private double taxPercentage;

    public Employee(int employeed,String employeeName,double basicSalary,double allowance,double taxPercentage){
        this.employeed = employeed;
        this.employeeName = employeeName;
        this.basicSalary = basicSalary;
        this.allowance = allowance;
        this.taxPercentage = taxPercentage;
    }


    public int getEmployeed(){
        return employeed;
    }

    public void setEmployeeName(String name){
        if(name == null){
            System.out.println("Invalid Name");
        }
        else{
            this.employeeName = name;
        }
    }

    public String getEmployeeName(){
        return employeeName;
    }

    public void setBasicSalary(int basicSalary){
        if(basicSalary < 0){
            System.out.println("Invalid Salary");
        }
        else{
            this.basicSalary = basicSalary;
        }
    }

    public double getBasicSalary(){
        return basicSalary;
    }

    public void setTaxPercentage(double taxPercentage){
        if(taxPercentage < 0 || taxPercentage > 30){
            System.out.println("Invalid Tax Percentage");
        }
        else{
            this.taxPercentage = taxPercentage;
        }
    }
    public double calcGrossSalary(){
        return basicSalary + allowance;
    }

    public double calcTaxAmount(){
        return calcGrossSalary() * (taxPercentage / 100);
    }

    public  double calcNetSalary(){
        return calcGrossSalary() * calcTaxAmount();
    }

    public void showTotalAmount(){
        System.out.println("Emp Id: " + employeed);
        System.out.println("Emp Name: " + employeeName);
        System.out.println("Basic Salary: " + basicSalary);
        System.out.println("Allowance: " + allowance);
        System.out.println("Gross Salary: " + calcGrossSalary());
        System.out.println("tax Amounr: " + calcTaxAmount());
        System.out.println("Net salary: " + calcNetSalary());
    }

}
