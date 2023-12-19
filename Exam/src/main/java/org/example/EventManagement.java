package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EventManagement extends Remote {
    void addEvent(Event event) throws RemoteException;
    Event getEvent(String title) throws RemoteException;
    List<Event> getAllEvents() throws RemoteException;
    void deleteEvent(String title) throws RemoteException;
}

