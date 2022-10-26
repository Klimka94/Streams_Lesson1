public class Test {
    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        Worker worker = new Worker();
        Thread clocker = new Thread(new Clock(worker));
        Thread messenger = new Thread(new Messanger(worker));
        clocker.start();
        messenger.start();
    }
}

class Worker {
    int seconds = 0;

    public void clock() {
        synchronized (Test.lock) {
            System.out.println(seconds);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            seconds++;

            while (seconds % 5 == 0) {
                try {
                    System.out.println(seconds);
                    Test.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Test.lock.notify();
            while (seconds % 7 == 0){
                try {
                    System.out.println(seconds);
                    Test.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Test.lock.notify();
        }
    }

    public void fiveSecMessage() {
        synchronized (Test.lock) {
            while (seconds % 5 != 0) {
                try {
                    Test.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            seconds++;
            System.out.println("every-5-sec-message");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Test.lock.notify();
        }
    }

    public void sevenSecMessage() {
        synchronized (Test.lock) {
            while (seconds % 7 != 0) {
                try {
                    Test.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                seconds++;
                System.out.println("every-7-sec-message");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Test.lock.notify();
            }
        }
    }
}


class Clock implements Runnable {
    Worker worker;

    public Clock(Worker worker) {
        this.worker = worker;
    }

    @Override
    public void run() {
        while (true) {
            worker.clock();
        }
    }
}

class Messanger implements Runnable {
    Worker worker;

    public Messanger(Worker worker) {
        this.worker = worker;
    }

    @Override
    public void run() {
        while (true) {
            worker.fiveSecMessage();
            worker.sevenSecMessage();
        }
    }
}