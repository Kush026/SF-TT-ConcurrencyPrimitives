package simple;

class MyTask implements Runnable {
  int i = 0;
  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName() + " starting task");
    for (; i < 10_000; i++) {
      System.out.println(Thread.currentThread().getName() + " i is " + i);
    }
    System.out.println(Thread.currentThread().getName() + " ending task");
  }
}

public class MyRunnable1 {
  public static void main(String[] args) {
    Runnable task = new MyTask();
    Thread t1 = new Thread(task);
    Thread t2 = new Thread(task);
    System.out.println(Thread.currentThread().getName() +
        " about to start the workers");

//    t1.run(); // NOPE!!! synchronous call
    t1.start();
    t2.start();

    System.out.println(Thread.currentThread().getName() +
        " returned from starting the workers");
  }
}
