package collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScheduledService {
    private final Object tasksLock = new Object();
    private final Object runnersLock = new Object();

    private List<Task> tasks = new ArrayList<>();
    private List<Runner> runners = new ArrayList<>();

    public ScheduledService() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    synchronized (tasksLock) {
                        for (Iterator<Task> it = tasks.iterator(); it.hasNext(); ) {
                            Task task = it.next();
                            if (task.isTime()) {
                                it.remove();
                                runTask(task);
                            } else task.decrementDelay();
                        }
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }.start();
    }

    private void runTask(Task task) {
        Runner runner = findAvailableRunner();
        runner.go(task.runnable);
    }

    private Runner findAvailableRunner() {
        synchronized (runnersLock) {
            for (Runner r : runners) {
                if (r.isAvailable()) return r;
            }

            Runner r = new Runner();
            runners.add(r);
            r.start();
            return r;
        }
    }

    public void schedule(Runnable task, long delay) {
        synchronized (tasksLock) {
            tasks.add(new Task(task, delay));
        }
    }

    class Task {
        public final Runnable runnable;
        private long delay;

        Task(Runnable runnable, long delay) {
            this.runnable = runnable;
            this.delay = delay;
        }

        public void decrementDelay() {
            delay -= 1;
        }

        public boolean isTime() {
            return delay == 0;
        }
    }

    class Runner extends Thread {
        private volatile Runnable task;

        public boolean isAvailable() {
            return task == null;
        }

        public void go(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            while (true) {
                while (task == null) {
                }

                task.run();
                task = null;
            }
        }
    }

    private static ScheduledService service = new ScheduledService();

    public static void main(String[] args) {
        class LazyRunnable implements Runnable {
            private final long millis;

            LazyRunnable(long millis) {
                this.millis = millis;
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ex) {
                }

                System.out.println("   <- Finished " + Thread.currentThread().getName());
            }
        }

        service.schedule(new LazyRunnable(1000), 500);
        service.schedule(new LazyRunnable(2000), 500);
        service.schedule(new LazyRunnable(100), 2000);
    }
}
