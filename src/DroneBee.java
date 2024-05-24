public class DroneBee extends Bee implements IBehaviour {
    public DroneBee(int x, int y, long birthTime, int birthX, int birthY, long lifetime) {
        super("Drone", x, y, "data/drone_bee50x50.png", birthTime, birthX, birthY, lifetime);
    }
    @Override
    public void loadImage(){
        setImage("data/drone_bee50x50.png");
    }
    @Override
    public void action(long simulationTime) {
        //TODO Add drone action
    }
}
