public class Test {
    static Object lock = new Object();
    public static void main(String[] args) throws InterruptedException {

        Worker worker = new Worker();
        Thread clock = new Thread(new Worker.Clock(worker));
        clock.start();
        Thread message = new Thread(new Worker.Messenger(worker));
        message.start();


    }

}


class Worker {
    int seconds = 0;

    public void Clock() {
        synchronized (Test.lock) {
            System.out.println(seconds);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            seconds++;

            if (seconds % 5 == 0) {
                try {
                    System.out.println(seconds);
                    Test.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            Test.lock.notify();

            if (seconds % 7 == 0) {

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

    public void message() {
        synchronized (Test.lock) {
            if (seconds % 5 != 0) {
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

            if (seconds % 7 != 0) {
                try {
                    Test.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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


    static class Clock implements Runnable {
        Worker worker;

        public Clock(Worker worker) {
            this.worker = worker;
        }

        @Override
        public void run() {
            while (true) {
                worker.Clock();
            }
        }
    }

    static class Messenger implements Runnable {
        Worker worker;

        public Messenger(Worker worker) {
            this.worker = worker;
        }

        @Override
        public void run() {
            while (true) {
                worker.message();
            }
        }
    }
}