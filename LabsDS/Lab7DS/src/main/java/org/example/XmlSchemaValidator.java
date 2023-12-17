package org.example;

import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class XmlSchemaValidator {
    private final Validator validator;

    public XmlSchemaValidator(String schemaFilePath) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(new File(schemaFilePath));
        this.validator = schema.newValidator();
    }

    public void validate(Source source) throws SAXException, IOException, IOException {
        validator.validate(source);
    }
}
