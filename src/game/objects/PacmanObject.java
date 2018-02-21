
package game.objects;

import java.awt.Graphics2D;
import java.io.Serializable;

public class PacmanObject extends CharacterObject implements Serializable {
    @Override
    public void createEvent() {
        super.createEvent();
        
        setSpriteSheet("pac_hero_sprites",16,16);
        
        imageIndex = 0;
        subImageIndex = 0;
        
        chompCounter = 0;
        defaultSpeed = 2;
        
        depth = -10;
        
        flickerCounter = 0;
        hiddenCounter = 120;
    }
    
    @Override
    public void setPlayed(){
        if (game.getPacmanPlayer() >= 0) {
            isPlayed = true;
            playerId = game.getPacmanPlayer();
            //createPin("P1");
        }
        else isPlayed = false;
    }
    
    @Override
    public void stepEvent() {
        // Rozruszanie z bezruchu.
            
        if (isPlayed) stepControl();
        
        // Animacja.
        
        if ((xSpeed + ySpeed != 0)
        && (!collisionCheck(x+(int)Math.signum(xSpeed),y+(int)Math.signum(ySpeed)))) {
            chompCounter += xSpeed + ySpeed;
            imageIndex = 1.5-1.5*Math.cos(chompCounter/5);
        }
        else {
            chompCounter = 0;
            imageIndex = approach(imageIndex,0,0.2);
        }
        
        // Ukrywanie się po śmierci i "mryganie".
        
        if (hiddenCounter > 0) {
            xSpeed = 0;
            ySpeed = 0;
            
            visible = false;
            tangible = false;
            
            hiddenCounter --;
            if ((hiddenCounter == 40) && (game.getLives() > 0) && 
                    (!game.getAllObjects(DotObject.class).isEmpty())) {
                ParticleObject o = (ParticleObject)createObject(ParticleObject.class,xstart,ystart);
                o.setParticle(4,7,0.15);
                o.setDepth(-10);
            }
            else if (hiddenCounter == 0) resetPosition();
        }
        else if (flickerCounter > 0) {
            tangible = false;
            visible = !visible;
            
            flickerCounter --;
        }
        else {
            tangible = true;
            visible = true;
        }
        
        // Zbieranie kulek.
        
        CollectibleObject obj;
        
        if ((obj = (CollectibleObject)getColliding(CollectibleObject.class)) != null) obj.getCollected(this); 
        
        double tmp = imageIndex;
        super.stepEvent();
        
        if ((imageIndex == 0) && (subImageIndex != 0)) imageIndex = tmp;
    }
    
    @Override
    public void drawEvent(Graphics2D g) {
        if (visible) super.drawEvent(g);
    }
    
    void getCaught(CharacterObject obj) {
        if (obj != null) {
            hiddenCounter = 150;
        
            ParticleObject o = (ParticleObject)createObject(ParticleObject.class,x,y);
            o.setParticle(3,11,0.25);
            o.setDepth(-10);

            game.addScore(-10);
            game.addLives(-1);
        }
        else {
            hiddenCounter = 250;
            
            ParticleObject o = (ParticleObject)createObject(ParticleObject.class,x,y);
            o.setParticle(5,8,0.14);
            o.setDepth(-10);
        }
    }
    
    public void resetPosition() {
        
        if (game.getAllObjects(DotObject.class).isEmpty()) {
            game.endGame(isPlayed); 
            destroy();
            return;
        }
        
        if (game.getLives() <= 0) {
            game.endGame(!isPlayed);
            destroy();
            return;
        }
        
        flickerCounter = 120;
        super.resetPosition();
        imageIndex = 0;
        subImageIndex = 0;
    }

    private int flickerCounter, chompCounter;
}
