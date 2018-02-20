package com.brzozaxd.connection.client.v2;

import com.brzozaxd.connection.common.PackToSendToServer;
import game.objects.GameObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientBrain extends Thread {
    private ClientSender clientSender;
    public PackToSendToServer packOut
            = new PackToSendToServer("name", 0, "direction", 0, false);
    // jakiś tam testowy pakiet na początek

    public static com.brzozaxd.connection.common.PackReceivedFromServer<GameObject> recPac;
    private static volatile ArrayList<String> connectedClientsBuffer = new ArrayList<>();
    private static volatile int notConnectedClientsAmountBuffer = -1;

    private boolean running = true;
    
    public ClientBrain(String addressIP, int portSending, int portReceving,
                       String clientName, int clientID){
        System.out.println("Client starting");

        String addressIP1 = precessAddressIP(addressIP);

        System.out.println("Klient podłącza się do adresu: " + addressIP1);
        System.out.println("Klient podłącza się na porcie: " + portSending);

        packOut.setPlayersName(clientName);
        packOut.setPlayersId(clientID);
        clientSender = new ClientSender(addressIP1, portSending, packOut);
        clientSender.start();
        try{
            ClientReceiver receiver = new ClientReceiver(addressIP1, portReceving);
            receiver.start();
        }catch (IOException e){
            System.err.print("Wyjątek w klasie odbierającej dane od serwera!\n");
        }
    }

    @Override
    public void run() {
        Scanner keyboard = new Scanner(System.in);
        while (running){
            clientSender.setPackOut(packOut);
        }
    }

    public void close() {running = false;}
    
    synchronized static void writeToBufConnClients(ArrayList<String> recConnClients, int notConnected){
        for (String recConnClient : recConnClients) {
            if (!connectedClientsBuffer.contains(recConnClient)) {
                connectedClientsBuffer.add(recConnClient);
            }
        }
        notConnectedClientsAmountBuffer = notConnected;
    }

    private String precessAddressIP(String addressIP){
        if (addressIP == "localhost"){
            return addressIP;
        }
        int length = addressIP.length();
        String IP = new String();
        if (length < 8 || length > 11){
            IP = addressIP;
        }else {
            if (addressIP.matches("\\d+")){
                //System.out.println("To jest adres IP: " + addressIP);
                IP = addressIP.substring(0, 3);
                IP += ".";
                IP += addressIP.substring(3, 6);
                IP += ".";
                IP += addressIP.substring(6, 7);
                IP += ".";
                IP += addressIP.substring(7);
                //System.out.println("Po przetworzeniu:" + IP);
            }

        }
        return IP;
    }
}
