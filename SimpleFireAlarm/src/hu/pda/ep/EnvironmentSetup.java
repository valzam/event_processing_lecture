package hu.pda.ep;

public class EnvironmentSetup {

    public static void main(String[] args) throws Exception {
    	
        thread(new SensorEventEPA());
        thread(new AlarmEventConsumer());
    	
        thread(new SmokeEventProducer(1, new boolean[] {false, false, false, true, true, true, true, false, false, false}));
        thread(new SmokeEventProducer(2, new boolean[] {true, false, false, false, false, false, false, false, true, false}));

        thread(new TemperatureEventProducer(11, new int[] {18, 18, 19, 35, 45, 55, 55, 25, 20, 20}));
        thread(new TemperatureEventProducer(12, new int[] {18, 18, 19, 17, 20, 21, 19, 19, 20, 20}));

    }
 
    public static void thread(Runnable runnable) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.start();
    }
    
}
