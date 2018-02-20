
package game.objects;

import java.awt.Graphics2D;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

abstract public class ActiveObject extends GameObject implements Serializable {
    @Override
    public void createEvent(){
        // Inicjalizacja zmiennych.
        tangible = true;
        visible = true;
        
        wraparoundEnabled = true;
        wallContact = false;
        
        x = 0;
        y = 0;
        xstart = x;
        ystart = y;
        
        xSpeed = 0;
        ySpeed = 0;
        
        xAdd = 0;
        yAdd = 0;
        
        xPull = 0;
        yPull = 0;
        
        depth = -1;
    }
    
    @Override
    public void stepEvent() {
        firstStep = false;
        counter ++;
        
        moveObject();
        
        if (wraparoundEnabled) {
            x = wraparound(x,false);
            y = wraparound(y,true);
        }
    }
    
    @Override
    public void drawEvent(Graphics2D g) {
        if ((!visible) || (destroyed)) return;
        
        if (spriteSheet == null) {
            g.setColor(Color.WHITE);
            g.fillRect(screenCenterX+(int)(scaleMod*(x+xorigin)),(int)(scaleMod*(y+yorigin)),16,16);
        }
        else {
            if (wraparoundEnabled){
                int xOverflow = collisionMap.getWidth()-(x+imageWidth);
                int yOverflow = collisionMap.getHeight()-(y+imageHeight);
                
                drawSpriteChopped(g,x,y,0,0,bounds(0, xOverflow,imageWidth),bounds(0,yOverflow,imageHeight));
                drawSpriteChopped(g,x,0,0,bounds(0,yOverflow+imageHeight,imageHeight),imageWidth,imageHeight);
                drawSpriteChopped(g,0,y,bounds(0, xOverflow +imageWidth,imageWidth),0,imageWidth,imageHeight);
                drawSpriteChopped(g,0,0,bounds(0, xOverflow +imageWidth,imageWidth),bounds(0,yOverflow+imageHeight,imageHeight),imageWidth,imageHeight);
            }
            else drawSpriteFull(g);
        }
    }
    
    @Override
    public void destroyEvent() {
    }
    
    void setSpriteSheet(String name, int imageWidth, int imageHeight) {
        // Zmienia wyznaczony zestaw sprite'ów.
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        
        this.spriteSheet = name;
        
        bBoxLeft = 0;
        bBoxRight = imageWidth;
        bBoxTop = 0;
        bBoxBottom = imageHeight;
    }
    
    private void drawSpriteFull(Graphics2D g) {
        drawSprite(g,getSprites(spriteSheet),x,y, subImageIndex,imageIndex,imageWidth,imageHeight);
    }
        
    private void drawSpriteChopped(Graphics2D g, int x, int y, int x1, int y1, int x2, int y2) {
        g.drawImage(
            getSprites(spriteSheet),
            (int)(scaleMod*(x+xorigin))+screenCenterX,
            (int)(scaleMod*(y+yorigin))+screenCenterY,
            (int)(scaleMod*(x+x2-x1+xorigin))+screenCenterX,
            (int)(scaleMod*(y+y2-y1+yorigin))+screenCenterY,
            imageWidth*(int)Math.floor(subImageIndex)+x1,imageHeight*(int)Math.floor(imageIndex)+y1,
            imageWidth*(int)Math.floor(subImageIndex)+x2,imageHeight*(int)Math.floor(imageIndex)+y2,
            null);
    }
    
    ActiveObject getColliding(Class ourClass) {
        // Zwraca ActiveObject, z którym w tej chwili kolidujemy.
        // Jeśli nie ma żadnego, zwraca null.
        
        ArrayList<GameObject> l = game.getAllObjects(ourClass);
        ActiveObject obj;

        for (GameObject aL : l) {
            obj = (ActiveObject) aL;
            if ((obj.isTangible()) && (!obj.isDestroyed()) && (collisionCheck(obj))) return obj;
        }
        
        return null;
    }
    
