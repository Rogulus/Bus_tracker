package model;

import model.Interfaces.Point2D;

/**
 * Třída reprezentující úsečku na mapě.
 * @author Marek Šťastný
 */

public class RouteElement{
    private Point2D start;
    private Point2D end;
    private double maxSpeed;
    private boolean startIsStop;
    private Street street;
    private int delaySinceStart;

    /**
     * Konstruktor třídy RouteElement
     * @param startIsStop příznak, jestli první bod je zastávka
     * @param start počáteční bod
     * @param end koncový bod
     * @param street ulice
     */

    public RouteElement( boolean startIsStop, Point2D start, Point2D end, Street street){
        this.startIsStop = startIsStop;
        this.start = start;
        this.end = end;
        this.street = street;
    }

    /**
     * Vrací dobu v sekundách, který říká jak dlouho trvá autobusu doje to konce úsečky. Zahrnuje i čekání autobusu na zastávce jestliže je první bod ulice zastávka.
     * @return doba v sekundách
     */

    //interval in seconds how long takes to the bus till it ends this route elem. include waiting at the bus stop if first element is bus stop
    public int getTransitDuration(){
        int durartion = 0;
        durartion = (int)(start.distance(end) / ((street.getMaxSpeed()/10)));//todo nejspis by nemela jit nastavit hodnota o pokud neni nastavena alternativni cesta
        if(startIsStop){
            durartion += ((BusStop)start).stopDuration;
        }
        return durartion + 1;
    }

    /**
     * Nastaví zpoždění
     * @param secDelay zpoždění v sekundách
     */

    //time value since the start of the first element till the start of this element of the route
    public void setDelaySinceStart(int secDelay){
        this.delaySinceStart = secDelay;
    }

    /**
     * Vrátí zpoždění od startu
     * @return zpoždění
     */

    public int getDelaySinceStart(){
        return delaySinceStart;
    }

    /**
     * Vrátí maximální rychlost na ulici
     * @return maximální rychlost na ulici
     */

    public double getMaxSpeed(){
        return street.getMaxSpeed();
    }

    /**
     * Vrátí ulici
     * @return ulice
     */

    public Street getStreet(){
        return street;
    }

    /**
     * Vrátí příznak toho, jestli první bod úsečky je zastávka
     * @return True nebo False
     */

    public boolean startIsStop(){
        return startIsStop;
    }

    /**
     * Vrátí počáteční bod úsečky
     * @return počátek úsečky
     */

    public Point2D getStart() {
        return start;
    }

    /**
     * Vrátí koncový bod úsečky
     * @return koncový bod úsečky
     */

    public Point2D getEnd() {
        return end;
    }

    /**
     * Přetížená metoda toString
     * @return vrací řetězec obsahující příznak, zda jde o zastávku a ulici, na které se nachází
     */

    public String toString(){
        return "route element: " + "is Stop:"+ startIsStop+ "street:" +street + "$$$";
    }
}