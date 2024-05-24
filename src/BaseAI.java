abstract class BaseAI {
    protected Bee bee;
    protected int V;

    public BaseAI(Bee bee, int V) {
        this.bee = bee;
        this.V = V;
    }
    public abstract void run();
}