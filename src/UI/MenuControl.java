
package UI;

import game.objects.LabyrinthObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.net.URL;

import game.pacman.ClientGame;
import game.pacman.Game;
import game.pacman.Sprite;

import game.objects.GameObject;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

// Od teraz, w tej klasie jest wszystko związane z ustawieniami Menu.

public class MenuControl {
    
    public MenuControl(Game game) {
        this.game = game;

        sprites.add(new Sprite("pac_hero_sprites",0,0,16,16));
        sprites.add(new Sprite("pac_ghost_sprites",0,0,16,16));
        sprites.add(new Sprite("pac_ghost_sprites",0,1,16,16));
        sprites.add(new Sprite("pac_ghost_sprites",0,2,16,16));
        sprites.add(new Sprite("pac_ghost_sprites",0,3,16,16));
    }
    
    public GameObject createObject(Class ourClass)
    {return game.createObject(ourClass);}
    
    public void gotoMenu(String which){
        for (GameObject o : game.getAllObjects(MenuObject.class))
        {o.destroy();}
        for (GameObject o : game.getAllObjects(TextObject.class))
        {o.destroy();}
        
        switch (which){
            case "start":{
                startMenu();
            }
            break;
            case "test":{
                testMenu();
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
            case  "create_game":{
                createGameMenu();
            }
            break;
            case  "join_game":{
                joinGameMenu();
            }
            break;
            case  "game_lobby":{
                gameLobbyMenu();
            }
            break;
        }
    }
    
    private void testMenu() {
        MenuObject startMenu = (MenuObject)createObject(MenuObject.class);
        startMenu.setFont("pac_font_sprites",8,8);
        startMenu.setTitle("TEST");
        
        startMenu.addMenuOption("Huh",null);
    }
    
    private void startMenu() {
        MenuObject startMenu = (MenuObject)createObject(MenuObject.class);
        startMenu.setFont("pac_font_sprites",8,8);
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
                game.close();
                return 1;
            });
        startMenu.addButtonPressOption(()-> {
                    game.close();
                    return 1;
                });
    }
    
    private void stageSelectMenu() {
        MenuObject stageSelectMenu = (MenuObject)createObject(MenuObject.class);
        stageSelectMenu.setFont("pac_font_sprites",8,8);
        stageSelectMenu.setTitle("SINGLE PLAYER");

        stageSelectMenu.addImageSpinnerOption(game.chosenCharacter, sprites);
        stageSelectMenu.addSpinnerOption("Lives ", game.startingLives, 5);
        stageSelectMenu.addSpinnerOption("Ghosts ", game.ghostsAmount, 4);

        // Ładowanie wszystkich plików .txt z "/resources/stages" jako poziomy.
        try
        {
            URL dirURL = getClass().getResource("/resources/stages");
            
            File folder = null;
            File[] allLabyrinths = null;
            
            if (dirURL.getProtocol().equals("jar")) {
                
                String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar

                InputStream in;
                String stageName;
                int c;
                
                while(entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    
                    if (name.startsWith("resources/stages/pac")) { //filter according to the path
                        
                                                try {
                              // Nazwa poziomu w pierwszej linii.
                              in = (getClass().getResourceAsStream("/"+name));

                              stageName = "";
                              while ((c = in.read()) != '\n') 
                                  stageName += Character.toString((char)c);
                          } catch (FileNotFoundException e){
                              System.err.println("Error: File not found");
                              stageName = "ERROR";
                          } catch (IOException e){
                              System.err.println("Exception: IOException");
                              stageName = "ERROR";
                          } catch (Exception e) {stageName = e.getMessage();}

                          // Nowy Callable.
                          stageSelectMenu.addMenuOption(stageName,() -> {
                                      LabyrinthObject l = (LabyrinthObject)createObject(LabyrinthObject.class);
                                      l.setSource("/"+name,true);
                                      return 1;
                                  });
                    }
                }
            }
            else {
                folder = new File(dirURL.toURI());
                allLabyrinths = folder.listFiles();

                InputStream in;
                String stageName;
                int c;

                for (File f : allLabyrinths) {
                    try {
                        // Nazwa poziomu w pierwszej linii.
                        in = new FileInputStream(f.getPath());

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

                    // Nowy Callable.
                    stageSelectMenu.addMenuOption(stageName,() -> {
                                LabyrinthObject l = (LabyrinthObject)createObject(LabyrinthObject.class);
                                l.setSource(f.getPath(),false);
                                return 1;
                            });
                }
            }
        }
        catch (Exception ignored)
        {}

        stageSelectMenu.addMenuOption("BACK", () -> {
                gotoMenu("start");
                return 1;
            });

        stageSelectMenu.addButtonPressOption(()-> {
                    gotoMenu("start");
                    return 1;
                });
    }
    
    private void serverSetupMenu() {
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("MULTIPLAYER");

        menu.addMenuOption("Create Game", () -> {
            gotoMenu("create_game");
            return 1;
        });
        menu.addMenuOption("Join Game", () -> {
            gotoMenu("join_game");
            return 1;
        });
        menu.addMenuOption("BACK", () -> {
                gotoMenu("start");
                return 1;
            });
        menu.addButtonPressOption(()-> {
                    gotoMenu("start");
                    return 1;
                });
    }

    private void createGameMenu() {
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("CREATE GAME");
        
        /* Prócz uruchomienia servera trzeba tutaj uruchomić jednego klienta lokalnie */
        menu.addMenuOption("Start", ()-> {
            game.getExecutor().submit(game.callableStartSever);
            Game.ipString.value = "127.0.0.1";
            //portString.value - takie jak zostało odczytane z MENU, czyli bez zmian
            Game.playerNumber.value = 0;
            
            game.startClient(Game.ipString.value, Game.portString.value, Game.playerNumber.value);
            return 1;
        });

        menu.addStringInputOption("Name: ", game.playerName, 7);
        menu.addNumberInputOption(Game.portString, 5);

        menu.addMenuOption("BACK", () -> {
            gotoMenu("server_setup");
            return 1;
        });
        menu.addButtonPressOption(()-> {
            gotoMenu("server_setup");
            return 1;
        });
    }
    
    private void joinGameMenu() {
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("JOIN GAME");

        menu.addMenuOption("Join",() -> {
            game.startClient(Game.ipString.value, Game.portString.value, Game.playerNumber.value);
            return 1;
        });

        menu.addStringInputOption("Name: ", game.playerName, 7);
        menu.addSpinnerOption("Player ID: ", Game.playerNumber, 3);
        menu.addStringInputOption("IP: ", Game.ipString, 14/*,"xxx.xxx.x.xx",9*/);
        menu.addNumberInputOption(Game.portString, 4);
        menu.addMenuOption("BACK", () -> {
            gotoMenu("server_setup");
            return 1;
        });
        menu.addButtonPressOption(()-> {
            gotoMenu("server_setup");
            return 1;
        });
    }
    
    private void gameLobbyMenu() {
        
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("GAME LOBBY");

        menu.addImageSpinnerOption(game.characterBlocked,
                                        game.chosenCharacter, sprites);
        menu.addMenuOption("READY",() -> {
            
            ClientGame clientGame = (ClientGame)game;
            clientGame.setReady();
            gotoMenu("game_lobby");
            
            return 1;
        });
        menu.addMenuOption("Exit Server", () -> {
            game.close();
            return 1;
        });
        menu.addButtonPressOption(()-> {
            game.close();
            return 1;
        });
    }

    private Game game;
    private ArrayList<Sprite> sprites = new ArrayList();
}
