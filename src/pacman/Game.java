
package pacman;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;

import java.io.*;

import java.awt.event.*;

public class Game
{
    class GameObjectComparator implements Comparator<GameObject> {
        // A private class made for sorting objects by depth.
        
        @Override
        public int compare(GameObject obj1, GameObject obj2){
            int d1 = obj1.getDepth(), d2 = obj2.getDepth();
            
            if (d1 > d2) return -1;
            else if (d1 < d2) return 1;
            else return 0; 
        }
    }
    
    public void init(){
        // Game parameters.
        
        running = true;
        
        framesPerSecond = 60;
        framesSkip = 1000/framesPerSecond;
        max_render_skip = 10;
        
        startingLives = new IntWrapper(3);
        numberOfGhost = new IntWrapper(4);
        isPacmanPlayed = new IntWrapper(0);
        
        objectList = new ArrayList();
        
        // Window init.
        windowInit(true);
        // Keyboard init.
        keyboardInit();
        
        gotoMenu("start");
        //createObject(LabyrinthObject.class);
        
        globalCounter = 0;
        gameLoop();
    }
    
    public GameObject createObject(Class ourClass){
        // Full procedure for adding a new object into the game.
        
        GameObject gameObject;
        
        try{
            gameObject = (GameObject) ourClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException i) {
            gameObject = new TestObject();
        }
        
        gameObject.game = this;
        gameObject.createEvent();
        gameObject.setPlayed();
        objectList.add(gameObject);
        
        return gameObject;
    }
    
    public GameObject getObject(Class ourClass){
        // Returns first added object of said class.
        // If there are none, returns null.
        
        GameObject gameObject;
        
        for (int i = 0; i < objectList.size(); i ++) {
            gameObject = objectList.get(i);
            if (objectList.get(i).getClass() == ourClass) return gameObject;
        }
        
        return null;
    }
    
    public ArrayList<GameObject> getAllObjects(Class ourClass)
    {
        // Similar to getObject, returns a list of all said objects.
        
        GameObject gameObject;
        ArrayList<GameObject> ourList = new ArrayList();
        
        for (int i = 0; i < objectList.size(); i ++){
            gameObject = objectList.get(i);
            if (ourClass.isInstance(gameObject)) ourList.add(gameObject);
        }
        
        return ourList;
    }
    
    public boolean keyboardCheck(String key){
        // Returns whether key is pressed.
        switch (key){
            case "left": return leftPressed;
            case "right": return rightPressed;
            case "up": return upPressed;
            case "down": return downPressed;
            case "escape": return escapePressed;
            case "enter": return enterPressed;
            case "q": return qPressed;
        }
        
        return false;
    }
    
    public boolean keyboardHoldCheck(String key){
        // Returns whether key is pressed.
        switch (key) {
            case "left": return leftHold;
            case "right": return rightHold;
            case "up": return upHold;
            case "down": return downHold;
            case "escape": return escapeHold;
            case "enter": return enterHold;
            case "q": return qHold;
        }
        return false;
    }
    
    public void gotoMenu(String which){
        switch (which){
            case "start":{
                startMenu();
            }
            break;
            case "stage_select":{
                stageSelectMenu();
            }
            break;
            case "server_setup":{
                serverSetupMenu();
            }
            break;
        }
    }
    
    public void endGame(boolean victory) {
        isPlayedGhostCreated = false;
        GameObject labirynt = getObject(LabyrinthObject.class);
        labirynt.destroy();
        
        gotoMenu("stage_select");
    }
    
    private void gameLoop() {
        // Handles consistent FPS rate.
        double nextStep = System.currentTimeMillis();
        int loops;
        
        while (running){
            loops = 0;
            
            while ((System.currentTimeMillis() > nextStep) && (loops < max_render_skip)) {
                gameStep();
                
                nextStep += framesSkip;
                globalCounter ++;
                loops ++;
            }            
            if (running) gameDraw();
            if (escapePressed) running = false;
        }
        
        gameWindow.setVisible(false);
        gameWindow.dispose();
    }
    
