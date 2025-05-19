import jdk.net.Sockets;
import java.awt.image.DataBufferDouble;
import java.io.*;
import java.net.*;
import java.util.*;
import java.io.FileWriter;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Server {
    private ServerSocket serverSocket;
    private String filename;
    // Create Server object
    public Server(ServerSocket serverSocket, String filename) throws IOException {
        this.serverSocket = serverSocket;
        this.filename = filename;
    }
    // Get current time in expected format
    public static String time() {
        LocalDateTime get = LocalDateTime.now();
        DateTimeFormatter reformat = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        return get.format(reformat) + ".txt";
    }
    public void startServer() throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                // Accept new socket into server socket
                Socket socket = serverSocket.accept();
                // Create new ClientHandler object
                ClientHandler clientHandler = new ClientHandler(socket, filename);
                // Runs the ClientHandler as a thread
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (Exception e) {
            System.out.println("Server is closed");
            closeServer();
        }
    }
    // Establish a thread to detect the call to stop server
    public void stopper() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Loop until keyword to stop server is detected, then close the server
                Scanner scan = new Scanner(System.in);
                while (!scan.nextLine().equals("STOP")) {
                    continue;
                }
                closeServer();
            }
        }).start();
    }
    public void closeServer() {
        try {
            serverSocket.close();
        }
        catch (Exception e) {
            System.out.println("Unable to close server");
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1005);
        Server server = new Server(serverSocket, time());
        // Tells user how to stop the server
        System.out.println("Type \"STOP\" to stop the server");
        server.stopper();
        server.startServer();
    }
}
