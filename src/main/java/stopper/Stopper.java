package stopper;

class MyWorker implements Runnable {
  public volatile boolean stop = false;


  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName() + " starting");

    while (! stop)
      ;

    System.out.println(Thread.currentThread().getName() + " stopping");
  }
}
public class Stopper {
  public static void main(String[] args) throws Throwable {
    MyWorker worker = new MyWorker();
    Thread t1 = new Thread(worker);
    System.out.println("main about to start worker");
    t1.start();
    Thread.sleep(1_000);
    System.out.println("main about to stop worker");
    worker.stop = true;
    System.out.println("worker.stop is " + worker.stop);
  }
}
