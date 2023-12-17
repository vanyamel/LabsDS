package org.example;
import javax.xml.transform.dom.DOMSource;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) {
        try {
            String schemaFilePath = "src/main/resources/groups.xsd";
            String xmlFilePath = "src/main/resources/groups.xml";
            XmlSchemaValidator validator = new XmlSchemaValidator(schemaFilePath);

            XmlCrudOperations crudOperations = new XmlCrudOperations(xmlFilePath);

            String newGroupId = crudOperations.createGroup("Group C");

            crudOperations.addStudentToGroup(newGroupId, "Mary", "Maryenko");

            crudOperations.updateStudentName("student2", "Alex", "Alexenko");

           // crudOperations.deleteStudent("student3");
            crudOperations.saveChanges();

            validator.validate(new DOMSource(crudOperations.getDocument()));
            System.out.println("XML документ успішно валідовано та оновлено!");

        } catch (Exception e) {
            System.out.println("Виникла помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}