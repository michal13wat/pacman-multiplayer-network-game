package com.brzozaxd.connection.common;

import java.io.Serializable;
import java.util.Objects;

/*
 *  Nazwa klasy jest logiczna jeżeli patrzy się od strony klieta.
 *  Żeby nie toworzyć nowych klas osobno dla servera, użyję tych.
 *  */

public class PackToSendToServer implements Serializable{
    private int playersId;
    private String playersName;
    private int character;
    private String pressedKey;
    private boolean ready;

    public PackToSendToServer(String playersName, int character, String pressedKey, int playersId, boolean ready){
        this.playersName = playersName;
        this.character = character;
        this.pressedKey = pressedKey;
        this.playersId = playersId;
        this.ready = ready;
    }

    public void setPlayersId(int playersId) {
        this.playersId = playersId;
    }
    
    public void setPlayersName(String playersName) {
        this.playersName = playersName;
    }

    public int getPlayersId() {
        return playersId;
    }
    
    public String getPlayersName() {
        return playersName;
    }
    
    public boolean isPlayerReady() {
        return ready;
    }

    public int getCharacter() {
        return character;
    }

    public String getPressedKey() {
        return pressedKey;
    }

    public boolean isEquals(PackToSendToServer pack){
        if (pack == null) return  false;
        if (this.playersId != pack.playersId) return false;
        if (!Objects.equals(this.playersName, pack.playersName)) return false;
        if (this.character != pack.character) return  false;
        if (!Objects.equals(this.pressedKey, pack.pressedKey)) return false;
        return  true;
    }

    public PackToSendToServer copy(){
        PackToSendToServer out = new PackToSendToServer(this.playersName, this.character,
                this.pressedKey, this.playersId, this.ready);
        return out;
    }
}
