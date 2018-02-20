
package game.pacman;

public class Sprite {
    // Zbiór wartości niezbędnych do tego, aby narysować pojedynczą klatkę.
    
    public Sprite(String src, double xat, double yat, int width, int height) {

        this.src = src;
        this.xat = xat;
        this.yat = yat;
        this.width = width;
        this.height = height;
    }

    private String src;
    private double xat;
    private double yat;
    private int width;
    private int height;
    
    public String getSrc()
    {return src;}
    public double getXat()
    {return xat;}
    public double getYat()
    {return yat;}
    public int getWidth()
    {return width;}
    public int getHeight()
    {return height;}
}
