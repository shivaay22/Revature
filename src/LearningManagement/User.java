package LearningManagement;

public class User {
    int userId;
    String name;

    void login(){
        System.out.println("log in succeessfully: " + name);
    }

    void displayUser(){
        System.out.println("userId: " + userId);
        System.out.println("Name: " + name);
    }
}

class Instructor extends User{
    String subject;
    int experince;

    void uploadCourse(){
        System.out.println("uploadCourse: " + name + " " + subject);
    }
}
