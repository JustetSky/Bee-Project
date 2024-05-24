import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.io.Serializable;

abstract class Bee implements Serializable {
    private final String beeType;
    private int x;
    private int y;
    private int birthX;
    private int birthY;
    private transient BufferedImage image;
    private transient long birthTime;
    private transient long lifetime;
    private int uniqueId;

    private static Set<Integer> generatedIds = new HashSet<>();

    public Bee(String beeType, int x, int y, String imagePath, long birthTime, int birthX, int birthY, long lifetime) {
        this.beeType = beeType;
        this.x = x;
        this.y = y;
        this.birthX = birthX;
        this.birthY = birthY;
        setImage(imagePath);
        this.birthTime = birthTime;
        this.lifetime = lifetime;
        this.uniqueId = generateUniqueId();
    }
    public void setImage(String path) {
        try {
            this.image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    String getBeeType(){
        return beeType;
    }
    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }
    private int generateUniqueId() {
        Random random = new Random();
        int id = random.nextInt(1000); //Генерация уникального идентификатора
        while (generatedIds.contains(id)) {
            id = random.nextInt(1000);
        }
        generatedIds.add(id);
        return id;
    }
    abstract void loadImage();
    public long getLifetime() {
        return lifetime;
    }
    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }
    public void increaseLifetime() {
        lifetime++;
    }
    public int getUniqueId() {
        return uniqueId;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getBirthX() {
        return birthX;
    }
    public int getBirthY() {
        return birthY;
    }
    public void setBirthTime(long birthTime){this.birthTime = birthTime;}
}

