import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

class ControlPanel extends JPanel {
    private Habitat habitat; //Объект класса среды симуляции
    private JButton startButton; //Кнопка запуска симуляции
    private JButton stopButton; //Кнопка остановки симуляции
    private JToggleButton showInfoToggle; //Переключатель отображения информации
    private ButtonGroup timeGroup; //Группа радиокнопок
    private JRadioButton showTimeRadioButton; //Радиокнопка показа таймера
    private JRadioButton hideTimeRadioButton; //Радиокнопка скрытия таймера
    private JButton objectsButton; //Кнопка информиции о текущих объектах
    private JTextField droneTimeField; //Поле изменениня интервала спавна трутней
    private JTextField workerTimeField; //Поле изменениня интервала спавна рабочих
    private JComboBox<Double> probabilityComboBox; //Комбобокс изменения шанса спавна рабочих
    private JTextField droneLifeTimeField; //Поле изменениня времени жизни трутней
    private JTextField workerLifeTimeField; //Поле изменениня времени жизни рабочих
    public JMenuBar mainMenu; //Основное меню //Меню времени
    private JMenuItem startButtonMenu; //Кнопка запуска симуляции из меню
    private JMenuItem stopButtonMenu; //Кнопка остановки симуляции из меню
    private JMenuItem showHideInfo; //Переключатель отображения информации из меню
    private boolean showHideInfoMenuSelected; //Флаг показа информации для кнопки из меню
    private JMenuItem showTimeMenu; //Кнопка показа таймера из меню
    private JMenuItem hideTimeMenu; //Кнопка скрытия таймера из меню
    private JMenuItem saveButtonMenu;
    private JMenuItem loadButtonMenu;
    private JMenuItem saveBeesDBMenu;
    private JMenuItem saveWorkersDBMenu;
    private JMenuItem saveDronesDBMenu;
    private JMenuItem loadBeesDBMenu;
    private JMenuItem loadWorkersDBMenu;
    private JMenuItem loadDronesDBMenu;
    private JMenuItem clientsBar;
    private JButton sleepResumeWorkerAI; //Кнопка засыпания/пробуждения потока рабочих
    private JButton sleepResumeDroneAI; //Кнопка засыпания/пробуждения потока дронов
    private JComboBox<Integer> workerAIPriorityCombobox; //Комбобокс изменения приоритета потока рабочих
    private JComboBox<Integer> droneAIPriorityCombobox; //Комбобокс изменения приоритета потока дронов
    private JButton consoleButton;
    private JButton pushButton;

