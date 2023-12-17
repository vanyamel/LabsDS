package org.example;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import javax.jms.*;

public class JmsClientProducer {
    public void sendMessage(String messageText) {
        try {
            // Налаштування з'єднання
            ConnectionFactory factory = new RMQConnectionFactory();
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Створення черги
            Destination destination = session.createQueue("UniversityQueue");
            MessageProducer producer = session.createProducer(destination);

            // Створення та відправлення повідомлення
            TextMessage message = session.createTextMessage(messageText);
            producer.send(message);

            // Закриття з'єднання
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}