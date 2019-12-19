import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;


public class Node {


    public static ArrayList<Reading> medias = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        assert (args.length == 2);


        int[] ports = IntStream.rangeClosed(1000, 1050).toArray();
        int serverPort = Integer.parseInt(args[0]);
        //int serverPort = Integer.parseInt(new Scanner(System.in).next());
        new Thread(new ServerClass(serverPort)).start();
        int NodesNumber = Integer.parseInt(args[1]);
        //int NodesNumber = Integer.parseInt(new Scanner(System.in).next());

        //cada 5seg?
        EmulatedSystemClock emulatedSystemClock = new EmulatedSystemClock();
        new Scanner(System.in).next();
        long start_t = System.currentTimeMillis();

        System.out.println((System.currentTimeMillis() - start_t) / 1000);
        while ((System.currentTimeMillis() - start_t) / 1000f < (5.00f - 5.00f * 0.2)) {//MAL
            long tReading = emulatedSystemClock.currentTimeMillis();
            for (int a = 0; a < NodesNumber; a++) {
                if (ports[a] != serverPort) {
                    new Thread(new ClientClass(ports[a], tReading)).start();//tiempo de medida
                }
            }
            Thread.sleep(1000);

        }
        Thread.sleep(10000);
        //
        if (!medias.isEmpty()) {
            System.out.print("Temp: ");
            if (medias.stream().filter(reading -> reading.getTemp() != -1f).mapToDouble(Reading::getTemp).filter(medida -> medida != -1f).average().isPresent())
                System.out.println(medias.stream().filter(reading -> reading.getTemp() != -1f).mapToDouble(Reading::getTemp).filter(medida -> medida != -1f).average().getAsDouble());
            System.out.print("Press: ");
            if (medias.stream().filter(reading -> reading.getPress() != -1f).mapToDouble(Reading::getPress).filter(medida -> medida != -1f).average().isPresent())
                System.out.println(medias.stream().filter(reading -> reading.getPress() != -1f).mapToDouble(Reading::getPress).filter(medida -> medida != -1f).average().getAsDouble());
            System.out.print("Hum: ");
            if (medias.stream().filter(reading -> reading.getHum() != -1f).mapToDouble(Reading::getHum).filter(medida -> medida != -1f).average().isPresent())
                System.out.println(medias.stream().filter(reading -> reading.getHum() != -1f).mapToDouble(Reading::getHum).filter(medida -> medida != -1f).average().getAsDouble());
            System.out.print("Co: ");
            if (medias.stream().filter(reading -> reading.getCo() != -1f).mapToDouble(Reading::getCo).filter(medida -> medida != -1f).average().isPresent())
                System.out.println(medias.stream().filter(reading -> reading.getCo() != -1f).mapToDouble(Reading::getCo).filter(medida -> medida != -1f).average().getAsDouble());
            System.out.print("NO2: ");
            if (medias.stream().filter(reading -> reading.getNo2() != -1f).mapToDouble(Reading::getNo2).filter(medida -> medida != -1f).average().isPresent())
                System.out.println(medias.stream().filter(reading -> reading.getNo2() != -1f).mapToDouble(Reading::getNo2).filter(medida -> medida != -1f).average().getAsDouble());
            System.out.print("SO2: ");
            if (medias.stream().filter(reading -> reading.getSo2() != -1f).mapToDouble(Reading::getSo2).filter(medida -> medida != -1f).average().isPresent())
                System.out.println(medias.stream().filter(reading -> reading.getSo2() != -1f).mapToDouble(Reading::getSo2).filter(medida -> medida != -1f).average().getAsDouble());

        }
        //System.out.println(medias);


    }

    private static class ClientClass implements Runnable {
        private int portSend;
        private long tReading;

        public ClientClass(int portSend, long tReading) {
            this.portSend = portSend;
            this.tReading = tReading;
        }

        public void run() {

            //long startTime = System.nanoTime();//usar emulated system clock¿?


            //each second
            Reading temp = new Reading();
            String sendString = Arrays.toString(temp.generateReadings(tReading));


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

            String receiveString = packageManagement(sendString, rcvBuf, portSend, address, socket);
            System.out.println(receiveString);
            if (receiveString.length() == 0 || Integer.parseInt(receiveString) != sendString.length()) {
                String stringBuffer2 = packageManagement(sendString, rcvBuf, portSend, address, socket);
                System.out.println("ACK1 fail");
                if (stringBuffer2.length() == 0 || stringBuffer2.length() != sendString.length()) {
                    socket.close();
                    System.out.println("ACK2 fail");
                    return;//???
                }
            }
            System.out.println("Client received: " + receiveString);
            if (!medias.contains(temp))
                //if (medias.stream().noneMatch(reading -> reading.getNo2().equals(temp.getNo2())))
                medias.add(temp);
            // close the datagram socket
            socket.close(); //CLOSE
        }

        private String packageManagement(String sendString, byte[] rcvBuf, int port, InetAddress address, DatagramSocket socket) {
            // send each character as a separate datagram packet


            byte[] sendBuf = sendString.getBytes(StandardCharsets.UTF_8);

            // create a datagram packet for sending data
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
                    address, port);

            // send a datagram packet from this socket
            try {
                socket.send(packet); //SENDTO
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(new String(sendBuf));

            String receiveString = "";//Buffer used for received String

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
                receiveString = (new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength()));

            }
            return receiveString;
        }

    }


    private static class ServerClass implements Runnable {
        private int PORT; // server port

        public ServerClass(int PORT) {
            this.PORT = PORT;
        }

        public void run() {
            long startTime = System.nanoTime();
            byte[] rcvBuf = new byte[256]; // Buffer for rcv bytes received bytes
            byte[] sendBuf;// sent bytes
            String rcvStr;

            // create a UDP socket and bind it to the specified port on the local
            // host
            DatagramSocket socket = null; //QUE lost-rate??¿?
            try {
                socket = new SimpleSimulatedDatagramSocket(PORT, 0.4, 1000);
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
                System.out.println(tempRcv);

                String[] splitString = tempRcv.substring(1, tempRcv.length() - 1).split(",");
                int a = 0;
                Reading temp = new Reading(Float.parseFloat(splitString[a++]), Float.parseFloat(splitString[a++]), Float.parseFloat(splitString[a++]),
                        Float.parseFloat(splitString[a++]), Float.parseFloat(splitString[a++]), Float.parseFloat(splitString[a++]), Float.parseFloat(splitString[a].substring(1)));


                if (!medias.contains(temp))
                    medias.add(temp);
                System.out.println(tempRcv);
                sendBuf = String.valueOf(tempRcv.length()).getBytes();

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

}
