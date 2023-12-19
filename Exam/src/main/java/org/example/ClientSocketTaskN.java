package org.example;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class ClientSocketTaskN {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void sendEvent(Event event) throws IOException {
        out.writeObject(event);
    }

    public void sendCommand(String command) throws IOException {
        out.writeObject(command);
    }

    public Object readResponse() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientSocketTaskN client = new ClientSocketTaskN();
        client.startConnection("127.0.0.1", 6666);

        // Example: Add a new event
        Event event = new Event("Meeting", LocalDateTime.now(), "Work", false, LocalDateTime.now().plusHours(1));
        client.sendEvent(event);

        // Example: Get all events
        client.sendCommand("GET_ALL_EVENTS");
        System.out.println(client.readResponse());

        // Example: Delete an event
        client.sendCommand("DELETE_EVENT Meeting");
        System.out.println(client.readResponse());

        client.stopConnection();
    }
}
