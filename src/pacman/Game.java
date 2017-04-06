
package pacman;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;

import java.io.*;

import java.awt.event.*;
import java.util.concurrent.Callable;

public class Game
{
    class GameObjectComparator implements Comparator<GameObject>
    {
        // A private class made for sorting objects by depth.
        
        @Override
        public int compare(GameObject obj1, GameObject obj2)
        {
            int d1 = obj1.getDepth(), d2 = obj2.getDepth();
            
            if (d1 > d2)
            {return -1;}
            else if (d1 < d2)
            {return 1;}
            else
            {return 0;}
        }
    }

    // MB >
    class MenuHandling implements ActionListener, ItemListener {
        public MenuHandling(){
            menuItem.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //...Get information from the action event...

            // File
            if (e.getActionCommand() == "Start"){
                stop = false;
            } else if (e.getActionCommand() == "Stop") {
                stop = true;
            } else if (e.getActionCommand() == "Wyjście"){
                running = false;
            }else
                // Settings
                if (e.getActionCommand() == "Konfiguracja serwera"){
                    stop = true;
                    serverConf = new ServerConfiguration();
                }else if (e.getActionCommand() == "Konfiguracja gry"){
                    stop = true;
                    gameConf = new GameConfiguration();
                    gameConf.refreshChoice();
                /* TODO
                * I don't know why when I call above methods it
                * it throws exception. */
                }

        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            //...Get information from the item event...
            //...Display it in the text area...
            System.out.print("ItemEvent\n");
            System.out.print(e.getItem());
            System.out.print("\n");
        }
    }

    // MB <
    
