package airline.User;

import airline.Flight.FlightSystem;
import airline.utils.User.User;

import java.util.HashSet;
import java.util.Optional;

public abstract class UserSystem {

    private static UserSystem instance = new UserSystem(){};
    private final HashSet<User> users = new HashSet<>();

    private UserSystem() {
        users.add(new Passenger("tarookz", "1234"));
        users.add(new Passenger("ahmed", "0000"));
        users.add(new Passenger("mohamed", "1111"));
        users.add(new Passenger("ali", "2222"));

        users.add(new Admin("admin", "admin"));
        users.add(new Admin("hambozo", "9999"));
    }

    public static UserSystem getInstance() {
        return instance;
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst();
    }

    public Optional<User> signup(String username, String password) {
        if(users.stream().noneMatch(user -> user.getUsername().equals(username))) {
            Passenger passenger = new Passenger(username, password);
            users.add(passenger);
            return Optional.of(passenger);
        }
        return Optional.empty();
    }

    public Optional<User> getUser(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public HashSet<User> getUsers() {
        return users;
    }
}
