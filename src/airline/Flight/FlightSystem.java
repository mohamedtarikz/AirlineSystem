package airline.Flight;
import airline.User.Passenger;
import airline.utils.Flight.*;
import airline.utils.Location.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FlightSystem implements AdminServices, PassengerServices{
    private static FlightSystem instance = new FlightSystem();
    private final HashMap<Flight, HashSet<Passenger>> flights = new HashMap<>();
    private Integer domesticFlightsNum = 0;
    private Integer internationalFlightsNum = 0;

    private FlightSystem() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");

        flights.put(new Flight("D" + (++domesticFlightsNum),
                new FlightSpecs(
                        LocationParser.parseLocation("Cairo, Egypt"),
                        LocationParser.parseLocation("Alexandria, Egypt"),
                        LocalDateTime.parse("25-03-2025, 06:30", dateTimeFormat),
                        80, 1000.0)
        ), new HashSet<>());
        flights.put(new Flight("D" + (++domesticFlightsNum),
                new FlightSpecs(
                        LocationParser.parseLocation("Luxor, Egypt"),
                        LocationParser.parseLocation("Cairo, Egypt"),
                        LocalDateTime.parse("15-04-2025, 12:30", dateTimeFormat),
                        100, 1500.0)
        ), new HashSet<>());
        flights.put(new Flight("D" + (++domesticFlightsNum),
                new FlightSpecs(
                        LocationParser.parseLocation("Hurghada, Egypt"),
                        LocationParser.parseLocation("Sharm El-Sheikh, Egypt"),
                        LocalDateTime.parse("10-04-2025, 20:30", dateTimeFormat),
                        50, 1750.0)
        ), new HashSet<>());

        flights.put(new Flight("I" + (++internationalFlightsNum),
                new FlightSpecs(
                        LocationParser.parseLocation("Cairo, Egypt"),
                        LocationParser.parseLocation("Paris, France"),
                        LocalDateTime.parse("20-05-2025, 06:00", dateTimeFormat),
                        150, 5000.0)
        ), new HashSet<>());
        flights.put(new Flight("I" + (++internationalFlightsNum),
                new FlightSpecs(
                        LocationParser.parseLocation("Cairo, Egypt"),
                        LocationParser.parseLocation("New York, USA"),
                        LocalDateTime.parse("30-05-2025, 08:45", dateTimeFormat),
                        200, 8000.0)
        ), new HashSet<>());
        flights.put(new Flight("I" + (++internationalFlightsNum),
                new FlightSpecs(
                        LocationParser.parseLocation("Moscow, Russia"),
                        LocationParser.parseLocation("Alexandria, Egypt"),
                        LocalDateTime.parse("05-06-2025, 01:30", dateTimeFormat),
                        100, 7000.0)
        ), new HashSet<>());
    }

    public static FlightSystem getInstance() {
        return instance;
    }

    @Override
    public void addFlight(FlightSpecs flightSpecs) {
        if (flightSpecs.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Departure time cannot be in the past");
        }
        if (flightSpecs.getDeparture().country().equals(flightSpecs.getDestination().country())) {
            flights.put(new Flight("D" + (++domesticFlightsNum), flightSpecs),
                    new HashSet<>());
        } else {
            flights.put(new Flight("I" + (++internationalFlightsNum), flightSpecs),
                    new HashSet<>());
        }
    }
    @Override
    public boolean removeFlight(String id) {
        flights.get(flights.keySet().stream()
                .filter(flight -> flight.getId().equals(id))
                .findFirst().orElseThrow()).forEach(passenger -> passenger.getCart().cancelFlight(id));
        return flights.entrySet().removeIf(entry -> entry.getKey().getId().equals(id));
    }

    @Override
    public boolean modifyFlight(String id, LocalDateTime newDepartureTime) {
//        System.out.println(newDepartureTime);
        if(newDepartureTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Departure time cannot be in the past");
        }

        flights.keySet().stream()
                        .filter(flight -> flight.getId().equals(id))
                        .findFirst()
                        .ifPresent(flight -> {flight.getFlightSpecs().setDepartureTime(newDepartureTime);
//                                              System.out.println(flight + "\n");
                                              });
//        System.out.println(flights.keySet());

        return flights.keySet().stream().anyMatch(flight -> flight.getId().equals(id));
    }

    @Override
    public boolean bookFlight(String id, Passenger passenger, int numTickets) {
        Optional<Flight> flightOptional = flights.keySet().stream()
                .filter(flight -> flight.getId().equals(id))
                .findFirst();
        if(flightOptional.isEmpty() || numTickets > flightOptional.orElseThrow().getAvailableSeats() || numTickets < 1) {
            return false;
        }
        Flight flight = flightOptional.orElseThrow();
        flights.get(flight).add(passenger);
        flight.updateAvailableSeats(-numTickets);
        passenger.getCart().bookFlight(flight, numTickets);
        return true;
    }

    @Override
    public boolean cancelFlight(String id, Passenger passenger) {
        Optional<Flight> flightOptional = flights.keySet().stream()
                .filter(flight -> flight.getId().equals(id))
                .findFirst();
        if(flightOptional.isEmpty() || !flights.get(flightOptional.orElseThrow()).contains(passenger)) {
            return false;
        }
        Flight flight = flightOptional.orElseThrow();
        flights.get(flight).remove(passenger);
        flight.updateAvailableSeats(passenger.getCart().getNumTickets(id));
        passenger.getCart().cancelFlight(flight.getId());
        return true;
    }

    @Override
    public boolean modifyBooking(String id, Passenger passenger, int newNumTickets) {
        Optional<Flight> flightOptional = flights.keySet().stream()
                .filter(flight -> flight.getId().equals(id))
                .findFirst();
        if(flightOptional.isEmpty() ||
                newNumTickets > flightOptional.orElseThrow().getAvailableSeats() ||
                newNumTickets < 1 ||
                !flights.get(flightOptional.orElseThrow()).contains(passenger)) {

            return false;
        }
        Flight flight = flightOptional.orElseThrow();
        flight.updateAvailableSeats(passenger.getCart().getNumTickets(id) - newNumTickets);
        passenger.getCart().bookFlight(flight, newNumTickets);
        return true;
    }

    public HashSet<Flight> getFlights(FlightType type) {
        return flights.keySet().stream().filter(flight -> flight.getType().equals(type)).collect(Collectors.toCollection(HashSet::new));
    }

    public boolean isRegistered(String flightId, Passenger passenger) {
        Optional<Flight> flight = flights.keySet().stream()
                .filter(f -> f.getId().equals(flightId))
                .findFirst();
        if (flight.isEmpty()) {
            return false;
        }
        Set<Passenger> passengers = flights.get(flight.orElseThrow());
        if (passengers == null) {
            return false;
        } else {
            return passengers.stream().anyMatch(p -> p.getUsername().equals(passenger.getUsername()));
        }
    }

}
