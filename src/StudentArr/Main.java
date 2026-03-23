package StudentArr;

import java.util.Scanner;

public class Main {
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Size: ");
        int n = sc.nextInt();
        sc.nextLine();

        Student[] s1 = new Student[n];

        int marks[][] = new int[n][3];

        for(int i=0;i<n;i++){
            System.out.print("Enter name: ");
            String name = sc.nextLine();
            System.out.println("Enter age: ");
            int age = sc.nextInt();
            System.out.println("Enter reg no: ");
            int rno = sc.nextInt();

            int total = 0;
            for(int j=0;j<3;j++){
                System.out.println("Enter marks: ");
                marks[i][j] = sc.nextInt();
                total += marks[i][j];
            }
            double ave = total / 3;
            sc.nextLine();
            s1[i] = new Student(name,age,rno,total,ave);
        }
        for(int i=0;i<n;i++)
        {
            s1[i].print();
        }

    }
}
