package clientAndServer;

/**
 * Created by User on 2017-04-17.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class ServerThread extends Thread {
    ServerSocketChannel serverSocket;
    private static volatile int port;
    private static int threadCnt = 0;
    private int threadNum;
    private static int connectedClients = 0;

    private static volatile PackReceivedFromServer objToSend = null;
    public static volatile PackToSendToServer objReceived = null;

    SocketChannel playerWhiteSocket;
    static boolean[] verifySendObject = null;

    public ServerThread() {
        threadNum = threadCnt;
        threadCnt++;
        verifySendObject = new boolean[Server.getClientAmount()];
        for (int i = 0; i < verifySendObject.length; i++){
            verifySendObject[i] = false;
        }
    }

    @Override
    public void run() {
        try
        {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(true);

            serverSocket.socket().bind(new InetSocketAddress(port++));

            System.out.print("Wątek serwerowy nr: " + threadNum + "\n");
            playerWhiteSocket = serverSocket.accept();
            try{
                playerWhiteSocket.socket().setSoTimeout(20);
            }catch (SocketException e){
                System.out.print("Złapano wyjątek związany z timeout-em w serwerze\n");
            }
            System.out.print("Podłączono klienta \n");
            connectedClients++;
            if (connectedClients == Server.getClientAmount()){
                System.out.print("Wszyscy klienci podłączeni - Naciśnij Enter aby rozpocząć grę\n");
            }

            while (true){
                if (isSentByXThread(threadNum)){
                    System.out.print("Rozopoczynam wysłanie obiektu \n");
                    sendObject(playerWhiteSocket, objToSend);

                    threadXSendObject(threadNum);
                    System.out.print("Wysłano obiekt \n");
                }
                receiveObject(playerWhiteSocket);

//                System.out.print("Pętla wątku obierającego nr.: " + threadNum + " \n");
            }
        }
        catch(IOException ex) {
            System.out.print("Złapano wyjątek w wysyłającym wątku serwera!!!");
            ex.printStackTrace();
        }
    }


    private void sendObject(SocketChannel sChannel, PackReceivedFromServer object) throws IOException
    {
        ObjectOutputStream oos = new
                ObjectOutputStream(sChannel.socket().getOutputStream());
        oos.writeObject(object);
        oos.flush();
    }

    private void receiveObject (SocketChannel sChannel)
    {
        try {
            ObjectInputStream ois =
                    new ObjectInputStream(sChannel.socket().getInputStream());
            objReceived = (PackToSendToServer)ois.readObject();
            System.out.print(objReceived.getPlayersName() + ", "
                    + objReceived.getCharacter() + "\n");
            System.out.print("Metoda odbierająca... \n");
        } catch (ClassNotFoundException | IOException e){
            // TODO - nie wiem dlaczego ciągle tutaj wywala ten wyjątek...
            // TODO (program nawet nie zdąży wejść w ten blok try i już wywala wyjątek...)
            // Jak odpalam to w osobnym projekcie, to też wywala wyjątek cały czas, ale
            // mimo to jako tako to działa...

//            System.out.print("Błąd serwera - odbieranie obiektu od klienta nie powiodło się!!! \n\n");
        }
    }

    // Jeśli obiekt został wysłany przez wyszystkie wątki, to wtedy ustaw kolejny do wysłania
    public static synchronized void setObjToSend(PackReceivedFromServer objToSend) {
        if (isSent()){
            ServerThread.objToSend = objToSend;
            for (int i = 0; i < verifySendObject.length; i++){
                verifySendObject[i] = true;
            }
        }
    }

    private static synchronized boolean isSent(){
        for (int i = 0; i < verifySendObject.length; i++){
            if (verifySendObject[i] == true)
                return false;
        }
        return true;
    }

    private static synchronized boolean isSentByXThread(int x){
        return verifySendObject[x];
    }

    private static synchronized void threadXSendObject(int x){
        verifySendObject[x] = false;
    }

    public static synchronized PackToSendToServer getObjReceived() {
        return objReceived;
    }

    public static synchronized void setObjReceived(PackToSendToServer objReceived) {
        ServerThread.objReceived = objReceived;
    }

    public static void setPort(int port) {
        ServerThread.port = port;
    }
}