package hu.pda.ep;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SensorEventEPA implements Runnable {

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
            Destination source = session.createQueue("SENSORS");
            Destination sink = session.createQueue("ALARMS");
            // Create a MessageConsumer from the Session to the Topic
            MessageConsumer consumer = session.createConsumer(source);
            MessageProducer producer = session.createProducer(sink);
            
            boolean currentlySmoking = false;
            int windowCounter = 0;
            
            for (int i = 0; i < 100; i++) {
                // Wait for a message
                Message message = consumer.receive(10000);
                
                if (message instanceof MapMessage) {          
                    MapMessage incomingMessage = (MapMessage) message;
                    String curType = incomingMessage.getString("type");
               
                	if (curType.equals("smoke")) {
                    	currentlySmoking = incomingMessage.getBoolean("value");
                	}
                	
                	if (curType.equals("temperature")){

                    	if (currentlySmoking && windowCounter <= 5 && incomingMessage.getInt("value") > 50){
                            MapMessage outgoingMessage = session.createMapMessage();   	        
                            outgoingMessage.setLong("timestamp", System.nanoTime());
                            outgoingMessage.setString("type", "ALARM");
                            producer.send(outgoingMessage);            
                    	}
                    	
                    	windowCounter++;

                	}


                	if (windowCounter > 5){
                		windowCounter = 0;
                	}

                    System.out.println("SensorEventConsumer received message of type "+ 
                    		incomingMessage.getString("type") +" from sensor " + incomingMessage.getInt("sensor") + " at time "+ 
                    		incomingMessage.getLong("timestamp") +" with value " + incomingMessage.getObject("value"));

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
