import java.util.*;
import java.io.*;
import java.net.*;

public class Server implements Runnable {

    protected boolean running;
    private ServerSocket serverSocket;
    protected ArrayList<ClientHandler> clientHandlers;

    public Server(int port)
    {
        this.running = true;
        // Create a server socket
        this.serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            running = false;
            //Ignore
        }

        // Create a list of client handlers
        this.clientHandlers = new ArrayList<ClientHandler>();

    }

    @Override
    public void run() {
        
        while (running) {
            try{
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                clientHandlers.add(clientHandler);
            }
            catch (IOException e) {
                //Ignore
            }
        }
    }

    public void broadcast(String message, String sender) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message, sender);
        }
    }

    public void broadcast(String message, String sender, String colour) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message, sender, colour);
        }
    }

    synchronized public void shutdown() {
        this.running = false;

        //Close all client connections
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.close(false);
        }
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            //Ignore
        }
        System.out.println("Server shut down");
    }



    public static void main(String[] args) {
        Server server = new Server(1234);
        Thread serverThread = new Thread(server);
        serverThread.start();

        try (BufferedReader systemin = new BufferedReader(new InputStreamReader(System.in))) {
            //Get input from system manager
            while (true) {
                String inputMessage = systemin.readLine();
                if (inputMessage.equals("/shutdown")) {
                    server.shutdown();
                    break;
                } else {
                    server.broadcast(inputMessage, "System");
                }
            }
        }
        catch (IOException e) {
            //Ignore
        }

    }

    class ClientHandler implements Runnable {

        private Socket socket;
        private BufferedReader in;
        private BufferedWriter out;
        private String clientName;
        private String clientColour;


        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            sendMessage("What is your name?", "Server");
            try{
                clientName = in.readLine();
            }
            catch (IOException e){
                //Ignore
            }
            sendMessage("Choose a colour.", "Server");
            try{
                clientColour = in.readLine();
            }
            catch (IOException e){
                //Ignore
            }
            sendMessage("Welcome to the server, " + clientName, "Server");

            while (running) {
                try {
                    String inputMessage = in.readLine();
                    if (inputMessage == null) {
                        break;
                    }
                    if (inputMessage.startsWith("/quit")){
                        broadcast(clientName + " has left!", "Server");
                        close(true);
                    }
                    broadcast(inputMessage, clientName, clientColour);
                } catch (IOException e) {
                    //Ignore
                }
            }
        }

        public void sendMessage(String message, String sender) {
            try {
                out.write(sender + ": " + message + "\n");
                out.flush();
            } catch (IOException e) {
                //Ignore
            }
        }

        public void sendMessage(String message, String sender, String colour)
        {
            message = Colour.colouredString(message, colour);
            sendMessage(message, sender);
        }

        public void sendCommand(String command) {
            try {
                out.write("/" + command + "\n");
                out.flush();
            } catch (IOException e) {
                //Ignore
            }
        }

        public void close(boolean fromClient) {
            if (!fromClient)
            {
                sendMessage("Server has closed...", "Server");
                //Send close to client
                sendCommand("quit");
            }
            else{
                //remove from list
                clientHandlers.remove(this);
            }
            
            //close all streams
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                //Ignore
            }

            //close the socket
            try {
                this.socket.close();
            } catch (IOException e) {
                //Ignore
            }

            

        }
        
    }
        

}