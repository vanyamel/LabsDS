package org.example;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import javax.jms.*;

public class JmsServerConsumer {
    public void receiveMessage() {
        try {
            // Налаштування з'єднання
            ConnectionFactory factory = new RMQConnectionFactory();
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Створення черги
            Destination destination = session.createQueue("UniversityQueue");
            MessageConsumer consumer = session.createConsumer(destination);

            // Очікування повідомлення
            Message message = consumer.receive();

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received: " + textMessage.getText());
                // Обробка повідомлення
            }

            // Закриття з'єднання
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}