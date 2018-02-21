
package game.objects;

import java.io.Serializable;

public class TestObject extends ActiveObject implements Serializable {
    @Override
    public void createEvent() {
        setSpriteSheet("pac_hero_sprites",16,16);
        
        imageIndex = 0;
        subImageIndex = 0;
        
        xstart = 64+(int)(System.currentTimeMillis()/100)%128;
        ystart = 120;
    }
    
    @Override
    public void stepEvent() {

        x = xstart+(int)(64.0*Math.cos(myCounter/8));
        y = ystart-(int)(64.0*Math.sin(myCounter/8));
        myCounter += mySpeed;
        mySpeed = Math.max(Math.min(mySpeed+1.0*Math.random()-0.5,4.0),-4.0);
        super.stepEvent();
    }
    
    private double myCounter = 0;
    private double mySpeed = 0;
}
