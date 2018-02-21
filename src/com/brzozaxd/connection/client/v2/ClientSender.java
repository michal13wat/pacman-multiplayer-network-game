package com.brzozaxd.connection.client.v2;

import com.brzozaxd.connection.common.PackToSendToServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSender extends Thread {
	private volatile String hostName = null;
	private int portNumber = -1;
    private volatile PackToSendToServer packOut = null;
    private com.brzozaxd.connection.common.PackToSendToServer prevPackOut = null;

	ClientSender(String hostName, int portNumber, PackToSendToServer packOut) {
		this.hostName = hostName;
		this.portNumber = portNumber;
        this.packOut = packOut;
	}
	
	public void run() {
		try (Socket socket = new Socket(hostName, portNumber);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		) {
            while (true){
                Thread.sleep(5*1000/60);
                if(!packOut.isEquals(prevPackOut)){
                    out.flush();
                    prevPackOut = packOut.copy();
                    out.writeUnshared(packOut);
                }
            }
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName + "at ECl");
		} catch (IOException ignored) {
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    synchronized void setPackOut(PackToSendToServer packOut) {
        this.packOut = packOut;
    }
}
