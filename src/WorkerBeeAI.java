class WorkerBeeAI extends BaseAI implements Runnable {
    private Thread thread;
    private double[] speedBox = {1.1, 1.13, 1.15, 1.17, 1.2};
    private double speed = speedBox[V-1];
    int targetX = 20;
    int targetY = 50;
    int maxTargetX = bee.getBirthX();
    int maxTargetY = bee.getBirthY();
    private final Object lock = new Object();
    private boolean isAwake = true;
    private boolean flag = true;

    public WorkerBeeAI(WorkerBee bee, int V) {
        super(bee, V);
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
            if (flag) {
                if ((int)(bee.getX() / speed) <= targetX && (int)(bee.getY() / speed) <= targetY){
                    flag = false;
                    bee.setX((int)(bee.getX() * speed));
                    bee.setY((int)(bee.getY() * speed));
                }
                else{
                    if ((int)(bee.getX() / speed) > targetX) bee.setX((int)(bee.getX() / speed));
                    if ((int)(bee.getY() / speed) > targetY) bee.setY((int)(bee.getY() / speed));
                }
            }
            else {
                if ((int)(bee.getX() * speed) >= maxTargetX && (int)(bee.getY() * speed) >= maxTargetY){
                    flag = true;
                    bee.setX((int)(bee.getX() / speed));
                    bee.setY((int)(bee.getY() / speed));
                }
                else{
                    if ((int)(bee.getX() * speed) < maxTargetX) bee.setX((int)(bee.getX() * speed));
                    if ((int)(bee.getY() * speed) < maxTargetY) bee.setY((int)(bee.getY() * speed));
                }
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