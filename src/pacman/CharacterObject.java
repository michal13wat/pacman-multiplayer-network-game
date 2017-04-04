
package pacman;

abstract public class CharacterObject extends ActiveObject {
    @Override
    public void createEvent() {
        super.createEvent();
        
        wraparoundEnabled = true;
        wallContact = true;
        
        imageIndex = 0;
        subimageIndex = 0;
        
        defaultSpeed = 1.3;
        
        depth = -5;
    }
    
    @Override
    public void stepEvent() {
        randomFactor = Math.random(); // For that one step.
        
        if (xspeed+yspeed == 0) subimageIndex = 0; 
        else {
            double deg = Math.toDegrees(Math.atan2(-yspeed,xspeed));
            
            if (deg < 0) deg += 360;
            
            if (deg < 45) subimageIndex = 1;
            else if (deg < 135) subimageIndex = 2;
            else if (deg < 225) subimageIndex = 3;
            else if (deg < 315) subimageIndex = 4;
            else subimageIndex = 1;
        }
        
        super.stepEvent();
    }
    
    protected void stepControl() {
        if ((hiddenCounter == 0) /*&& (xspeed + yspeed == 0)*/) {
            if (game.keyboardHoldCheck("left")) xspeed = -defaultSpeed;
            else if (game.keyboardHoldCheck("right")) xspeed = defaultSpeed;
            else if (game.keyboardHoldCheck("up")) yspeed = -defaultSpeed;
            else if (game.keyboardHoldCheck("down")) yspeed = defaultSpeed;
        }
    }
    
    public void resetPosition(){
        x = xstart;
        y = ystart;
        
        if (!isPlayed){
            if (Math.random() >= 0.5) xspeed = defaultSpeed;
            else xspeed = -defaultSpeed;
        }
        else xspeed = 0;
        
        
        yspeed = 0;
    }
    
    @Override
    protected void hitWall(boolean isVertical) {
        // Bouncing off walls.
        
        if (isVertical) {
            double tmp = yspeed;
            yspeed = 0;
            
            if (!collisionCheck(x-1,y)) {
                // Random factor
                if (!collisionCheck(x+1,y)) {
                    if (willTurnRight()) xspeed = defaultSpeed;
                    else if (willTurnLeft()) xspeed = -defaultSpeed;
                }
                else if ((mustTurn()) || (willTurnLeft())) xspeed = -defaultSpeed;
            }
            // No choice
            else if ((!collisionCheck(x+1,y)) && (mustTurn())) xspeed = defaultSpeed;
            else if (mustTurn()) yspeed = -tmp;
        }
        else {
            double tmp = xspeed;
            xspeed = 0;
            
            if (!collisionCheck(x,y-1)) {
                // Random factor
                if (!collisionCheck(x,y+1)) {
                    if (willTurnDown()) yspeed = defaultSpeed;
                    else if (willTurnUp()) yspeed = -defaultSpeed;
                }
                else if ((mustTurn()) || (willTurnUp())) yspeed = -defaultSpeed;
            }
            // No choice
            else if ((!collisionCheck(x,y+1)) && (mustTurn())) yspeed = defaultSpeed;
            else  if (mustTurn()) xspeed = -tmp;
        }
    }
    
    @Override
    protected void passedEntrance(boolean isVertical) {
        // Going into entrances.
        
        if (!mayEnter()) return;
        
        if (isVertical) {
            xspeed = 0;
            xadd = 0;
            
            if ((!collisionCheck(x,y-1)) && (willEnterUp())) yspeed = -defaultSpeed;
            else if ((!collisionCheck(x,y+1)) && (willEnterDown())) yspeed = defaultSpeed;
        }
        else {
            yspeed = 0;
            yadd = 0;
            
            if ((!collisionCheck(x-1,y)) && (willEnterLeft())) xspeed = -defaultSpeed;
            else if ((!collisionCheck(x+1,y)) && (willEnterRight())) xspeed = defaultSpeed;
        }
    }
    
    // Following functions implement either keyboard, or randomness.
    // Bouncing off walls.
    protected boolean mustTurn() {
        return !isPlayed;
    }
    protected boolean willTurnLeft(){
        return !isPlayed ? (randomFactor >= 0.5) : game.keyboardHoldCheck("left");
    }
    protected boolean willTurnRight(){
        return !isPlayed ? (randomFactor < 0.5) : game.keyboardHoldCheck("right");
    }
    protected boolean willTurnUp(){
        return !isPlayed ? (randomFactor >= 0.5) : game.keyboardHoldCheck("up");
    }
    protected boolean willTurnDown(){
        return !isPlayed ? (randomFactor < 0.5) : game.keyboardHoldCheck("down");
    }
    // Going into entrances.
    protected boolean mayEnter(){
        return isPlayed ? true : (randomFactor >= 0.5);
    }
    protected boolean willEnterLeft(){
        return isPlayed ? game.keyboardHoldCheck("left") : true;
    }
    protected boolean willEnterRight(){
        return isPlayed ? game.keyboardHoldCheck("right") : true;
    }
    protected boolean willEnterUp(){
        return isPlayed ? game.keyboardHoldCheck("up") : true;
    }
    protected boolean willEnterDown(){
        return isPlayed ? game.keyboardHoldCheck("down") : true;
    }
    
    
    public boolean isPlayed(){
        return isPlayed;
    }
    
    public void setIsPlayed(boolean isPlayed){
        this.isPlayed = isPlayed;
    }
    
    protected boolean isPlayed;
    protected double defaultSpeed;
    protected double randomFactor;
    protected int hiddenCounter;
}
