package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IUniversityManagement extends Remote {
    String addGroup(String groupName) throws RemoteException;
    String deleteGroup(String groupName) throws RemoteException;
    String addStudent(String studentName, String groupName) throws RemoteException;
    String deleteStudent(String studentName) throws RemoteException;
    String transferStudent(String studentName, String newGroup) throws RemoteException;
    String editStudent(String studentName, String newName) throws RemoteException;
    List<String> listStudentsByGroup(String groupName) throws RemoteException;
    List<String> listGroups() throws RemoteException;
    List<String> listAllStudents() throws RemoteException;
}
