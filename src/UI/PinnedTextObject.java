
package UI;

import game.objects.GameObject;
import java.io.Serializable;

public class PinnedTextObject extends TextObject implements Serializable {
    // Rozszerzona wersja TextObject, która może być
    // "przypięta" do jakiegoś innego GameObject.
    
    @Override
    public void stepEvent() {
        super.stepEvent();
        this.xorigin = pin.getXorigin();
        this.yorigin = pin.getYorigin();
        x = pin.getX()+addx;
        y = pin.getY()+addy;
    }

    private GameObject pin;
    private int addx, addy;
}
