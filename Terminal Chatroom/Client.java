import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.*;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    // Create Client object
    public Client (Socket socket, String username) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.username = username;
        this.writer.write(this.username + "\n");
        this.writer.flush();
    }
    // Close client object
    public void closeClient() {
        try {
            socket.close();
            reader.close();
            writer.close();
        }
        catch (Exception e) {
            System.out.println("Unable to close client class");
        }
    }
    // Thread to send message
    public void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()) {
                    // Get message from user terminal
                    Scanner scan = new Scanner(System.in);
                    message = scan.nextLine();
                    try {
                        // Exit the group chat
                        if (message.equals("EXIT")) {
                            closeClient();
                            break;
                        }
                        // Detect call for search function and sends the original message to ClientHandler
                        if (message.startsWith("SEARCH")) {
                            writer.write(message + "\n");
                            writer.flush();
                        }
                        // Adds username for normal messages
                        else {
                            writer.write(username + ": " +message + "\n");
                            writer.flush();
                        }
                    }
                    catch (Exception e) {
                        closeClient();
                        break;
                    }
                }
            }
        }).start();
    }
    // Thread to receive message
    public void receiveMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()) {
                    try {
                        // Read and print messages broadcasted from ClientHandler
                        message = reader.readLine();
                        System.out.println(message);
                    }
                    catch (Exception e) {
                        closeClient();
                        break;
                    }
                }
            }
        }).start();
    }
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        // Tells user how to exit the chatroom and search from chatroom
        System.out.println("Type \"EXIT\" to exit the chat room");
        System.out.println("Type \"SEARCH + keyword\" to search from chat");
        // Get username
        System.out.println("Enter your username for the group chat:");
        String username = scan.nextLine();
        // Establish server connection
        Socket socket = new Socket("localhost", 1005);
        // Create new Client object
        Client client = new Client(socket, username);
        // Establish threads to receive and send messages
        client.receiveMessage();
        client.sendMessage();
    }
}
