
package org.example;


import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class UniversityManagement extends UnicastRemoteObject implements IUniversityManagement {
    private Map<String, Group> groups = new HashMap<>();
    private Map<String, Student> students = new HashMap<>();

    protected UniversityManagement() throws RemoteException {
        super();
    }

    @Override
    public String addGroup(String groupName) throws RemoteException {
        if (groups.containsKey(groupName)) {
            return "Group already exists: " + groupName;
        }
        groups.put(groupName, new Group(groupName));
        return "Group added: " + groupName;
    }

    @Override
    public String deleteGroup(String groupName) throws RemoteException {
        if (!groups.containsKey(groupName)) {
            return "Group not found: " + groupName;
        }
        groups.remove(groupName);
        return "Group deleted: " + groupName;
    }

    @Override
    public String addStudent(String studentName, String groupName) throws RemoteException {
        if (students.containsKey(studentName)) {
            return "Student already exists: " + studentName;
        }
        if (!groups.containsKey(groupName)) {
            return "Group not found: " + groupName;
        }
        students.put(studentName, new Student(studentName, groupName));
        return "Student added: " + studentName;
    }

    @Override
    public String deleteStudent(String studentName) throws RemoteException {
        if (!students.containsKey(studentName)) {
            return "Student not found: " + studentName;
        }
        students.remove(studentName);
        return "Student deleted: " + studentName;
    }

    @Override
    public String transferStudent(String studentName, String newGroup) throws RemoteException {
        if (!students.containsKey(studentName)) {
            return "Student not found: " + studentName;
        }
        if (!groups.containsKey(newGroup)) {
            return "New group not found: " + newGroup;
        }
        Student student = students.get(studentName);
        student.setGroup(newGroup);
        return "Student transferred: " + studentName + " to " + newGroup;
    }

    @Override
    public String editStudent(String studentName, String newName) throws RemoteException {
        if (!students.containsKey(studentName)) {
            return "Student not found: " + studentName;
        }
        Student student = students.get(studentName);
        student.setName(newName);
        return "Student name changed: " + newName;
    }

    @Override
    public List<String> listStudentsByGroup(String groupName) throws RemoteException {
        List<String> studentList = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getGroup().equals(groupName)) {
                studentList.add(student.getName());
            }
        }
        return studentList;
    }

    @Override
    public List<String> listGroups() throws RemoteException {
        return new ArrayList<>(groups.keySet());
    }

    @Override
    public List<String> listAllStudents() throws RemoteException {
        List<String> allStudents = new ArrayList<>();
        for (Student student : students.values()) {
            allStudents.add(student.getName() + " - " + student.getGroup());
        }
        return allStudents;
    }

    public static void main(String[] args) {
        try {
            UniversityManagement obj = new UniversityManagement();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Java RMI registry created.");
            } catch (RemoteException e) {
                registry = LocateRegistry.getRegistry();
                System.out.println("Using existing Java RMI registry.");
            }

            String name = "UniversityManagement";
            if (registry.list().length == 0) {
                registry.bind(name, obj);
                System.out.println("Server ready");
            } else {
                System.out.println("Server already bound and ready");
            }
        } catch (AlreadyBoundException | RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

