import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Node {
        static final int PORT = 10001; // server port


        public void runServer() throws IOException{
            byte[] rcvBuf = new byte[256]; // Buffer for rcv bytes received bytes
            byte[] sendBuf = new byte[256];// sent bytes
            String rcvStr;

            // create a UDP socket and bind it to the specified port on the local
            // host
            DatagramSocket socket = new SimpleSimulatedDatagramSocket(PORT, 0.2, 1000); //QUE lost-rate??¿?

            while (true) {
                // create a DatagramPacket for receiving packets
                DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);

                // receive packet
                socket.receive(packet); //RECVFROM

                // construct a new String by decoding the specified subarray of
                // bytes
                // using the platform's default charset
                rcvStr = new String(packet.getData(), packet.getOffset(),
                        packet.getLength());
                System.out.println("Server received: " + rcvStr);

                // encode a String into a sequence of bytes using the platform's
                // default charset
                sendBuf = rcvStr.toUpperCase().getBytes();
                System.out.println("Server sends: " + rcvStr.toUpperCase());

                // create a DatagramPacket for sending packets
                DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, packet.getAddress(), packet.getPort());

                // send packet
                socket.send(sendPacket); //SENDTO
            }
         }

//-----------------------------------------------------------------------------------------------------------------------------------------------------------

        public void runClient() throws IOException{

            String sendString = "Any string...";

            byte[] rcvBuf = new byte[256]; // received bytes

            // encode this String into a sequence of bytes using the platform's
            // default charset and store it into a new byte array

            // determine the IP address of a host, given the host's name
            InetAddress address = InetAddress.getByName("localhost");

            // create a datagram socket and bind it to any available
            // port on the local host
            //DatagramSocket socket = new SimulatedDatagramSocket(0.2, 1, 200, 50); //SOCKET
            DatagramSocket socket = new SimpleSimulatedDatagramSocket(0.2, 1000); //SOCKET

            System.out.print("Client sends: ");
            // send each character as a separate datagram packet
            for (int i = 0; i < sendString.length(); i++) {
                byte[] sendBuf = new byte[1];// sent bytes
                sendBuf[0] = (byte) sendString.charAt(i);

                // create a datagram packet for sending data
                DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
                        address, PORT);

                // send a datagram packet from this socket
                socket.send(packet); //SENDTO
                System.out.print(new String(sendBuf));
            }
            System.out.println("");

            StringBuffer receiveString = new StringBuffer();

            while (true) {
                // create a datagram packet for receiving data
                DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);

                try {
                    // receive a datagram packet from this socket
                    socket.receive(rcvPacket); //RECVFROM
                } catch (SocketTimeoutException e) {
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }

                // construct a new String by decoding the specified subarray of bytes
                // using the platform's default charset
                receiveString.append(new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength()));

            }
            System.out.println("Client received: " + receiveString);

            // close the datagram socket
            socket.close(); //CLOSE
        }

}


