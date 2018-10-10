package MainFiles;

import jslEngine.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

public class MainClass extends jslEngine {

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

    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////       Classes       //////////////////////////////////////////////////

    private class Pin extends jslObject {

        private LinkedList<DotPair> dotPairs = new LinkedList<>();

        public Pin() {
            dotPairs.add(new DotPair(WW()/2, 40));
            dotPairs.add(new DotPair(WW()/2, WH()-40));

        }

        public void update(float et) {
            for(DotPair d : dotPairs) {
                d.update(et);
            }
        }

        public void render(Graphics g) {
            for(int i=0; i<dotPairs.size()-1; i++) {
                dotPairs.get(i).render(g, dotPairs.get(i+1));
            }

            for(DotPair d : dotPairs) {
                d.render(g);
            }
        }
//
//        public void onClick() {
//
//            System.out.println("I was clicked!" + (new Random()).nextInt(40));
//        }
    }

    private class DotPair {

        public Dot[] dots = new Dot[2];

        public DotPair(float x, float y) {
            for (int i=0; i<dots.length; i++) {
                dots[i] = new Dot(x + i*20, y);
                jsl.add(dots[i]);
            }
            setY(y);
        }

        public void update(float et) {
            for (int i=0; i<dots.length; i++) {
                dots[i].update(et);
            }
        }

        public void render(Graphics g) {
            for (int i=0; i<dots.length; i++) {
                dots[i].render(g);
            }
        }

        public void render(Graphics g, DotPair d) {
            g.setColor(new Color(209, 209, 209));

            int[] xP = new int[4];
            int[] yP = new int[4];

            int dw = (int)dots[0].getW();
            int dh = (int)dots[0].getH();

            xP[0] = (int)dots[0].getX()+dw/2;
            xP[1] = (int)dots[1].getX()+dw/2;
            xP[2] = (int)d.dots[1].getX()+dw/2;
            xP[3] = (int)d.dots[0].getX()+dw/2;

            yP[0] = (int)dots[0].getY()+dh/2;
            yP[1] = (int)dots[1].getY()+dh/2;
            yP[2] = (int)d.dots[1].getY()+dh/2;
            yP[3] = (int)d.dots[0].getY()+dh/2;

            g.fillPolygon(xP, yP, 4);
        }

        public void setY(float y) {
            for (int i=0; i<dots.length; i++) {
                dots[i].setY(y);
            }
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

        private boolean isDraging;

        public Dot(float x, float y) {
            setPosition(x, y);
            setSize(20, 20);
        }

        public void update(float et) {

        }

        public void render(Graphics g) {
            g.setColor(new Color(255,255, 255));
            g.fillOval((int)(x), (int)(y), (int)w, (int)h);
        }

        public void onDrag() {
            setX(mouse.getX()-getW()/2);
            setY(mouse.getY()-getH()/2);
        }

    }
}
