package airline.utils.Flight;

import airline.User.Passenger;

public interface PassengerServices {
    boolean bookFlight(String id, Passenger passenger, int numTickets);
    boolean cancelFlight(String id, Passenger passenger);
    boolean modifyBooking(String id, Passenger passenger, int newNumTickets);
}
