package MainFiles;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import jslEngine.*;

import javax.annotation.processing.SupportedSourceVersion;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class MainClass extends jslEngine {

    private boolean allPointVisible = false;

    public MainClass() {
        start("Gwóźdź programu", 700, 450);

        Pin pin = new Pin();

        jsl.add(pin);
    }

    protected void update(float et) {

    }

    protected void render(Graphics g) {

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

    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////       Classes       //////////////////////////////////////////////////

    private class Pin extends jslObject {

        private LinkedList<DotPair> dotPairs = new LinkedList<>();
        private SingleDot head, end;

        public Pin() {
            dotPairs.add(new DotPair(WW()/2, 100));
            dotPairs.add(new DotPair(WW()/2, 200));
            dotPairs.add(new DotPair(WW()/2, 300));

            head = new SingleDot(WW()/2,40, null);
            end = new SingleDot(WW()/2, WH()-40, null);
        }

        public void update(float et) {
            for(DotPair d : dotPairs) {
                d.update(et);
            }
        }

        public void render(Graphics g) {
            if(dotPairs.size() > 0) {
                Graphics2D g2 = (Graphics2D)g;
                RenderingHints rh = new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHints(rh);

                int[] xP = new int[dotPairs.size()*2+2];
                int[] yP = new int[dotPairs.size()*2+2];

                int dw = (int) dotPairs.get(0).dots[0].getW();
                int dh = (int) dotPairs.get(0).dots[0].getH();

                xP[0] = (int)head.getX() + dw / 2;
                yP[0] = (int)head.getY() + dh / 2;

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

        public DotPair(float x, float y) {
            jsl.add(this);

            for (int i=0; i<dots.length; i++) {
                dots[i] = new Dot(x + i*20, y, this);
            }
            moved();
            this.y = y;
            setH(dots[0].getH());
            System.out.println(y);
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

        public void onEnter() {

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
            setSize(20, 20);
            defaultSettings.isRendering = false;
            settings = defaultSettings;
            defaultSettings.bgColor =
            onHoverSettings.bgColor =
            settings.bgColor = new Color(21, 76, 19);
            defaultSettings.txtColor =
            onHoverSettings.txtColor =
            settings.txtColor = new Color(11, 70, 33);

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
    }

    private class SingleDot extends Dot {

        public SingleDot(float x, float y, DotPair pair) {
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
//            setY(mouse.getY()-getH()/2);
        }
    }
}
