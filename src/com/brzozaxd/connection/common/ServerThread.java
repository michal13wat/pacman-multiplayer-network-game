package com.brzozaxd.connection.common;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.nio.channels.SocketChannel;


public class ServerThread extends Thread {
    private static int threadCnt = 0;
    private int threadNum;
    private static boolean unlock = false;

    private static volatile byte[] bytesToSend = null;
    private static volatile PackReceivedFromServer objToSend = null;
    private static volatile PackToSendToServer objReceived = null;
    
    private ObjectOutputStream oos = null;
    private BufferedOutputStream bos = null;
    private ObjectInputStream ois = null;
    
    private SocketChannel playerWhiteSocket;
    private static boolean[] verifySendObject = null;
    
    private boolean running = true;

    ServerThread(SocketChannel socket) {
        this.playerWhiteSocket = socket;

        threadNum = threadCnt;
        threadCnt++;
        verifySendObject = new boolean[MyServer.getClientAmount()];
        for (int i = 0; i < verifySendObject.length; i++){
            verifySendObject[i] = false;
        }
    }
    
    @Override
    public void run() {
        try
        {
            System.out.print("Wątek serwerowy nr: " + threadNum + "\n");
            
            try{
                playerWhiteSocket.socket().setSoTimeout(25);
            }catch (SocketException e){
                System.out.print("Złapano wyjątek związany z timeout-em w serwerze\n");
            }
            
            System.out.print("Podłączono klienta \n");
            
            oos = new ObjectOutputStream(playerWhiteSocket.socket().getOutputStream());
            bos = new BufferedOutputStream(playerWhiteSocket.socket().getOutputStream());
            ois = new ObjectInputStream(playerWhiteSocket.socket().getInputStream());
            
            while (running){
                if (isSentByXThread(threadNum)){
                    System.out.print("Rozopoczynam wysłanie obiektu \n");
                    sendObject(bytesToSend);

                    threadXSendObject(threadNum);
                    System.out.print("Wysłano obiekt \n");
                }
                
                receiveObject();
            }
        }
        catch(IOException ex) {
            System.out.print("Złapano wyjątek w wysyłającym wątku serwera!!!");
            ex.printStackTrace();
        }
        
        System.out.println("Zamykamy wątek serwerowy nr. " + threadNum + "!");
    }
    
    void stopThread() {
        running = false;
        try {
            playerWhiteSocket.close();
            oos.close();
            bos.close();
            ois.close();
        }
        catch (Exception ignored) {}
    }

    private void sendObject(byte[] bytes) throws IOException {
        bos.write(bytes);
        bos.flush();
    }

    synchronized private void receiveObject() {
        try {
            objReceived = (PackToSendToServer)ois.readObject();
        } catch (IOException | ClassNotFoundException ignored){
        }
    }

    private static synchronized boolean isSentByXThread(int x){
        return verifySendObject[x];
    }

    private static synchronized void threadXSendObject(int x){
        verifySendObject[x] = false;
    }
}