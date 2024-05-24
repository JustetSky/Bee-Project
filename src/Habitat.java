import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;
import java.sql.*;

public class Habitat extends JPanel implements KeyListener {
    private final int width; //Ширина окна симуляции
    private final int height; //Высота окна симуляции
    private int bees; //Общее количество пчел
    private int droneBees; //Количество пчел трутней
    private int workerBees; //Количество пчел рабочих
    private static boolean showTimer = true; //Флаг показа таймера
    private static int N1 = 5; //Время в секундах, через которое рождается трутень
    private static int N2 = 2; //Время в секундах, через которое рождаются рабочие пчелы
    private static double P = 0.8; //Вероятность рождения рабочей пчелы
    private static final double K = 0.25; //Процент трутней от общего числа пчел
    private Boolean simulationRunning = false; //Флаг работы симуляции
    public ArrayList<Bee> beeList = new ArrayList<>(); //Массив пчел
    private TreeMap<Integer, Long> birthTimeTree = new TreeMap<>(); //Дерево айди и времени рождения
    public ArrayList<WorkerBeeAI> workerBeeAIThreads = new ArrayList<>(); //Массив потоков пчел рабочих
    public ArrayList<DroneBeeAI> droneBeeAIThreads = new ArrayList<>(); //Массив потоков пчел трутней
    private long simulationTime = 0; //Таймер
    private static int workerLifeTime = 5; //Время жизни пчелы рабочего
    private static int droneLifeTime = 10; //Время жизни пчелы трутня
    private static boolean sleepWorkerAI = false; //Флаг сна потока пчел рабочих
    private static boolean sleepDroneAI = false; //Флаг сна потока пчел трутней
    private boolean changePriority = false; //Флаг изменения приоритета потоков
    private static int workerAIPriority = 9; //Приоритет потока пчел рабочих
    private static int droneAIPriority = 8; //Приоритет потока пчел трутней

    private final static String userNameSQL = "postgres"; //Имя пользователя БД
    private final static String passwordSQL = "b33P4ss"; //Пароль пользователя БД
    private final static String connectionUrlSQL = "jdbc:postgresql://localhost:5432/postgres"; //Ссылка на БД

    public Habitat(int width, int height) {
        this.width = width;
        this.height = height;
        setFocusable(true);
        addKeyListener(this);
        setBounds(0, 0, width, height);
    }