    private void gameStep() {
        // Runs the designated Step code for each and every object.
        // Deletes the "destroyed" ones.
        
        GameObject gameObject;
        
        for (int i = 0; i < objectList.size(); i++) {
            gameObject = objectList.get(i);
            
            if (!gameObject.isDestroyed()) gameObject.stepEvent();
        }
        
        for (int i = 0; i < objectList.size(); i++) {
            gameObject = objectList.get(i);
            
            if (gameObject.isDestroyed()) {
                gameObject.destroyEvent();
                objectList.remove(i);
            }
        }
        
        keyboardSetHold();
    }
    
    private void gameDraw() {
        // Same as game_step, but triggers Draw code.
        // Pastes drawn frame onto JPanel.
        
        BufferedImage buf = new BufferedImage(
                gameWindow.getSize().width,gameWindow.getSize().height,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D)buf.getGraphics();
        
        // Sort the list according to depth.
        Collections.sort(objectList,new GameObjectComparator());
        
        setSize();
        
        GameObject gameObject;
        for (int i = 0; i < objectList.size(); i++) {
            gameObject = objectList.get(i);
            
            gameObject.setScale(drawScale);
            gameObject.setCenter(drawCenterX,drawCenterY);
            gameObject.drawEvent(graphics);
        }
        
        graphics = (Graphics2D)gameRenderer.getGraphics();
        graphics.drawImage(buf,0,0,gameRenderer);
    }
    
    private void setSize() {
        // Setting the size according to window dimensions and labyrinth / menu height.
        LabyrinthObject labirynth;
        MenuObject menu;
        
        drawScale = 1.0;
        drawCenterX = 0;
        drawCenterY = 0;
        
        labirynth = (LabyrinthObject)getObject(LabyrinthObject.class);
        
        if (labirynth != null) {
            if (labirynth.getHeight() >= labirynth.getWidth()) {
                drawScale = (double)gameWindow.getSize().height/(
                        labirynth.getHeight()+labirynth.getY()+16);
                drawCenterX = (int)(gameWindow.getSize().width/2.0-drawScale*(
                        labirynth.getWidth()+labirynth.getX()+72)/2.0);
                drawCenterY = (int)(gameWindow.getSize().height/2.0-drawScale*(
                        labirynth.getHeight()+labirynth.getY()+16)/2.0);
            }
            else {
                drawScale = (double)gameWindow.getSize().width/(
                        labirynth.getWidth() + labirynth.getX()+88);
                drawCenterX = (int)(gameWindow.getSize().width / 2.0 - drawScale * (
                        labirynth.getWidth() + labirynth.getX()+88)/2.0);
                drawCenterY = (int)(gameWindow.getSize().height / 2.0 - drawScale * (
                        labirynth.getHeight() + labirynth.getY()+16)/2.0);
            }
        }
        else {
            menu = (MenuObject)getObject(MenuObject.class);
            
            drawScale = (double)gameWindow.getSize().height/(Math.max(
                    96,menu.getMenuHeight())+16);
            drawCenterX = (int)(gameWindow.getSize().width/2.0-drawScale*(
                    menu.getMenuWidth()+menu.getX())/2.0);
            drawCenterY = (int)(gameWindow.getSize().height/2.0-drawScale*(
                    Math.max(96,menu.getMenuHeight())-menu.getY())/2.0);
        }
    }
    
    private void startMenu()
    {
        MenuObject startMenu = (MenuObject)createObject(MenuObject.class);
        startMenu.setFont("/resources/pac_font_sprites.png",8,8);
        startMenu.setTitle("PACMAN");
        
        startMenu.addMenuOption("Single player",() -> {
                    gotoMenu("stage_select");
                    return 1;
                });
        startMenu.addMenuOption("Multiplayer",() -> {
                    gotoMenu("server_setup");
                    return 1;
                });
        
        startMenu.addMenuOption("EXIT", () -> {
                running = false;
                return 1;
            });
        startMenu.addButtonPressOption("exitOnQ",()-> {
                    running = false;
                    return 1;
                }, "q" );
    }
    
