
package game.objects;

import UI.PinnedTextObject;

abstract public class CharacterObject extends ActiveObject {
    @Override
    public void createEvent() {
        super.createEvent();
        
        wraparoundEnabled = true;
        wallContact = true;
        
        imageIndex = 0;
        subImageIndex = 0;
        
        defaultSpeed = 1.3;
        
        depth = -5;
    }
    
    @Override
    public void stepEvent() {
        if (playerPin != null)
        {pinControl();}
        
        randomFactor = Math.random(); // For that one step.
        
        if (xSpeed+ySpeed == 0) subImageIndex = 0;
        else {
            double deg = Math.toDegrees(Math.atan2(-ySpeed,xSpeed));
            
            if (deg < 0) deg += 360;
            
            if (deg < 45) subImageIndex = 1;
            else if (deg < 135) subImageIndex = 2;
            else if (deg < 225) subImageIndex = 3;
            else if (deg < 315) subImageIndex = 4;
            else subImageIndex = 1;
        }
        
        if (turnCounter > 0)
        {turnCounter--;}
        
        super.stepEvent();
    }
    
    protected void stepControl() {
        if ((hiddenCounter == 0) /*&& (xSpeed + ySpeed == 0)*/) {
            if (xSpeed + ySpeed == 0)
            {
                if ((game.keyboardHoldCheck("left",playerId)) && (!game.keyboardHoldCheck("right",playerId)))
                    xSpeed = -defaultSpeed;
                else if ((game.keyboardHoldCheck("right",playerId)) && (!game.keyboardHoldCheck("left",playerId)))
                    xSpeed = defaultSpeed;
                if ((game.keyboardHoldCheck("up",playerId)) && (!game.keyboardHoldCheck("down",playerId)))
                    ySpeed = -defaultSpeed;
                else if ((game.keyboardHoldCheck("down",playerId)) && (!game.keyboardHoldCheck("up",playerId)))
                    ySpeed = defaultSpeed;
            }
            else if (turnCounter == 0)
            {
                double tmpX = xSpeed;
                double tmpY = ySpeed;
                if ((game.keyboardHoldCheck("left",playerId)) && (!game.keyboardHoldCheck("right",playerId)) && (xSpeed > 0))
                    xSpeed = -defaultSpeed;
                else if ((game.keyboardHoldCheck("right",playerId)) && (!game.keyboardHoldCheck("left",playerId)) && (xSpeed < 0))
                    xSpeed = defaultSpeed;
                else if ((game.keyboardHoldCheck("up",playerId)) && (!game.keyboardHoldCheck("down",playerId)) && (ySpeed > 0))
                    ySpeed = -defaultSpeed;
                else if ((game.keyboardHoldCheck("down",playerId)) && (!game.keyboardHoldCheck("up",playerId)) && (ySpeed < 0))
                    ySpeed = defaultSpeed;
                if ((tmpX != xSpeed) || (tmpY != ySpeed)) turnCounter = 5;
            }
        }
    }
    
    public void resetPosition(){
        x = xstart;
        y = ystart;
        
        if (!isPlayed){
            if (Math.random() >= 0.5) xSpeed = defaultSpeed;
            else xSpeed = -defaultSpeed;
        }
        else xSpeed = 0;
        
        
        ySpeed = 0;
    }
    