    public void init()
    {
        // Game parameters.
        
        running = true;
        
        framesPerSecond = 60;
        framesSkip = 1000/framesPerSecond;
        max_render_skip = 10;
        
        startingLives = new IntWrapper(3);
        
        objectList = new ArrayList<GameObject>();
        
        // Window init.
        windowInit(true);
        // Keyboard init.
        keyboardInit();

        turnOnGame = true;
//        while (true){
//            if (turnOnGame){
                gotoMenu("stage_select");
//                System.out.print("asdf");
//            } else{
//                break;
//            }
//        }
        //createObject(LabyrinthObject.class);

        // MB >
        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("Plik");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        //File
        menuItem = new JMenuItem("Start",
                new  ImageIcon("/resources/pac_ghost_sprites.png"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        menuItem = new JMenuItem("Stop",
                new ImageIcon("resources/menu_icons/Exit.png"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        menu.addSeparator();

        menuItem = new JMenuItem("Wyjście",
                new ImageIcon("/resources/menu_icons/Exit.png"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        //Settings.
        menu = new JMenu("Ustawienia");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        menuItem = new JMenuItem("Konfiguracja serwera",
                new ImageIcon("/resources/menu_icons/Exit.png"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        menuItem = new JMenuItem("Konfiguracja gry",
                new ImageIcon("/resources/menu_icons/Exit.png"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        // Help
        menu = new JMenu("Pomoc");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        menuItem = new JMenuItem("Jak w to grać?",
                new ImageIcon("/resources/menu_icons/Exit.png"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        menuItem = new JMenuItem("O programie",
                new ImageIcon("/resources/menu_icons/Exit.png"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        menuItem = new JMenuItem("Wspomóż autorów",
                new ImageIcon("/resources/menu_icons/Exit.png"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);
        menu.addActionListener(new MenuHandling());

        gameWindow.setJMenuBar(menuBar);

        // MB <


        globalCounter = 0;
        gameLoop();
    }
    
    public GameObject createObject(Class ourClass)
    {
        // Full procedure for adding a new object into the game.
        
        GameObject o;
        
        try
        {
            o = (GameObject)ourClass.newInstance();
        }
        catch (InstantiationException i)
        {o = new TestObject();}
        catch (IllegalAccessException i)
        {o = new TestObject();}
        
        o.game = this;
        objectList.add(o);
        
        return o;
    }
    
    public GameObject getObject(Class ourClass)
    {
        // Returns first added object of said class.
        // If there are none, returns null.
        
        GameObject o;
        
        for (int i = 0; i < objectList.size(); i ++)
        {
            o = objectList.get(i);
            
            if (objectList.get(i).getClass() == ourClass)
            {return o;}
        }
        
        return null;
    }
    
    public ArrayList<GameObject> getAllObjects(Class ourClass)
    {
        // Similar to getObject, returns a list of all said objects.
        
        GameObject o;
        ArrayList<GameObject> ourList = new ArrayList();
        
        for (int i = 0; i < objectList.size(); i ++)
        {
            o = objectList.get(i);
            
            if (ourClass.isInstance(o))
            {ourList.add(o);}
        }
        
        return ourList;
    }
    
    public boolean keyboardCheck(String key)
    {
        // Returns whether key is pressed.

        if (key.equals("left")) {
            return leftPressed;
        } else if (key.equals("right")) {
            return rightPressed;
        } else if (key.equals("up")) {
            return upPressed;
        } else if (key.equals("down")) {
            return downPressed;
        } else if (key.equals("escape")) {
            return escapePressed;
        } else if (key.equals("enter")) {
            return enterPressed;
        } else if (key.equals("q")) {
            return qPressed;
        }
        
        return false;
    }
    
    public boolean keyboardHoldCheck(String key)
    {
        // Returns whether key is pressed.

        if (key.equals("left")) {
            return leftHold;
        } else if (key.equals("right")) {
            return rightHold;
        } else if (key.equals("up")) {
            return upHold;
        } else if (key.equals("down")) {
            return downHold;
        } else if (key.equals("escape")) {
            return escapeHold;
        } else if (key.equals("enter")) {
            return enterHold;
        } else if (key.equals("q")) {
            return qHold;
        }
        
        return false;
    }
    
    public void gotoMenu(String which)
    {
        if (which.equals("stage_select")) {
            MenuObject startMenu = (MenuObject) createObject(MenuObject.class);
            startMenu.setFont("/resources/pac_font_sprites.png", 8, 8);
            startMenu.setTitle("STAGE SELECT");
            startMenu.addSpinnerOption("Lives ", null, startingLives, 1, 5);
            File folder = new File("src/resources/stages");
            final File[] allLabyrinths = folder.listFiles();
            InputStream in;
            String stageName;
            int c;
            for (File f : allLabyrinths) {
                try {
                    // Reading the stage name.
                    in = new FileInputStream(f);

                    stageName = "";
                    while ((c = in.read()) != '\n') {
                        stageName += Character.toString((char) c);
                    }
                } catch (FileNotFoundException e) {
                    stageName = "ERROR";
                } catch (IOException e) {
                    stageName = "ERROR";
                }

                // New callable.
                final File finalFile = f;
                startMenu.addMenuOption(stageName,
                        new Callable<Integer>() {
                            @Override
                            public Integer call() {
                                LabyrinthObject l = (LabyrinthObject) createObject(LabyrinthObject.class);
                                l.setSource(finalFile);
                                turnOnGame = true;
                                return 1;
                            }
                        }
                );
            }
            startMenu.addMenuOption("Settings",
                    new Callable<Integer>() {
                        @Override
                        public Integer call() {
                            System.out.print("Settings\n\n");
//                            final File finalFile = allLabyrinths[0];
//                            LabyrinthObject l = (LabyrinthObject) createObject(LabyrinthObject.class);
//                            l.setSource(finalFile);
                            turnOnGame = false;
                            return 1;
                        }
                    }
            );
            startMenu.addMenuOption("EXIT",
                    new Callable<Integer>() {
                        @Override
                        public Integer call() {
                            turnOnGame = true;
                            running = false;
                            return 1;
                        }
                    }
            );
            startMenu.addButtonPressOption("exitOnQ",
                    new Callable<Integer>() {
                        @Override
                        public Integer call() {
                            turnOnGame = true;
                            running = false;
                            return 1;
                        }
                    },
                    "q"
            );

        }
    }
    
    public void endGame(boolean victory)
    {
        GameObject l = getObject(LabyrinthObject.class);
        l.destroy();
        
        gotoMenu("stage_select");
    }
    
    private void gameLoop()
    {
        // Handles consistent FPS rate.
        
        double nextStep = System.currentTimeMillis();
        int loops = 0;
        
        while (running == true)
        {
            loops = 0;
            
            while ((System.currentTimeMillis() > nextStep)
                && (loops < max_render_skip))
            {
                // MB >
                if (!stop) {
                    gameStep();
                }
                // MB <


                nextStep += framesSkip;
                globalCounter ++;
                loops ++;
            }
            
            if (running)
            {gameDraw();}
            
            if (escapePressed)
            {running = false;}
        }
        
        gameWindow.setVisible(false);
        gameWindow.dispose();
    }
    
    private void gameStep()
    {
        // Runs the designated Step code for each and every object.
        // Deletes the "destroyed" ones.
        
        GameObject o;
        
        for (int i = 0; i < objectList.size(); i++)
        {
            o = objectList.get(i);
            
            if (!o.isDestroyed())
            {o.stepEvent();}
        }
        
        for (int i = 0; i < objectList.size(); i++)
        {
            o = objectList.get(i);
            
            if (o.isDestroyed())
            {
                o.destroyEvent();
                objectList.remove(i);
            }
        }
        
        keyboardSetHold();
    }
    
    private void gameDraw()
    {
        // Same as game_step, but triggers Draw code.
        // Pastes drawn frame onto JPanel.
        
        BufferedImage buf = new BufferedImage(
                gameWindow.getSize().width,gameWindow.getSize().height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D)buf.getGraphics();
        
        // Sort the list according to depth.
        Collections.sort(objectList,new GameObjectComparator());
        
        setSize();
        
        GameObject o;
        for (int i = 0; i < objectList.size(); i++)
        {
            o = objectList.get(i);
            
            o.setScale(drawScale);
            o.setCenter(drawCenterX,drawCenterY);
            o.drawEvent(g);
        }

//        MenuBar temp;
//        for (int i = 0; i < menuBar.getMenuCount() ; i++){
//            temp = menuBar.
//        }
//        menuBar.repaint();
          menuBar.updateUI();
//        gameWindow.setJMenuBar(menuBar);    // MB
//        JMenuBar menuElement;
//        for (menuElement : menuBar){
//
//        }
        
        g = (Graphics2D)gameRenderer.getGraphics();
        g.drawImage(buf,300,0,gameRenderer);
    }
    
    private void setSize()
    {
        // Setting the size according to window dimensions and labyrinth / menu height.
        LabyrinthObject l;
        MenuObject m;
        
        drawScale = 1.0;
        drawCenterX = 0;
        drawCenterY = 0;
        
        l = (LabyrinthObject)getObject(LabyrinthObject.class);
        
        if (l != null)
        {
            if (l.getHeight() >= l.getWidth())
            {
                drawScale = (double)gameWindow.getSize().height/(l.getHeight()+l.getY()+16);
                drawCenterX = (int)(gameWindow.getSize().width/2.0-drawScale*(l.getWidth()+l.getX()+72)/2.0);
                drawCenterY = (int)(gameWindow.getSize().height/2.0-drawScale*(l.getHeight()+l.getY()+16)/2.0);
            }
            else
            {
                drawScale = (double)gameWindow.getSize().width/(l.getWidth()+l.getX()+88);
                drawCenterX = (int)(gameWindow.getSize().width/2.0-drawScale*(l.getWidth()+l.getX()+88)/2.0);
                drawCenterY = (int)(gameWindow.getSize().height/2.0-drawScale*(l.getHeight()+l.getY()+16)/2.0);
            }
        }
        else
        {
            m = (MenuObject)getObject(MenuObject.class);
            
            drawScale = (double)gameWindow.getSize().height/(Math.max(96,m.getMenuHeight())+16);
            drawCenterX = (int)(gameWindow.getSize().width/2.0-drawScale*(m.getMenuWidth()+m.getX())/2.0);
            drawCenterY = (int)(gameWindow.getSize().height/2.0-drawScale*(Math.max(96,m.getMenuHeight())-m.getY())/2.0);
        }
    }
    
    private void windowInit(boolean fullscreen)
    {
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
        
        if (fullscreen)
        {gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);}
    }
    
    private void keyboardInit()
    {
        // Adds a new listener for following keys:
        
        gameWindow.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case (KeyEvent.VK_LEFT): {leftPressed = true;} break;
                    case (KeyEvent.VK_RIGHT): {rightPressed = true;} break;
                    case (KeyEvent.VK_UP): {upPressed = true;} break;
                    case (KeyEvent.VK_DOWN): {downPressed = true;} break;
                    case (KeyEvent.VK_ESCAPE): {escapePressed = true;} break;
                    case (KeyEvent.VK_ENTER): {enterPressed = true;} break;
                    case (KeyEvent.VK_Q): {qPressed = true;} break;
                    // MB >
                    case (KeyEvent.VK_P) : stop = !stop; break;
                    // MB <
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                    case (KeyEvent.VK_LEFT): {leftPressed = false;} break;
                    case (KeyEvent.VK_RIGHT): {rightPressed = false;} break;
                    case (KeyEvent.VK_UP): {upPressed = false;} break;
                    case (KeyEvent.VK_DOWN): {downPressed = false;} break;
                    case (KeyEvent.VK_ESCAPE): {escapePressed = false;} break;
                    case (KeyEvent.VK_ENTER): {enterPressed = false;} break;
                    case (KeyEvent.VK_Q): {qPressed = false;} break;
                }
            }
        });
    }
    
    private void keyboardSetHold()
    {
        leftHold = leftPressed;
        rightHold = rightPressed;
        upHold = upPressed;
        downHold = downPressed;
        escapeHold = escapePressed;
        enterHold = enterPressed;
        qHold = qPressed;
    }
    
    public static void main(String[] args)
{
    Game new_game = new Game();
    new_game.init();
}
    
    private JFrame gameWindow;
    private JPanel gameRenderer;
    // MB >
    //Where the GUI is created:
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;
    // MB <

    private boolean running;

    // MB >
    private boolean stop = false;
    private ServerConfiguration serverConf;
    private GameConfiguration gameConf;
    // MB <

    private int globalCounter;
    
    private ArrayList<GameObject> objectList;
    
    private int framesPerSecond;
    private int framesSkip;
    private int max_render_skip;
    
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

    private boolean turnOnGame = true; // MB

    private int gameScore;
    private int gameLives;
    private IntWrapper startingLives;
    
    private double drawScale;
    private int drawCenterX, drawCenterY;
    
    public void close()
    {running = false;}
    
    public void addLives(int p)
    {gameLives += p;}
    public int getLives()
    {return gameLives;}
    public void setLives(int p)
    {gameLives = p;}
    public int getStartingLives()
    {return startingLives.value;}
    
    public void addScore(int p)
    {
        gameScore += p;
        if (gameScore < 0)
        {gameScore = 0;}
    }
    public int getScore()
    {return gameScore;}
    public void setScore(int s)
    {gameScore = s;}
}

