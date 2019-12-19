import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

class ServerClass implements Runnable {
    private int PORT; // server port

    public ServerClass(int PORT) {
        this.PORT = PORT;
    }

    public void run() {
        long startTime = System.nanoTime();
        byte[] rcvBuf = new byte[256]; // Buffer for rcv bytes received bytes
        byte[] sendBuf = new byte[256];// sent bytes
        String rcvStr;

        // create a UDP socket and bind it to the specified port on the local
        // host
        DatagramSocket socket = null; //QUE lost-rate??¿?
        try {
            socket = new SimpleSimulatedDatagramSocket(PORT, 0.1, 1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        StringBuffer rcvString = new StringBuffer();
        while (true) {
            // create a DatagramPacket for receiving packets
            DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);

            // receive packet
            try {
                socket.receive(packet); //RECVFROM
            } catch (IOException e) {
                e.printStackTrace();
            }

            // construct a new String by decoding the specified subarray of
            // bytes
            // using the platform's default charset
            String tempRcv = new String(packet.getData(), packet.getOffset(), packet.getLength());
            rcvString.append(tempRcv);
            if(tempRcv.equals("]")){
        System.out.println("Server RCV: "+rcvString);
        rcvString = new StringBuffer();

            }



            // encode a String into a sequence of bytes using the platform's
            // default charset


            // create a DatagramPacket for sending packets
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, packet.getAddress(), packet.getPort());

            // send packet
            try {
                socket.send(sendPacket); //SENDTO
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //------------------------------------------------------------------------------------------------------------------
}

class ClientClass implements Runnable {
    private int portSend;

    public ClientClass(int portSend) {
        this.portSend = portSend;
    }

    public void run() {
        //long startTime = System.nanoTime();//usar emulated system clock¿?
        EmulatedSystemClock eck = new EmulatedSystemClock();
        long startTime = eck.currentTimeMillis();


        //each second
        Reading temp = new Reading();
        String sendString = Arrays.toString(temp.generateReadings(eck.currentTimeMillis() - startTime));

        byte[] rcvBuf = new byte[256]; // received bytes

        // encode this String into a sequence of bytes using the platform's
        // default charset and store it into a new byte array

        // determine the IP address of a host, given the host's name
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // create a datagram socket and bind it to any available
        // port on the local host
        //DatagramSocket socket = new SimulatedDatagramSocket(0.2, 1, 200, 50); //SOCKET
        DatagramSocket socket = null; //SOCKET
        try {
            socket = new SimpleSimulatedDatagramSocket(0.4, 1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        System.out.print("Client sends: ");

        StringBuffer receiveString = packageManagement(sendString, rcvBuf, portSend, address, socket);
        System.out.println(receiveString);
        if (receiveString.length() == 0 || receiveString.length() != sendString.length()) {
            StringBuffer stringBuffer2 = packageManagement(sendString, rcvBuf, portSend, address, socket);
            System.out.println("ACK1 fail");
            if (stringBuffer2.length() == 0 || stringBuffer2.length() != sendString.length()) {
                socket.close();
                System.out.println("ACK2 fail");
                return;//???
            }
        }
        System.out.println("Client received: " + receiveString);

        // close the datagram socket
        socket.close(); //CLOSE
    }

    private StringBuffer packageManagement(String sendString, byte[] rcvBuf, int port, InetAddress address, DatagramSocket socket) {
        // send each character as a separate datagram packet

        for (int i = 0; i < sendString.length(); i++) {
            byte[] sendBuf = new byte[1];// sent bytes
            sendBuf[0] = (byte) sendString.charAt(i);

            // create a datagram packet for sending data
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
                    address, port);

            // send a datagram packet from this socket
            try {
                socket.send(packet); //SENDTO
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(new String(sendBuf));
        }
        System.out.println("");

        //==========================================================================================================
        StringBuffer receiveString = new StringBuffer();//Buffer used for received String

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
        return receiveString;
    }

}

public class Node {


    public static void main(String[] args) throws InterruptedException {
        assert (args.length==2);

        ArrayList<Thread> hilosSend = new ArrayList<>();
        int[] ports = IntStream.rangeClosed(1000, 1050).toArray();
        int serverPort= Integer.parseInt(args[0]);
        //int serverPort = Integer.parseInt(new Scanner(System.in).next());
        new Thread(new ServerClass(serverPort)).start();
        int NodesNumber= Integer.parseInt(args[1]);
        //int NodesNumber = Integer.parseInt(new Scanner(System.in).next());

        //cada 5seg?
        EmulatedSystemClock emulatedSystemClock = new EmulatedSystemClock();
        new Scanner(System.in).next();
        while (true) {
            while (emulatedSystemClock.currentTimeMillis() > (5.00f - 5.00f * 0.2)) {
                Thread tempThread;
                for (int a = 0; a < NodesNumber; a++) {
                    if (ports[a] != serverPort) {
                        tempThread = new Thread(new ClientClass(ports[a]));//tiempo de medida
                        hilosSend.add(tempThread);
                        tempThread.start();
                    }
                }
                Thread.sleep(1000);
            }
            System.out.println("medias?");
        }
    }
}
