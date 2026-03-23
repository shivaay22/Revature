package EMP;
import java.util.*;

public class Main {
    public static void main(String args[]){
        Employee emp = new Employee(22,"Bhardwaj",24121,5451,21);
        emp.showTotalAmount();
        emp.setBasicSalary(6526);
        emp.setTaxPercentage(23);
        emp.showTotalAmount();
    }
}