    public ControlPanel(Habitat habitat) {
        this.habitat = habitat;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Стилизация кнопок
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        showInfoToggle = new JToggleButton("Show Info");
        objectsButton = new JButton("Objects");
        sleepResumeWorkerAI = new JButton("Sleep {w}");
        sleepResumeDroneAI = new JButton("Sleep {d}");
        consoleButton = new JButton("Console");
        pushButton = new JButton("Push");

        startButton.setFont(new Font("Unispace", Font.BOLD, 14));
        startButton.setBackground(new Color(0x2dce98));
        startButton.setForeground(Color.white);
        startButton.setUI(new StyledButtonUI());

        stopButton.setFont(new Font("Unispace", Font.BOLD, 14));
        stopButton.setBackground(new Color(0xffff5252, true));
        stopButton.setForeground(Color.white);
        stopButton.setUI(new StyledButtonUI());

        showInfoToggle.setFont(new Font("Unispace", Font.BOLD, 14));
        showInfoToggle.setBackground(new Color(0xffff5252));
        showInfoToggle.setForeground(Color.white);
        showInfoToggle.setUI(new StyledButtonUI());

        timeGroup = new ButtonGroup();
        showTimeRadioButton = new JRadioButton("Show Timer");
        hideTimeRadioButton = new JRadioButton("Hide Timer");

        showTimeRadioButton.setFont(new Font("Unispace", Font.BOLD, 14));
        showTimeRadioButton.setBackground(new Color(0x2dce98));
        showTimeRadioButton.setForeground(Color.white);
        showTimeRadioButton.setUI(new StyledButtonUI());

        hideTimeRadioButton.setFont(new Font("Unispace", Font.BOLD, 14));
        hideTimeRadioButton.setBackground(new Color(0xffff5252));
        hideTimeRadioButton.setForeground(Color.white);
        hideTimeRadioButton.setUI(new StyledButtonUI());

        timeGroup.add(showTimeRadioButton);
        timeGroup.add(hideTimeRadioButton);
        showTimeRadioButton.setSelected(true);

        objectsButton.setFont(new Font("Unispace", Font.BOLD, 14));
        objectsButton.setBackground(new Color(0x2dce98));
        objectsButton.setForeground(Color.white);
        objectsButton.setUI(new StyledButtonUI());

        sleepResumeWorkerAI.setFont(new Font("Unispace", Font.BOLD, 14));
        sleepResumeWorkerAI.setBackground(new Color(0xffff5252));
        sleepResumeWorkerAI.setForeground(Color.white);
        sleepResumeWorkerAI.setUI(new StyledButtonUI());

        sleepResumeDroneAI.setFont(new Font("Unispace", Font.BOLD, 14));
        sleepResumeDroneAI.setBackground(new Color(0xffff5252));
        sleepResumeDroneAI.setForeground(Color.white);
        sleepResumeDroneAI.setUI(new StyledButtonUI());

        consoleButton.setFont(new Font("Unispace", Font.BOLD, 14));
        consoleButton.setBackground(new Color(0x2D9ECE));
        consoleButton.setForeground(Color.white);
        consoleButton.setUI(new StyledButtonUI());

        pushButton.setFont(new Font("Unispace", Font.BOLD, 14));
        pushButton.setBackground(new Color(0x2D9ECE));
        pushButton.setForeground(Color.white);
        pushButton.setUI(new StyledButtonUI());

        droneTimeField = new JTextField(5);
        workerTimeField = new JTextField(5);
        droneLifeTimeField = new JTextField(5);
        workerLifeTimeField = new JTextField(5);
        probabilityComboBox = new JComboBox<>();
        workerAIPriorityCombobox = new JComboBox<>();
        droneAIPriorityCombobox = new JComboBox<>();

        droneTimeField.setFont(new Font("Unispace", Font.PLAIN, 14));
        workerTimeField.setFont(new Font("Unispace", Font.PLAIN, 14));
        droneLifeTimeField.setFont(new Font("Unispace", Font.PLAIN, 14));
        workerLifeTimeField.setFont(new Font("Unispace", Font.PLAIN, 14));
        probabilityComboBox.setFont(new Font("Unispace", Font.PLAIN, 14));
        workerAIPriorityCombobox.setFont(new Font("Unispace", Font.PLAIN, 14));
        droneAIPriorityCombobox.setFont(new Font("Unispace", Font.PLAIN, 14));

        droneTimeField.setText(String.valueOf(habitat.getN1()));
        workerTimeField.setText(String.valueOf(habitat.getN2()));
        droneLifeTimeField.setText(String.valueOf(habitat.getDroneLifeTime()));
        workerLifeTimeField.setText(String.valueOf(habitat.getWorkerLifeTime()));

        probabilityComboBox.addItem(0.1);
        probabilityComboBox.addItem(0.2);
        probabilityComboBox.addItem(0.3);
        probabilityComboBox.addItem(0.4);
        probabilityComboBox.addItem(0.5);
        probabilityComboBox.addItem(0.6);
        probabilityComboBox.addItem(0.7);
        probabilityComboBox.addItem(0.8);
        probabilityComboBox.addItem(0.9);
        probabilityComboBox.setSelectedItem(habitat.getP());

        workerAIPriorityCombobox.addItem(1);
        workerAIPriorityCombobox.addItem(2);
        workerAIPriorityCombobox.addItem(3);
        workerAIPriorityCombobox.addItem(4);
        workerAIPriorityCombobox.addItem(5);
        workerAIPriorityCombobox.addItem(6);
        workerAIPriorityCombobox.addItem(7);
        workerAIPriorityCombobox.addItem(8);
        workerAIPriorityCombobox.addItem(9);
        workerAIPriorityCombobox.setSelectedItem(habitat.getWorkerAIPriority());

        droneAIPriorityCombobox.addItem(1);
        droneAIPriorityCombobox.addItem(2);
        droneAIPriorityCombobox.addItem(3);
        droneAIPriorityCombobox.addItem(4);
        droneAIPriorityCombobox.addItem(5);
        droneAIPriorityCombobox.addItem(6);
        droneAIPriorityCombobox.addItem(7);
        droneAIPriorityCombobox.addItem(8);
        droneAIPriorityCombobox.addItem(9);
        droneAIPriorityCombobox.setSelectedItem(habitat.getDroneAIPriority());

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!habitat.getSimulationRunning()) {
                    habitat.startSimulation();
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    startButtonMenu.setEnabled(false);
                    stopButtonMenu.setEnabled(true);
                }
            }
        });
        startButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        stopButton.addActionListener(e -> {
            if (habitat.getSimulationRunning()) {
                habitat.setSimulationRunning(false);
                if (showInfoToggle.isSelected()) {

                    JTextArea textArea = new JTextArea();
                    textArea.append("Total bees: " + habitat.getBees() + "; Drones: " + habitat.getDroneBees() + "; Workers: " + habitat.getWorkerBees() + ";\n");
                    textArea.append("Total simulation time: " + habitat.getSimulationTime() + " sec");

                    textArea.setEditable(false);

                    ImageIcon icon = new ImageIcon("data/bee_java_icon64x64.png");
                    int option = JOptionPane.showConfirmDialog(this, new JScrollPane(textArea), "Simulation Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);
                    if (option == JOptionPane.OK_OPTION) {
                        habitat.stopSimulation();
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                        startButtonMenu.setEnabled(true);
                        stopButtonMenu.setEnabled(false);
                    } else {
                        habitat.setSimulationRunning(true);
                        habitat.startSimulation();
                    }

                }
                else{
                    habitat.stopSimulation();
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    startButtonMenu.setEnabled(true);
                    stopButtonMenu.setEnabled(false);
                }
            }
        });
        stopButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });
        stopButton.setEnabled(false);

        showInfoToggle.addActionListener(e -> {
            if(showInfoToggle.isSelected()){showInfoToggle.setBackground(new Color(0x2dce98));}
            else {showInfoToggle.setBackground(new Color(0xffff5252));}
        });
        showInfoToggle.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        showTimeRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.setShowTimer(true);
                habitat.repaint();
                if (showTimeRadioButton.isSelected()) {
                    showTimeRadioButton.setBackground(new Color(0x2dce98));
                    hideTimeRadioButton.setBackground(new Color(0xffff5252));
                }
            }
        });
        showTimeRadioButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        hideTimeRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.setShowTimer(false);
                habitat.repaint();
                if (hideTimeRadioButton.isSelected()) {
                    hideTimeRadioButton.setBackground(new Color(0x2dce98));
                    showTimeRadioButton.setBackground(new Color(0xffff5252));
                }
            }
        });
        hideTimeRadioButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        objectsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Alive objects");
                dialog.setModal(true);
                ImageIcon icon = new ImageIcon("data/bee_java_icon32x32.png");

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                TreeMap<Integer, Long> birthTimes = habitat.getBirthTimes();
                for (Map.Entry<Integer, Long> entry : birthTimes.entrySet()) {
                    Font font = new Font("Unispace", Font.PLAIN, 14);
                    JLabel label = new JLabel("ID: " + entry.getKey() + ", Birth time: " + entry.getValue());
                    label.setFont(font);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(label);
                }

                JScrollPane scrollPane = new JScrollPane(panel);
                dialog.add(scrollPane);

                dialog.setIconImage(icon.getImage());
                dialog.setSize(new Dimension(300, 400));
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });
        objectsButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        sleepResumeWorkerAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.setSleepWorkerAI(!habitat.getSleepWorkerAI());
                if(habitat.getSleepWorkerAI()){
                    sleepResumeWorkerAI.setText("Resume {w}");
                    sleepResumeWorkerAI.setBackground(new Color(0x2dce98));
                }
                else{
                    sleepResumeWorkerAI.setText("Sleep {w}");
                    sleepResumeWorkerAI.setBackground(new Color(0xffff5252));
                }
            }
        });
        sleepResumeWorkerAI.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        sleepResumeDroneAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.setSleepDroneAI(!habitat.getSleepDroneAI());
                if(habitat.getSleepDroneAI()){
                    sleepResumeDroneAI.setText("Resume {d}");
                    sleepResumeDroneAI.setBackground(new Color(0x2dce98));
                }
                else{
                    sleepResumeDroneAI.setText("Sleep {d}");
                    sleepResumeDroneAI.setBackground(new Color(0xffff5252));
                }
            }
        });
        sleepResumeDroneAI.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        consoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Console");

                ImageIcon icon = new ImageIcon("data/bee_java_icon64x64.png");
                frame.setIconImage(icon.getImage());

                JTextField inputField = new JTextField(50); //Поле для ввода
                ArrayList<String> commandHints = new ArrayList<>();
                commandHints.add("return alive workers");
                commandHints.add("return alive drones");

                JTextArea displayArea = new JTextArea(20, 50); //Поле консоли для отображения
                displayArea.setEditable(false);


                JScrollPane scrollPane = new JScrollPane(displayArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                frame.add(scrollPane, BorderLayout.CENTER);

                frame.add(inputField, BorderLayout.SOUTH);

                displayArea.append("╔══╗─╔═══╗╔═══╗───╔═══╗╔═══╗╔══╗─╔══╗╔═══╗╔══╗╔════╗\n" +
                        "║╔╗║─║╔══╝║╔══╝───║╔═╗║║╔═╗║║╔╗║─╚╗╔╝║╔══╝║╔═╝╚═╗╔═╝\n" +
                        "║╚╝╚╗║╚══╗║╚══╗───║╚═╝║║╚═╝║║║║║──║║─║╚══╗║║────║║──\n" +
                        "║╔═╗║║╔══╝║╔══╝───║╔══╝║╔╗╔╝║║║║╔╗║║─║╔══╝║║────║║──\n" +
                        "║╚═╝║║╚══╗║╚══╗───║║───║║║║─║╚╝║║╚╝╚╗║╚══╗║╚═╗──║║──\n" +
                        "╚═══╝╚═══╝╚═══╝───╚╝───╚╝╚╝─╚══╝╚═══╝╚═══╝╚══╝──╚╝──\n");

                inputField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String text = inputField.getText();
                        displayArea.append(text + "\n");

                        String input = inputField.getText().toLowerCase();

                        if (input.equals("return alive workers")) {
                            displayArea.append("Count of alive workers: " + Integer.toString(habitat.getWorkerBees()) + "\n");
                        } else if (input.equals("return alive drones")) {
                            displayArea.append("Count of alive drones: " + Integer.toString(habitat.getDroneBees())+ "\n");
                        } else {
                            displayArea.append("Input error. Type 'return alive workers' or 'return alive drones'.\n");
                        }
                        inputField.setText("");
                    }
                });
                inputField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char typedChar = e.getKeyChar();
                        if (typedChar != KeyEvent.VK_BACK_SPACE && typedChar != KeyEvent.VK_ENTER && typedChar != KeyEvent.VK_TAB) {
                            SwingUtilities.invokeLater(() -> {
                                String input = inputField.getText().toLowerCase();
                                for (String hint : commandHints) {
                                    if (hint.startsWith(input)) {
                                        inputField.setText(hint);
                                        inputField.setSelectionStart(input.length());
                                        inputField.setSelectionEnd(hint.length());
                                        return;
                                    }
                                }
                            });
                        }
                    }
                });

                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        consoleButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        pushButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for(String client : ClientThread.clientsList){
                        if(!client.equals(ClientThread.clientPort + "")){
                            ClientThread.outputStream.writeObject(client);
                            ClientThread.outputStream.writeObject(habitat.getBeeList());
                        }
                    }
                } catch (IOException ex) {
                    ex.getMessage();
                }
            }
        });
        pushButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                habitat.requestFocusInWindow();
            }
        });

        droneTimeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    droneTimeField.transferFocus();
                    if(Integer.parseInt(droneTimeField.getText()) < 1){
                        droneTimeField.setText(String.valueOf(habitat.getN1()));
                    }
                }
            }
        });
        droneTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if(Integer.parseInt(droneTimeField.getText()) > 1){
                        habitat.setN1(Integer.parseInt(droneTimeField.getText()));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
                habitat.requestFocusInWindow();
            }
        });

        workerTimeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    workerTimeField.transferFocus();
                    if(Integer.parseInt(workerTimeField.getText()) < 1){
                        workerTimeField.setText(String.valueOf(habitat.getN1()));
                    }
                }
            }
        });
        workerTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if(Integer.parseInt(workerTimeField.getText()) > 1){
                        habitat.setN2(Integer.parseInt(workerTimeField.getText()));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
                habitat.requestFocusInWindow();
            }
        });

        probabilityComboBox.addActionListener(e -> {
            habitat.setP((Double) probabilityComboBox.getSelectedItem());
            habitat.requestFocusInWindow();
        });

        droneLifeTimeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    droneTimeField.transferFocus();
                    if(Integer.parseInt(droneLifeTimeField.getText()) < 1){
                        droneLifeTimeField.setText(String.valueOf(habitat.getDroneLifeTime()));
                    }
                }
            }
        });
        droneLifeTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if(Integer.parseInt(droneLifeTimeField.getText()) > 1){
                        habitat.setDroneLifeTime(Integer.parseInt(droneLifeTimeField.getText()));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
                habitat.requestFocusInWindow();
            }
        });

        workerLifeTimeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    droneTimeField.transferFocus();
                    if(Integer.parseInt(workerLifeTimeField.getText()) < 1){
                        workerLifeTimeField.setText(String.valueOf(habitat.getWorkerLifeTime()));
                    }
                }
            }
        });
        workerLifeTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if(Integer.parseInt(workerLifeTimeField.getText()) > 1){
                        habitat.setWorkerLifeTime(Integer.parseInt(workerLifeTimeField.getText()));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
                habitat.requestFocusInWindow();
            }
        });

        workerAIPriorityCombobox.addActionListener(e -> {
            habitat.setWorkerAIPriority((int)workerAIPriorityCombobox.getSelectedItem());
            if((int)workerAIPriorityCombobox.getSelectedItem() == (int)droneAIPriorityCombobox.getSelectedItem()){
                if((int)workerAIPriorityCombobox.getSelectedItem() > 1){
                    droneAIPriorityCombobox.setSelectedItem((int)workerAIPriorityCombobox.getSelectedItem() - 1);
                    habitat.setDroneAIPriority((int)droneAIPriorityCombobox.getSelectedItem());
                }
                else{
                    droneAIPriorityCombobox.setSelectedItem((int)workerAIPriorityCombobox.getSelectedItem() + 1);
                    habitat.setDroneAIPriority((int)droneAIPriorityCombobox.getSelectedItem());
                }
            }
            habitat.setChangePriority(true);
            habitat.requestFocusInWindow();
        });

        droneAIPriorityCombobox.addActionListener(e -> {
            habitat.setDroneAIPriority((int)droneAIPriorityCombobox.getSelectedItem());
            if((int)droneAIPriorityCombobox.getSelectedItem() == (int)workerAIPriorityCombobox.getSelectedItem()){
                if((int)droneAIPriorityCombobox.getSelectedItem() > 1){
                    workerAIPriorityCombobox.setSelectedItem((int)droneAIPriorityCombobox.getSelectedItem() - 1);
                    habitat.setWorkerAIPriority((int)workerAIPriorityCombobox.getSelectedItem());
                }
                else{
                    workerAIPriorityCombobox.setSelectedItem((int)droneAIPriorityCombobox.getSelectedItem() + 1);
                    habitat.setWorkerAIPriority((int)workerAIPriorityCombobox.getSelectedItem());
                }
            }
            habitat.setChangePriority(true);
            habitat.requestFocusInWindow();
        });

        //Меню
        mainMenu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        saveButtonMenu = new JMenuItem("Save");
        loadButtonMenu = new JMenuItem("Load");
        fileMenu.add(saveButtonMenu);
        fileMenu.addSeparator();
        fileMenu.add(loadButtonMenu);

        JMenu settingsMenu = new JMenu("Settings");
        startButtonMenu = new JMenuItem("Start");
        stopButtonMenu = new JMenuItem("Stop");
        stopButtonMenu.setEnabled(false);
        showHideInfo = new JMenuItem("Show Info");
        settingsMenu.add(startButtonMenu);
        settingsMenu.addSeparator();
        settingsMenu.add(stopButtonMenu);
        settingsMenu.addSeparator();
        settingsMenu.add(showHideInfo);

        JMenu timeMenu = new JMenu("Time");
        showTimeMenu = new JMenuItem("Show Time");
        hideTimeMenu = new JMenuItem("Hide Time");
        showTimeMenu.setEnabled(false);

        timeMenu.add(showTimeMenu);
        timeMenu.addSeparator();
        timeMenu.add(hideTimeMenu);

        JMenu clientsMenu = new JMenu("Clients");
        clientsBar = new JMenuItem("View all");

        clientsMenu.add(clientsBar);

        clientsBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Connected Clients");
                dialog.setModal(true);
                ImageIcon icon = new ImageIcon("data/bee_java_icon32x32.png");

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                for (String client : ClientThread.clientsList) {
                    Font font = new Font("Unispace", Font.PLAIN, 14);
                    JLabel label = new JLabel("Client:  " + client);
                    label.setFont(font);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(label);
                }

                JScrollPane scrollPane = new JScrollPane(panel);
                dialog.add(scrollPane);

                dialog.setIconImage(icon.getImage());
                dialog.setSize(new Dimension(300, 400));
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });

        JMenu dataBaseMenu = new JMenu("DataBase");
        saveBeesDBMenu = new JMenuItem("Save Bees");
        saveWorkersDBMenu = new JMenuItem("Save Workers");
        saveDronesDBMenu = new JMenuItem("Save Drones");
        loadBeesDBMenu = new JMenuItem("Load Bees");
        loadWorkersDBMenu = new JMenuItem("Load Workers");
        loadDronesDBMenu = new JMenuItem("Load Drones");

        dataBaseMenu.add(saveBeesDBMenu);
        timeMenu.addSeparator();
        dataBaseMenu.add(loadBeesDBMenu);
        timeMenu.addSeparator();
        dataBaseMenu.add(saveWorkersDBMenu);
        timeMenu.addSeparator();
        dataBaseMenu.add(loadWorkersDBMenu);
        timeMenu.addSeparator();
        dataBaseMenu.add(saveDronesDBMenu);
        timeMenu.addSeparator();
        dataBaseMenu.add(loadDronesDBMenu);

        saveBeesDBMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    habitat.saveBeesDB();
                } catch (SQLException ex) {
                    System.err.println("Ошибка при работе с базой данных: " + ex.getMessage());
                }
            }
        });
        loadBeesDBMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    habitat.loadBeesDB();
                } catch (SQLException ex) {
                    System.err.println("Ошибка при работе с базой данных: " + ex.getMessage());
                }
            }
        });
        saveWorkersDBMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    habitat.saveWorkersDB();
                } catch (SQLException ex) {
                    System.err.println("Ошибка при работе с базой данных: " + ex.getMessage());
                }
            }
        });
        loadWorkersDBMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    habitat.loadWorkersDB();
                } catch (SQLException ex) {
                    System.err.println("Ошибка при работе с базой данных: " + ex.getMessage());
                }
            }
        });
        saveDronesDBMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    habitat.saveDronesDB();
                } catch (SQLException ex) {
                    System.err.println("Ошибка при работе с базой данных: " + ex.getMessage());
                }
            }
        });
        loadDronesDBMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    habitat.loadDronesDB();
                } catch (SQLException ex) {
                    System.err.println("Ошибка при работе с базой данных: " + ex.getMessage());
                }
            }
        });

        mainMenu.add(fileMenu);
        mainMenu.add(settingsMenu);
        mainMenu.add(timeMenu);
        mainMenu.add(clientsMenu);
        mainMenu.add(dataBaseMenu);

        startButtonMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!habitat.getSimulationRunning()) {
                    habitat.startSimulation();
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    startButtonMenu.setEnabled(false);
                    stopButtonMenu.setEnabled(true);
                }
            }
        });
        stopButtonMenu.addActionListener(e -> {
            if (habitat.getSimulationRunning()) {
                habitat.setSimulationRunning(false);
                if (showHideInfoMenuSelected) {

                    JTextArea textArea = new JTextArea();
                    textArea.append("Total bees: " + habitat.getBees() + "; Drones: " + habitat.getDroneBees() + "; Workers: " + habitat.getWorkerBees() + ";\n");
                    textArea.append("Total simulation time: " + habitat.getSimulationTime() + " sec");

                    textArea.setEditable(false);
                    ImageIcon icon = new ImageIcon("data/bee_java_icon32x32.png");
                    int option = JOptionPane.showConfirmDialog(null, new JScrollPane(textArea), "Simulation Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, icon);
                    if (option == JOptionPane.OK_OPTION) {
                        habitat.stopSimulation();
                        startButtonMenu.setEnabled(true);
                        stopButtonMenu.setEnabled(false);
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                    } else {
                        habitat.setSimulationRunning(true);
                        habitat.startSimulation();
                    }

                }
                else{
                    habitat.stopSimulation();
                    startButtonMenu.setEnabled(true);
                    stopButtonMenu.setEnabled(false);
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                }
            }
        });
        showHideInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHideInfoMenuSelected = !showHideInfoMenuSelected;
                showInfoToggle.setSelected(!showInfoToggle.isSelected());
                if(showInfoToggle.isSelected()){showInfoToggle.setBackground(new Color(0x2dce98));}
                else {showInfoToggle.setBackground(new Color(0xffff5252));}
                if(showHideInfoMenuSelected){
                    showHideInfo.setText("Hide Info");
                }
                else showHideInfo.setText("Show Info");
            }
        });
        saveButtonMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean check = false;
                if (habitat.getSimulationRunning()) {
                    habitat.setSimulationRunning(false);
                    check = true;
                }
                habitat.saveObjects();
                if (check) {
                    habitat.setSimulationRunning(true);
                    habitat.startSimulation();
                }
            }
        });
        loadButtonMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean check = false;
                if (habitat.getSimulationRunning()) {
                    habitat.setSimulationRunning(false);
                    check = true;
                }
                habitat.loadObjects();
                repaint();
                if (check) {
                    habitat.setSimulationRunning(true);
                    habitat.startSimulation();
                }
            }
        });
        showTimeMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.setShowTimer(true);
                habitat.repaint();
                showTimeRadioButton.setBackground(new Color(0x2dce98));
                hideTimeRadioButton.setBackground(new Color(0xffff5252));
                showTimeMenu.setEnabled(false);
                hideTimeMenu.setEnabled(true);
            }
        });
        hideTimeMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.setShowTimer(false);
                habitat.repaint();
                hideTimeRadioButton.setBackground(new Color(0x2dce98));
                showTimeRadioButton.setBackground(new Color(0xffff5252));
                showTimeMenu.setEnabled(true);
                hideTimeMenu.setEnabled(false);
            }
        });

        add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(showInfoToggle);
        buttonPanel.add(showTimeRadioButton);
        buttonPanel.add(hideTimeRadioButton);
        buttonPanel.add(objectsButton);
        buttonPanel.add(sleepResumeWorkerAI);
        buttonPanel.add(sleepResumeDroneAI);
        buttonPanel.add(consoleButton);
        buttonPanel.add(pushButton);
        add(buttonPanel);

        add(Box.createVerticalStrut(5));

        JPanel topInputPanel = new JPanel();
        topInputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Font font = new Font("Unispace", Font.PLAIN, 14);
        JLabel label1 = new JLabel("Drone birth time:");
        label1.setFont(font);
        topInputPanel.add(label1);
        topInputPanel.add(droneTimeField);
        JLabel label2 = new JLabel("Worker birth time:");
        label2.setFont(font);
        topInputPanel.add(label2);
        topInputPanel.add(workerTimeField);
        JLabel label3 = new JLabel("Drone life time:");
        label3.setFont(font);
        topInputPanel.add(label3);
        topInputPanel.add(droneLifeTimeField);
        JLabel label4 = new JLabel("Worker life time:");
        label4.setFont(font);
        topInputPanel.add(label4);
        topInputPanel.add(workerLifeTimeField);
        JLabel label5 = new JLabel("Worker birth prob:");
        label5.setFont(font);
        topInputPanel.add(label5);
        topInputPanel.add(probabilityComboBox);
        add(topInputPanel);

        JPanel bottomInputPanel = new JPanel();
        bottomInputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel label6 = new JLabel("WorkerBeeAI thread priority:");
        label6.setFont(font);
        bottomInputPanel.add(label6);
        bottomInputPanel.add(workerAIPriorityCombobox);
        JLabel label7 = new JLabel("DroneBeeAI thread priority:");
        label7.setFont(font);
        bottomInputPanel.add(label7);
        bottomInputPanel.add(droneAIPriorityCombobox);
        add(bottomInputPanel);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.BLACK);
        add(separator);

        add(Box.createVerticalGlue());
    }
    public JMenuBar getMainMenu(){
        return mainMenu;
    }
}