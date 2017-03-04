
package pacman;

abstract public class CharacterObject extends ActiveObject
{
    @Override
    public void createEvent()
    {
        super.createEvent();
        
        wraparoundEnabled = true;
        wallContact = true;
        
        imageIndex = 0;
        subimageIndex = 0;
        
        defaultSpeed = 1.3;
        
        depth = -5;
    }
    
    @Override
    public void stepEvent()
    {
        randomFactor = Math.random(); // For that one step.
        
        if (xspeed+yspeed == 0)
        {
            subimageIndex = 0;
        }
        else
        {
            double deg = Math.toDegrees(Math.atan2(-yspeed,xspeed));
            
            if (deg < 0)
            {deg += 360;}
            
            if (deg < 45)
            {subimageIndex = 1;}
            else if (deg < 135)
            {subimageIndex = 2;}
            else if (deg < 225)
            {subimageIndex = 3;}
            else if (deg < 315)
            {subimageIndex = 4;}
            else
            {subimageIndex = 1;}
        }
        
        super.stepEvent();
    }
    
    @Override
    protected void hitWall(boolean isVertical)
    {
        // Bouncing off walls.
        
        if (isVertical == true)
        {
            double tmp = yspeed;
            yspeed = 0;
            
            if (!collisionCheck(x-1,y))
            {
                if (!collisionCheck(x+1,y)) // Random factor
                {
                    if (willTurnRight())
                    {xspeed = defaultSpeed;}
                    else if (willTurnLeft())
                    {xspeed = -defaultSpeed;}
                }
                else if ((mustTurn()) || (willTurnLeft()))
                {xspeed = -defaultSpeed;}
            }
            else if ((!collisionCheck(x+1,y)) && (mustTurn())) // No choice
            {xspeed = defaultSpeed;}
            else if (mustTurn())
            {yspeed = -tmp;}
        }
        else
        {
            double tmp = xspeed;
            xspeed = 0;
            
            if (!collisionCheck(x,y-1))
            {
                if (!collisionCheck(x,y+1)) // Random factor
                {
                    if (willTurnDown())
                    {yspeed = defaultSpeed;}
                    else if (willTurnUp())
                    {yspeed = -defaultSpeed;}
                }
                else if ((mustTurn()) || (willTurnUp()))
                {yspeed = -defaultSpeed;}
            }
            else if ((!collisionCheck(x,y+1)) && (mustTurn())) // No choice
            {yspeed = defaultSpeed;}
            else  if (mustTurn())
            {xspeed = -tmp;}
        }
    }
    
    @Override
    protected void passedEntrance(boolean isVertical)
    {
        // Going into entrances.
        
        if (!mayEnter())
        {return;}
        
        if (isVertical == true)
        {
            xspeed = 0;
            xadd = 0;
            
            if ((!collisionCheck(x,y-1)) && (willEnterUp()))
            {yspeed = -defaultSpeed;}
            else if ((!collisionCheck(x,y+1)) && (willEnterDown()))
            {yspeed = defaultSpeed;}
        }
        else
        {
            yspeed = 0;
            yadd = 0;
            
            if ((!collisionCheck(x-1,y)) && (willEnterLeft()))
            {xspeed = -defaultSpeed;}
            else if ((!collisionCheck(x+1,y)) && (willEnterRight()))
            {xspeed = defaultSpeed;}
        }
    }
    
    // Following functions implement either keyboard, or randomness.
    
    abstract protected boolean mustTurn(); // Bouncing off walls.
    abstract protected boolean willTurnLeft();
    abstract protected boolean willTurnRight();
    abstract protected boolean willTurnUp();
    abstract protected boolean willTurnDown();
    abstract protected boolean mayEnter(); // Going into entrances.
    abstract protected boolean willEnterLeft();
    abstract protected boolean willEnterRight();
    abstract protected boolean willEnterUp();
    abstract protected boolean willEnterDown();
    
    protected double defaultSpeed;
    protected double randomFactor;
}
