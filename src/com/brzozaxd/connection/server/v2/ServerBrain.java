package com.brzozaxd.connection.server.v2;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import game.objects.GameObject;

public class ServerBrain extends Thread {

    private static int clientAmount;
    private static ArrayList<ServerReceiver> clientList = new ArrayList<>();
    private static ArrayList<ServerSender> brcList = new ArrayList<>();
    public static volatile ArrayList<String> connectedClients = new ArrayList<>();
    static volatile int notConnectedClients = clientAmount;
    private int recPort;
    private int brcPort;

    public static LinkedBlockingDeque<com.brzozaxd.connection.common.PackToSendToServer> recPacks
            = new LinkedBlockingDeque<>();
    
    public static com.brzozaxd.connection.common.PackReceivedFromServer<GameObject> packOut
            = new com.brzozaxd.connection.common.PackReceivedFromServer<>();
    public static byte[] bytesOut;

    private boolean running = true;
    
    public ServerBrain(int recPort, int brcPort, int clientAmount) {
        System.out.println("Server starting...");
        this.recPort = recPort;
        this.brcPort = brcPort;
        ServerBrain.clientAmount = clientAmount;
    }

    public void run(){
        int threadCounter = 0;

        System.out.println("Listening for connections.");
        try (ServerSocket serverSocket = new ServerSocket(recPort);
             ServerSocket broadcastSocket = new ServerSocket(brcPort);) {
            while (running) {
                if (!checkIfAllClientsAreConnected()) {
                    ServerReceiver newClient = new ServerReceiver(serverSocket.accept());
                    newClient.start();
                    clientList.add(newClient);

                    ServerSender serverSenderClient = new ServerSender(broadcastSocket.accept(), threadCounter);
                    serverSenderClient.start();
                    brcList.add(serverSenderClient);
                    threadCounter++;
                } else {
                    System.out.print("Wszyscy klienci podłączeni.\n");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + recPort + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public void close() {running = false;}
    
    public static void disconnectAll() throws InterruptedException {
        for (int i = 0; i < clientList.size(); i++) {
            clientList.get(i).closeSocket();
            brcList.get(i).closeSocket();
        }
        Thread.sleep(150);
    }

    synchronized static boolean checkIfAllClientsAreConnected() {
        return (clientList.size() == clientAmount);
    }
}
