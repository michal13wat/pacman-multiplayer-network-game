
package pacman;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class SpawnerObject extends ActiveObject {
    // Class spawns certain ActiveObject with set delay.
    
    @Override
    public void createEvent() {
        myObjects = new ArrayList();
        counter = 0;
    }
    
    @Override
    public void stepEvent() {
        boolean blocked = false;
        
        if ((blockable) && (getColliding(CharacterObject.class) != null)) blocked = true;
            
        if (myObjects.size() >= maximumObjects) counter = delay;
        
        if ((counter-delay >= 0) && (counter-delay >= interval) && (!blocked)) {
            ActiveObject newSpawn = (ActiveObject)game.createObject(spawnedObject);

            newSpawn.setPosition(x,y);
            newSpawn.setOrigin(xorigin,yorigin);
            newSpawn.setCollisionMap(collisionMap);

            myObjects.add(newSpawn);

            counter = delay;
        }
        
        for (int i = 0; i < myObjects.size(); i++) {
            if (myObjects.get(i).isDestroyed()) myObjects.remove(i);
        }
        
        counter ++;
    }
    
    @Override
    public void drawEvent(Graphics2D g) {
        // No drawing.
    }
    
    public void setSpawner(Class spawnedObject, int maximumObjects, int interval, int delay, boolean blockable) {
        this.spawnedObject = spawnedObject;
        this.maximumObjects = maximumObjects;
        this.interval = interval;
        this.delay = delay;
        this.blockable = blockable;
    }
    
    private Class spawnedObject;
    private int maximumObjects;
    private int interval;
    private int delay;
    private boolean blockable;
    
    private ArrayList<GameObject> myObjects;
}