    private void stageSelectMenu()
    {
        MenuObject stageSelectMenu = (MenuObject)createObject(MenuObject.class);
        stageSelectMenu.setFont("/resources/pac_font_sprites.png",8,8);
        stageSelectMenu.setTitle("STAGE SELECT");

        ArrayList<Sprite> sprites = new ArrayList();
        sprites.add(new Sprite("/resources/pac_hero_sprites.png",0,0,16,16));
        sprites.add(new Sprite("/resources/pac_ghost_sprites.png",0,0,16,16));
        sprites.add(new Sprite("/resources/pac_ghost_sprites.png",0,1,16,16));
        sprites.add(new Sprite("/resources/pac_ghost_sprites.png",0,2,16,16));
        sprites.add(new Sprite("/resources/pac_ghost_sprites.png",0,3,16,16));

        /*sprites.add(new Sprite("/resources/pac_font_sprites.png",21,3,8,8));
        sprites.add(new Sprite("/resources/pac_font_sprites.png",22,3,8,8));
        sprites.add(new Sprite("/resources/pac_font_sprites.png",23,3,8,8));
        sprites.add(new Sprite("/resources/pac_font_sprites.png",24,3,8,8));
        sprites.add(new Sprite("/resources/pac_font_sprites.png",25,3,8,8));*/
        stageSelectMenu.addImageSpinnerOption("Character ", null, isPacmanPlayed, 0, 1, sprites);

        stageSelectMenu.addSpinnerOption("Lives ",null,startingLives,1,5);
        stageSelectMenu.addSpinnerOption("Ghosts ", null, numberOfGhost, 1, 4);

        // Loading all .txt files in "/resources/labyrinths" as stages.
        File folder = new File("src/resources/stages");
        File[] allLabyrinths = folder.listFiles();

        InputStream in;
        String stageName;
        int c;

        for (File f : allLabyrinths) {
            try {
                // Reading the stage name.
                in = new FileInputStream(f);

                stageName = "";
                while ((c = in.read()) != '\n') 
                    stageName += Character.toString((char)c);
            } catch (FileNotFoundException e){
                System.err.println("Error: File not found");
                stageName = "ERROR";
            } catch (IOException e){
                System.err.println("Exception: IOException");
                stageName = "ERROR";
            }

            // New callable.
            final File finalFile = f;
            stageSelectMenu.addMenuOption(stageName,() -> {
                        LabyrinthObject l = (LabyrinthObject)createObject(LabyrinthObject.class);
                        l.setSource(finalFile);
                        return 1;
                    });
        }

        stageSelectMenu.addMenuOption("BACK", () -> {
                gotoMenu("start");
                return 1;
            });

        stageSelectMenu.addButtonPressOption("exitOnQ",()-> {
                    running = false;
                    return 1;
                }, "q" );
    }
    
    private void serverSetupMenu()
    {
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.setFont("/resources/pac_font_sprites.png",8,8);
        menu.setTitle("IP SETUP");
        
        menu.addStringInputOption("Server IP: ",null,testString,5);
        menu.addMenuOption("Create Game",null);
        menu.addMenuOption("Join Game",null);
        
        menu.addMenuOption("BACK", () -> {
                gotoMenu("start");
                return 1;
            });
        menu.addButtonPressOption("exitOnQ",()-> {
                    running = false;
                    return 1;
                }, "q" );
    }
    
