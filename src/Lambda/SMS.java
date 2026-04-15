package Lambda;

public class SMS {

    private int id;
    private String name;
    private String course;
    private double marks;
    private int age;

    public SMS(int id, String name, String course, double marks,int age){
        this.id = id;
        this.name = name;
        this.course = course;
        this.marks = marks;
        this.age = age;

    }

    public String getName() { return name; }
    public String getCourse() { return course; }
    public double getMarks() { return marks; }
    public int getAge() { return age; }

    @Override
    public String toString(){
        return id + " " + name + " " + course + " " + marks + " " + age;
    }

}
