package com.brzozaxd.connection.server.v2;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSender extends Thread {

	private Socket socket = null;
	private final int DELAY = 1000/60; //1;
    private int threadID;

	ServerSender(Socket socket, int threadID) {
		super("ServerSender");
		this.socket = socket;
        this.threadID = threadID;
	}

	public void run() {
		try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()))
		{
			System.out.println("ServerSender in thread " + threadID + " estabilished.");
		while (true) {
                if(!ServerBrain.checkIfAllClientsAreConnected()){
                    out.reset();

                    ServerBrain.packOut.setNotConnectedClients(ServerBrain.notConnectedClients);
                    ServerBrain.packOut.setConnectedClients(ServerBrain.connectedClients);
                    if(ServerBrain.bytesOut != null){
                        out.writeUnshared(ServerBrain.packOut);
                    }else {
                        System.out.println("=========  Pusty bufor z danymi wyjściowymi!!!  =========");
                    }
                    out.flush();
				}
				Thread.sleep(DELAY);
			}

		} catch (IOException e) {
			try {
				socket.close();
				System.out.println("ServerSender in thread: " + threadID + " stopped: " + e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void closeSocket(){
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}