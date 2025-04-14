import airline.Flight.FlightSystem;
import airline.IOSystem.OutputSystem;
import airline.User.Passenger;
import airline.User.UserSystem;
import airline.utils.Instructions.Instruction;
import airline.utils.IOSystem.*;
import airline.utils.Instructions.User.*;
import airline.utils.Instructions.Passenger.*;
import airline.utils.Instructions.Admin.*;
import airline.utils.User.User;
import airline.utils.User.UserType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AirportServer {
    private static ServerSocket serverSocket; // The server socket to accept client connections
    private static volatile List<Socket> clients = new ArrayList<>(); // Stores active client connections
    private static volatile HashMap<User, ObjectOutputStream> users = new HashMap<>(); // Maps users to their output streams
    private static volatile boolean isRunning = true; // Flag to track server state

    private static FlightSystem flightSystem = FlightSystem.getInstance(); // Singleton instance of FlightSystem
    private static UserSystem userSystem = UserSystem.getInstance(); // Singleton instance of UserSystem

    public static void main(String[] args) {
        // Add a shutdown hook to handle graceful server shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(AirportServer::shutdown));

        try {
            // Initialize the server socket on port 3000
            serverSocket = new ServerSocket(3000);
            System.out.println("Server started on port 3000");

            // Main server loop to accept client connections
            while (isRunning) {
                serverSocket.setSoTimeout(1000); // Prevents blocking indefinitely
                try {
                    // Accept new client connections
                    Socket client = serverSocket.accept();
                    System.out.println("New client connected!");

                    // Handle each client in a separate thread
                    new Thread(() -> handleClient(client)).start();
                } catch (SocketTimeoutException ignored) {
                    // Timeout occurs every second to allow graceful shutdown checks
                }
            }
        } catch (IOException e) {
            System.out.println("Error while creating server socket: " + e.getMessage());
        }
    }

    /**
     * Gracefully shuts down the server and notifies all clients.
     */
    private static void shutdown() {
        System.out.println("Shutting down server...");
        isRunning = false;

        // Notify all connected clients about the server shutdown
        users.values().forEach(outputStream -> {
            try {
                outputStream.writeObject("SERVER_SHUTDOWN");
            } catch (IOException e) {
                System.out.println("Error while closing client socket: " + e.getMessage());
            }
        });
        clients.forEach(client -> {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error while closing client socket: " + e.getMessage());
            }
        });
        clients.clear();

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error while closing server socket: " + e.getMessage());
        }
    }

    /**
     * Executes an instruction received from a client.
     *
     * @param instruction The instruction object
     * @param out The output stream to send responses
     * @param user A reference to the current user session
     */
    private static void executeInstruction(Instruction instruction, ObjectOutputStream out, AtomicReference<User> user) throws IOException {
        switch (instruction.getInstruction()) {
            // Handle signing up and in a user
            case UserOptions.SIGN_UP:
                SignUp signUp = (SignUp) instruction;

                // Check if a user is already logged in with the same username
                if (users.keySet().stream().anyMatch(user1 -> user1.getUsername().equals(signUp.getUsername()))) {
                    out.writeObject("User already logged in with this username.");
                    break;
                }

                user.set(userSystem.signup(signUp.getUsername(), signUp.getPassword()).orElse(null));
                out.writeObject(user.get());
                out.writeObject(user.get() != null ? "User signed up successfully!" : "Failed to sign up.");
                if (user.get() != null) {
                    users.put(user.get(), out);
                }
                break;

            case UserOptions.SIGN_IN:
                SignIn signIn = (SignIn) instruction;

                // Check if a user is already logged in with the same username
                if (users.keySet().stream().anyMatch(logInUser -> logInUser.getUsername().equals(signIn.getUsername()))) {
                    out.writeObject("User already logged in with this username.");
                    break;
                }

                user.set(userSystem.login(signIn.getUsername(), signIn.getPassword()).orElse(null));
                out.writeObject(user.get());
                out.writeObject(user.get() != null ? "User signed in successfully!" : "Failed to sign in.");
                if (user.get() != null) {
                    users.put(user.get(), out);
                }
                break;

            // Handle operations common between passengers and admins
            // send all flights to client
            case UserOptions.VIEW_FLIGHTS:
                OutputSystem.DisplayFlights(flightSystem, out);
                break;


            // Handle operations specific to passengers
            case PassengerOptions.BOOK_FLIGHT:
                BookFlight bookFlight = (BookFlight) instruction;
                boolean booked = flightSystem.bookFlight(bookFlight.getFlightId(),
                                                         bookFlight.getPassenger(),
                                                         bookFlight.getNumSeats());
                out.writeObject(booked ? "Flight booked successfully!" : "Failed to book flight.");
                break;

            case PassengerOptions.MODIFY_BOOKING:
                ModifyBooking modifyBooking = (ModifyBooking) instruction;
                boolean modified = flightSystem.modifyBooking(modifyBooking.getFlightId(),
                                                              modifyBooking.getPassenger(),
                                                              modifyBooking.getNewNumSeats());
                out.writeObject(modified ? "Booking modified successfully!" : "Failed to modify booking.");
                break;

            case PassengerOptions.CANCEL_FLIGHT:
                CancelFlight cancelFlight = (CancelFlight) instruction;
                boolean cancelled = flightSystem.cancelFlight(cancelFlight.getFlightId(),
                                                              cancelFlight.getPassenger());
                out.writeObject(cancelled ? "Flight cancelled successfully!" : "Failed to cancel flight.");
                break;

            case PassengerOptions.VIEW_CART:
                ViewCart viewCart = (ViewCart) instruction;
                OutputSystem.DisplayCart(out, viewCart.getPassenger());
                break;


            // Handle operations specific to admins
            case AdminOptions.ADD_FLIGHT:
                try {
                    AddFlight addFlight = (AddFlight) instruction;
                    flightSystem.addFlight(addFlight.getFlightSpecs());

                    // Notify all passengers about the new flight
                    users.forEach((RegisteredUser, outputStream) -> {
                        try {
                            outputStream.reset();
                            outputStream.writeObject("NEW FLIGHT ADDED, (" +
                                    addFlight.getFlightSpecs().getDeparture() +
                                    " -> " + addFlight.getFlightSpecs().getDestination() + ")");
                            outputStream.flush();
                        } catch (IOException e) {
                            System.out.println("Error while sending updated flight list to client: " + e.getMessage());
                        }
                    });
                    out.writeObject("Flight added successfully!");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    out.writeObject(e.getMessage());
                }
                break;

            case AdminOptions.REMOVE_FLIGHT:
                RemoveFlight removeFlight = (RemoveFlight) instruction;

                // Notify all passengers who had reservations in this flight that it got canceled
                users.forEach((RegistredUser, outputStream) -> {
                    if (RegistredUser.getRole() == UserType.ADMIN) {
                        return;
                    }
                    if (flightSystem.isRegistered(removeFlight.getFlightId(), (Passenger) RegistredUser)) {
                        try {
                            outputStream.reset();
                            outputStream.writeObject("Flight: " + removeFlight.getFlightId() + " has been canceled.");
                            outputStream.flush();
                        } catch (IOException e) {
                            System.out.println("Error while sending flight removal notification to user: " + e.getMessage());
                        }
                    }
                });

                boolean removed = flightSystem.removeFlight(removeFlight.getFlightId());
                out.writeObject(removed ? "Flight removed successfully!" : "No Such flightId.");
                break;

            case AdminOptions.MODIFY_FLIGHT:
                try {
                    ModifyFlight modifyFlight = (ModifyFlight) instruction;

                    // Notify all passengers who had reservations in this flight that it got rescheduled
                    users.forEach((RegistredUser, outputStream) -> {
                        if (RegistredUser.getRole() == UserType.ADMIN) {
                            return;
                        }
                        if (flightSystem.isRegistered(modifyFlight.getFlightId(), (Passenger) RegistredUser)) {
                            try {
                                outputStream.reset();
                                outputStream.writeObject("Flight: " + modifyFlight.getFlightId() +
                                        " has been rescheduled to: " + modifyFlight.getDepartureTime());
                                outputStream.flush();
                            } catch (IOException e) {
                                System.out.println("Error while sending flight removal notification to user: " + e.getMessage());
                            }
                        }
                    });

                    boolean modifiedFlight = flightSystem
                                            .modifyFlight(modifyFlight.getFlightId(),
                                                          modifyFlight.getDepartureTime());
                    out.writeObject(modifiedFlight ? "Flight modified successfully!" : "No Such flightId.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    out.writeObject(e.getMessage());
                }
                break;

            default:
                System.out.println("Invalid instruction received from client: " + user.get().getUsername());
        }
    }

    /**
     * Handles a client connection.
     *
     * @param client The socket representing the client connection
     */

    private static void handleClient(Socket client) {
        AtomicReference<User> user = new AtomicReference<>(null);
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            clients.add(client);

            // Set a timeout of 10 minutes for client connections
            client.setSoTimeout(600000);

            try {
                Object inputInstruction;
                while (true) {
                    inputInstruction = in.readObject();
                    if (inputInstruction instanceof Instruction instruction) {
                        executeInstruction(instruction, out, user);
                    } else {
                        System.out.println("Invalid instruction received from client: " + client.getInetAddress());
                    }
                }
            }
            // Catch exceptions and handle them
            catch (SocketTimeoutException e) {
                // Remove the user from the users list if the connection times out
                if (user.get() != null) {
                    User finalUser = user.get();
                    users.keySet()
                            .stream()
                            .filter(user1 -> user1.getUsername().equals(finalUser.getUsername()))
                            .findFirst().ifPresent(users::remove);
                }
                System.out.println("TIMEOUT - Disconnecting client: " + client.getInetAddress());
                try {
                    out.writeObject("TIMEOUT");
                } catch (Exception ignored) {}
            } catch (IOException | ClassNotFoundException ignored) {
            } catch (RuntimeException e) {
                System.out.println("Error while handling client: " + e.getMessage());
            } finally {
                // Close the client connection
                try {
                    // Remove the user from the users list if the connection ends
                    if (user.get() != null) {
                        User finalUser = user.get();
                        users.keySet()
                                .stream()
                                .filter(user1 -> user1.getUsername().equals(finalUser.getUsername()))
                                .findFirst().ifPresent(users::remove);
                    }
                    client.close();
                    clients.remove(client);
                } catch (IOException e) {
                    System.out.println("Error while closing client socket: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error while creating client socket: " + e.getMessage());
        }
    }
}