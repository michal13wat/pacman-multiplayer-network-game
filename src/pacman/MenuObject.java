
package pacman;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class MenuObject extends GameObject {
    
    private class MenuOption {
        // Private class for different options.
        
        public void setMenuOption(String name, Callable function) {
            this.name = name;
            this.function = function;
        }
        
        public String getName() {
            return name;
        }
        
        protected String name;
        protected Callable function;
        
        public Callable getFunction() {
            return function;
        }
    }

    private class ButtonPressOption extends MenuOption {
        // Activated upon pressing a button.
        
        public void setMenuOption(String name, Callable function, String button) {
            this.name = name;
            this.function = function;
            this.button = button;
        }
        
        public String getButton() {
            return button;
        }
        
        protected String button;
    }
    
    private class SpinnerOption extends MenuOption {
        // Changing the value of a variable.
        
        public void setMenuOption(String name, Callable function, IntWrapper value, int lbound, int rbound) {
            this.name = name;
            this.function = function;
            this.valueWrapper = value;
            this.leftBound = lbound;
            this.rightBound = rbound;
        }
        
        public void addValue(int x) {
            valueWrapper.value += x;
            if (valueWrapper.value > rightBound) valueWrapper.value = rightBound;
            if (valueWrapper.value < leftBound) valueWrapper.value = leftBound;
        }
        
        public int getValue() {
            return valueWrapper.value;
        }
        
        public boolean spinnerFull(String dir) {
            if (dir == "left") return (valueWrapper.value == leftBound);
            if (dir == "right") return (valueWrapper.value == rightBound);
            
            return false;
        }
        
        protected IntWrapper valueWrapper;
        protected int leftBound, rightBound;
    }

    @Override
    public void createEvent() {
        myOptions = new ArrayList<MenuOption>();
        hiddenOptions = new ArrayList<MenuOption>();
        menuTitle = null;
        depth = -50;
    }
    
    @Override
    public void stepEvent() {
        MenuOption m = myOptions.get(cursorPos);
        
        if (game.keyboardCheck("enter")) select(m);
        else if ((game.keyboardCheck("left")) && (!game.keyboardHoldCheck("left"))) {
            if (m instanceof SpinnerOption) ((SpinnerOption)m).addValue(-1);
        }
        else if ((game.keyboardCheck("right")) && (!game.keyboardHoldCheck("right"))) {
            if (m instanceof SpinnerOption) ((SpinnerOption)m).addValue(1);
        }
        else if ((game.keyboardCheck("up")) && (!game.keyboardHoldCheck("up"))) {
            cursorPos --;
            if (cursorPos < 0) cursorPos += myOptions.size();
        }
        else if ((game.keyboardCheck("down")) && (!game.keyboardHoldCheck("down"))) 
            cursorPos = (cursorPos+1)%myOptions.size();
        
        for (int i = 0; i < hiddenOptions.size(); i ++) {
            MenuOption o = hiddenOptions.get(i);
            if ((o instanceof ButtonPressOption)
            && (game.keyboardCheck(((ButtonPressOption)o).getButton()))
            && (!game.keyboardHoldCheck(((ButtonPressOption)o).getButton())))
                select(o);
        }
    }

    @Override
    public void destroyEvent() {
        if (renderer != null) renderer.destroy();
    }

    @Override
    public void drawEvent(Graphics2D g) {
        MenuOption o = null;
        
        if (renderer == null) {
            renderer = (TextObject)createObject(TextObject.class,x,y);
            renderer.loadFont(fontSource,fontWidth,fontHeight);
        }
        
        if (menuTitle != null) {
            renderer.setPrefix(" ");
            renderer.setText(menuTitle);
            renderer.setPosition(x,y-fontHeight);
            renderer.drawEvent(g);
        }
        
        for (int i = 0; i < myOptions.size(); i ++) {
            o = myOptions.get(i);
            renderer.setPostfix("");
            
            if (o instanceof SpinnerOption) {
                // Drawing the spinner arrows.
                SpinnerOption so = (SpinnerOption)o;
                String s = "";
                if ((so.spinnerFull("left")) || (cursorPos != i)) s += " ";
                else s += "<";
                s += String.valueOf(so.getValue());
                if ((so.spinnerFull("right")) || (cursorPos != i)) s += " ";
                else s += ">";

                renderer.setPostfix(s);
            }
            
            // Selected arrow.
            if (cursorPos == i) renderer.setPrefix("> ");
            else renderer.setPrefix("  ");
            
            renderer.setText(o.getName());
            renderer.setPosition(x,y+fontHeight*i);
            renderer.drawEvent(g);
        }
    }

    public void addMenuOption(String name, Callable newFunction) {
        MenuOption o = new MenuOption();
        o.setMenuOption(name,newFunction);
        myOptions.add(o);
    }
    
    public void addButtonPressOption(String name, Callable newFunction, String button) {
        ButtonPressOption o = new ButtonPressOption();
        o.setMenuOption(name,newFunction,button);
        hiddenOptions.add(o);
    }

    public void addSpinnerOption(String name, Callable newFunction, IntWrapper value, int lbound, int rbound) {
        SpinnerOption o = new SpinnerOption();
        o.setMenuOption(name,newFunction,value,lbound,rbound);
        myOptions.add(o);
    }
    
    private void select(MenuOption o) {
        try {
            Callable f = o.getFunction();
            if (f != null) {
                f.call();
                destroy();
            }
        }
        catch (Exception e) {}
    }
    
    protected TextObject renderer;

    private ArrayList<MenuOption> myOptions, hiddenOptions;
    protected String fontSource;
    protected String menuTitle;
    protected int fontWidth, fontHeight;
    protected int cursorPos;

    public void setFont(String src, int w, int h) {
        fontSource = src;
        fontWidth = w;
        fontHeight = h;
    }
    
    public void setTitle(String s) {
        if (menuTitle == null) y += fontHeight;
        menuTitle = s;
    }
    
    public int getMenuHeight() {
        int h = myOptions.size();
        if (menuTitle != null) h += 1;
        
        return fontHeight*h;
    }
    
    public int getMenuWidth() {
        int max = menuTitle.length();
        String s;
        
        for (int i = 0; i < myOptions.size(); i ++) {
            s = myOptions.get(i).getName();
            if (s.length() > max)
            {max = s.length();}
        }
        
        return fontWidth*max;
    }
}