    private void windowInit(boolean fullscreen) {
        // Adds a new JFrame with a single JPanel.
        
        gameWindow = new JFrame("Testing");
        
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Insets tmp_ins = gameWindow.getInsets();
        gameWindow.setSize(
                2*256+tmp_ins.left+tmp_ins.right,
                2*224+tmp_ins.top+tmp_ins.bottom);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setVisible(true);
        
        gameRenderer = new JPanel();
        gameWindow.add("Center",gameRenderer);
        
        // Fullscreen.
        
        if (fullscreen) gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void keyboardInit() {
        // Adds a new listener for following keys:
        
        gameWindow.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case (KeyEvent.VK_LEFT): leftPressed = true;
                        break;
                    case (KeyEvent.VK_RIGHT): rightPressed = true;
                        break;
                    case (KeyEvent.VK_UP): upPressed = true;
                        break;
                    case (KeyEvent.VK_DOWN): downPressed = true;
                        break;
                    case (KeyEvent.VK_ESCAPE): escapePressed = true;
                        break;
                    case (KeyEvent.VK_ENTER): enterPressed = true;
                        break;
                    case (KeyEvent.VK_Q): qPressed = true; 
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case (KeyEvent.VK_LEFT): leftPressed = false; 
                        break;
                    case (KeyEvent.VK_RIGHT): rightPressed = false; 
                        break;
                    case (KeyEvent.VK_UP): upPressed = false;
                        break;
                    case (KeyEvent.VK_DOWN): downPressed = false;
                        break;
                    case (KeyEvent.VK_ESCAPE): escapePressed = false;
                        break;
                    case (KeyEvent.VK_ENTER): enterPressed = false;
                        break;
                    case (KeyEvent.VK_Q): qPressed = false;
                        break;
                }
            }
        });
    }
    
    private void keyboardSetHold() {
        leftHold = leftPressed;
        rightHold = rightPressed;
        upHold = upPressed;
        downHold = downPressed;
        escapeHold = escapePressed;
        enterHold = enterPressed;
        qHold = qPressed;
    }
    
    
    
    public static void main(String[] args) {
        Game new_game = new Game();
        new_game.init();
    }
    
    private JFrame gameWindow;
    private JPanel gameRenderer;
    private boolean running;
    
    private int globalCounter;
    
    private ArrayList<GameObject> objectList;
    
    private int framesPerSecond;
    private int framesSkip;
    private int max_render_skip;
    
    private String testString = "TEST";
    
    private IntWrapper isPacmanPlayed;
    private IntWrapper numberOfGhost;
   
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean downPressed;
    private boolean escapePressed;
    private boolean enterPressed;
    private boolean qPressed;
    
    private boolean leftHold;
    private boolean rightHold;
    private boolean upHold;
    private boolean downHold;
    private boolean escapeHold;
    private boolean enterHold;
    private boolean qHold;
    
    private int gameScore;
    private int gameLives;
    private IntWrapper startingLives;
    
    private double drawScale;
    private int drawCenterX, drawCenterY;
    
    private boolean isPlayedGhostCreated = false;
    
    public boolean isPlayedGhostCreated(){
        return isPlayedGhostCreated;
    }
    
    public void setPlayedGhostCreated(boolean is){
        isPlayedGhostCreated = is;
    }
    
    public void close(){
        running = false;
    }
    
    public void addLives(int p){
        gameLives += p;
        if (gameLives < 0) gameLives = 0;
    }
    
    public int getLives() {
        return gameLives;
    }
    
    public void setLives(int p){
        if(p > 0) gameLives = p;
    }
    
    public int getStartingLives(){
        return startingLives.value;
    }
    
    public boolean isPacmanPlayed(){
        return (isPacmanPlayed.value == 0);
    }
    
    public int getNumberOfGhosts(){
        return numberOfGhost.value;
    }
    
    public void addScore(int p){
        gameScore += p;
        if (gameScore < 0) gameScore = 0;
    }
    
    public int getScore() {
        return gameScore;
    }
    
    public void setScore(int s) {
        if (s > 0) gameScore = s;
        else gameScore = 0;
    }
}

