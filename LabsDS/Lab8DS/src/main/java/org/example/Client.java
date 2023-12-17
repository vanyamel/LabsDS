package org.example;

import java.io.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/*


public class Client {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public String sendMessage(String message) throws IOException {
        try {
            out.writeObject(message);
            return (String) in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection("127.0.0.1", 6666);

        String response = client.sendMessage("ADD_GROUP Math");
        System.out.println(response);

        response = client.sendMessage("ADD_STUDENT Alice Math");
        System.out.println(response);
        System.out.println(client.sendMessage("ADD_GROUP Math"));
        System.out.println(client.sendMessage("ADD_STUDENT Alice Math"));
        System.out.println(client.sendMessage("TRANSFER_STUDENT Alice Physics"));
        System.out.println(client.sendMessage("EDIT_STUDENT Alice Alicia"));
        System.out.println(client.sendMessage("LIST_STUDENTS_BY_GROUP Physics"));
        System.out.println(client.sendMessage("LIST_GROUPS"));
        System.out.println(client.sendMessage("LIST_ALL_STUDENTS"));

        client.stopConnection();
    }
}
*/




public class Client {
    private Client() {}

    public static void main(String[] args) {
        try {
            // Отримуємо реєстр RMI
            Registry registry = LocateRegistry.getRegistry(null);

            // Пошук віддаленого об'єкта за ім'ям
            IUniversityManagement stub = (IUniversityManagement) registry.lookup("UniversityManagement");

            // Виклики віддалених методів
            System.out.println(stub.addGroup("Math"));
            System.out.println(stub.addStudent("Alice", "Math"));
            System.out.println(stub.transferStudent("Alice", "Physics"));
            System.out.println(stub.editStudent("Alice", "Alicia"));
            System.out.println(stub.deleteStudent("Alicia"));
            System.out.println(stub.deleteGroup("Physics"));

            // Отримання списку студентів за групою
            List<String> studentsInMath = stub.listStudentsByGroup("Math");
            System.out.println("Students in Math: " + studentsInMath);

            // Отримання списку всіх груп
            List<String> allGroups = stub.listGroups();
            System.out.println("All groups: " + allGroups);

            // Отримання повного списку студентів
            List<String> allStudents = stub.listAllStudents();
            System.out.println("All students: " + allStudents);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}