    boolean collisionCheck(int xcheck, int ycheck) {
        // Sprawdzanie kolizji, bazowane na zmiennych bbox.
        // Niezbyt precyzyjne.
        
        if ((!wallContact) || (collisionMap == null)) return false;
        
        int i = bBoxLeft, j;
        
        while (i < bBoxRight) {
            j = bBoxTop;
            
            while (j < bBoxBottom) {
                if (collisionMap.checkCollisionScaled(wraparound(xcheck+i,false),wraparound(ycheck+j,true)))
                    return true;
                
                if (j == bBoxBottom -1) j = bBoxBottom;
                else j = Math.min(j+(bBoxBottom - bBoxTop -1)/2, bBoxBottom -1);
            }
            
            if (i == bBoxBottom -1) i = bBoxRight;
            else i = Math.min(i+(bBoxRight - bBoxLeft -1)/2, bBoxRight -1);
        }
        
        return false;
    }
    
    private boolean collisionCheck(ActiveObject other) {
        // Sprawdzanie kolizji z innymi obiektami.
        
        if ((other == null) || (other == this)) return false;
        
        if ((x+this.bBoxRight -1 >= other.getX()+other.getBBox("left"))
            && (x+this.bBoxLeft <= other.getX()+other.getBBox("right")-1)) {
            if ((y+this.bBoxTop <= other.getY()+other.getBBox("bottom")-1)
                    && (y+this.bBoxBottom -1 >= other.getY()+other.getBBox("top")))
                return true;
        }
        
        return false;
    }
    
    private void moveObject() {
        // Generalny kod do ruchu.
        // Unika nie-całkowitych pozycji.

        boolean openOnLeft = !collisionCheck(x - 1, y);
        boolean openOnTop = !collisionCheck(x, y - 1);
        boolean openOnRight = !collisionCheck(x + 1, y);
        boolean openOnBottom = !collisionCheck(x, y + 1);
        
        xAdd += xSpeed;
        yAdd += ySpeed;
        
        while (Math.abs(xAdd) >= 1) {
            if (collisionCheck(x+(int)Math.signum(xAdd),y)) {
                hitWall(false);
                xAdd = 0;
            }
            else {
                x += (int)Math.signum(xAdd);
                xAdd = approach(xAdd,0,1);
                
                if (((!collisionCheck(x,y-1)) && (!openOnTop))
                        || ((!collisionCheck(x,y+1)) && (!openOnBottom)))
                    passedEntrance(true);
            }
        }
        
        while (Math.abs(yAdd) >= 1) {
            if (collisionCheck(x,y+(int)Math.signum(yAdd))) {
                hitWall(true);
                yAdd = 0;
            }
            else {
                y += (int)Math.signum(yAdd);
                yAdd = approach(yAdd,0,1);
                
                if (((!collisionCheck(x-1,y)) && (!openOnLeft))
                  || ((!collisionCheck(x+1,y)) && (!openOnRight))) passedEntrance(false);
            }
        }
        
        xSpeed += xPull;
        ySpeed += yPull;
    }
    
    private int wraparound(int coord, boolean isVertical) {
        // Zwraca koordynaty "owinięte" wokół brzegów mapy.
        
        if ((wraparoundEnabled) && (collisionMap != null)) {
            if (isVertical) {
                if (coord > collisionMap.getHeight()) return coord-collisionMap.getHeight();
                else if (coord < 0) return coord+collisionMap.getHeight();
            }
            else {
                if (coord > collisionMap.getWidth()) return coord-collisionMap.getWidth();
                else if (coord < 0) return coord+collisionMap.getWidth();
            }
        }
        
        return coord;
    }
    
    protected void hitWall(boolean isVertical) {
    }
    
    protected void passedEntrance(boolean isVertical) {
    }
    
    private String spriteSheet;
    protected double imageIndex, subImageIndex;
    private int imageWidth, imageHeight;
    int bBoxLeft, bBoxRight, bBoxTop, bBoxBottom;
    
    protected double xSpeed, ySpeed, xAdd, yAdd, xPull, yPull;

    boolean wallContact, wraparoundEnabled;
    boolean tangible;
    
    private int getBBox(String loc) {
        switch (loc) {
            case "left": return bBoxLeft;
            case "right": return bBoxRight;
            case "top": return bBoxTop;
            case "bottom": return bBoxBottom;
        }
        
        return 0;
    }
    
    private boolean isTangible() {
        return tangible;
    }
}
