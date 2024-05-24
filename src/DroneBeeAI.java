class DroneBeeAI extends BaseAI implements Runnable {
    private Thread thread;
    private int N;
    private double[] speedBox = {1.2, 1.23, 1.25, 1.27, 1.3};
    private double speed = speedBox[V-1];
    private int time;
    private final Object lock = new Object();
    private boolean isAwake = true;

    public DroneBeeAI(DroneBee bee, int V, int N) {
        super(bee, V);
        this.N = N;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (lock) {
                try {
                    while (!isAwake) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            time++;
            if((int)(bee.getX() / speed) > 20 && time % N == 0){
                bee.setX((int)(bee.getX() / speed));
            }
            else if((int)(bee.getX() * speed) < 1110){
                bee.setX((int)(bee.getX() * speed));
            }
            if((int)(bee.getY() / speed) > 50 && time % N == 0){
                bee.setY((int)(bee.getY() / speed));
            }
            else if((int)(bee.getY() * speed) < 520){
                bee.setY((int)(bee.getY() * speed));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    public void start() {
        if (thread == null && isAwake) {
            thread = new Thread(this);
            thread.start();
        }
    }
    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }
    public void putToSleep() {
        synchronized (lock) {
            isAwake = false;
        }
    }
    public void wakeUp() {
        synchronized (lock) {
            isAwake = true;
            lock.notify();
        }
    }
    public void setThreadPriority(int priority) {
        if (thread != null) {
            thread.setPriority(priority);
        }
    }
    public Bee getBee(){return bee;}
}