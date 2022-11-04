import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends JFrame implements Runnable{
    //Components
    private final JButton start = new JButton("Connect to server");
    private final JButton sendButton = new JButton("Send");
    private JTable messageBoard;
    private JScrollPane sp;
    private JTextArea input = new JTextArea();

    //Variables
    public DataInputStream receive;
    public DataOutputStream send;
    public String name;
    public ArrayList<String> messages = new ArrayList<>();

    //Constants
    public final String[] columns = new String[]{"Messages"};

    //Constructor
    public Client(){
        super("Connect to Server");
        configureGUI();
        eventSetup();
        this.setVisible(true);
        this.run();
    }

    public static void main(String[] args){
        Client client = new Client();
    }

    //Configure graphical interface of start of program
    private void configureGUI() {
        this.setSize(400,400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.add(start);

    }

    //configure
    private void eventSetup() {
        /*This start code does three things
        First it gets the user's name
        then connects to the server
        then it opens the messaging window
         */

        start.addActionListener(conn -> {
            name = JOptionPane.showInputDialog(this, "Please enter your name");
            connect();
            //Connection made, redoing graphics
            changeGUI();

        });

        //This code takes the value of the text area and sends it to the server
        sendButton.addActionListener(sendMsg ->{
            String message = input.getText();
            if (message==null){
                sendButton.setText("Cannot be empty");
            }
            else{
                sendButton.setText("Send");
                try {
                    send.writeUTF("["+name+"]: "+message);
                    send.flush();
                    System.out.println("["+name+"]: "+message+" sent");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            input.setText("");
        });
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(50);
                if (receive!=null){
                    while(receive.available()==0){
                        //Waiting
                    }
                    String messageRecieved = receive.readUTF();
                    System.out.println(messageRecieved);
                    messages.add(messageRecieved);
                    Object[][] data = new Object[messages.size()][1];

                    for (int i = 0; i < messages.size(); i++) {
                        String item = messages.get(i);
                        data[i][0] = item;
                    }
                    messageBoard = new JTable(data, columns);
                    messageBoard.setFillsViewportHeight(true);
                    messageBoard.setFont(new Font("Times New Roman",Font.PLAIN,18));
                    this.getContentPane().setVisible(false);
                    this.getContentPane().remove(sp);
                    sp = new JScrollPane(messageBoard);
                    this.getContentPane().add(sp,BorderLayout.CENTER);
                    this.getContentPane().setVisible(true);
                }
            } catch (IOException ignored) {

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void connect(){
        try {
            int port = Integer.parseInt(JOptionPane.showInputDialog(this, "Please enter the port number"));
            Socket clientSocket = new Socket("localhost",port);
            receive = new DataInputStream(clientSocket.getInputStream());
            send = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("connected to server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeGUI(){
        start.setVisible(false);
        this.remove(start);
        this.setLayout(new BorderLayout());
        messageBoard = new JTable(new Object[1][1],columns);
        messageBoard.setFillsViewportHeight(true);
        sp = new JScrollPane(messageBoard);
        this.add(sp, BorderLayout.CENTER);

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        input.setPreferredSize(new Dimension(325,40));
        input.setFont(new Font("Times New Roman", Font.PLAIN,18));
        p.add(input, BorderLayout.LINE_START);
        p.add(sendButton, BorderLayout.LINE_END);
        this.add(p, BorderLayout.SOUTH);


    }

}
