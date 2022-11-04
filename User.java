import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class User extends Thread{
    //Attributes
    private ServerSocket ss;
    private Socket accepter;
    private DataInputStream receive;
    private DataOutputStream send;
    private String inputString = "";

    //Constructor
    public User(int port) {
        try {
            ss = new ServerSocket(port);
            accepter = ss.accept();
            receive = new DataInputStream(accepter.getInputStream());
            send = new DataOutputStream(accepter.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.start();
    }

    public void close() {
        try {
            ss.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(){
        try {
            while (true) {
                while(receive.available()==0){
                    //Waiting
                }
                inputString = receive.readUTF();
                System.out.println(inputString+" received by class");
            }
        } catch (IOException ignored) {

        }
    }

    public void sendToUser(String outputString){
        try {
            send.writeUTF(outputString);
            send.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getInputString(){
        return inputString;
    }


}


    /*public void run() {

            System.out.println("Server created");
            String inputString = "";
            while (inputString != "exitcode") {
                inputString = receive.readUTF();
                String outputString = inputString;
                System.out.println(outputString);
                send.writeUTF(outputString);
                send.flush();
            }
            ss.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    */

