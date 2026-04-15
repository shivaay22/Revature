package PAMS;


import java.util.ArrayList;


public class PatientAppointmentSystem {

    public static void main(String args[]){

        ArrayList<String> appointments = new ArrayList<>();

        appointments.add("Rahul");
        appointments.add("Amit");
        appointments.add("Neha");
        appointments.add("Rahul");
        appointments.add("Priya");

        System.out.println(appointments);

        appointments.add(2,"Serious Patient");
        System.out.println(appointments);

        appointments.set(3,"Update Rahul");
        System.out.println(appointments);

        appointments.remove("Neha");
        System.out.println(appointments);

        String name = "Priya";

        if(appointments.contains(name)){
            System.out.println("Exist Patient");
        }
        else{
            System.out.println("Not there");
        }

        System.out.println("Size: " + appointments.size());

        for(String all : appointments){
            System.out.println("all patient: " + all);
        }

    }
}
