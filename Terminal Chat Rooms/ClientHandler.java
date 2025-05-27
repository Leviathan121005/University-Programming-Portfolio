import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<String> chatHistory = new ArrayList<String>();
    private Socket socket;
    private String clientUsername;
    private BufferedWriter writer;
    private BufferedReader reader;
    private BufferedWriter fileWriter;
    private int count;
    // Create ClientHandler object
    public ClientHandler(Socket socket,String filename) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientUsername = reader.readLine();
        // Create writer to write into .txt file
        this.fileWriter = new BufferedWriter(new FileWriter("chatStorage/" + filename, true));
        // Counts the amount of message sent by Client
        this.count = 0;
        clientHandlers.add(this);
        // Announce that a new user has entered the chatroom
        System.out.println("A New User Has Connected!");
        broadcastMessage("Server: " + this.clientUsername + " has entered the chat!", true);
    }
    // Close ClientHandler object
    public void closeClientHandler() {
        removeClientHandler();
        try {
            socket.close();
            writer.close();
            reader.close();
        } catch (Exception e) {
            System.out.println("Unable to close client handler");
        }
    }
    // Remove ClientHandler from ArrayList and announce that a user has left the chat
    public void removeClientHandler() {
        clientHandlers.remove(this);
        count = 0;
        broadcastMessage("Server: " + this.clientUsername + " has left the chat!", false);
    }
    // Get current time in expected format
    public static String time() {
        LocalDateTime get = LocalDateTime.now();
        DateTimeFormatter reformat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return get.format(reformat);
    }
    // Search function, returns true if call for search function is detected
    public boolean search(String s) {
        // Detects call for search function
        if (s.length() < 8) {
            return false;
        }
        if (s.startsWith("SEARCH")) {
            // Get the keyword
            String query = s.substring(7);
            // Loop through chat history and write all chats that contains keyword
            for (String string : chatHistory) {
                if (string.contains(query)) {
                    try {
                        writer.write(string + "\n");
                        writer.flush();
                    } catch (Exception e) {
                        System.out.println("Error in adding chat history");
                        closeClientHandler();
                    }
                }
            }
            return true;
        }
        return false;
    }
    // Running ClientHandler thread
    @Override
    public void run() {
        while (socket.isConnected()) {
            String messageFromClient;
            try {
                messageFromClient = reader.readLine();
                // Stop if reader returns null
                if (messageFromClient == null) {
                    closeClientHandler();
                    break;
                }
                // Check if user calls for search function
                else if (search(messageFromClient)) {
                    continue;
                }
                // Broadcast normal messages
                else {
                    count++;
                    broadcastMessage(messageFromClient, true);
                }
            }
            catch (IOException e) {
                closeClientHandler();
                break;
            }
        }
    }
    // Broadcast message to all clients
    public void broadcastMessage(String message, boolean time) {
        String newMessage = message;
        // Check if time is required to be printed
        if (time) {
            newMessage = message + "    " + time();
        }
        // Add the amount of message sent by user if there is
        if (count > 0) {
            newMessage += " " + count + " messages(s)";
        }
        try {
            // Store message into chatHistory
            chatHistory.add(newMessage);
            // Write message into .txt file
            fileWriter.write(newMessage + "\n");
            fileWriter.flush();
            // Close the file writer after storing Client has left the chat announcement
            // Is done separately from CloseClientHandler() to prevent "IOException stream is closed" when attempting to write into file
            if (newMessage.equals("Server: " + this.clientUsername + " has left the chat!")) {
                fileWriter.close();
            }
        } catch (Exception e) {
            System.out.println("Unable to write message into file");
            closeClientHandler();
        }
        // Loop through all ClientHandler in ArrayList and write message into their writer, except for the ClientHandler that gets the message
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!(clientHandler.clientUsername.equals(clientUsername))) {
                    clientHandler.writer.write(newMessage + "\n");
                    clientHandler.writer.flush();
                }
            }
            catch (Exception e) {
                System.out.println("Unable to broadcast message");
                closeClientHandler();
            }
        }
    }
}
