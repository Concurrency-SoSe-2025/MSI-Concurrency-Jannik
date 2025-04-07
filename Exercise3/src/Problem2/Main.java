package Problem2;

import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://example.org/");
        Downloader downloader = new Downloader(url, "example.html");

        BlockingListener listener = new BlockingListener();
        downloader.addListener(listener);

        // Thread 1: downloader.run()
        Thread t1 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName()
                        + ": waiting a bit so t2 can grab the listener lock");
                Thread.sleep(500); // Let t2 start first
                System.out.println(Thread.currentThread().getName()
                        + ": calling downloader.run()");
                downloader.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Downloader-Thread");

        // Thread 2: locks the listener, sleeps, then calls downloader.addListener(...)
        Thread t2 = new Thread(() -> {
            synchronized (listener) {
                System.out.println(Thread.currentThread().getName()
                        + ": got the listener lock, now sleeping inside it...");
                try {
                    Thread.sleep(2000); // Force an overlap while holding 'listener'
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()
                        + ": now calling downloader.addListener(...)");
                downloader.addListener(listener);
                System.out.println(Thread.currentThread().getName()
                        + ": done addListener(...)");
            }
        }, "Listener-Thread");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("If you see this line, the program didn't deadlock!");
    }
}

class BlockingListener implements ProgressListener {
    @Override
    public synchronized void onProgress(int n) {
        System.out.println(Thread.currentThread().getName()
                + " in onProgress, n = " + n);
        // Sleep to keep the lock a bit longer, increasing chance of collision
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}