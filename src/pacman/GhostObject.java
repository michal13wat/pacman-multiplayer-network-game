
package pacman;

import java.util.ArrayList;

public class GhostObject extends CharacterObject
{
    @Override
    public void createEvent()
    {
        super.createEvent();
        
        loadSpriteSheet("/resources/pac_ghost_sprites.png",16,16);
        
        myColor = 0;
        imageIndex = 0;
        subimageIndex = 0;
        
        //defaultSpeed = 1.3;
        defaultSpeed = 0.75;
        
        depth = -5;
        
        scareCounter = 0;
        hiddenCounter = 35;
    }
    
    @Override
    public void stepEvent()
    {
        if (firstStep == true)
        {
            resetPosition();
            
            // Color validation.

            ArrayList<GameObject> allGhosts = game.getAllObjects(this.getClass());
            GhostObject otherGhost = null;

            for (int i = 0; i < allGhosts.size(); i++)
            {
                otherGhost = (GhostObject)allGhosts.get(i);
                if ((otherGhost != this) && (otherGhost.myColor == myColor))
                {myColor ++;}
            }
        }
        
        // Scared/Hidden counter.
        
        if (scareCounter > 0)
        {
            imageIndex = 4;
            scareCounter --;
        }
        else
        {imageIndex = myColor;}
        
        if (hiddenCounter > 0)
        {
            visible = false;
            tangible = false;
            
            hiddenCounter --;
            if (hiddenCounter == 34)
            {
                ParticleObject o = (ParticleObject)createObject(ParticleObject.class,xstart,ystart);
                o.setParticle("/resources/pac_particle_sprites.png",16,16,2,8,0.2);
                o.setDepth(-10);
            }
            else if (hiddenCounter == 0)
            {resetPosition();}
        }
        else
        {
            visible = true;
            tangible = true;
        }
        
        // Catching PacMan.
        
        if (tangible)
        {
            PacmanObject obj;

            if ((obj = (PacmanObject)getColliding(PacmanObject.class)) != null)
            {
                if (scareCounter == 0)
                {obj.getCaught(this);}
                else
                {getCaught(obj);}
            }
        }
        
        defaultSpeed = 0.75+(int)game.getScore()/150.0;
        
        super.stepEvent();
    }
    
    public void resetPosition()
    {
        x = xstart;
        y = ystart;
        
        if (Math.random() >= 0.5)
        {xspeed = defaultSpeed;}
        else
        {xspeed = -defaultSpeed;}
        
        yspeed = 0;
    }
    
    public void getCaught(CharacterObject obj)
    {
        scareCounter = 0;
        hiddenCounter = 100;
        
        ParticleObject o = (ParticleObject)createObject(ParticleObject.class,x,y);
        o.setParticle("/resources/pac_particle_sprites.png",16,16,1,7,0.2);
        o.setDepth(-2);
    }
    
    @Override
    protected boolean mustTurn() // Has to turn at all times.
    {return true;}
    @Override
    protected boolean willTurnLeft() // Alternates between L/R.
    {return (randomFactor >= 0.5);}
    @Override
    protected boolean willTurnRight()
    {return (randomFactor < 0.5);}
    @Override
    protected boolean willTurnUp() // Alternates between U/D.
    {return (randomFactor >= 0.5);}
    @Override
    protected boolean willTurnDown()
    {return (randomFactor < 0.5);}
    
    @Override
    protected boolean mayEnter() // Doesn't necessarily go into entrances.
    {return (randomFactor >= 0.5);}
    @Override
    protected boolean willEnterLeft() // But if he decides so, he doesn't differentiate.
    {return true;}
    @Override
    protected boolean willEnterRight()
    {return true;}
    @Override
    protected boolean willEnterUp()
    {return true;}
    @Override
    protected boolean willEnterDown()
    {return true;}
    
    protected int scareCounter;
    protected int hiddenCounter;
    protected int myColor;
    
    public void scare(int time)
    {
        scareCounter = time;
    }
}
