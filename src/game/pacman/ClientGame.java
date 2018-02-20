
package game.pacman;

import com.brzozaxd.connection.client.v2.ClientBrain;
import com.brzozaxd.connection.common.PackReceivedFromServer;
import com.brzozaxd.connection.common.PackToSendToServer;
import game.objects.GameObject;
import game.objects.LabyrinthObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import UI.*;

public class ClientGame extends Game {
    
    ClientGame(JFrame gameWindow, JPanel gameRenderer, StringWrapper playerName, IntWrapper chosenCharacter) {
        this.gameWindow = gameWindow;
        this.gameRenderer = gameRenderer;
        this.playerName = playerName;
        this.chosenCharacter = chosenCharacter;
    }
    
    @Override
    public void init(){
        System.out.println("Inicjalizacja ClientGame.");
        
        Random random = new Random();
        clientId = Math.abs(random.nextInt());

        // Parametry gry.
        running = true;
        ready = false;
        
        wrapperInit();
        
        framesPerSecond = 60;
        framesSkip = 1000/framesPerSecond;
        max_render_skip = 10;
        objectList = new ArrayList<>();
        
        playerNumbers = new HashMap<>();
        playerNames = new HashMap<>();
        playerCharacters = new HashMap<>();
        keyboardControlRemote = new HashMap<>();
        playerReady = new HashMap<>();
        
        // Klawiatura.
        keyboardControl.keyboardInit();
        keyboardControlRemote = new HashMap<>();
        // Sprite'y.
        preloadSprites();
        
        // Wątek klienta.
        String addressIP = Game.ipString.value; //processAddressIP(Game.ipString.value);
        String port = Game.portString.value;
        int playerID = Game.playerNumber.value;
        
        int portInteger = new Integer(port);
        
        client = new ClientBrain(addressIP, portInteger, portInteger+1, playerName.value, playerID);
        client.start();
        
        gotoMenu("game_lobby");
        
        PlayerDisplayObject playerDisplay = (PlayerDisplayObject)createObject(PlayerDisplayObject.class);
        playerDisplay.loadFont("pac_font_sprites",8,8);
        playerDisplay.setPosition(0,48);
        
        globalCounter = 0;
        System.out.println("Inicjalizacja ClientGame zakończona.");
        gameLoop();
    }
    
    @Override
    protected void wrapperInit() {
        
        startingLives = new IntWrapper(3);
        playersAmount = new IntWrapper(4);
        ghostsAmount = new IntWrapper(4);
        playerNumber = new IntWrapper(1);
        
        pacmanPlayer = new IntWrapper(-1);
        ghostPlayer = new IntWrapper[4];
        for (int i = 0; i < 4; i++)
            ghostPlayer[i] = new IntWrapper(-1);
        
        characterBlocked = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            characterBlocked.add(new IntWrapper(0));
    }
    
    @Override
    protected void gameLoop() {
        // FPS.
        double nextStep = System.currentTimeMillis();
        int loops;
        
        while (running){
            loops = 0;
            
            //System.out.println("KLIENT - W tej chwili " + objectList.size() + " obiektów.");

            while ((System.currentTimeMillis() > nextStep) && (loops < max_render_skip)) {
                sendInput();
                receiveObjects();
                if (!ready) gameStep();
                
                nextStep += framesSkip;
                globalCounter ++;
                loops ++;
                
                if ((System.currentTimeMillis() <= nextStep) || (loops >= max_render_skip)) {
                    if ((running) && (!halted)) {
                        gameDraw();
                    }
                }
            
                if ((keyboardCheck("escape")) || (keyboardCheck("q"))) running = false;
            }
        }
        
        client.close();
    }
    
    @Override
    public GameObject createObject(Class ourClass){
        // Obiekty, które tworzymy na kliencie nie mają prawa istnieć.
        GameObject obj = super.createObject(ourClass);
        obj.dispose();
        return obj;
    }
    
    private void sendInput()
    {
        String name;
        int character;
        String pressedKey;
        
        name = playerName.value;
        character = chosenCharacter.value;
        pressedKey = checkPressedKeys();
        System.out.println("KLIENT " + name + (ready ? ("[OK] ") : "") + " " + pressedKey);

        client.packOut = new PackToSendToServer(name, character, pressedKey, clientId, ready);
    }
    
