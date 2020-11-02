
import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server {
    private ServerSocket server;
    private LinkedList<ClientThread> clients = new LinkedList<ClientThread>();
    private int productAmmount = 0;

    public static class ClientThread extends Thread {
        private Server server;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName;
        private int clientID;

        public ClientThread(Socket _socket, Server _server, int _clientID) {
            socket = _socket;
            server = _server;
            clientID = _clientID;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String message = in.readLine();
                clientName = message;

                out.println("Connected users: \n" + server.getClientNames());

                while ((message = in.readLine()) != null) {
                    if ("./exit".equals(message)) {
                        break;
                    } else {
                        server.echo(message, this);
                    }
                }

                in.close();
                out.close();
                socket.close();
                server.deleteClientName(clientID);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        public int getclientID(){
            return clientID;
        }
        public String getClientName() {
            return clientName;
        }

        public void sendMessage(String message, ClientThread sender) {
            out.println("[" + sender.getClientName() + sender.getclientID()+ "]: " + message); 
        }
    }
    public void deleteClientName(int clientID){
        for (ClientThread clientThread : clients) {
            if(clientThread.clientID == clientID)
            {
                clients.remove(clientThread);
            }
        }
    }

    public String getClientNames() {
        String result = "";
        for (int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getClientName()!=null)
            result += clients.get(i).getClientName() + ", ";
        }
        return result;
    }

    public void echo(String message, ClientThread sender) {
        clients.forEach(client -> {
            if (client.getclientID()!=sender.getclientID()) {
                client.sendMessage(message, sender);
            }
        });
    }

    public void start(int port) {

        Server serv = this;
        Thread acceptUsers = new Thread(){
            public void run(){
                try {
                    server = new ServerSocket(port);
                    System.out.println("Server listening on PORT " + port);
                    int userCount = 0;
                    while(true) {
                        ClientThread client = new ClientThread(server.accept(),serv, userCount);
                        clients.add(client);
                        client.start();
                        userCount++;
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };
        
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(7000);
    }
}