import airline.Flight.Flight;
import airline.utils.Instructions.*;
import airline.utils.Instructions.User.Exit;
import airline.utils.User.Cart;
import airline.utils.User.User;

import java.io.*;
import java.net.Socket;

/**
 * Represents the client-side of the Airport system, responsible for:
 * - Connecting to the server
 * - Sending and receiving messages
 * - Handling user authentication and commands
 */
public class AirportClient {
    private User user = null; // Stores the currently logged-in user
    private Socket socket; // Socket for client-server communication
    private ObjectOutputStream out; // Output stream to send objects to the server
    private volatile ObjectInputStream in; // Input stream to receive objects from the server

    /**
     * Initializes the client, sets up a shutdown hook, and connects to the server.
     */
    public AirportClient() {
        try {
            // Ensures the client disconnects properly when the application exits
            Runtime.getRuntime().addShutdownHook(new Thread(this::closeConnection));
        } catch (Exception e) {
            System.out.println("Error while adding shutdown hook: " + e.getMessage());
        }
        // Attempt to connect to the server
        connect("localhost", 3000);
    }

    /**
     * Connects the client to the server at the specified host and port.
     *
     * @param host The server hostname or IP address.
     * @param port The server port number.
     */
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port); // Establish connection
            out = new ObjectOutputStream(socket.getOutputStream()); // Initialize output stream
            in = new ObjectInputStream(socket.getInputStream()); // Initialize input stream

            // Start a separate thread to listen for incoming messages from the server
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            System.out.println("Error while creating client socket: " + e.getMessage());
        }
    }

    /**
     * Sends an instruction object to the server.
     *
     * @param instruction The instruction to be sent.
     */
    public void sendMessage(Instruction instruction) {
        try {
            out.writeObject(instruction); // Serialize and send the instruction
            out.flush(); // Ensure the data is sent immediately
        } catch (IOException e) {
            System.out.println("Error while sending message to server: " + e.getMessage());
        }
    }

    /**
     * Listens for messages from the server and processes them.
     * Runs in a separate thread to avoid blocking the main execution.
     */
    private void listenForMessages() {
        try {
            Object message;
            while (true) {
                message = in.readObject(); // Read incoming object from server

                // Handle different message types
                if (message instanceof User) {
                    this.user = (User) message; // Update user information
                } else if (message instanceof String || message instanceof Cart || message instanceof Flight) {
                    System.out.println(message); // Print received message to console
                } else {
                    System.out.println("Unknown message received from server.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection closed by server.");
        } finally {
            closeConnection(); // Ensure the client disconnects properly
        }
    }

    /**
     * Closes the client connection and notifies the server that the user is exiting.
     */
    public void closeConnection() {
        try {
            // Notify the server that the user is disconnecting
            sendMessage(new Exit(user));

            // Short delay to ensure the message is sent before closing the socket
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            // Close resources in order
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.out.println("Error closing client connection: " + e.getMessage());
        }
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return The User object, or null if no user is logged in.
     */
    public User getUser() {
        return user;
    }
}
