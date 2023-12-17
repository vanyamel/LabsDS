package org.example;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.UUID;

public class XmlCrudOperations {
    private Document document;
    private String filePath;
    private int groupCounter = 0;
    private int studentCounter = 0;

    public XmlCrudOperations(String filePath) throws Exception {
        this.filePath = filePath;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new File(filePath));
        document.getDocumentElement().normalize();
    }

    public String createGroup(String groupName) {
        Element root = document.getDocumentElement();

        // Створення нової групи
        Element newGroup = document.createElement("group");
        String newGroupId = "group" + (++groupCounter);
        newGroup.setAttribute("id", newGroupId);

        // Додавання назви групи
        Element name = document.createElement("name");
        name.setTextContent(groupName);
        newGroup.appendChild(name);

        // Додавання порожнього списку студентів
        Element students = document.createElement("students");
        newGroup.appendChild(students);

        // Додавання нової групи до кореневого елементу
        root.appendChild(newGroup);

        return newGroupId;
    }

    public void addStudentToGroup(String groupId, String firstName, String lastName) {
        NodeList groups = document.getElementsByTagName("group");
        for (int i = 0; i < groups.getLength(); i++) {
            Element group = (Element) groups.item(i);
            if (group.getAttribute("id").equals(groupId)) {
                Element students = (Element) group.getElementsByTagName("students").item(0);

                Element newStudent = document.createElement("student");
                String newStudentId = "student" + (++studentCounter);
                newStudent.setAttribute("id", newStudentId);

                Element fName = document.createElement("firstName");
                fName.setTextContent(firstName);
                newStudent.appendChild(fName);

                Element lName = document.createElement("lastName");
                lName.setTextContent(lastName);
                newStudent.appendChild(lName);

                students.appendChild(newStudent);
                break;
            }
        }
    }

    public void updateStudentName(String studentId, String newFirstName, String newLastName) {
        NodeList students = document.getElementsByTagName("student");
        for (int i = 0; i < students.getLength(); i++) {
            Element student = (Element) students.item(i);
            if (student.getAttribute("id").equals(studentId)) {
                student.getElementsByTagName("firstName").item(0).setTextContent(newFirstName);
                student.getElementsByTagName("lastName").item(0).setTextContent(newLastName);
                break;
            }
        }
    }

    public void deleteStudent(String studentId) {
        NodeList students = document.getElementsByTagName("student");
        for (int i = 0; i < students.getLength(); i++) {
            Element student = (Element) students.item(i);
            if (student.getAttribute("id").equals(studentId)) {
                student.getParentNode().removeChild(student);
                break;
            }
        }
    }

    public void saveChanges() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    public Document getDocument() {
        return document;
    }
}
