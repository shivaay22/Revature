package Products;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]){
        List<Products> p1 = new ArrayList<>();

        p1.add(new Products(1, "iPhone 15", "Electric", "Apple", 80000, 10, 4.8, 10, true));
        p1.add(new Products(2, "Samsung TV", "Electric", "Samsung", 45000, 5, 4.5, 15, true));
        p1.add(new Products(3, "Nike Shoes", "Fashion", "Nike", 7000, 0, 4.6, 20, false));
        p1.add(new Products(4, "T-Shirt", "Fashion", "Puma", 1200, 20, 4.2, 5, true));
        p1.add(new Products(5, "Rice Bag", "Grocery", "IndiaGate", 2500, 50, 4.1, 2, true));
        p1.add(new Products(6, "Laptop", "Electrics", "Dell", 60000, 8, 4.7, 12, true));


        p1.stream()
                .map(Products::getProductName)
                .forEach(System.out::println);

        p1.stream()
                .map(p -> p.getProductName())
                .forEach(name -> System.out.println(name));

        p1.stream()
                .filter(p -> "Electric".equals(p.getCategory()))
                .forEach(p -> System.out.println(p));

        p1.stream()
                .filter(p -> p.getPrice() > 5000)
                .forEach(p -> System.out.println(p));

        p1.stream()
                .filter(p -> p.isInStock())
                .forEach(p -> System.out.println(p));

        p1.stream()
                .sorted((a,b) -> Double.compare(a.getPrice(),b.getPrice()))
                .forEach(p -> System.out.println(p));

        boolean anyCostly = p1.stream()
                .anyMatch(p -> p.getPrice() > 70000);
        System.out.println(anyCostly);

        boolean allGood = p1.stream()
                .allMatch(p -> p.getRating() > 3.5);
        System.out.println(allGood);

        long fashionCount = p1.stream()
                .filter(p -> "Fashion".equals(p.getCategory()))
                .count();
        System.out.println(fashionCount);

        long outOfStock = p1.stream()
                .filter(p -> !p.isInStock())
                .count();
        System.out.println(outOfStock);

    }
}
