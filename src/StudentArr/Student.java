package StudentArr;

public class Student {
    String name;
    int age;
    int rno;
    int total;
    double ave;

    Student(String name, int age, int rno, int total, double ave){
        this.name = name;
        this.age = age;
        this.rno = rno;
        this.total = total;
        this.ave = ave;
    }

    void print()
    {
        System.out.println("Name: " + name);
        System.out.println("Roll no: " + age);
        System.out.println("Reg no: " + rno);
        System.out.println("Total Marks: " + total);
        System.out.println("Ave marks: " + ave);
    }
}