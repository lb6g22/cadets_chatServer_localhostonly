
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Server implements Runnable{

    //Components
    JFrame f;
    private final JButton begin = new JButton("Start server");
    private final JButton addUser = new JButton("Add User");
    private final JButton closeConn = new JButton("Close Server");

    //Variables
    public ArrayList<User> userList = new ArrayList<>();
    public HashMap<User,String> previousMessages = new HashMap<>();
    private int port = 8080;


    public static void main(String[] args){
        Server server = new Server();
        server.configureGUI();
        server.eventSetup();
        server.run();
    }

    private void configureGUI() {
        Font font = new Font("Comic Sans", Font.BOLD,18);
        begin.setFont(font);
        addUser.setFont(font);
        closeConn.setFont(font);

        f = new JFrame("Server Manager");
        f.setLayout(new GridLayout(3,1));
        f.setSize(400,400);
        f.add(begin);
        f.add(addUser);
        f.add(closeConn);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private void eventSetup() {
        addUser.setEnabled(false);
        closeConn.setEnabled(false);

        begin.addActionListener(likeAPowerSwitch -> {
            addUser.setEnabled(true);
            closeConn.setEnabled(true);
            begin.setEnabled(false);
        });

        closeConn.addActionListener(shutDown -> {
            shutdown();
        });

        addUser.addActionListener(oneMore ->{
            addUser.setText("Waiting for port number");
            //port = Integer.parseInt(JOptionPane.showInputDialog(this, "Please enter the port number"));
            User u = new User(port);
            previousMessages.put(u,"");
            userList.add(u);
            addUser.setText("Add User");
            port++;
        });

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent bye) {
                shutdown();
            }
        });
    }

    public void shutdown(){
        for (User client : userList) {
            client.close();
        }
        System.exit(0);
    }

    @Override
    public void run() {
        System.out.println("run active");
        while(true){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for(int i = 0; i < userList.size(); i++){
                //System.out.println("For active");
                User client = userList.get(i);
                String message = client.getInputString();
                String prev = previousMessages.get(client);
                //System.out.println("Is "+message+" equal to "+prev+"?");
                if(!prev.equals(message)){
                    System.out.println(message+" received");
                    for(User recipients : userList){
                        recipients.sendToUser(message);
                        System.out.println(message+" sent");
                    }
                    previousMessages.put(client,message);
                }
            }
        }

    }
}
