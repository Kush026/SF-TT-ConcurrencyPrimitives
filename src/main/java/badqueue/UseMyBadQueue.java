package badqueue;

class BadQueue<E> {
  private static final int CAPACITY = 10;
  private E[] data = (E[])new Object[CAPACITY];
  private int count = 0;
  private Object rendezvous = new Object();

  public void put(E e) throws InterruptedException {
    synchronized (rendezvous) {
      while (count >= CAPACITY) { // MUST USE A LOOP!
//        Thread.sleep(1); // still hold lock!!!
        // "deschedule the thread" but release the lock TEMPORARILY
        // AND GET IT BACK BEFORE CONTINUING
        // MUST Be executed on rendezvous, because of that
        // lock release/reclaim requirement
        // AND you must be locked in the first place
        rendezvous.wait();
      }
      data[count++] = e;
//      rendezvous.notify();
      rendezvous.notifyAll(); // horribly non-scalable!!!
    }
  }

  public E take() throws InterruptedException {
    synchronized (rendezvous) {
      while (count <= 0) {
        rendezvous.wait();
      }

//      rendezvous.notify(); // would work here too!
      // notified thread will not restart until it regains the lock
      E res = data[0];
      System.arraycopy(data, 1, data, 0, --count);
//      rendezvous.notify();
      rendezvous.notifyAll();
      return res;
    } // release the lock on exiting the synchronized block
  }
}

public class UseMyBadQueue {
  public static void main(String[] args) {
    final BadQueue<int[]> bqi = new BadQueue<>();

    Runnable producer = () -> {
      System.out.println("Producer starting...");
      try {
        for (int i = 0; i < 1_000; i++) {
          int[] data = new int[]{0, i}; // "transactionally invalid"
          if (i < 200) {
            Thread.sleep(1);
          }
          data[0] = i; // transactionally valid...

          if (i == 500) {
            // test the test!!!
            data[0] = -1; // should cause exactly one failure in consumer!
          }
          bqi.put(data); // "publish"
          data = null; // it's published DO NOT TOUCH the old data!
        }
      } catch (InterruptedException ie) {
        System.out.println("surprising! asked to shut down");
      }
      System.out.println("Producer ending...");
    };

    Runnable cons = () -> {
      System.out.println("Consumer started...");
      try {
        for (int i = 0; i < 1_000; i++) {
          int[] data = bqi.take();
          if (data[0] != data[1] || data[0] != i) {
            System.out.println("DATA ERROR !!!!");
          }
          if (i >= 800) {
            Thread.sleep(1); // force full queue for some period
          }
        }
      } catch (InterruptedException ie) {
        System.out.println("surprise! Consumer shutdown on request.");
      }
      System.out.println("Consumer ended...");
    };

    Thread pThread = new Thread(producer);
    Thread cThread = new Thread(cons);
    pThread.start();
    cThread.start();
    System.out.println("test started...");

//    BadQueue<Integer> bqi = new BadQueue<>();
//    bqi.put(1);
//    bqi.put(2);
//    System.out.println(bqi.take());
//    System.out.println(bqi.take());
//    bqi.put(3);
//    System.out.println(bqi.take());
  }
}
