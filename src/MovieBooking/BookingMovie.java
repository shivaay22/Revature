package MovieBooking;

import java.util.concurrent.CancellationException;

public class BookingMovie {

    private String customerName;
    private String movieName;
    private int tickets;
    private boolean isCancel = false;

    static int availableSeats = 10;
    static int price = 220;

    public void bookTicket(String name, String movie, int ticketCount, double payment)
        throws InvalidCustomerNameException,
            InvalidMovieException,
            InvalidTicketCountException,
            InsufficientSeatsException,
            PaymentFailedException{

        if(name == null || name.trim().isEmpty()){
            throw new InvalidCustomerNameException("Name should be not vacant");
        }

        if(!(movie.equals("diayana")) || movie.equals("cdscsc")){
            throw new InvalidMovieException("Invalid movie choose");
        }

        if(ticketCount <= 0){
            throw new InvalidTicketCountException("Must be greater than 0");
        }

        if(ticketCount > availableSeats){
            throw new InsufficientSeatsException("Not enough seats");
        }

        double totalAmount = ticketCount * price;

        if(payment < totalAmount){
            throw new PaymentFailedException("Insufficient amount");
        }

        this.customerName = name;
        this.movieName = movie;
        this.tickets  = ticketCount;

        availableSeats -= ticketCount;

        System.out.println("Booking Successfull");
    }
    public void cancelBooking() throws CancellationNotAllowedException {

        if (isCancel) {
            throw new CancellationNotAllowedException("Booking already cancelled");
        }

        isCancel = true;
        availableSeats += tickets;

        System.out.println("Booking Cancelled Successfully");
        System.out.println("Seats Restored: " + availableSeats);
    }
}
