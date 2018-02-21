package com.brzozaxd.connection.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/*
 *  Nazwa klasy jest logiczna jeżeli patrzy się od strony klieta.
 *  Żeby nie toworzyć nowych klas osobno dla servera, użyję tych.
 *  */
public class PackReceivedFromServer<T> implements Serializable {
    private String additionalInfo;
    private ArrayList<T> objectsList = new ArrayList<T>();          // główny element klasy!
    private ArrayList<Integer> deletedList = new ArrayList<>();
    private ArrayList<String> connectedClients = new ArrayList<>();
    private int notConnectedClients = 0;
    private Random randomizer = null;

    // Wartości z wrapperów.
    public int gameScore;
    public int gameLives;
    public int maxPlayers;

    private ArrayList<PackToSendToServer> clientFeedback = new ArrayList<>();
    
    public PackReceivedFromServer(){
    }

    public void clear() {
        objectsList.clear();
        deletedList.clear();
        connectedClients.clear();
        clientFeedback.clear();
        randomizer = null;
    }
    
    public void addObject(T obj){
        objectsList.add(obj);
    }

    public void addDeletedList(ArrayList<Integer> list) {
        deletedList.addAll(list);
    }
    
    public void addFeedback(ArrayList<PackToSendToServer> list) {
        clientFeedback.addAll(list);
    }

    public ArrayList<T> getObjectsList() {
        return objectsList;
    }

    public ArrayList<Integer> getDeletedList() {
        return deletedList;
    }
    
    public ArrayList<PackToSendToServer> getClientFeedback() {
        return clientFeedback;
    }
    
    public Random getRandomized() {
        return randomizer;
    }
    
    public void addConnectedClient(String name){
        this.connectedClients.add(name);
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

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
