import java.net.*;
import java.util.concurrent.Semaphore;
import java.io.*;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    Console console = System.console();

    public void Connect(String ip, int port) throws IOException {
        Semaphore semaphore = new Semaphore(0);
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        Thread watchServer = new Thread() {
            public void run() {
                try {
                    String message;
                    while((message = in.readLine()) != null) {
                        System.out.println(message);   
                        semaphore.release();  
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };

        Thread watchConsole = new Thread() {
            public void run() {
                try {
                    watchServer.start();
                    semaphore.acquire();
                    System.out.println("Escribe ./exit para desactivar el cliente");
                    String message;
                    while((message = console.readLine()) != null) {
                        if ("./exit".equals(message)) {
                            out.println(message);
                            break;
                        }
                    }
                    Disconnect();
                } catch(IOException exception) {
                    exception.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        watchConsole.start();
    }

    public void Disconnect() throws IOException{
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.Connect("127.0.0.1", 7000);
    }
}

