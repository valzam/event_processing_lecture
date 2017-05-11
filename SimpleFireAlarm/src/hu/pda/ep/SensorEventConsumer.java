package hu.pda.ep;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SensorEventConsumer implements Runnable {
    
	public void run() {

		try {
		
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination
            Destination destination = session.createQueue("SENSORS");

            // Create a MessageConsumer from the Session to the Topic
            MessageConsumer consumer = session.createConsumer(destination);

            for (int i = 0; i < 100; i++) {
                // Wait for a message
                Message message = consumer.receive(10000);
                
                if (message instanceof MapMessage) {
                    MapMessage m = (MapMessage) message;
                    System.out.println("SensorEventConsumer received message of type "+ m.getString("type") +" from sensor " + m.getInt("sensor") + " at time "+ m.getLong("timestamp") +" with value " + m.getObject("value"));
                } else {
                    System.out.println("SensorEventConsumer received: " + message);
                }
            }

            // Clean up
            consumer.close();
            session.close();
            connection.close();
		}
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
		}
	}

}