    private void receiveObjects()
    {
        // odebranie obiektów od serwera i symulacja wyświetlenia obiektów na mapie
        if (ClientBrain.recPac != null) {
            PackReceivedFromServer<GameObject> packReceivedFromServer = ClientBrain.recPac;
            ClientBrain.recPac = null;

            System.out.print("KLIENT - Odebrano obiekty. \n");
            System.out.print("KLIENT - Connected clients: \n");
            for (int i = 0; i < packReceivedFromServer.getConnectedClients().size(); i++){
                System.out.print("\t- " + packReceivedFromServer.getConnectedClients().get(i) + "\n");
            }
            System.out.print("KLIENT - Waiting for: " + packReceivedFromServer.getNotConnectedClients() +
                    " players\n");
            
            gameScore = packReceivedFromServer.gameScore;
            gameLives = packReceivedFromServer.gameLives;
            playersAmount.value = packReceivedFromServer.maxPlayers;
            
            // Przybieranie nowej listy jako własna.
            overlapIds(packReceivedFromServer.getObjectsList());
            deleteIds(packReceivedFromServer.getDeletedList());
            LabyrinthObject labyrinth = null;
            
            if (packReceivedFromServer.getRandomized() != null) random = packReceivedFromServer.getRandomized();
            
            // Ustawianie wszystkim obiektom tej gry jako bazowej.
            for (GameObject o : objectList) {
                //System.out.println("K " + o.getClass() + " " + o.getX() + "," + o.getY());
                o.setGame(this);
                
                // Szukanie labirytnu.
                if (o instanceof LabyrinthObject) {
                    labyrinth = (LabyrinthObject)o;
                }
            }
            
            if (labyrinth != null) {
                // Powiadamianie obiektów o labiryncie.
                for (GameObject o : objectList) {
                    o.setCollisionMap(labyrinth);
                }
            }
            
            for (PackToSendToServer pack : packReceivedFromServer.getClientFeedback()){
                
                int id = pack.getPlayersId();
                
                if (id != clientId) {
                    if (!playerNumbers.containsKey(id)) {
                        // Tutaj jakiś błąd chyba...
                        System.out.print("Klient poznał nowego gracza - name = " + pack.getPlayersName()
                                + ", ID = " + id + "\n");
                        
                        playerNumbers.put(id, ++playersConnected);
                        playerNames.put(id, pack.getPlayersName());
                        playerCharacters.put(id, pack.getCharacter());
                        keyboardControlRemote.put(id, new KeyboardControlRemote(this));
                        playerReady.put(id,false);
                        
                        // Ustawianie postaci.
                        chosenCharacter.value = pack.getCharacter();
                        chooseCharacter(false,id);
                        
                        for (Integer i : keyboardControlRemote.keySet())
                            System.out.println("KLIENT - new remote keyboard - " + i);
                    }
                    
                    if ((pack.isPlayerReady()) && (!playerReady.get(id))) {
                        characterBlocked.get(pack.getCharacter()).value = 1;
                        playerReady.put(id, true);
                    }
                    else
                        playerCharacters.put(id, pack.getCharacter());
                    
                    // Ustawianie odpowiednich wejść z klawiatury
                    try
                    {((KeyboardControlRemote)getKeyboard(id)).feedInput(pack.getPressedKey());}
                    catch (Exception e) {System.out.println("WRYYYYYYYYYY");}
                }
            }
        }
    }
    
    private void overlapIds(ArrayList<GameObject> newList) {
        System.out.print("KLIENT - MAMY " + objectList.size() + " ODEBRANE: " + newList.size());
        for (GameObject oo : newList)
            objectList.removeIf(o -> (o.getId() == oo.getId()) || (o.isDisposable()));
        objectList.addAll(newList);
        System.out.print("\n");
    }
    
    private void deleteIds(ArrayList<Integer> newList) {
        for (int id : newList)
            objectList.removeIf(o -> o.getId() == id);
    }
    
    @Override
    public KeyboardControl getKeyboard(int i) {
        if (i == clientId) return keyboardControl;
        if (keyboardControlRemote.containsKey(i)) return keyboardControlRemote.get(i);
        return keyboardControl;
    }

    private ClientBrain client;
    private int playersConnected = 0;
    private int clientId;
    
    private boolean ready;
    
    public void setReady() {ready = true;}
    public boolean isReady() {return ready;}
}
