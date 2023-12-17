/*


package org.example;


import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private static Map<String, Group> groups = new HashMap<>();
    private static Map<String, Student> students = new HashMap<>();

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
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

                String message;
                while ((message = (String) in.readObject()) != null) {
                    String response = processCommand(message);
                    out.writeObject(response);
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private String processCommand(String message) {
            String[] parts = message.split(" ");
            String command = parts[0];
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            switch (command) {
                case "ADD_GROUP":
                    return addGroup(parts[1]);
                case "DELETE_GROUP":
                    return deleteGroup(parts[1]);
                case "ADD_STUDENT":
                    return addStudent(parts[1], parts[2]);
                case "DELETE_STUDENT":
                    return deleteStudent(parts[1]);
                case "TRANSFER_STUDENT":
                    return transferStudent(args[0], args[1]);
                case "EDIT_STUDENT":
                    return editStudent(args[0], args[1]);
                case "LIST_STUDENTS_BY_GROUP":
                    return listStudentsByGroup(args[0]);
                case "LIST_GROUPS":
                    return listGroups();
                case "LIST_ALL_STUDENTS":
                    return listAllStudents();
                default:
                    return "Unknown command";
            }
        }

        private static String addGroup(String groupName) {
            if (!groups.containsKey(groupName)) {
                groups.put(groupName, new Group(groupName));
                return "Group added: " + groupName;
            }
            return "Group already exists: " + groupName;
        }

        private static String deleteGroup(String groupName) {
            if (groups.containsKey(groupName)) {
                groups.remove(groupName);
                return "Group deleted: " + groupName;
            }
            return "Group not found: " + groupName;
        }

        private static String addStudent(String studentName, String groupName) {
            if (!students.containsKey(studentName)) {
                students.put(studentName, new Student(studentName, groupName));
                return "Student added: " + studentName;
            }
            return "Student already exists: " + studentName;
        }

        private static String deleteStudent(String studentName) {
            if (students.containsKey(studentName)) {
                students.remove(studentName);
                return "Student deleted: " + studentName;
            }
            return "Student not found: " + studentName;
        }
    }

    private static String transferStudent(String studentName, String newGroup) {
        if (students.containsKey(studentName)) {
            Student student = students.get(studentName);
            student.setGroup(newGroup);
            return "Student " + studentName + " transferred to group " + newGroup;
        }
        return "Student not found: " + studentName;
    }

    private static String editStudent(String studentName, String newName) {
        if (students.containsKey(studentName)) {
            Student student = students.get(studentName);
            student.setName(newName);
            return "Student " + studentName + " name changed to " + newName;
        }
        return "Student not found: " + studentName;
    }

    private static String listStudentsByGroup(String groupName) {
        StringBuilder sb = new StringBuilder();
        for (Student student : students.values()) {
            if (student.getGroup().equals(groupName)) {
                sb.append(student.getName()).append("\n");
            }
        }
        return sb.toString();
    }

    private static String listGroups() {
        return String.join("\n", groups.keySet());
    }

    private static String listAllStudents() {
        StringBuilder sb = new StringBuilder();
        for (Student student : students.values()) {
            sb.append(student.getName()).append(" - ").append(student.getGroup()).append("\n");
        }
        return sb.toString();
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(6666);
    }
}
 */