
package UI;

import java.awt.*;
import java.io.*;

import game.objects.GameObject;

public class TextObject extends GameObject implements Serializable {
    
    @Override
    public void createEvent() {
        ourText = "";
        ourPrefix = "";
        ourPostfix = "";
        font = null;
        fontWidth = 0;
        fontHeight = 0;
        
        depth = -50;
    }

    @Override
    public void stepEvent() {
        //
    }
    
    @Override
    public void drawEvent(Graphics2D graphics) {
        if (!visible)
        {return;}
        
        drawMyText(graphics);
    }
    
    @Override
    public void destroyEvent() {
    }
    
    void forceDraw(double scaleMod, int screenCenterX, int screenCenterY, Graphics2D graphics) {
        setCenter(screenCenterX,screenCenterY);
        setScale(scaleMod);
        
        visible = true;
        drawMyText(graphics);
        visible = false;
    }
    
    public void loadFont(String font, int width, int height) {
        this.font = font;
        
        fontWidth = width;
        fontHeight = height;
    }

    private Point charToPos(int c) {
        // row 0 - Uppercase, row 1 - Lowercase, row 2 - Digits, row 3 - Misc
        if (c == ' ') return new Point(10,2);
        if ((c >= 'A') && (c <= 'Z')) return new Point(c-'A',0);
        if ((c >= 'a') && (c <= 'z')) return new Point(c-'a',1);
        if ((c >= '0') && (c <= '9')) return new Point(c-'0',2);
        
        switch (c) {
            case '!': return new Point(0,3);
            case '"': return new Point(1,3);
            case '#': return new Point(2,3);
            case '$': return new Point(3,3);
            case '%': return new Point(4,3);
            case '&': return new Point(5,3);
            case '\'': return new Point(6,3);
            case '(': return new Point(7,3);
            case ')': return new Point(8,3);
            case '*': return new Point(9,3);
            case '+': return new Point(10,3);
            case ',': return new Point(11,3);
            case '-': return new Point(12,3);
            case '.': return new Point(13,3);
            case '/': return new Point(14,3);
            case ':': return new Point(15,3);
            case ';': return new Point(16,3);
            case '<': return new Point(17,3);
            case '=': return new Point(18,3);
            case '>': return new Point(19,3);
            case '?': return new Point(20,3);
            case '_': return new Point(25,2);
            case 201: return new Point(21,3); // Symbol PacMan'a.);
            case 202: return new Point(22,3); // Czerwony duszek.
            case 203: return new Point(23,3); // Żółty duszek.
            case 204: return new Point(24,3); // Niebieski duszek.
            case 205: return new Point(25,3); // Różowy duszek.
        }
        
        return new Point(10,2);
    }
    
    void drawMyText(Graphics2D graphics) {
        
        int c = 0, tmp_x = 0, tmp_y = 0;
        Point fontPos = null;
        
        for (int i = 0; i < ourText.length(); i ++) {
            c = ourText.charAt(i);
            
            if (c == '\n') {
                tmp_x = 0;
                tmp_y += fontHeight;
            }
            else {
                fontPos = charToPos(c);
                drawSprite(graphics,getSprites(font),x+tmp_x,y+tmp_y,fontPos.x,fontPos.y,fontWidth,fontHeight);
                tmp_x += fontWidth;
            }
        }
    }

    protected String ourText, ourPrefix, ourPostfix;
    private String font;
    protected int fontWidth, fontHeight;

    public void setText(String s) {
        ourText = ourPrefix+s+ourPostfix;
    }
    
    public void setPrefix(String s) {
        ourPrefix = s;
    }
    
    void setPostfix(String s) {
        ourPostfix = s;
    }
}
