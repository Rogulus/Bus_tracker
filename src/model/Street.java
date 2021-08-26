package model;

import java.util.*;

/**
 * Třída reprezentující jednu ulici.
 * @author Michael Kinc
 */

public class Street {
    private String name;
    private Crossroad start;
    private Crossroad end;
    private double maxSpeed;
    private Map<String, BusStop> streetStops;
    private Set<Line> lines = new TreeSet<>();
    private Set<Bus> buses = new TreeSet<>();

    /**
     * Konstruktor třídy Street.
     * @param name jméno třídy
     * @param startCrossroadName název počátečního bodu ulice
     * @param endCrossroadName název koncového bodu ulice
     * @param parentMap mapa
     */

    public Street(String name, String startCrossroadName, String endCrossroadName, MyMap parentMap){
        this.name = name;
        this.start = parentMap.getCrossroad(startCrossroadName);
        this.end = parentMap.getCrossroad(endCrossroadName);
        this.start.addStreet(this);
        this.end.addStreet(this);
        this.streetStops = new TreeMap<>();
        this.maxSpeed = 42;
        parentMap.addStreet(this);
    }

    /**
     * Metoda, která testuje, zda je ulice součástí nějaké linky
     * @return Vrací True (v případě, že se daná ulice nachází na nějaké line) nebo False (v případěže se daná ulie nenachází na žádné lince).
     */

    public boolean isPartOfLine(){
        return ! lines.isEmpty();
    }

    /**
     * Metoda, která vrací linky, které jezdí přes danou ulici.
     * @return Vrací set linek.
     */

    public Set<Line> getLines() {
        return lines;
    }

    /**
     * Přidá linku do ulice.
     * @param line linka
     */

    public void addLine(Line line) {
        this.lines.add(line);
    }

    /**
     * Odstraní linku z ulice.
     * @param line linka
     */

    public void removeLine(Line line){
        lines.remove(line);
    }

    /**
     * Vrací maximální rychlost na ulici.
     * @return Maximální rychlost na ulici.
     */


    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Nataví ulici maximální rychlost.
     * @param maxSpeed maximálni rychlost
     */

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        for(Bus bus: buses){
            bus.changeVelocity(maxSpeed);
        }
        for(Line line: lines){
            line.recalculateAllTransitDuration();
        }
    }

    /**
     * Přidá zastávku do ulice.
     * @param stop zastávka
     */

    public void addStop(BusStop stop){
        this.streetStops.put(stop.getName(), stop);
    }

    /**
     * Přidá bus do ulice.
     * @param bus autobus
     */

    public void addBus(Bus bus){
        this.buses.add(bus);
    }

    /**
     * Odstraní bus z ulice.
     * @param bus autobus
     */

    public void removeBus(Bus bus){
        this.buses.remove(bus);
    }

    /**
     * Vrátí jméno ulice.
     * @return Jméno ulice.
     */

    public String getName(){
        return this.name;
    }

    /**
     * Vrátí souřadnice začátku ulice
     * @return Souřadnice začátku ulice.
     */

    public Crossroad getStart() {
        return this.start;
    }

    /**
     * Vrátí souřadnice konce ulice
     * @return Souřadnice konce ulice.
     */

    public Crossroad getEnd() {
        return end;
    }

    /**
     * Vrací hodnotu na x-ové souřadnici prostředku ulice.
     * @return hodnota x-ové souřadnice
     */

    public int getHalfPointX(){
        return PointEntity.countX(start, end, 50);
    }

    /**
     * Vrací hodnotu na y-ové souřadnici prostředku ulice.
     * @return hodnota y-ové souřadnice
     */

    public int getHalfPointY(){
        return PointEntity.countY(start, end, 50);
    }

    /**
     * Zjišťuje, jestli koncový nebo počáteční bod jsou křižovatky.
     * @param crossroad bod křižovatky
     * @return True v případě úspěchu, False v případě neúspěchu
     */

    public boolean containsCrossroad(Crossroad crossroad){
        return ((end == crossroad) || (start == crossroad));
    }

    /**
     * Zjišťuje, zda ulice má danou zastávku.
     * @param stop zastávka
     * @return True v případě úspěchu, False v případě neúspěchu
     */

    public boolean haveStop(BusStop stop) {
        boolean result = false;
        for(BusStop s: new ArrayList<>(streetStops.values())){
            if(s.equals(stop)){
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Přetížená funkce toString.
     * @return vrací String s názvem ulice, počátkem a koncem.
     */

    public String toString(){
        return name +" start:"+ start.getName()+" end:" + end.getName();
    }
}