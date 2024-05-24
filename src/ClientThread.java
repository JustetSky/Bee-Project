import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//Поток обработки клиента
public class ClientThread extends Thread{
    private Habitat habitat;
    private static final int SERVER_PORT = 3333;
    public static List<String> clientsList = new LinkedList<>();
    //Порт текущего клиента
    public static int clientPort;
    //Потоки для работы с сервером
    public static ObjectInputStream inputStream;
    public static ObjectOutputStream outputStream;
    public ClientThread(Habitat habitat) {
        this.habitat = habitat;
    }
    @Override
    public void run() {
        try(Socket socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);){
            //Инициализация потоков
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Соединение с сервером установлено у клиента - " + socket);
            clientPort = socket.getLocalPort();
            //Чтение списка клиентов
            clientsList = (LinkedList<String>) inputStream.readObject();
            System.out.println("Клиенты - " + clientsList);
            //Бесконечный цикл для постоянной обработки поступаемых данных
            while (true){
                //Чтение данных
                Object tempData = inputStream.readObject();
                //Обработка списка клиентов
                if(tempData instanceof LinkedList) {
                    if (tempData != clientsList) {
                        clientsList = (LinkedList<String>) tempData;
                        System.out.println("Список клиентов обновлен - " + clientsList);
                    }
                }
                //Обработка запроса
                if(tempData instanceof String && ((String)tempData).equals("request")){
                    // Обновляем список пчел на стороне клиента
                    ArrayList<Bee> bees = (ArrayList<Bee>)inputStream.readObject();
                    int N = 2;
                    //Выбираем N рандомных пчел
                    if (bees.size() > N) {
                        ArrayList<Bee> randomBees = new ArrayList<>();
                        Random random = new Random();
                        for (int i = 0; i < N; i++) {
                            int randomIndex = random.nextInt(bees.size());
                            randomBees.add(bees.get(randomIndex));
                        }
                        bees = randomBees;
                    }
                    System.out.println("Пчелы получены - " + bees);
                    habitat.updateBeeList(bees);
                }
                Thread.sleep(100);
            }
        }catch (Exception e){

        }
    }
}
