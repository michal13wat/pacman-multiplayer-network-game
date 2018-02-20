
package game.objects;

import java.io.Serializable;

public class DotObject extends CollectibleObject implements Serializable
{
    @Override
    public void createEvent() {
        super.createEvent();
        
        pointReward = 1;
        
        imageIndex = 0;
        subImageIndex = 0;
    }
}
