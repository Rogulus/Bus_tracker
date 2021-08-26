package model;

import model.Interfaces.Executable;
import model.Interfaces.Point2D;

import java.lang.*;
import java.util.*;


/**
* Třída reprezentujíí jednu linku.
* @author Michael Kinc
*/


public class Line implements Executable, Comparable<Object> {
    String color;
    List<Street> alternativeStreets = new ArrayList<>();
    List<Street> originalStreets = new ArrayList<>();
    List<Street> streets;
    List<BusStop> stops;//in context of street must be placed in demanded order
    List<Connection> connections;

    List<RouteElement> routeElementList = new ArrayList<>();
    Point2D firstElement = null;
    Point2D secondElement = null;
    boolean wasStop = false;

    String name;
    MyMap parentMap;
    DPMB dpmb;

    /**
     * Konstruktor třídy Line
     * @param name jméno linky
     * @param color barva linky na mapě
     * @param parentMap mapa
     * @param dpmb instance třída DPMB, která uchovává všechny linky a autobusy
     */

    public Line(String name, String color, MyMap parentMap, DPMB dpmb){
        this.connections = new ArrayList<>();
        this.name = name;
        this.color = color;
        this.streets = new ArrayList<>();
        this.stops = new ArrayList<>();
        this.parentMap = parentMap;
        this.dpmb = dpmb;
    }

    /**
     * Metoda, která vrací barvu dané linky
     * @return barva linky
     */

    public String getColor(){
        return color;
    }

    /**
     * Metoda, ktera vrací list zastávek, kde linka zastavuje.
     * @return Vrací list zastávek.
     */

    public List<BusStop> getStops() {
        return this.stops;
    }

    /**
     * Metoda, která vrací zpoždění spoje na lince
     * @param connection spoj linky
     * @return Vrací zpoždění spoje na lince. Pokud nenajde daný spoj v seznamu spojů, vrací -1.
     */

    public int getConnectionDelay(Connection connection) {
        for (Connection c : this.connections) {
            if (c == connection) {
                return connection.getDelay();
            }
        }
        return -1;
    }

    /**
     * Vrací list všech ulic, kterými linka jezdí.
     * @return Vrací list ulic.
     */

    public List<Street> getStreets(){
        return streets;
    }

    /**
     * Přidá ulici do linky a linku do ulice.
     * @param name jméno přidávané ulice
     */

    public void addStreet(String name){
        Street street = parentMap.getStreet(name);
        street.addLine(this);
        originalStreets.add(street);
    }

    /**
     * Přidá ulici do alternativní cesty.
     * @param street název přidávané ulice
     */

    public void addStreetToAlternativeRoute(Street street){
        alternativeStreets.add(street);
    }

    /**
     * Přidá zastávku do linky.
     * @param name jméno přidávané zastávky
     */

    public void addStop(String name) {
        stops.add(parentMap.getStop(name));
    }

    /**
     * Vytvoří nový spoj linky.
     * @param startHour začátek spoje v hodinách
     * @param startMinute začátek spoje v minutách
     */

    public void addConnection(int startHour,int startMinute){
        connections.add(new Connection(startHour, startMinute, this, dpmb));
    }

    /**
     * Nastaví linku. Nataví body, po kterých linka jede, spočítá dobu jízdy a nastaví všechny spoje.
     */

    public void setUpLine(){
        streets = originalStreets;
        createRouteElementSequence(originalStreets);
        recalculateAllTransitDuration();
        for(Connection c: connections){
            c.setUp();
        }
    }

    /**
     * Nastaví alternativní trasu linky při objížďce.
     */

    public void setUpAlternativeLine(){
        for(Street street : streets){
            street.removeLine(this);
        }
        for (Street newStreet : alternativeStreets){
            newStreet.addLine(this);
        }
        streets.clear();
        streets.addAll( alternativeStreets);
        alternativeStreets.clear();
        createRouteElementSequence(streets);
        recalculateAllTransitDuration();
    }


    /**
     * Vytvoří sekvenci úseček, po kterých jede linka.
     * @param streets list ulic
     */

