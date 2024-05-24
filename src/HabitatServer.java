import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

//Сервер
public class HabitatServer {
    //Мап для потоков вывода
    private static Map<Socket, ObjectOutputStream> clientsOutputs = new HashMap<>();
    //Мап для потоков ввода
    private static Map<Socket, ObjectInputStream> clientsInputs = new HashMap<>();
    private static final int SERVER_PORT = 3333;
    public static void main(String[] args) {
        //Подключение сервера
        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
            System.out.println("Сервер успешно подключен");
            //Бесконечный цикл для постоянной обработки клиентов
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новый клиент получен - " + clientSocket);
                //Добавление потоков в мапы
                clientsOutputs.put(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()));
                clientsInputs.put(clientSocket, new ObjectInputStream(clientSocket.getInputStream()));
                //Обработка каждого клиента в отдельном потоке
                new ServerThread(clientSocket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Поток для обработки клиентов
    private static class ServerThread extends Thread{
        //Сокет текущего клиента
        private Socket clientSocket;
        //Сокета клиента которому отправляются объекты
        private Socket targetSocket;

        public ServerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        //Обновление списка клиентов у всех пользователей
        private static void updateLists(){
            LinkedList<String> ports = new LinkedList<>();
            clientsOutputs.forEach((client, value) -> {
                ports.add(String.valueOf(client.getPort()));
            });
            clientsOutputs.forEach((client, value) -> {
                try {
                    value.writeObject(ports);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void run() {
            try {
                updateLists();
                //Бесконечный цикл для постоянной обработки данных
                while (true) {
                    try {
                        //Считывание передаваемых данных
                        Object data = clientsInputs.get(clientSocket).readObject();
                        //Обработка списка пчел
                        if(data instanceof String && !data.equals("request")){
                            ArrayList<Bee> bees;
                            //Получение пользователя для обмена
                            String pushPort = (String) data;
                            if((bees = (ArrayList<Bee>)clientsInputs.get(clientSocket).readObject()) != null) {
                                System.out.println("Отправка пчел клиенту - " + pushPort + " " + bees);
                                for (Map.Entry<Socket, ObjectOutputStream> entry : clientsOutputs.entrySet()) {
                                    if (pushPort.equals(entry.getKey().getPort() + "")) {
                                        targetSocket = entry.getKey();
                                        break;
                                    }
                                }
                                clientsOutputs.get(targetSocket).writeObject("request");
                                clientsOutputs.get(targetSocket).writeObject(bees);
                            }
                        }
                        Thread.sleep(100);
                    }catch (EOFException exception){
                        return;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }catch (SocketException e){
                //Обработка отключения клиента от сервера
                clientsOutputs.remove(clientSocket);
                clientsInputs.remove(clientSocket);
                System.out.println("Клиент завершил работу - " + clientSocket.getPort());
                updateLists();
            }catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}