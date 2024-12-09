import java.io.*;
import java.net.*;

public class Client2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (SocketException e) {
                    System.out.println("Соединение с сервером закрыто.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();

            String userMessage;
            while ((userMessage = console.readLine()) != null) {
                out.println(userMessage);
                if (userMessage.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
