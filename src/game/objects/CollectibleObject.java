
package game.objects;

import java.io.Serializable;

abstract public class CollectibleObject extends ActiveObject implements Serializable
{
    @Override
    public void createEvent()
    {
        super.createEvent();
        
        setSpriteSheet("pac_collectible_sprites",16,16);
        
        bBoxLeft = 6;
        bBoxRight = 10;
        bBoxTop = 6;
        bBoxBottom = 10;
        
        imageIndex = 0;
        subImageIndex = 0;
    }
    
    @Override
    public void stepEvent() {
        firstStep = false;
        // Memory savin'
    }
    
    public void getCollected(CharacterObject obj) {
        // Aktualizacja wyniku.
        game.addScore(pointReward);
        
        ParticleObject o = (ParticleObject)createObject(ParticleObject.class,x,y);
        
        o.setParticle(0,4,0.6);
        o.setDepth(-1);
        
        destroy();
    }
    
    int pointReward;
}
