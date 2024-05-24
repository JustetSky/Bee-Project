public class WorkerBee extends Bee implements IBehaviour {
    public WorkerBee(int x, int y, long birthTime, int birthX, int birthY, long lifetime) {
        super("Worker", x, y, "data/worker_bee50x50.png", birthTime, birthX, birthY, lifetime);
    }
    @Override
    public void loadImage(){
        setImage("data/worker_bee50x50.png");
    }
    @Override
    public void action(long timeElapsed) {
        //TODO Add worker action
    }
}