    private static void saveConfigFile() {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("config.txt"))) {
            dos.writeBoolean(showTimer); //Флаг показа таймера
            dos.writeInt(N1); //Время в секундах, через которое рождается трутень
            dos.writeInt(N2); //Время в секундах, через которое рождаются рабочие пчелы
            dos.writeDouble(P); //Вероятность рождения рабочей пчелы
            dos.writeInt(workerLifeTime); //Время жизни пчелы рабочего
            dos.writeInt(droneLifeTime); //Время жизни пчелы трутня
            dos.writeBoolean(sleepWorkerAI); //Флаг сна потока пчел рабочих
            dos.writeBoolean(sleepDroneAI); //Флаг сна потока пчел трутней
            dos.writeInt(workerAIPriority); //Приоритет потока пчел рабочих
            dos.writeInt(droneAIPriority); //Приоритет потока пчел трутней
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void loadConfigFile() {
        File file = new File("config.txt");
        if (!file.exists()) //Если файла не существует - выходим
        {
            return;
        }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            Habitat.showTimer = dis.readBoolean(); //Флаг показа таймера
            Habitat.N1 = dis.readInt(); //Время в секундах, через которое рождается трутень
            Habitat.N2 = dis.readInt(); //Время в секундах, через которое рождаются рабочие пчелы
            Habitat.P = dis.readDouble(); //Вероятность рождения рабочей пчелы
            Habitat.workerLifeTime = dis.readInt(); //Время жизни пчелы рабочего
            Habitat.droneLifeTime = dis.readInt(); //Время жизни пчелы трутня
            Habitat.sleepWorkerAI = dis.readBoolean(); //Флаг сна потока пчел рабочих
            Habitat.sleepDroneAI = dis.readBoolean(); //Флаг сна потока пчел трутней
            Habitat.workerAIPriority = dis.readInt(); //Приоритет потока пчел рабочих
            Habitat.droneAIPriority = dis.readInt(); //Приоритет потока пчел трутней

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(20, 50, 1140, 520);
        g.setColor(Color.BLACK);
        for (Bee bee : beeList) {
            bee.draw(g);
        }
        g.setFont(new Font("Unispace", Font.PLAIN, 15));
        if(showTimer){
            g.drawString("Total simulation time: " + simulationTime + " sec", 20, 20);
        }
        if(!simulationRunning){
            g.drawString("Total bees: " + bees + "; Drones: " + droneBees + "; Workers: " + workerBees + ";", 20, 40);
        }
    }
    private int countBeesOfType(ArrayList<Bee> bees, String type) {
        int count = 0;
        for (Bee bee : bees) {
            if (bee.getBeeType().equals(type)) {
                count++;
            }
        }
        return count;
    }
    public void update(long simulationTime) {
        for (Bee bee : new ArrayList<>(beeList)) {
            bee.increaseLifetime();
            if(bee.getBeeType().equals("Worker") && bee.getLifetime() == workerLifeTime){
                for (WorkerBeeAI workerBeeAI : new ArrayList<>(workerBeeAIThreads)) {
                    if(workerBeeAI.getBee() == bee){
                        workerBeeAI.stop();
                        workerBeeAIThreads.remove(workerBeeAI);
                    }
                }
                beeList.remove(bee);
                birthTimeTree.remove(bee.getUniqueId());
            }
            else if(bee.getBeeType().equals("Drone") && bee.getLifetime() == droneLifeTime){
                beeList.remove(bee);
                birthTimeTree.remove(bee.getUniqueId());
                for (DroneBeeAI droneBeeAI : new ArrayList<>(droneBeeAIThreads)) {
                    if(droneBeeAI.getBee() == bee){
                        droneBeeAI.stop();
                        droneBeeAIThreads.remove(droneBeeAI);
                    }
                }
            }
            if(bee.getBeeType().equals("Worker")){
                WorkerBeeAI workerBeeAI = new WorkerBeeAI((WorkerBee) bee, 2);
                if(!sleepWorkerAI){
                    workerBeeAI.start();
                }
                else{
                    workerBeeAI.putToSleep();
                }
                workerBeeAIThreads.add(workerBeeAI);
            }
            else if(bee.getBeeType().equals("Drone")){
                DroneBeeAI droneBeeAI = new DroneBeeAI((DroneBee) bee, 5, 2);
                if(!sleepDroneAI){
                    droneBeeAI.start();
                }
                else{
                    droneBeeAI.putToSleep();
                }
                droneBeeAIThreads.add(droneBeeAI);
            }
        }
        Random random = new Random();
        int randomX = random.nextInt(20, 1110);
        int randomY = random.nextInt(50, 520);

        if ((beeList.isEmpty() || (countBeesOfType(beeList, "Drone") < K * beeList.size())) && ((simulationTime > 0) && simulationTime % N1 == 0)) {
            DroneBee newDrone = new DroneBee(randomX, randomY, simulationTime,randomX, randomY, 0);
            beeList.add(newDrone);
            birthTimeTree.put(newDrone.getUniqueId(), simulationTime);
        }
        if (random.nextDouble() < P && ((simulationTime > 0) && simulationTime % N2 == 0)) {
            WorkerBee newWorker = new WorkerBee(randomX, randomY, simulationTime,randomX, randomY, 0);
            beeList.add(newWorker);
            birthTimeTree.put(newWorker.getUniqueId(), simulationTime);
        }

        bees = beeList.size();
        droneBees = countBeesOfType(beeList, "Drone");
        workerBees = countBeesOfType(beeList, "Worker");

        if(sleepWorkerAI){
            for (WorkerBeeAI workerBeeAI : workerBeeAIThreads) {
                workerBeeAI.putToSleep();
            }
        }
        else{
            for (WorkerBeeAI workerBeeAI : workerBeeAIThreads) {
                workerBeeAI.wakeUp();
            }
        }
        if(sleepDroneAI){
            for (DroneBeeAI droneBeeAI : droneBeeAIThreads) {
                droneBeeAI.putToSleep();
            }
        }
        else{
            for (DroneBeeAI droneBeeAI : droneBeeAIThreads) {
                droneBeeAI.wakeUp();
            }
        }
        if(changePriority){
            for (WorkerBeeAI workerBeeAI : workerBeeAIThreads) {
                workerBeeAI.setThreadPriority(workerAIPriority);
            }
            for (DroneBeeAI droneBeeAI : droneBeeAIThreads) {
                droneBeeAI.setThreadPriority(droneAIPriority);
            }
            changePriority = false;
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_B) {
            startSimulation();
        } else if (e.getKeyCode() == KeyEvent.VK_E) {
            stopSimulation();
        } else if (e.getKeyCode() == KeyEvent.VK_T) {
            showTimer = !showTimer;
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public void stopSimulation() {
        for (WorkerBeeAI workerBeeAI : workerBeeAIThreads) {
            workerBeeAI.stop();
        }
        for (DroneBeeAI droneBeeAI : droneBeeAIThreads) {
            droneBeeAI.stop();
        }
        beeList.clear();
        birthTimeTree.clear();
        repaint();
        simulationRunning = false;
    }

    public void startSimulation() {
        if(!simulationRunning) simulationTime = 0;
        simulationRunning = true;
        Thread simulationThread = new Thread(() -> {
            while (simulationRunning) {
                update(simulationTime);
                try {
                    Thread.sleep(1000); //Обновление каждую секунду
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                simulationTime++;
            }
        });

        simulationThread.start();
        simulationThread.setPriority(Thread.MAX_PRIORITY);
    }
    public void saveObjects() {
        JFileChooser fileChooser = new JFileChooser();

        //Выбор файла из текущей директории проекта
        File currentDir = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(currentDir);

        //Выбор файлов с расширением *.bin
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary Files (*.bin)", "bin");
        fileChooser.setFileFilter(filter);

        //Дефолтное имя файла
        fileChooser.setSelectedFile(new File("objects.bin"));

        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".bin")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".bin");
            }

            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
                outputStream.writeObject(beeList);  //Сохранение списока пчел
                outputStream.writeObject(birthTimeTree);  //Сохранение дерева айди и времени рождения
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void loadObjects() {
        JFileChooser fileChooser = new JFileChooser();

        //Выбор файла из текущей директории проекта
        File currentDir = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(currentDir);

        //Выбор файлов с расширением *.bin
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary Files (*.bin)", "bin");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            stopSimulation();
            File selectedFile = fileChooser.getSelectedFile();

            if (selectedFile.getName().toLowerCase().endsWith(".bin")) {
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(selectedFile))) {
                    beeList = (ArrayList<Bee>) inputStream.readObject();
                    birthTimeTree = (TreeMap<Integer, Long>) inputStream.readObject();
                    for(Bee bee : beeList){
                        bee.setBirthTime(simulationTime);
                        bee.setLifetime(0);
                        bee.loadImage();
                    }
                    for (Integer key : birthTimeTree.keySet()) {
                        birthTimeTree.put(key, simulationTime);
                    }
                    System.out.println("Количество пчел после загрузки: " + beeList.size());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    int getN1(){return N1;};
    void setN1(int N1){this.N1 = N1;};
    int getN2(){return N2;};
    void setN2(int N2){this.N2 = N2;};
    double getP(){return P;};
    void setP(double P){this.P = P;};
    void setSimulationRunning(boolean simulationRunning){
        this.simulationRunning = simulationRunning;
    }
    void setWorkerLifeTime(int workerLifeTime){this.workerLifeTime = workerLifeTime;};
    void setDroneLifeTime(int droneLifeTime){this.droneLifeTime = droneLifeTime;};
    boolean getSimulationRunning(){
        return simulationRunning;
    }
    boolean getShowTimer(){
        return showTimer;
    }
    void setShowTimer(boolean showTimer){
        this.showTimer = showTimer;
    }
    int getBees(){
        return bees;
    }
    int getDroneBees(){return droneBees;}
    int getWorkerBees(){return workerBees;}
    long getSimulationTime(){return simulationTime;}
    int getWorkerLifeTime(){return workerLifeTime;};
    int getDroneLifeTime(){return droneLifeTime;};
    public TreeMap<Integer, Long> getBirthTimes() {return new TreeMap<>(birthTimeTree);}
    void setSleepWorkerAI(boolean sleepWorkerAI){this.sleepWorkerAI = sleepWorkerAI;}
    boolean getSleepWorkerAI(){return this.sleepWorkerAI;}
    void setSleepDroneAI(boolean sleepDroneAI){this.sleepDroneAI = sleepDroneAI;}
    boolean getSleepDroneAI(){return this.sleepDroneAI;}
    void setChangePriority(boolean changePriority){this.changePriority = changePriority;}
    void setWorkerAIPriority(int workerAIPriority){this.workerAIPriority = workerAIPriority;}
    int getWorkerAIPriority(){return this.workerAIPriority;}
    void setDroneAIPriority(int droneAIPriority){this.droneAIPriority = droneAIPriority;}
    int getDroneAIPriority(){return this.droneAIPriority;}
    public ArrayList getBeeList(){return new ArrayList<>(beeList);}

    //Подключение к серверу в отдельном потоке
    private void clientServerConnection(){
        new ClientThread(this).start();
    }

    public void updateBeeList(ArrayList<Bee> newBeeList) {
        beeList.addAll(newBeeList);
        for(Bee bee : beeList){
            bee.setBirthTime(simulationTime);
            bee.setLifetime(0);
            bee.loadImage();
        }
        repaint();
    }

    //Проверка и создание таблицы
    private void checkTableCreate(Connection connection, Statement statement) throws SQLException {
        ResultSet tables = null;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            tables = metaData.getTables(null, null, "bees_table", null);
            if (!tables.next()) {
                statement.executeUpdate("CREATE TABLE bees_table (" +
                        "beeType VARCHAR(10) NOT NULL," +
                        "x INTEGER NOT NULL," +
                        "y INTEGER NOT NULL," +
                        "birthX INTEGER NOT NULL," +
                        "birthY INTEGER NOT NULL)");
                System.out.println("Таблица успешно создана");
            }
        } finally {
            if (tables != null)
                tables.close();
        }
    }
    //Проверка таблицы и вывод ошибки
    private boolean checkTableError(Connection connection) throws SQLException {
        ResultSet tables = null;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            //Получение таблицы
            tables = metaData.getTables(null, null, "bees_table", null);
            if (!tables.next()) {
                return false;
            }
            return true;
        }finally {
            if(tables != null)
                tables.close();
        }
    }
    //Сохранение всех объектов
    public void saveBeesDB() throws SQLException {
        PreparedStatement preparedStatement = null;
        //Подключение к бд
        try(Connection connection = DriverManager.getConnection(connectionUrlSQL, userNameSQL, passwordSQL);
            Statement statement = connection.createStatement();){
            //Проверка таблицы на существование
            checkTableCreate(connection, statement);
            //Удаление старых данных
            statement.executeUpdate("DELETE FROM bees_table");
            if(beeList.isEmpty())
                return;
            //Вставка объектов
            preparedStatement = connection.prepareStatement("INSERT INTO " +
                    "bees_table (beeType, x, y, birthX, birthY) VALUES (?,?,?,?,?)");
            for(Bee bee: beeList){
                preparedStatement.setString(1, bee.getBeeType());
                preparedStatement.setInt(2, bee.getX());
                preparedStatement.setInt(3, bee.getY());
                preparedStatement.setInt(4, bee.getBirthX());
                preparedStatement.setInt(5, bee.getBirthY());
                preparedStatement.executeUpdate();
            }
        }finally {
            if(preparedStatement != null)
                preparedStatement.close();
        }
    }
    //Получение всех пчел
    public void loadBeesDB() throws SQLException {
        ResultSet resultSet = null;
        beeList.clear();
        //Подключение к бд
        try(Connection connection = DriverManager.getConnection(connectionUrlSQL, userNameSQL, passwordSQL);
            Statement statement = connection.createStatement();){
            //Проверка таблицы на существование
            if(!checkTableError(connection))
                return;
            resultSet = statement.executeQuery("SELECT * FROM bees_table");
            updateBeeList(generationBees(resultSet));
        }finally {
            if(resultSet != null)
                resultSet.close();
        }
    }
    //Сохранение пчел рабочих
    public void saveWorkersDB() throws SQLException {
        PreparedStatement preparedStatement = null;
        //Подключение к бд
        try(Connection connection = DriverManager.getConnection(connectionUrlSQL, userNameSQL, passwordSQL);
            Statement statement = connection.createStatement();){
            //Проверка таблицы на существование
            checkTableCreate(connection, statement);
            //Удаление старых данных
            statement.executeUpdate("DELETE FROM bees_table");
            if(beeList.isEmpty())
                return;
            //Вставка объектов
            preparedStatement = connection.prepareStatement("INSERT INTO " +
                    "bees_table (beeType, x, y, birthX, birthY) VALUES (?,?,?,?,?)");
            for(Bee bee: beeList){
                if(bee.getBeeType().equals("Worker")) {
                    preparedStatement.setString(1, bee.getBeeType());
                    preparedStatement.setInt(2, bee.getX());
                    preparedStatement.setInt(3, bee.getY());
                    preparedStatement.setInt(4, bee.getBirthX());
                    preparedStatement.setInt(5, bee.getBirthY());
                    preparedStatement.executeUpdate();
                }
            }
        }finally {
            if(preparedStatement != null)
                preparedStatement.close();
        }
    }
    //Сохранение пчел трутней
    public void saveDronesDB() throws SQLException {
        PreparedStatement preparedStatement = null;
        //Подключение к бд
        try(Connection connection = DriverManager.getConnection(connectionUrlSQL, userNameSQL, passwordSQL);
            Statement statement = connection.createStatement();){
            //Проверка таблицы на существование
            checkTableCreate(connection, statement);
            //Удаление старых данных
            statement.executeUpdate("DELETE FROM bees_table");
            if(beeList.isEmpty())
                return;
            //Вставка объектов
            preparedStatement = connection.prepareStatement("INSERT INTO " +
                    "bees_table (beeType, x, y, birthX, birthY) VALUES (?,?,?,?,?)");
            for(Bee bee: beeList){
                if(bee.getBeeType().equals("Drone")) {
                    preparedStatement.setString(1, bee.getBeeType());
                    preparedStatement.setInt(2, bee.getX());
                    preparedStatement.setInt(3, bee.getY());
                    preparedStatement.setInt(4, bee.getBirthX());
                    preparedStatement.setInt(5, bee.getBirthY());
                    preparedStatement.executeUpdate();
                }
            }
        }finally {
            if(preparedStatement != null)
                preparedStatement.close();
        }
    }
    //Получение пчел рабочих
    public void loadWorkersDB() throws SQLException {
        ResultSet resultSet = null;
        beeList.clear();
        //Подключение к бд
        try(Connection connection = DriverManager.getConnection(connectionUrlSQL, userNameSQL, passwordSQL);
            Statement statement = connection.createStatement();){
            //Проверка таблицы на существование
            if(!checkTableError(connection))
                return;
            resultSet = statement.executeQuery("SELECT * FROM bees_table WHERE beeType='Worker'");
            updateBeeList(generationBees(resultSet));
        }finally {
            if(resultSet != null)
                resultSet.close();
        }
    }
    //Получение пчел трутней
    public void loadDronesDB() throws SQLException {
        ResultSet resultSet = null;
        beeList.clear();
        //Подключение к бд
        try(Connection connection = DriverManager.getConnection(connectionUrlSQL, userNameSQL, passwordSQL);
            Statement statement = connection.createStatement();){
            //Проверка таблицы на существование
            if(!checkTableError(connection))
                return;
            resultSet = statement.executeQuery("SELECT * FROM bees_table WHERE beeType='Drone'");
            updateBeeList(generationBees(resultSet));
        }finally {
            if(resultSet != null)
                resultSet.close();
        }
    }
    //Формирование временного массива пчел
    private ArrayList<Bee> generationBees(ResultSet resultSet) throws SQLException {
        ArrayList<Bee> tempList = new ArrayList<>();
        while (resultSet.next()){
            Bee bee = null;
            String beeType = resultSet.getString("beeType");
            int x = resultSet.getInt("x");
            int y = resultSet.getInt("y");
            int birthX = resultSet.getInt("birthX");
            int birthY = resultSet.getInt("birthY");
            if(beeType.equals("Worker"))
                bee = new WorkerBee(
                        x,
                        y,
                        simulationTime,
                        birthX,
                        birthY,
                        0
                );
            else
                bee = new DroneBee(
                        x,
                        y,
                        simulationTime,
                        birthX,
                        birthY,
                        0
                );
            tempList.add(bee);
        }
        return tempList;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bee Simulation");

        int width = 1200;
        int height = 800;

        Habitat habitat = new Habitat(width, height);
        habitat.loadConfigFile(); //Загрузка конфигурационного файла
        habitat.clientServerConnection();


        habitat.setFocusable(true);
        ControlPanel controlPanel = new ControlPanel(habitat);
        controlPanel.setFocusable(false);

        Image icon = new ImageIcon("data/bee_java_icon32x32.png").getImage();
        frame.setIconImage(icon);
        frame.add(habitat, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.setJMenuBar(controlPanel.getMainMenu());

        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        habitat.requestFocusInWindow();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            habitat.saveConfigFile(); //Сохранение конфигурационного файла
        }));
    }
}
