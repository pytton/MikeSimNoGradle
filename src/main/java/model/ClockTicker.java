package main.java.model;

public class ClockTicker extends Thread {

    public void run()  {

        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1 second tick");

        }
    }
}
