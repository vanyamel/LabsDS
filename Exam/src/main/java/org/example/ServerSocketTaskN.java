package org.example;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSocketTaskN {
    private ServerSocket serverSocket;
    private static Map<String, Event> events = new ConcurrentHashMap<>();

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                Object message;
                while ((message = in.readObject()) != null) {
                    Object response = processCommand(message);
                    out.writeObject(response);
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private Object processCommand(Object message) {
            if (message instanceof String) {
                String command = (String) message;
                if (command.startsWith("GET_EVENT")) {
                    String title = command.substring("GET_EVENT ".length());
                    Event event = events.get(title);
                    return event != null ? event : "Event not found";
                } else if (command.equals("GET_ALL_EVENTS")) {
                    return new ArrayList<>(events.values());
                } else if (command.startsWith("DELETE_EVENT")) {
                    String title = command.substring("DELETE_EVENT ".length());
                    return events.remove(title) != null ? "Event deleted successfully" : "Event not found";
                }
            } else if (message instanceof Event) {
                Event event = (Event) message;
                events.put(event.getTitle(), event);
                return "Event added/updated successfully";
            }
            return "Invalid command or data";
        }
    }

    public static void main(String[] args) {
        int port = 6666;
        ServerSocketTaskN server = new ServerSocketTaskN();
        try {
            server.start(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot start server on port " + port);
        }
    }
}

