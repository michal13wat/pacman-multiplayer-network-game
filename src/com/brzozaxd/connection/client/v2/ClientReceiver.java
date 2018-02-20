package com.brzozaxd.connection.client.v2;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientReceiver extends Thread {
	private boolean broadcastListening = true;
    private String hostName;
    private int portNumber;

	ClientReceiver(String hostName, int portNumber) throws IOException {
		super("Dawac dane");
        this.hostName = hostName;
        this.portNumber = portNumber;
	}

	public void run() {
		try (Socket brcSocket = new Socket(hostName, portNumber);
				ObjectInputStream in = new ObjectInputStream(brcSocket.getInputStream());
		) {
			while (broadcastListening) { try {
				ClientBrain.recPac = (com.brzozaxd.connection.common.PackReceivedFromServer)in.readObject();
                ClientBrain.writeToBufConnClients(ClientBrain.recPac.getConnectedClients(),
                        ClientBrain.recPac.getNotConnectedClients());
				}
                catch (EOFException e) {
                        System.err.println("EOF");
                        broadcastListening = false;
                }
                 catch (NullPointerException | ClassNotFoundException e) {
                    //receive data
					 e.printStackTrace();
		        }
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName + "at BrcCl");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostName + " at BrcCl");
			broadcastListening = false;
		}
		System.out.println("BREC ENDING");
	}
}