    @Override
    protected void hitWall(boolean isVertical) {
        // Bouncing off walls.
        
        if (isVertical) {
            double tmp = ySpeed;
            ySpeed = 0;
            
            if (!collisionCheck(x-1,y)) {
                // Random factor
                if (!collisionCheck(x+1,y)) {
                    if (willTurnRight()) xSpeed = defaultSpeed;
                    else if (willTurnLeft()) xSpeed = -defaultSpeed;
                }
                else if ((mustTurn()) || (willTurnLeft())) xSpeed = -defaultSpeed;
            }
            // No choice
            else if ((!collisionCheck(x+1,y)) && (mustTurn())) xSpeed = defaultSpeed;
            else if (mustTurn()) ySpeed = -tmp;
        }
        else {
            double tmp = xSpeed;
            xSpeed = 0;
            
            if (!collisionCheck(x,y-1)) {
                // Random factor
                if (!collisionCheck(x,y+1)) {
                    if (willTurnDown()) ySpeed = defaultSpeed;
                    else if (willTurnUp()) ySpeed = -defaultSpeed;
                }
                else if ((mustTurn()) || (willTurnUp())) ySpeed = -defaultSpeed;
            }
            // No choice
            else if ((!collisionCheck(x,y+1)) && (mustTurn())) ySpeed = defaultSpeed;
            else  if (mustTurn()) xSpeed = -tmp;
        }
    }
    
    @Override
    protected void passedEntrance(boolean isVertical) {
        // Going into entrances.
        
        if (!mayEnter()) return;
        
        if (isVertical) {
            xSpeed = 0;
            xAdd = 0;
            
            if ((!collisionCheck(x,y-1)) && (willEnterUp())) ySpeed = -defaultSpeed;
            else if ((!collisionCheck(x,y+1)) && (willEnterDown())) ySpeed = defaultSpeed;
        }
        else {
            ySpeed = 0;
            yAdd = 0;
            
            if ((!collisionCheck(x-1,y)) && (willEnterLeft())) xSpeed = -defaultSpeed;
            else if ((!collisionCheck(x+1,y)) && (willEnterRight())) xSpeed = defaultSpeed;
        }
    }

    private void pinControl() {
        // Nothing interesting here...
        int waitForPin = 200;
        if (!visible) {
            playerPin.setVisible(false);
            pinCounter = waitForPin /8;
            pinState = 0;
        }
        
        if (pinCounter > 0) {
            pinCounter--;
            if (pinState == 0)
            playerPin.setVisible(false);
        }
        else {
            if (pinState == 1) {
                if (pinFlicker > 0) {
                    pinFlicker--;
                    playerPin.setVisible(!playerPin.getVisible());
                    int pinFlickerTime = 10;
                    pinCounter = pinFlickerTime;
                }
                else {
                    pinCounter = waitForPin;
                    pinState = 0;
                }
            }
            else if (pinState == 0) {
                int pinMaxFlickers = 5;
                pinFlicker = pinMaxFlickers;
                pinState = 1;
            }
        }
    }
    
    // Following functions implement either keyboard, or randomness.
    // Bouncing off walls.
    private boolean mustTurn() {
        return !isPlayed;
    }
    private boolean willTurnLeft(){
        return !isPlayed ? (randomFactor >= 0.5) : game.keyboardHoldCheck("left",playerId);
    }
    private boolean willTurnRight(){
        return !isPlayed ? (randomFactor < 0.5) : game.keyboardHoldCheck("right",playerId);
    }
    private boolean willTurnUp(){
        return !isPlayed ? (randomFactor >= 0.5) : game.keyboardHoldCheck("up",playerId);
    }
    private boolean willTurnDown(){return !isPlayed ? (randomFactor < 0.5) : game.keyboardHoldCheck("down",playerId);}
    // Going into entrances.
    private boolean mayEnter(){
        return isPlayed || (randomFactor >= 0.5);
    }
    private boolean willEnterLeft(){
        return !isPlayed || game.keyboardHoldCheck("left", playerId);
    }
    private boolean willEnterRight(){
        return !isPlayed || game.keyboardHoldCheck("right", playerId);
    }
    private boolean willEnterUp(){
        return !isPlayed || game.keyboardHoldCheck("up", playerId);
    }
    private boolean willEnterDown(){
        return !isPlayed || game.keyboardHoldCheck("down", playerId);
    }


    boolean isPlayed;
    double defaultSpeed;
    private double randomFactor;
    int hiddenCounter;
    private int turnCounter;
    
    int playerId;
    
    private PinnedTextObject playerPin = null;
    private int pinCounter = 0;
    private int pinState = 0;
    private int pinFlicker = 0;
}