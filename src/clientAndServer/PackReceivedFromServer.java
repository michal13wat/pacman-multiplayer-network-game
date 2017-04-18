package clientAndServer;

/**
 * Created by User on 2017-04-17.
 */
import java.io.Serializable;
import java.util.ArrayList;

/*
 *  Nazwa klasy jest logiczna jeżeli patrzy się od strony klieta.
 *  Żeby nie toworzyć nowych klas osobno dla servera, użyję tych.
 *  */
public class PackReceivedFromServer<T> implements Serializable {
    private String additionalInfo;
    private ArrayList<T> objectsList = new ArrayList<T>();
    private ArrayList<String> connectedClients = new ArrayList<>();
    private int notConnectedClients = 0;

    public PackReceivedFromServer(){
    }
    public PackReceivedFromServer(ArrayList<T> objectsList, String additionalInfo,
                                  ArrayList<String> connectedClients, int notConnectedClients){
        this.objectsList = objectsList;
        this.additionalInfo = additionalInfo;
        this.connectedClients = connectedClients;
        this.notConnectedClients = notConnectedClients;
    }

    public void addObject(T obj){
        objectsList.add(obj);
    }
    public void addList(ArrayList<T> list){
        objectsList.addAll(list);
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public ArrayList<T> getObjectsList() {
        return objectsList;
    }

    public void setConnectedClients(ArrayList<String> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public ArrayList<String> getConnectedClients() {
        return connectedClients;
    }

    public void setNotConnectedClients(int notConnectedClients) {
        this.notConnectedClients = notConnectedClients;
    }

    public int getNotConnectedClients() {
        return notConnectedClients;
    }
}
