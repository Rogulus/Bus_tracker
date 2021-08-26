package model;

import model.Interfaces.Listener;

import java.util.List;
import java.util.Map;

/**
 * Třída, která zapouzdřuje celý model aplikace MMBT.
 * @author Marek Šťastný
 */


public class MMBTModel implements Listener{
    MyMap map;
    DPMB dpmb;
    MyTimer mainTimer;
    Listener listener;

    /**
     * Konstruktor třídy MMBTModel
     */

    public MMBTModel(){
        map = MyMap.getInstance();
        dpmb = new DPMB(map);
        map.loadMap("data/mapData.json");
        dpmb.loadData("data/lines.json");
        mainTimer = MyTimer.getInstance();
        mainTimer.setListener(this);
        dpmb.setLines();

    }

    /**
     * Vrací čas v řetězci.
     * @return řětězec času
     */

    public String getStringTime(){
        return mainTimer.getDayTime().toString();
    }

    /**
     * Vratí zpoždění spoje dané linky
     * @param line linka
     * @param connection spoj
     * @return vrátí zpoždění
     */

    public int getDelayOfConnection(Line line, Connection connection) {
        return dpmb.getDelayOfLine(line, connection);
    }

    /**
     * Vrátí čas
     * @return čas
     */

    public int getTime(){
        return mainTimer.getTime();
    }

    /**
     * Nastaví čas časovači
     * @param time čas
     */

    public void setTime(int time){
        mainTimer.setTime(new DayTime(time));
        dpmb.setLines();
    }

    /**
     * Vraátí šířku mapy
     * @return šířka mapy
     */

    public int getMapWidth(){
        return map.getWidth();
    }

    /**
     * Vrátí výšku mapy
     * @return výška mapy
     */

    public int getMapHight(){
       return map.getHight();
    }

    /**
     * Spustí časovač
     */

    public void run(){
        mainTimer.run();
    }

    /**
     * Zastaví časovač
     */

    public void stop(){
        mainTimer.stop();
    }

    /**
     * Vrací sekundu modelového času.
     * @return sekunda modelového času
     */

    public int getSecondDurance(){
        return mainTimer.getSecDurance();
    }

    /**
     * Upraví sekundu modelového času
     * @param secDurance sekunda
     */

    public void modifySecondDurance(int secDurance){
        mainTimer.modifySecDurance(secDurance);
    }

    /**
     * Nastaví listener. Po zaregistrování dostává listener upozornění o změně modelového času
     * @param listener listener
     */

    public void setListener(Listener listener){
        this.listener = listener;
    }

    /**
     * Vrátí všechny ulice
     * @return list ulic
     */

    public Map<String, Street> getAllStreets(){
        return map.getAllStreets();
    }

    /**
     * Vrátí všechny autobusy
     * @return list autobusů
     */

    public List<Bus> getAllBuses(){
        return dpmb.getAllBuses();
    }

    /**
     * Vrátí všechny linky
     * @return list linek
     */

    public List<Line> getAllLines(){
        return dpmb.getAllLines();
    }

    /**
     * Vrátí všechny zastávky
     * @return list zastávek
     */

    public List<BusStop> getAllStops(){
        return map.getAllStops();
    }

    /**
     * Zavolá listener
     */

    public void call(){
        if( listener != null){
            listener.call();
        }
    }
}
