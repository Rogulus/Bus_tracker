package model;

import java.lang.*;
import java.util.*;

/**
 * Třída repzentující map s ulicemi, zastávkami a křižovatkami implementovaná jako jedináček.
 * @author Michael Kinc
 */

public class MyMap {
    private Map<String, Crossroad> crossroads;
    private Map<String, Street> streets;
    private Map<String, BusStop> stops;
    private int width;
    private int hight;
    private static MyMap instance = null;

    /**
     * Konstruktor třídy MyMap
     */

    private MyMap() {
        streets = new TreeMap<>();
        crossroads = new TreeMap<>();
        stops = new TreeMap<>();
    }

    /**
     * Třída, která vrací instanci třídy MyMap. V případě, že tato instance ještě neexistuje, tak ji vytvoří.
     * @return vrací instanci třídy MyMap
     */

    public static MyMap getInstance(){
        if(instance == null){
            instance = new MyMap();
        }
        return instance;
    }

    /**
     * Metoda, která naplní mapu daty.
     * @param filePath cesta k souboru
     */

    public void loadMap(String filePath) {
        MapDataLoader mapLoader = new MapDataLoader();
        mapLoader.loadMapData(filePath,this);
    }

    /**
     * Vrací šířku mapy
     * @return šířka mapy
     */

    public int getWidth(){
        return width;
    }

    /**
     * Vrací výšku mapy.
     * @return výška mapy
     */

    public int getHight() {
        return hight;
    }

    /**
     * Nastaví šířku mapy
     * @param width šířka map
     */

    public void setWidth(int width){
        this.width = width;
    }

    /**
     * Nastaví výšku mapy
     * @param hight výška mapy
     */

    public void setHight(int hight){
        this.hight = hight;
    }

    /**
     * Přidá křižovatku do mapy.
     * @param crossroad křižovatka reprezentující koncový nebo počáteční bod ulice
     */

    public void addCrossroad(Crossroad crossroad){
        this.crossroads.put(crossroad.getName(), crossroad);
    }

    /**
     * Přídá ulici do mapy
     * @param street ulice
     */

    public void addStreet(Street street){
        this.streets.put(street.getName(), street);
    }

    /**
     * Přidá zastávku do mapy.
     * @param stop zastávka
     */

    public void addStop(BusStop stop){
        this.stops.put(stop.getName(), stop);
    }

    /**
     * Vrací křižovatku podle zadaného jména
     * @param name jméno křižovatky
     * @return Vrací křižovatku.
     */

    public Crossroad getCrossroad(String name)  {
        Crossroad tmp_c = crossroads.get(name);
        if(tmp_c == null){
            throw new IllegalArgumentException("Map does not contain: " + name + " crossroad");
        }else {
            return tmp_c;
        }
    }

    /**
     * Vrací ulici podle zadaného jména.
     * @param name jméno ulice
     * @return vrací ulici
     */

    public Street getStreet(String name)  {
        Street tmp_c = streets.get(name);
        if(tmp_c == null){
            throw new IllegalArgumentException("Map does not contain: " + name + " street");
        }else {
            return tmp_c;
        }
    }

    /**
     * Vrací zastávku podle zadaného jména.
     * @param name jméno zastávky
     * @return Vrací zastávku.
     */

    public BusStop getStop(String name)  {
        BusStop tmp_c = stops.get(name);
        if(tmp_c == null){
            throw new IllegalArgumentException("Map does not contain: " + name + " stop");
        }else {
            return tmp_c;
        }
    }

    /**
     * Vypíše na standardní výstup všechny křižovatky.
     */

    public void printCrossroads() {
        System.out.println(crossroads);
    }

    /**
     * Vypíše na standardní výstup všechny ulice.
     */

    public void printStreets() {
        System.out.println(streets);
    }

    /**
     * Vypíše na standardní výstup všechny zastávky.
     */

    public void printStops() {
        System.out.println(stops);
    }

    /**
     * Vrací všechny ulice
     * @return list ulic
     */

    public Map<String, Street> getAllStreets(){
        return streets;
        }

    /**
     * Vrací všechny zastávky
     * @return list zastávek
     */

    public List<BusStop> getAllStops(){
        return new ArrayList<>(stops.values());
        }

}