
package game.objects;

import java.io.Serializable;

public class ParticleObject extends ActiveObject implements Serializable {
    
    @Override
    public void stepEvent() {
        super.stepEvent();
        
        subImageIndex += imageSpeed;
        if (subImageIndex >= imageCount) destroy();
    }
    
    void setParticle(int imageIndex, int imageCount, double imageSpeed) {
        setSpriteSheet("pac_particle_sprites", 16, 16);
        
        this.subImageIndex = 0;
        
        this.imageIndex = imageIndex;
        this.imageCount = imageCount;
        this.imageSpeed = imageSpeed;
    }
    
    private double imageSpeed;
    private int imageCount;
}
