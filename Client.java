import java.util.*;

import javax.swing.plaf.TreeUI;

import java.io.*;
import java.net.*;

public class Client implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    BufferedReader systemin;
    private boolean running;

    public Client(String host, int port) {
        running = true;
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Server is not Running");
            running = false;
        }
        systemin = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        //Reads messages from the server
        try {
            while (running) {
                String line = in.readLine();
                if (line.startsWith("/quit"))
                {
                    close();
                }
                else
                {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            close();
            return;
        }

    }

    public void close()
    {
        this.running = false;
        //Close all streams
        try {
            in.close();
            out.close();
            socket.close();
            systemin.close();
        } catch (IOException e) {
            //Ignore
        }
        finally{
            System.out.println("Server Disconnected");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }


    public static void main(String[] args) {
        //Create a client
        Client client = new Client("localhost", 1234);

        //Start the client
        Thread thread = new Thread(client);
        thread.start();

        //Get input from the user
        try{
            
            while (true) {
                String inputMessage = client.systemin.readLine();
                if (inputMessage == null)
                {
                    break;
                }
                if (inputMessage.startsWith("/quit")) {
                    client.sendMessage("/quit"); //Send quit to server to remove client
                    client.close();
                    break;
                } else {
                    client.sendMessage(inputMessage);
                }
            }
        }
        catch (IOException e) {
            //Ignore as will be closed by the client
        }
        finally {
            System.out.println("Client closed");
        }
    }
}
