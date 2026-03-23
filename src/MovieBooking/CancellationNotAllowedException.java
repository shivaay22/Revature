package MovieBooking;

class CancellationNotAllowedException extends Exception {
    public CancellationNotAllowedException(String msg) {
        super(msg);
    }
}
