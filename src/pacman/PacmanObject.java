
package pacman;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class PacmanObject extends CharacterObject
{
    @Override
    public void createEvent()
    {
        super.createEvent();
        
        loadSpriteSheet("/resources/pac_hero_sprites.png",16,16);
        
        imageIndex = 0;
        subimageIndex = 0;
        
        chompCounter = 0;
        defaultSpeed = 2;
        
        depth = -10;
        
        flickerCounter = 0;
        hiddenCounter = 120;
    }
    
    @Override
    public void stepEvent()
    {
        if ((hiddenCounter == 0) && (xspeed + yspeed == 0)) // Kickstarting velocity.
        {
            if (game.keyboardCheck("left"))
            {xspeed = -defaultSpeed;}
            else if (game.keyboardCheck("right"))
            {xspeed = defaultSpeed;}
            else if (game.keyboardCheck("up"))
            {yspeed = -defaultSpeed;}
            else if (game.keyboardCheck("down"))
            {yspeed = defaultSpeed;}
        }
        
        // Animation.
        
        if ((xspeed + yspeed != 0)
        && (!collisionCheck(x+(int)Math.signum(xspeed),y+(int)Math.signum(yspeed))))
        {
            chompCounter += xspeed + yspeed;
            imageIndex = 1.5-1.5*Math.cos(chompCounter/5);
        }
        else
        {
            chompCounter = 0;
            imageIndex = approach(imageIndex,0,0.2);
        }
        
        // Hiding after death and flicker control.
        
        if (hiddenCounter > 0)
        {
            xspeed = 0;
            yspeed = 0;
            
            visible = false;
            tangible = false;
            
            hiddenCounter --;
            if ((hiddenCounter == 40) && (game.getLives() > 0) && (!game.getAllObjects(DotObject.class).isEmpty()))
            {
                ParticleObject o = (ParticleObject)createObject(ParticleObject.class,xstart,ystart);
                o.setParticle("/resources/pac_particle_sprites.png",16,16,4,7,0.15);
                o.setDepth(-10);
            }
            else if (hiddenCounter == 0)
            {resetPosition();}
        }
        else if (flickerCounter > 0)
        {
            tangible = false;
            visible = !visible;
            
            flickerCounter --;
        }
        else
        {
            tangible = true;
            visible = true;
        }
        
        // Collecting dots.
        
        CollectibleObject obj;
        
        if ((obj = (CollectibleObject)getColliding(CollectibleObject.class)) != null)
        {
            obj.getCollected(this);
        }
        
        double tmp = imageIndex;
        super.stepEvent();
        
        if ((imageIndex == 0) && (subimageIndex != 0))
        {imageIndex = tmp;}
    }
    
    @Override
    public void drawEvent(Graphics2D g)
    {
        if (visible == true)
        {super.drawEvent(g);}
    }
    
    public void getCaught(CharacterObject obj)
    {
        if (obj != null)
        {
            hiddenCounter = 150;
        
            ParticleObject o = (ParticleObject)createObject(ParticleObject.class,x,y);
            o.setParticle("/resources/pac_particle_sprites.png",16,16,3,11,0.25);
            o.setDepth(-10);

            game.addScore(-10);
            game.addLives(-1);
        }
        else
        {
            hiddenCounter = 250;
            
            ParticleObject o = (ParticleObject)createObject(ParticleObject.class,x,y);
            o.setParticle("/resources/pac_particle_sprites.png",16,16,5,8,0.14);
            o.setDepth(-10);
        }
    }
    
    private void resetPosition()
    {
        if (game.getAllObjects(DotObject.class).isEmpty())
        {
            game.endGame(true);
            destroy();
            return;
        }
        
        if (game.getLives() <= 0)
        {
            game.endGame(false);
            destroy();
            return;
        }
        
        flickerCounter = 120;
        x = xstart;
        y = ystart;
        xspeed = 0;
        yspeed = 0;
        imageIndex = 0;
        subimageIndex = 0;
    }
    
    @Override
    protected boolean mustTurn() // Doesn't necessarily turn.
    {return false;}
    @Override
    protected boolean willTurnLeft() // Checks keyboard keys to determine that.
    {return game.keyboardCheck("left");}
    @Override
    protected boolean willTurnRight()
    {return game.keyboardCheck("right");}
    @Override
    protected boolean willTurnUp()
    {return game.keyboardCheck("up");}
    @Override
    protected boolean willTurnDown()
    {return game.keyboardCheck("down");}
    
    @Override
    protected boolean mayEnter() // Always has to enter...
    {return true;}
    @Override
    protected boolean willEnterLeft() // But has another set of checks with keyboard keys.
    {return game.keyboardCheck("left");}
    @Override
    protected boolean willEnterRight()
    {return game.keyboardCheck("right");}
    @Override
    protected boolean willEnterUp()
    {return game.keyboardCheck("up");}
    @Override
    protected boolean willEnterDown()
    {return game.keyboardCheck("down");}
    
    private int flickerCounter, hiddenCounter, chompCounter;
}
