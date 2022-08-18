package counter;

class MyCounter implements Runnable {
  private Object rendezvous = new Object();
  public /*volatile*/ long count = 0;
  @Override
  public void run() {
    for (int i = 0; i < 50_000_000; i++) {
      synchronized (this.rendezvous) {
        count++;
      }
    }
  }
}

public class Counter {
  public static void main(String[] args) throws Throwable {
    MyCounter mc = new MyCounter();
//    mc.run();
    Thread t1 = new Thread(mc);
    t1.start();
    Thread t2 = new Thread(mc);
    t2.start();
//    Thread.sleep(2_000);
    t1.join();
    t2.join();
    System.out.println(mc.count);
  }
}
