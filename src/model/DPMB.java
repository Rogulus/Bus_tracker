package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída zapouzdřující všechny linky autobusy a mapu.
 * @author Michael Kinc
 */

public class DPMB {
    List<Line> lines;
    List<Bus> buses;
    MyMap map;

    /**
     * Kontruktor sloužící pro vytvoření objektu třídy DPMB.
     * @param map mapa
     */

    public DPMB(MyMap map){
        lines = new ArrayList<Line>();
        buses = new ArrayList<Bus>();
        this.map = map;
    }

    /**
     * Vrátí všechny autobusy
     * @return vrací list autobusů
     */

    public List<Bus> getAllBuses(){
        return buses;
    }

    /**
     * Vrací všechny linky.
     * @return list linek
     */


    public List<Line> getAllLines(){
        return lines;
    }

    /**
     * Načítání dat ze souboru.
     * @param sourcePath cesta k souboru
     */

    public void loadData(String sourcePath){
        LinesLoader linesLoader = new LinesLoader();
        linesLoader.loadLines(sourcePath, map, this);
    }

    /**
     * Přidá linku
     * @param line linka
     */

    public void addLine(Line line){
        this.lines.add(line);
    }

    /**
     * Vratí zpoždění spoje na lince
     * @param line linka
     * @param connection spoj
     * @return vrací zpoždění. V případě neúspěchu vrací -1.
     */

    public int getDelayOfLine(Line line, Connection connection) {
        for (Line l : this.lines) {
            if (l == line) {
                return line.getConnectionDelay(connection);
            }
        }
        return -1;
    }

    /**
     * Přídá nový bus
     * @param bus autobus
     */

    public void registerBus(Bus bus){
        buses.add(bus);
    }

    /**
     * Smaže bus
     * @param bus autobus
     */

    public void unregisterBus(Bus bus){
        buses.remove(bus);
    }

    /**
     * Nastaví linku
     */

    public void setLines(){
        buses.clear();
        for(Line line: lines){
            line.setUpLine();
        }
    }
}
