
package game.objects;

import java.io.Serializable;

public class CherryObject extends CollectibleObject implements Serializable
{
    @Override
    public void createEvent() {
        super.createEvent();
        
        pointReward = 8;
        lifetime = 500;
        
        imageIndex = 1;
        subImageIndex = 0;
    }
    
    @Override
    public void stepEvent() {
        if (counter%40>=20) subImageIndex = 1;
        else subImageIndex = 0;
        
        if (counter > lifetime) destroy();
        else if (counter > lifetime*3/4) visible = !visible;
        
        counter ++;
    }
    
    private int lifetime;
}
