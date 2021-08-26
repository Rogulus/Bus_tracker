package model;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Třída reprezentující jednu křižovatku, tedy počáteční či koncový bod ulice
 * @author Michael Kinc
 */

public class Crossroad extends PointEntity{
    private String name;
    private MyMap parentMap;
    private List<Street> adjacentStreets;

    /**
     * Konstruktor sloužící k vytvoření křižovatky.
     * @param name název bodu (křižovatky)
     * @param x hodnota na ose x
     * @param y hodnota na ose y
     * @param parentMap mapa
     */

    public Crossroad(String name, int x, int y, MyMap parentMap){
        super(x, y);
        this.name = name;
        this.adjacentStreets = new ArrayList<>();
        this.parentMap = parentMap;
        this.parentMap.addCrossroad(this);
    }

    /**
     * Vrací jméno křižovatky.
     * @return Jméno křižovatky.
     */

    public String getName(){
        return name;
    }

    /**
     * Přidá ulici do listu přilehlých ulic.
     * @param neighborStreet přidávaná ulice
     */

    public void addStreet(Street neighborStreet){
        this.adjacentStreets.add(neighborStreet);
    }

    /**
     * Vrací list přilehlých ulice.
     * @return List přilehlých ulic.
     */

    public List<Street> getAdjacentStreets(){
        return adjacentStreets;
    }

    /**
     * Přetížená metoda toString
     * @return řetězec s názvem ulice a souřadnicemi x a y
     */

    @Override
    public String toString(){
        return (name + " x:" + x + " y:" + y);
    }

    /**
     * Přetížená metoda compareTo
     * @param o porovnávaý object
     * @return Vrací kladnou hodnotu v případě, že první objekt je lexikograficky větší, a zápornou hodnotu, pokud je to naopak.
     */

    public int compareTo(Object o){
        if(o == null){
            throw new NullPointerException();
        }
        else if(o instanceof Crossroad) {
            Crossroad c = (Crossroad) o;
            return (this.name.compareTo(c.getName()));
        }
        else{
            throw new IllegalArgumentException();
        }
    }


}