package airline.utils.Flight;

import airline.utils.Location.Location;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class FlightSpecs implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Location departure;
    private final Location destination;
    private LocalDateTime departureTime;
    private final Integer seats;
    private final Double price;


    public FlightSpecs(Location departure, Location destination, LocalDateTime departureTime, Integer seats, Double price) {
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.seats = seats;
        this.price = price;
    }

    public Location getDeparture() {
        return departure;
    }

    public Location getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        System.out.println("Departure time was: " + this.departureTime);
        this.departureTime = departureTime;
        System.out.println("Departure time is now: " + this.departureTime);
    }

    public Integer getSeats() {
        return seats;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return  "Departure: " + departure +
                " -> Destination: " + destination +
                "\nDeparture Time: " + departureTime +
                ", Ticket Price: " + price;
    }

}
