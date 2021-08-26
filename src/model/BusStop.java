package model;


/**
 * Třída reprezentující jednu autobusovou zastávku.
 * @author Michael Kinc
 */

public class BusStop extends PointEntity{
String name;
public int stopDuration = 15;


    /**
     * Kontsruktor sloužící k vytvoření autobusové zastávky.
     * @param name název zastávky
     * @param x x-ová souřadnice zastávky
     * @param y y-ová souřqdnice zastávky
     */

    public BusStop(String name, int x, int y){
        super(x, y);
        this.name = name;
    }

    /**
     * Kontsruktor sloužící k vytvoření autobusové zastávky.
     * @param name jméno zastávky
     * @param streetName název ulice, na které leží
     * @param percent procentuální vzdálenost zastávky od koncového nebo počátečního bodu ulice
     * @param parentMap mapa
     */

    public BusStop(String name, String streetName, int percent, MyMap parentMap){
        super(BusStop.countX(parentMap.getStreet(streetName).getStart(), parentMap.getStreet(streetName).getEnd(), percent),
                BusStop.countY(parentMap.getStreet(streetName).getStart(), parentMap.getStreet(streetName).getEnd(), percent)
        );
        this.name = name;
        parentMap.getStreet(streetName).addStop(this);
        parentMap.addStop(this);
    }

    /**
     * Vrací název zastávky.
     * @return název zastávky
     */

    public String getName(){
        return this.name;
    }

    /**
     * Přetížená metoda toString
     * @return vrací řetězec s názvem zástávky a se souřadnicemi x a y
     */

    public String toString(){
        return name +" x:"+ this.getX()+" y:" + this.getY();
    }
}
