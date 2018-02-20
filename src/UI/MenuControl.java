
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
//import pacman.*;

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
//            case "display_connected_players": {
//                displayConnectedClients();
//            }
//            break;
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
        startMenu.addButtonPressOption("exitOnQ",()-> {
                    game.close();
                    return 1;
                }, "q" );
    }
    
    private void stageSelectMenu() {
        MenuObject stageSelectMenu = (MenuObject)createObject(MenuObject.class);
        stageSelectMenu.setFont("pac_font_sprites",8,8);
        stageSelectMenu.setTitle("SINGLE PLAYER");
        
        /*sprites.add(new Sprite("pac_font_sprites",21,3,8,8));
        sprites.add(new Sprite("pac_font_sprites",22,3,8,8));
        sprites.add(new Sprite("pac_font_sprites",23,3,8,8));
        sprites.add(new Sprite("pac_font_sprites",24,3,8,8));
        sprites.add(new Sprite("pac_font_sprites",25,3,8,8));*/
        stageSelectMenu.addImageSpinnerOption("Character ", null, game.chosenCharacter, 0, 4, sprites);
        stageSelectMenu.addSpinnerOption("Lives ",null,game.startingLives,1,5);
        stageSelectMenu.addSpinnerOption("Ghosts ", null, game.ghostsAmount, 1, 4);

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
                    final File finalFile = f;
                    stageSelectMenu.addMenuOption(stageName,() -> {
                                LabyrinthObject l = (LabyrinthObject)createObject(LabyrinthObject.class);
                                l.setSource(f.getPath(),false);
                                return 1;
                            });
                }
            }
        }
        catch (Exception e)
        {}

        stageSelectMenu.addMenuOption("BACK", () -> {
                gotoMenu("start");
                return 1;
            });

        stageSelectMenu.addButtonPressOption("exitOnQ",()-> {
                    gotoMenu("start");
                    return 1;
                }, "q" );
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
        menu.addButtonPressOption("exitOnQ",()-> {
                    gotoMenu("start");
                    return 1;
                }, "q" );
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
            //game.getExecutor().submit(game.callableStartClient);
            //game.halt();
            
            game.startClient(Game.ipString.value, Game.portString.value, Game.playerNumber.value);
            return 1;
        });
        
        //menu.addSpinnerOption("Plrs Amout: ", null, game.playersAmount, 1, 4);
        menu.addStringInputOption("Name: ", null, game.playerName, null, 7);
        menu.addNumberInputOption("Port: ",null,Game.portString,null,5);

        menu.addMenuOption("BACK", () -> {
            gotoMenu("server_setup");
            return 1;
        });
        menu.addButtonPressOption("exitOnQ",()-> {
            gotoMenu("server_setup");
            return 1;
        }, "q" );
    }
    
    private void joinGameMenu() {
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("JOIN GAME");

        //menu.addNumberInputOption("IP: ",null,ipString,"xxx.xxx.x.xx",9);
        menu.addMenuOption("Join",() -> {
            // TODO - UWAGA - na koniec wywalić poniższą linijkę, bo docelowo ma być bez zmian!!!
            // TODO - takie jak zostało odczytane z MENU !!!
            //Game.ipString.value = "localhost";
//            portString.value - takie jak zostało odczytane z MENU, czyli bez zmian
//            playerNumber.value - takie jak zostało odczytane z MENU, czyli bez zmian
            //game.getExecutor().submit(game.callableStartClient);
            //game.halt();
            
            game.startClient(Game.ipString.value, Game.portString.value, Game.playerNumber.value);
            
            return 1;
        });
        menu.addStringInputOption("Name: ", null, game.playerName, null, 7);
        menu.addSpinnerOption("Player ID: ", null, Game.playerNumber, 1, 3);
        menu.addStringInputOption("IP: ",null,Game.ipString,null,14/*,"xxx.xxx.x.xx",9*/);
        menu.addNumberInputOption("Port: ",null,Game.portString,null,4);
        menu.addMenuOption("BACK", () -> {
            gotoMenu("server_setup");
            return 1;
        });
        menu.addButtonPressOption("exitOnQ",()-> {
            gotoMenu("server_setup");
            return 1;
        }, "q" );
    }
    
    private void gameLobbyMenu() {
        
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("GAME LOBBY");

        menu.addImageSpinnerOption("Character ", null, game.characterBlocked,
                                        game.chosenCharacter, 0, 4, sprites);
        menu.addMenuOption("READY",() -> {
            
            ClientGame clientGame = (ClientGame)game;
            clientGame.setReady(true);
            gotoMenu("game_lobby");
            
            return 1;
        });
        menu.addMenuOption("Exit Server", () -> {
            game.close();
            return 1;
        });
        menu.addButtonPressOption("exitOnQ",()-> {
            game.close();
            return 1;
        }, "q" );
    }

