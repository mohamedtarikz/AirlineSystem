package airline.Flight;
import airline.utils.Flight.FlightSpecs;
import airline.utils.Flight.FlightType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Flight implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String id;
    private FlightSpecs flightSpecs;
    private Integer availableSeats;
    private final FlightType type;

    public Flight(String id, FlightSpecs flightSpecs) {
        this.id = id;
        this.flightSpecs = flightSpecs;
        this.availableSeats = flightSpecs.getSeats();
        if(flightSpecs.getDeparture().country().equals(flightSpecs.getDestination().country())) {
            this.type = FlightType.DOMESTIC;
        } else {
            this.type = FlightType.INTERNATIONAL;
        }
    }

    public String getId() {
        return id;
    }

    public FlightSpecs getFlightSpecs() {
        return flightSpecs;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void updateAvailableSeats(Integer seats) {
        this.availableSeats += seats;
    }

    public FlightType getType() {
        return type;
    }

    @Override
    public String toString() {
        return  "Flight Details: \n" +
                "ID: '" + id + "'" +
                "\nDeparture: " + flightSpecs.getDeparture() +
                "\nDestination: " + flightSpecs.getDestination() +
                "\nDeparture Time: " + flightSpecs.getDepartureTime() +
                "\nTotal Seats: " + flightSpecs.getSeats() +
                "\nAvailable Seats: " + availableSeats +
                "\nTicket Price: " + flightSpecs.getPrice() +
                "\n===========================\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Flight flight = (Flight) object;
        return id.equals(flight.id);
    }

}
