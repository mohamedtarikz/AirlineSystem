package airline.utils.Instructions.Passenger;
import airline.User.Passenger;
import airline.utils.IOSystem.PassengerOptions;

public class BookFlight extends PassengerInstruction {
    private final String flightId;
    private final Integer numSeats;

    public BookFlight(String flightNumber, Integer seatNumber, Passenger passenger) {
        super(PassengerOptions.BOOK_FLIGHT, passenger);
        this.flightId = flightNumber;
        this.numSeats = seatNumber;
    }

    public String getFlightId() {
        return flightId;
    }

    public Integer getNumSeats() {
        return numSeats;
    }
}