//    public void displayConnectedClients(){
//        System.out.print("Ekran oczekiwania na graczy\n");
//
//        MenuObject menu = (MenuObject)createObject(MenuObject.class);
//        menu.hidePrefixMenu();
//        menu.setFont("pac_font_sprites",8,8);
//        menu.setTitle("Connected:");
//
//        // Czekaj na odebranie danych z serwera
//        while (ClientBrain.getNotConnectedClientsAmountBuffer() < 0){
//            System.out.println("Liczba niepodłączonych klientów = " +
//                    ClientBrain.getNotConnectedClientsAmountBuffer());
//            try{ Thread.sleep(500);}
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        for (int i = 0; i < 4; i++){
//            menu.addMenuOption("-", null);
//        }
//        //menu.addStringDisplayOption("- ", null, null, "- xxxxxxxx");
////        menu.addMenuOption("- Michal", null);
////        menu.addMenuOption("- Jan", null);
////        menu.addMenuOption("- Jakub", null);
////        menu.addMenuOption("- ", null);
//
//        menu.updateMenuOption("-", "- asdf");
//        menu.addMenuOption("", null);       //  enter
//        menu.addMenuOption("", null);       //  enter
//        menu.addMenuOption("Waiting for: " + 0, null);
//        menu.addMenuOption("player", null);
//
//
//        menu.addButtonPressOption("exitOnQ",()-> {
//                game.close();
//                return 1;
//            }, "q" );
//    }

    
    /*private void displayConnectedClients() {
        PackReceivedFromServer pack;
        
        //int players = game.playersAmount.value;
        
        System.out.println("IN LOBBY");
        
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.hidePrefixMenu();
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("Connected:");
        for (int i = 0; i < game.getMaxPlayersAmount(); i++){
            menu.addStringDisplayOption("- ", null, null, "- xxxxxxxx");
        }
//        menu.addMenuOption("- Michal", null);
//        menu.addMenuOption("- Jan", null);
//        menu.addMenuOption("- Jakub", null);
//        menu.addMenuOption("- ", null);
        menu.addMenuOption("", null);       //  enter
        menu.addMenuOption("", null);       //  enter
        menu.addMenuOption("Waiting for: " + 0, null);
        menu.addMenuOption("player", null);

        //menu.addButtonPressOption("exitOnQ",()-> {
        //        game.close();
        //        return 1;
        //    }, "q" );
    }*/

    /*private void displayConnectedClientsOld() {
        PackReceivedFromServer pack;
        while (Game.packReceivedFromServer == null);   // czekaj aż klient coś odbierze z serwera i dopiero to wypisz
        pack = Game.packReceivedFromServer;
        
        //int players = game.playersAmount.value;
        
        System.out.println("IN LOBBY");
        
        MenuObject menu = (MenuObject)createObject(MenuObject.class);
        menu.hidePrefixMenu();
        menu.setFont("pac_font_sprites",8,8);
        menu.setTitle("Connected:");
        for (int i = 0; i < pack.getConnectedClients().size(); i++){
            menu.addMenuOption("- " + pack.getConnectedClients().get(i), null);
        }
        for (int i = 0; i < pack.getNotConnectedClients(); i++){
            menu.addMenuOption("- ", null);
        }
//        menu.addMenuOption("- Michal", null);
//        menu.addMenuOption("- Jan", null);
//        menu.addMenuOption("- Jakub", null);
//        menu.addMenuOption("- ", null);
        menu.addMenuOption("", null);       //  enter
        menu.addMenuOption("", null);       //  enter
        menu.addMenuOption("Waiting for: " + pack.getNotConnectedClients(), null);
        menu.addMenuOption("player", null);

        menu.addButtonPressOption("exitOnQ",()-> {
                game.close();
                return 1;
            }, "q" );
    }*/
    
    Game game;
    ArrayList<Sprite> sprites = new ArrayList();
}
