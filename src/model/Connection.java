package model;

import model.Interfaces.Executable;

import java.util.List;

/**
 * Třída reptezentující spoj jedné linky
 * @author Michael Kinc
 */

public class Connection implements Executable {
    private MyTimer timer;
    private DayTime startTime;
    private DayTime end = new DayTime();
    private Line line;
    private Bus bus;
    private int sequencePosition = 0;
    private DPMB dpmb;
    private boolean isSet = false;
    private List<RouteElement> routeElementList;
    private int routeElemListSize;
    private boolean wasAtStop = false;
    private boolean normalDuranceSet = false;
    private int     delay;

    /**
     * Konstruktor spoje,
     * @param line linka
     * @param start začátek spoje
     * @param dpmb dpmb
     */

    public Connection(Line line, DayTime start,  DPMB dpmb){
        timer = MyTimer.getInstance();
        this.dpmb = dpmb;
        this.line = line;
        this.startTime = start;
    }

    /**
     * Konstruktor spoje.
     * @param startHour hodiny začátku spoje
     * @param startMinute minuty začátku spoje
     * @param line linka
     * @param dpmb dpmb
     */

    public Connection(int startHour, int startMinute,Line line, DPMB dpmb) {
        timer = MyTimer.getInstance();
        this.dpmb = dpmb;
        this.line = line;
        this.startTime = new DayTime(DayTime.toSec(startHour, startMinute, 0));
    }

    /**
     * Metoda, která vrací linku
     * @return linka
     */

    public Line getLine() {
        return line;
    }

    /**
     * Nastaví spoj a umístí autobus na správné místo na mapě.
     */

    public void setUp(){
        resetConnectionParameters();
        if(startTime.getTime() < timer.getTime() && timer.getTime() < end.getTime() ){
            placeBus();
        }
        //bus runs over the midnight
        else if((startTime.getTime() > end.getTime()) && timer.getTime() < end.getTime()){
            placeBus();
        }
        else {

            timer.registerTimeEvent(this, startTime);
        }
    }

    /**
     * Vrací zpoždění spoje.
     * @return zpoždění spoje
     */

    public int getDelay() {
        if(delay >= 0) {
            return delay;
        }
        else{
            return 0;
        }
    }

    /**
     * Vyresetuje parametry spoje
     */

    private void resetConnectionParameters(){
        isSet = false;
        sequencePosition = 0;

    }

    /**
     * Vytvoří novou instanci autobusu (spustí ho)
     * @param routeElemIndex počáteční pozice autobusu
     */

    public void startBus(int routeElemIndex) {
        routeElementList = line.getRouteElementList();
        routeElemListSize = routeElementList.size();
        RouteElement re = routeElementList.get(routeElemIndex);
        bus = new Bus(this, timer);
        dpmb.registerBus(bus);
    }

    /**
     * Spustí linku.
     */

    public void execute(){
        if(isSet) {
            redirectBus();
        }
        else{
            startBus(0);
            redirectBus();
            isSet = true;
        }
    }

    /**
     * Umístí autobus na požadovaný úsek cesty.
     */

    private void placeBus(){
        routeElementList = line.getRouteElementList();
        routeElemListSize = routeElementList.size();
        int i = 0;
        for(RouteElement re: routeElementList){
            //if bus is at this time in given route element
            DayTime reStart = new DayTime(startTime.getTime() + re.getDelaySinceStart());
            DayTime reEnd = new DayTime(startTime.getTime() + re.getDelaySinceStart() + re.getTransitDuration());
            if ((reStart.getTime() <= timer.getTime() && timer.getTime() < reEnd.getTime()) ||

                    //condition for bus over midnight
                    ((reStart.getTime() > reEnd.getTime()) && (timer.getTime() < reEnd.getTime()) )
            ) {
                //place the bus at the route element
                sequencePosition = i + 1;
                bus = new Bus( this, timer);
                dpmb.registerBus(bus);
                re.getStreet().addBus(bus);
                //System.out.println("BUS STARTS FROM PLACE BUS: " + re.getStart() + re.getEnd());
                if (re.startIsStop()) {
                    bus.move(re.getStart().getX(), re.getStart().getY());
                    bus.stayAtPosition(((BusStop) re.getStart()).stopDuration);
                    wasAtStop = true;
                    isSet = true;
                    return;
                } else {
                    bus.placeBus(re);
                    isSet = true;
                    return;
                }
            }
            i ++;
        }
    }

    /**
     * Posune autobus na další úsek cesty.
     */

    public void redirectBus(){

        if(sequencePosition != 0){
            RouteElement previousElem = routeElementList.get(sequencePosition - 1);
            previousElem.getStreet().removeBus(bus);
        }

        if(sequencePosition < routeElemListSize){
            RouteElement routeElement = routeElementList.get(sequencePosition);
            routeElement.getStreet().addBus(bus);
            //System.out.println("Redirect bus: "+ routeElement.getStart() + routeElement.getEnd() );
            if(routeElement.startIsStop()) {
                if (!wasAtStop) {
                    bus.stayAtPosition(((BusStop) routeElement.getStart()).stopDuration);
                    wasAtStop = true;
                    return;
                }
            }
            bus.go(routeElement.getStart(), routeElement.getEnd(), routeElement.getMaxSpeed());
            sequencePosition++;
            wasAtStop = false;
        }
        else{
            dpmb.unregisterBus(bus);
        }
    }

    /**
     * Nastaví koncový čas spoje.
     * @param endTime čas
     */

    public void setEndTime(DayTime endTime){
        if( ! normalDuranceSet) {
            delay = 0;
            normalDuranceSet = true;
        }
        else {
            {
                delay += endTime.getTime() - end.getTime();
            }
        }
        end.setTime(endTime.getTime());
        //System.out.println("connection:" + this + " delay: " + delay);
    }

    /**
     * Vrací počáteční čas spoje.
     * @return počáteční čas spoje.
     */

    public DayTime getStartTime(){
        return startTime;
    }

    /**
     * Vrátí počáteční čas spoje jako řetězec.
     * @return řetězec s počátečním časem.
     */

    public String getStringStartTime(){
        return startTime.toString();
    }

    /**
     * Přepsaná funkce toString
     * @return vrací řetězec se setartovním časem spoje.
     */
    public String toString(){
        return "connection: " + this.startTime ;
    }
}