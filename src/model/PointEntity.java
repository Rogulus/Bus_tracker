package model;

import model.Interfaces.Point2D;


/**
 * Třídy reprezentující bod.
 * @author Marek Šťastný
 */

public class PointEntity implements Point2D {
    int x;
    int y;

    /**
     * Konstruktor třídy PointEntity
     * @param x hodnota na ose x
     * @param y hodnota na ose y
     */

    public PointEntity(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Vrací x
     * @return x
     */

    public int getX(){
        return this.x;
    }

    /**
     * Vrací y
     * @return y
     */

    public int getY() {
        return this.y;
    }

    /**
     * Vrací vzdálenost dvou bodů
     * @param point bod
     * @return vzdálenost dvou bodů
     */

    public int distance(Point2D point){
        return (int)Math.sqrt(Math.pow(point.getX()-this.x, 2) + Math.pow(point.getY()-this.y,2));
    }

    /**
     * Spočítá x dle procentuální vzdálenost.
     * @param start počáteční bod
     * @param end koncový bod
     * @param percent procenta
     * @return vrací x
     */

    public static int countX(Point2D start, Point2D end, int percent){
        return start.getX() + (((end.getX() - start.getX()) * percent) / 100);
    }

    /**
     * Spočítá y dle procentruální vzdálenost
     * @param start počáteční bod
     * @param end koncový bod
     * @param percent procenta
     * @return vrací y
     */

    public static int countY(Point2D start, Point2D end, int percent){
        return start.getY() + (((end.getY() - start.getY()) * percent) / 100);
    }

}