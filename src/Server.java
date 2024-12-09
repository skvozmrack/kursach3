import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, ClientHandler> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Сервер запущен...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private String username;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                registerUser();

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/quit")) {
                        disconnect();
                        break;
                    }
                    broadcastMessage(username + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }

        private void registerUser() throws IOException {
            out.println("Введите имя пользователя:");
            username = in.readLine();
            synchronized (clients) {
                if (clients.containsKey(username)) {
                    out.println("Имя пользователя уже занято. Попробуйте другое.");
                    registerUser();
                } else {
                    clients.put(username, this);
                    out.println("Регистрация успешна. Добро пожаловать!");
                    broadcastMessage(username + " присоединился к чату.");
                }
            }
        }

        private void disconnect() {
            if (username != null) {
                synchronized (clients) {
                    clients.remove(username);
                    broadcastMessage(username + " вышел из чата.");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (ClientHandler client : clients.values()) {
                    client.out.println(message);
                }
            }
        }
    }
}
