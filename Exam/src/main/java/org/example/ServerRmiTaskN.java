package org.example;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRmiTaskN extends UnicastRemoteObject implements EventManagement {
    private static final Map<String, Event> events = new ConcurrentHashMap<>();

    public ServerRmiTaskN() throws RemoteException {
        super();
    }

    @Override
    public void addEvent(Event event) throws RemoteException {
        events.put(event.getTitle(), event);
    }

    @Override
    public Event getEvent(String title) throws RemoteException {
        return events.get(title);
    }

    @Override
    public List<Event> getAllEvents() throws RemoteException {
        return new ArrayList<>(events.values());
    }

    @Override
    public void deleteEvent(String title) throws RemoteException {
        events.remove(title);
    }

    public static void main(String[] args) {
        try {
            ServerRmiTaskN server = new ServerRmiTaskN();
            Registry registry;

            registry = LocateRegistry.createRegistry(1099);
            registry.bind("EventManagement", server);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

