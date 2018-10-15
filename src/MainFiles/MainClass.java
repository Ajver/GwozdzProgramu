package MainFiles;

import jslEngine.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class MainClass extends jslEngine {

    private boolean allPointVisible = false;
    private jslButton newPoint;
    private Pin pin;


    public MainClass() {
        start("Gwóźdź programu", 700, 450);
        setAntialiasing(true);

        jsl.setAutorender(false);

        pin = new Pin();

        newPoint = jsl.newButton("Nowy punkt");

        jsl.add(pin);
    }

    protected void update(float et) {

    }

    protected void render(Graphics g) {
        pin.render(g);
    }

    public static void main(String[] args) {
        new MainClass();
    }

    protected void onKeyPressed() {
        if(key.getKeyCode() == KeyEvent.VK_SPACE) {
            allPointVisible = true;
        }
    }

    protected void onKeyReleased() {
        if(key.getKeyCode() == KeyEvent.VK_SPACE) {
            allPointVisible = false;
        }
    }

    protected void onUnclick(jslObject o) {
        if(o == newPoint) {
            o.settings.bgColor = new Color(255, 100, 255);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////       Classes       //////////////////////////////////////////////////

    private class Pin extends jslObject {

        private LinkedList<DotPair> dotPairs = new LinkedList<>();
        private End end;
        private Head head;

        public Pin() {
            x = WW()/2;
            w = 10;
            dotPairs.add(new DotPair(0, 100));
            dotPairs.add(new DotPair(0, 200));
            dotPairs.add(new DotPair(0, 300));

            head = new Head(0,40);
            end = new End(0, WH()-40, null);
        }

        public void update(float et) {
            for(DotPair d : dotPairs) {
                d.update(et);
            }
        }

        public void render(Graphics g) {
            if(dotPairs.size() > 0) {
                int[] xP = new int[dotPairs.size()*2+3];
                int[] yP = new int[dotPairs.size()*2+3];

                int dw = (int) dotPairs.get(0).dots[0].getW();
                int dh = (int) dotPairs.get(0).dots[0].getH();

                xP[0] = (int)head.dots[0].getX() + dw / 2;
                yP[0] = (int)head.dots[0].getY() + dh / 2;

                for (int i = 0; i < dotPairs.size(); i++) {
                    DotPair d = dotPairs.get(i);

                    xP[i + 1] = (int) d.dots[0].getX() + dw / 2;
                    yP[i + 1] = (int) d.dots[0].getY() + dh / 2;
                }

                xP[dotPairs.size() + 1] = (int)end.getX() + dw / 2;
                yP[dotPairs.size() + 1] = (int)end.getY() + dh / 2;

                for (int i = 0; i < dotPairs.size(); i++) {
                    DotPair d = dotPairs.get(dotPairs.size()-i-1);

                    xP[i + dotPairs.size() + 2] = (int) d.dots[1].getX() + dw / 2;
                    yP[i + dotPairs.size() + 2] = (int) d.dots[1].getY() + dh / 2;
                }

                xP[xP.length - 1] = (int)head.dots[1].getX() + dw / 2;
                yP[yP.length - 1] = (int)head.dots[1].getY() + dh / 2;

                g.setColor(new Color(209, 209, 209));
                g.fillPolygon(xP, yP, xP.length);

                for(DotPair d : dotPairs) {
                    d.render(g);
                }
                head.render(g);
                end.render(g);
            }
        }
//
//        public void onClick() {
//
//            System.out.println("I was clicked!" + (new Random()).nextInt(40));
//        }
    }

    private class DotPair extends jslObject {

        public Dot[] dots = new Dot[2];

        public DotPair() {}

        public DotPair(float x, float y) {
            jsl.add(this);

            createDots(x, y);
        }

        protected void createDots(float x, float y) {
            for (int i=0; i<dots.length; i++) {
                dots[i] = new Dot(x + i*10 - 10, y, this);
            }
            moved();
            this.y = y;
            setH(dots[0].getH());
        }

        public void update(float et) {
            for (int i=0; i<dots.length; i++) {
                dots[i].update(et);
            }
        }

        public void render(Graphics g) {
            if(hover) {
                g.setColor(new Color(45, 27, 27));
                g.drawLine((int)(dots[0].getX()+dots[0].getW()/2), (int)(dots[0].getY()+dots[0].getH()/2),
                        (int)(dots[1].getX()+dots[0].getW()/2), (int)(dots[1].getY()+dots[0].getH()/2));
                //g.fillRect((int)x, (int)y, (int)w, (int)h);
            }

            for (int i=0; i<dots.length; i++) {
                dots[i].render(g);
            }
        }

        public void setY(float y) {
            for (int i=0; i<dots.length; i++) {
                dots[i].setY(y);
            }
            this.y = y;
        }

        public void moved() {
//            float sx = dots[0].getX() + dots[1].getY();
//            setX(sx/2.0f);

            setX(Math.min(dots[0].getX(), dots[1].getX()) + dots[0].getW()/2);

            float diffX = dots[0].getX() - dots[1].getX();
            setW(Math.abs(diffX));

            // Swaping
            if(dots[0].getX() > dots[1].getX()) {
                Dot temp = dots[0];
                dots[0] = dots[1];
                dots[1] = temp;
            }
        }

        public void onClick() {
            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        }

        public void onLeave() {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        public void onUnclick() {
            onLeave();
        }

        public void onDrag() {
            setY(mouse.getY());
        }

//
//        public void onClick() {
//            for (int i=0; i<dots.length; i++) {
//                if (dots[i].isPointIn(mouse.getX(), mouse.getY())) {
//                    dots[i].onClick();
//                }
//            }
//        }
    }

    private class Dot extends jslObject {

        private DotPair pair;

        public Dot(float x, float y, DotPair pair) {
            this.pair = pair;
            setPosition(x, y);
            setSize(10, 10);
            defaultSettings.isRendering = false;
            settings = defaultSettings;
            defaultSettings.bgColor =
            onHoverSettings.bgColor =
            settings.bgColor = new Color(21, 76, 19);
            defaultSettings.txtColor =
            onHoverSettings.txtColor =
            settings.txtColor = new Color(11, 70, 33);

            setMaxX(WW()-w);
            setMinX(0);
            setMaxY(WH()-h);
            setMinY(0);

            jsl.add(this);
        }

        public void update(float et) {

        }

        public void render(Graphics g) {
            if(settings.isRendering) {
                g.setColor(settings.bgColor);
                g.fillOval((int) (x), (int) (y), (int) w, (int) h);
            }else if(allPointVisible) {
                g.setColor(settings.txtColor);
                g.drawOval((int) (x), (int) (y), (int) w, (int) h);
            }
        }

        public void onDrag() {
            setX(mouse.getX()-getW()/2);
//            setY(mouse.getY()-getH()/2);

            pair.moved();
        }

        public void onClick() {
            settings = onHoverSettings;
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        }

        public void onLeave() {
            settings = defaultSettings;
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        public void onUnclick() {
            onLeave();
        }
    }

    private class End extends Dot {

        public End(float x, float y, DotPair pair) {
            super(x, y, pair);
            defaultSettings.bgColor =
            onHoverSettings.bgColor =
            settings.bgColor = new Color(76, 19, 19);
            defaultSettings.txtColor =
            onHoverSettings.txtColor =
            settings.txtColor = new Color(112, 19, 19);
        }

        public void onDrag() {
            setX(mouse.getX()-getW()/2);

            float ny = mouse.getY()-getH()/2;

            setY(ny);
        }

        public void onClick() {
            settings = onHoverSettings;
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    private class Head extends DotPair {

        private float hx, hy;
        // how - Height Over Width
        private float hw = 60, how = 0.25f;

        public Head(float x, float y) {
            this.hx = x - hw*0.5f;
            this.hy = y - 2;

            createDots(x, y);
        }

        public void render(Graphics g) {
            g.setColor(new Color(200, 200, 200));
            g.fillOval((int)hx, (int)hy, (int)hw, (int)(hw*how));

            if(hover) {
                g.setColor(new Color(45, 27, 27));
                g.drawLine((int)(dots[0].getX()+dots[0].getW()/2), (int)(dots[0].getY()+dots[0].getH()/2),
                        (int)(dots[1].getX()+dots[0].getW()/2), (int)(dots[1].getY()+dots[0].getH()/2));
                //g.fillRect((int)x, (int)y, (int)w, (int)h);
            }

            for (int i=0; i<dots.length; i++) {
                dots[i].render(g);
            }
        }
    }
}
