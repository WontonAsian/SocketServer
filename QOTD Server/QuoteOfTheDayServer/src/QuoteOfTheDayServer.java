import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class QuoteOfTheDayServer {

   private static final int PORT = 17;
   private static final String[] QUOTES = {
       "My name is Inigo Montoya. You killed my father. Prepare to die.",
       "As you wish.",
       "Inconceivable!",
       "You keep using that word. I do not think it means what you think it means.",
       "Death cannot stop true love. All it can do is delay it for a while.",
       "Have fun storming the castle!",
       "Life is pain, Highness. Anyone who says differently is selling something.",
       "I decided I wanted to enter that light, And at the edge of it, I found you.",
       "I feel like I'm always searching for someone, or something.",
   };
   private static final Random random = new Random();

   public static void main(String[] args) throws IOException {
       System.out.println("Starting Quote of the Day server...");
       // TCP server
       ServerSocket tcpServerSocket = new ServerSocket(PORT);
       new Thread(() -> {
           while (true) {
               try {
                   Socket clientSocket = tcpServerSocket.accept();
                   new Thread(() -> {
                       try {
                           handleTcpClient(clientSocket);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }).start();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }).start();
       // UDP server
       DatagramSocket udpSocket = new DatagramSocket(PORT);
       new Thread(() -> {
           while (true) {
               try {
                   byte[] buffer = new byte[512];
                   DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                   udpSocket.receive(packet);
                   new Thread(() -> {
                       try {
                           handleUdpClient(packet);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }).start();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }).start();
   }

   private static void handleTcpClient(Socket clientSocket) throws IOException {
       OutputStream outputStream = clientSocket.getOutputStream();
       String quote = getQuote();
       System.out.println("Sending quote to TCP: " + quote);
       outputStream.write(quote.getBytes());
       outputStream.close();
       clientSocket.close();
   }

   private static void handleUdpClient(DatagramPacket packet) throws IOException {
       byte[] buffer = packet.getData();
       String quote = getQuote();
       System.out.println("Sending quote to UDP: " + quote);
       DatagramPacket responsePacket = new DatagramPacket(quote.getBytes(), quote.length(), packet.getAddress(), packet.getPort());
       DatagramSocket socket = new DatagramSocket();
       socket.send(responsePacket);
       socket.close();
   }

   private static String getQuote() {
       int index = random.nextInt(QUOTES.length);
       return QUOTES[index];
   }
}
