
import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server {
    private ServerSocket server;
    private LinkedList<ClientThread> clients = new LinkedList<ClientThread>();
    public int productAmmount = 0;

    public static class ClientThread extends Thread {
        private Server server;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
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

                String message;

                out.println("Usuario Conectado");

                while ((message = in.readLine()) != null) {
                    if ("./exit".equals(message)) {
                        break;
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

        public synchronized void sendMessage(int producto) {
            out.println("Se ha enviado el producto: "+ producto + " a el cliente: " + getclientID() +
             "\n"); 
             
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

    public void echo() {

        for(int i = 0 ;i < clients.size(); i++)
        {
            ClientThread client = clients.get(i);
                if(productAmmount != 0){
                    client.sendMessage(productAmmount);
                    productAmmount--;
                }else{
                    break;
                }
            
        }
    }

    public void start(int port) {

        Server serv = this;
        Thread createProducts = new Thread(){
            public void run(){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Inserte la cantidad de productos disponibles: ");
                    String message = " ";
    
                    while(message != null){
                        if(message.equals("./exit")){
                            break;
                        }
                        message =  reader.readLine();
                        productAmmount = Integer.parseInt(message);
    
                        while(productAmmount != 0){
                            serv.echo();
                        }
                    }
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }
        };
        
        Thread acceptUsers = new Thread(){
            public void run(){
                try {
                    server = new ServerSocket(port);
                    System.out.println("Productor Inicializado en el puerto: " + port);
                    int userCount = 0;
                    System.out.println("Esperando Usuarios...");
                    while(true) {
                        ClientThread client = new ClientThread(server.accept(), serv, userCount);
                        clients.add(client);
                        client.start();
                        userCount++;

                        if(userCount == 1){
                            createProducts.start();
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };
        acceptUsers.start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(7000);
    }
}