    public void createRouteElementSequence(List<Street> streets){
        //setting for addPointsToSequence
        firstElement = null;
        routeElementList.clear();

        Crossroad c1;
        Crossroad c2;
        Street previousStreet = null;

        for(Street street: streets) {
            c1 = street.getStart();
            c2 = street.getEnd();

            if(previousStreet != null) {
                if(previousStreet.containsCrossroad(c1)){
                    addStopsToSequence(street);
                    addToSequence(false, c2, street);
                }
                else{
                    addStopsToSequence(street);
                    addToSequence(false, c1, street);
                }
            }
            else{
                if(streets.get(1).containsCrossroad(c1)){//todo co kdyz jen jedna ulcie
                    addToSequence(false, c2, street);
                    addStopsToSequence(street);
                    addToSequence(false, c1, street);
                }
                else{
                    addToSequence(false, c1, street);
                    addStopsToSequence(street);
                    addToSequence(false, c2, street);
                }
            }

            previousStreet = street;
        }
        //System.out.println(routeElementList);
    }

    /**
     * Vrací první úsečku
     * @return Vrací první úsečku v listu úseček.
     */

    public RouteElement getFirstRouteElement(){
        return routeElementList.get(0);
    }

    /**
     * Vrací poslední psečku
     * @return Vrací poslední úsečku v listu úseček.
     */

    public RouteElement getLastRouteElement(){
        return routeElementList.get(routeElementList.size() - 1);
    }


    /**
     * Vrací jméno linky
     * @return Vrací jméno linky.
     */

    public String getName() {
        return name;
    }

    /**
     * Metoda, která vrací list spojů v dané lince.
     * @return Vrací list spojů v lince.
     */

    public List<Connection> getConnections(){
        return connections;
    }

    public void execute(){
        //System.out.println("Line: " + this.name + " was executed.");
    }

    /**
     * Přetížená metoda toString
     * @return Vrací jméno linky. Dale ulice, zastávky po kterých jede a spoje dané linky.
     */

    public String toString(){
        return "line name: " + this.name + "\n streets :  " + this.streets + "\n stops :  " + this.stops + "\n connections :  " + this.connections;
    }

    /**
     * Přirání úsečky do sekvence úseček.
     * @param isStop příznak, že se jedná o zastávku
     * @param point bod na mapě
     * @param street ulice
     */

    private void addToSequence(boolean isStop, Point2D point, Street street){
        if( firstElement == null) {
            firstElement = point;
            secondElement = point;
            wasStop =  isStop;
        }
        else {
            firstElement = secondElement;
            secondElement = point;
            this.routeElementList.add(new RouteElement(wasStop, firstElement, secondElement, street));
            wasStop = isStop;
        }
    }

    /**
     * Přidání zástávky do sekvence.
     * @param street ulice
     */

    private void addStopsToSequence(Street street){
        for(BusStop stop: stops){
            if(street.haveStop(stop)){
                addToSequence(true, stop, street);
            }
        }
    }

    /**
     * Přetížená metoda compareTo
     * @param b objekt k porovnání
     * @return Vrací kladnou hodnotu v případě, že první objekt je lexikograficky větší, a zápornou hodnotu, pokud je to naopak.
     */

    public int compareTo(Object b){
        return this.toString().compareTo(b.toString());
    }


    /**
     * Vrací list úseček.
     * @return Vrací list úseček.
     */

    public List<RouteElement> getRouteElementList(){
        return new ArrayList<>(routeElementList);
    }

    //sets the new value of the transitDuration for the line

    /**
     * Nastaví novou hodnotu délky jízdy pro linku.
     * @return Vrací dobu jízdy.
     */

    public int recalculateAllTransitDuration(){
        int transitDuration = 0;
        for(RouteElement re: routeElementList){
            re.setDelaySinceStart(transitDuration);
            transitDuration += re.getTransitDuration();
            //System.out.println("startTime:" + re.getDelaySinceStart() + "endTime:" + (re.getDelaySinceStart()+re.getTransitDuration()) + "re transit Duration:" +  re.getTransitDuration());
        }
        for(Connection c: connections){
            c.setEndTime(new DayTime(c.getStartTime().getTime() + transitDuration));
            //System.out.println("connection:"+" startTime:" + c.getStartTime() + "endTime:" + c );
        }
        return transitDuration;
    }
}