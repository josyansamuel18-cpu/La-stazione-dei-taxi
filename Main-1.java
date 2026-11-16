
class MySemaphore {

    private int permits;

    MySemaphore(int n) {
        permits = n;
    }

    public synchronized void acquire()
            throws InterruptedException {

        while (permits == 0) {
            wait();
        }

        permits--;
    }

    public synchronized void release() {
        permits++;
        notifyAll();
    }
}

class Passenger extends Thread {

    private int id;
    private int rounds;

    Passenger(int id, int rounds) {
        this.id = id;
        this.rounds = rounds;
    }

    public void run() {

        try {

            for (int i = 0; i < rounds; i++) {

                Main.pool.acquire();

                Main.mutex.acquire();

                Main.busy++;

                if (Main.busy > Main.maxBusy) {
                    Main.maxBusy = Main.busy;
                }

                Main.uses[id - 1]++;
                Main.total++;

                Main.mutex.release();

                Thread.sleep(20);

                Main.mutex.acquire();

                Main.busy--;

                Main.mutex.release();

                Main.pool.release();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {

    static MySemaphore pool = new MySemaphore(3);
    static MySemaphore mutex = new MySemaphore(1);

    static int busy = 0;
    static int maxBusy = 0;
    static int total = 0;

    static int[] uses = new int[7];

    public static void main(String[] args)
            throws InterruptedException {

        int[] rounds = {2, 3, 4, 2, 5, 3, 2};

        Passenger[] passengers = new Passenger[7];

        for (int i = 0; i < 7; i++) {

            passengers[i] = new Passenger(i + 1, rounds[i]);

            passengers[i].start();
        }

        for (int i = 0; i < 7; i++) {

            passengers[i].join();
        }

        System.out.println("Total uses: " + total);

        for (int i = 0; i < 7; i++) {

            System.out.println(
                "Passenger " + (i + 1) + ": "
                + uses[i] + " uses"
            );
        }

        System.out.println(
            "Most in use at the same time: " + maxBusy
        );
    }
}
