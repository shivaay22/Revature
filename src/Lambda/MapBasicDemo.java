package Lambda;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapBasicDemo {
    public static void main(String[] args) {
        Map<Integer, String> employees = new HashMap();
        employees.put(101, "Harish");
        employees.put(102, "Shivam");
        employees.put(103, "Ananya");
//        System.out.println(employees);
//        System.out.println("103 " + (String)employees.get(103));
        employees.put(104, "dummy");
//        System.out.println(" Before Removes 104  ");
//        System.out.println(employees);
        employees.remove(104);
//        System.out.println(" After Removes 104  ");
//        System.out.println(employees);
//        System.out.println("is 102 available ? " + employees.containsKey(102));
//        System.out.println("is Dummy available ? " + employees.containsValue("Dummy"));
//        System.out.println("employees size  " + employees.size());
        Set<Integer> employeeKeySet = employees.keySet();
//        System.out.println(employeeKeySet);
        Collection<String> employeeNames = employees.values();
//        System.out.println(employeeNames);

        for(Map.Entry<Integer, String> entry : employees.entrySet()) {
            PrintStream var10000 = System.out;
            String var10001 = String.valueOf(entry.getKey());
            var10000.println("Id : " + var10001 + " - " + (String)entry.getValue());
        }

//        employees.putIfAbsent(105, "Nithin");
//        System.out.println(employees);
//        employees.replace(105, "Sun");
//        System.out.println(employees);
//        System.out.println((String)employees.getOrDefault(106, "NA"));
        employees.forEach((id, name) -> System.out.println("Emp Id:" + id + "Name : " + name));
        employees.computeIfAbsent(106, (k) -> "New Employee");
        System.out.println(employees);
        employees.computeIfPresent(106, (k, v) -> "Janani");
        System.out.println(employees);
        employees.merge(102, " Kumar", (oldValue, newValue) -> oldValue + newValue);
        System.out.println(employees);
    }
}

