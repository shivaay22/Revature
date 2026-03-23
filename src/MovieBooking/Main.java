package MovieBooking;


import java.util.Scanner;

public class Main {

    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        BookingMovie b1 = new BookingMovie();

        try{
            System.out.println("Enter Name: ");
            String name = sc.nextLine();

            System.out.println("Enter movie name: ");
            String movieName = sc.nextLine();

            System.out.println("Enter Number of Tickets: ");
            int tickets = sc.nextInt();

            System.out.println("Enter Payment: ");
            double amount = sc.nextDouble();

            b1.bookTicket(name,movieName,tickets,amount);

            System.out.println("Want to ticket cancel: ");
            sc.nextLine();
            String choice = sc.nextLine();

            if(choice.equalsIgnoreCase("yes")){
                b1.cancelBooking();
            }
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        finally {
            System.out.println("Thanks for using our application");
        }

    }
}
