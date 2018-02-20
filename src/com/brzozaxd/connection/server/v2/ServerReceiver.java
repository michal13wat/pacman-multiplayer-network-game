package com.brzozaxd.connection.server.v2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerReceiver extends Thread {
	private Socket socket = null;

	ServerReceiver(Socket socket) {
		super("EchoServerThread");
		this.socket = socket;
	}

	public void run() {

		try (
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		) {

			while (true){
                try {
					com.brzozaxd.connection.common.PackToSendToServer packIn
							= (com.brzozaxd.connection.common.PackToSendToServer) in.readObject();
                    ServerBrain.recPacks.addLast(packIn);
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                    break;
                }
			}
		} catch (IOException e) {
			try {
				socket.close();
				System.out.println("Connection to client closed.");
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}

	// zamykanie polaczenia
	void closeSocket(){
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}