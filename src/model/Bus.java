package model;

import model.Interfaces.DynamicPoint2D;
import model.Interfaces.Executable;
import model.Interfaces.Point2D;

import java.lang.*;
import java.lang.Math;

/**
 * Třída reprezentující autobus obsluhující právě jeden spoj Linky hromadné dopravy. Autobus vždy vykonává pohyb po
 * přímce mezi dvěma body a poté te třeba jej přenastavit na pohyb mezi dalšími body.
 *
 * @author Marek Šťastný
 */
public class Bus implements Point2D, DynamicPoint2D, Executable, Comparable<Object> {
    private double x = 0.0;
    private double y = 0.0;

    private Connection connection;
    private MyTimer timer;
    private Point2D start;
    private Point2D end;
    private double percentDone;
    private double distanceToGo;
    private double distanceDone;
    private double velocity;
    private double backupVelocity;
    private double maxVelocity = 90;
    private int delay = 0;

    /**
     * Kontsruktor sloužící k vytvoření autobusové zastávky.
     * @param connection Instance spoje, která je obsluhována autobusem.
     * @param timer Zdroj času celé simulace.
     */
    public Bus(Connection connection, MyTimer timer){
        this.connection = connection;
        this.timer = timer;
    }

    /**
     * Změní rychlost pohybu autobusu.
     * @param velocity Udává, kolik jednotek má urazit autobus za jednotku času
     */
    public void changeVelocity(double velocity){
        if(velocity < maxVelocity) {
            this.velocity = velocity;
        }
        else{
            this.velocity = maxVelocity;
        }
    }

    /**
     * Vrací honotu souřadnice x autobusu v daném okamžiku.
     * @return Souřadnice x.
     */
    public int getX(){
        return (int)this.x;
    }

    /**
     * Vrací honotu souřadnice y autobusu v daném okamžiku.
     * @return Souřadnice y.
     */
    public int getY() {
        return (int)this.y;
    }

    /**
     * Změní pro autobus honotu souřadnice x.
     * @return Nová souřadnice x.
     */
    public void changeX(int x){
        this.x = x;
    }

    /**
     * Změní pro autobus honotu souřadnice y.
     * @return Nová souřadnice y.
     */
    public void changeY(int y){
        this.y = y;
    }

    /**
     * Změní polohu autobusu.
     * @param x Nová souřadnice.
     * @param y Nová souřaddnice.
     */
    public void move(int x, int y){
        changeX(x);
        changeY(y);
    }

    /**
     * Vrací vzdálenost autobusu od požadovaného objektu.
     * @return Souřadnice x.
     */
    public int distance(Point2D point){
        return (int)Math.sqrt(Math.pow(point.getX()-this.x, 2) + Math.pow(point.getY()-this.y,2));
    }

    /**
     * Provádí přepočet polohy autobusu, každou sekundu modelového času. V případě, že autobus dojede na konec úseku volá metodu redirectBus spoje
     */
    public void execute(){
        if(velocity!= 0) {
            this.distanceDone += velocity/10;

            double percentDone;
            if (distanceToGo != 0) {
                percentDone = (100 * distanceDone) / distanceToGo;
            } else {
                percentDone = 100;
            }
            if (percentDone < 100) {

                x = (start.getX() + ((end.getX() - start.getX()) * (percentDone)) / 100);
                y = (start.getY() + ((end.getY() - start.getY()) * (percentDone)) / 100);
            } else {
                x = end.getX();
                y = end.getY();
                timer.unregisterEverySecondEvent(this);
                connection.redirectBus();
            }

           // System.out.println("distance done:" + String.valueOf(this.distanceDone) + " distance to go:" + String.valueOf(this.distanceToGo) + " bus at position:" + String.valueOf(this.getX()) + " " + String.valueOf(this.getY()));

        }
    }

    /**
     * Umístí autobus na správnou pozici daného úseku vzhledem k aktuálnímu času.
     * @param routeElement Úsek spoje na který má být pro aktuální čas umístěn autobus.
     */
    public void placeBus(RouteElement routeElement){
        this.start = routeElement.getStart();
        this.end = routeElement.getEnd();
        this.distanceToGo = start.distance(end);
        this.distanceDone = 0;
        this.velocity = routeElement.getMaxSpeed();
        timer.registerEverySecondEvent(this);

            DayTime globalStartTime = new DayTime(connection.getStartTime().getTime() + routeElement.getDelaySinceStart());
            DayTime globaEndTime = new DayTime(globalStartTime.getTime() + routeElement.getTransitDuration());


        int onTheRouteElementTime;

        if(globalStartTime.getTime() < globaEndTime.getTime()) {
            onTheRouteElementTime = timer.getTime() - globalStartTime.getTime();
        }
        else{
                DayTime midnight = new DayTime();
                onTheRouteElementTime = ((86400 - globalStartTime.getTime()) + globaEndTime.getTime());
            }

               // System.out.println(onTheRouteElementTime);
            if(distanceToGo != 0) {
                    percentDone = (int) ((velocity * (onTheRouteElementTime) * 10) / distanceToGo);
                }
            else{
                percentDone = 100;
            }

            if(percentDone >= 100 || distanceToGo == 0){
                x = end.getX();
                y = end.getY();
                timer.unregisterEverySecondEvent(this);
                connection.redirectBus();
                return;
            }
            else if (globalStartTime.getTime() <= timer.getTime() && timer.getTime() <= globalStartTime.getTime() + routeElement.getTransitDuration() ) {
                x = (start.getX() + ((end.getX() - start.getX()) * (percentDone)) / 100);
                y = (start.getY() + ((end.getY() - start.getY()) * (percentDone)) / 100);
                distanceDone = (percentDone * distanceToGo) /100;

            }
            //System.out.println("percent done:" + String.valueOf(this.percentDone) + " distance to go:" + String.valueOf(this.distanceToGo) + " bus at position:" + String.valueOf(this.getX()) + " " + String.valueOf(this.getY()));
    }

    /**
     * Spustí jízdu autobusu pro danou úsečku.
     * @param start Počáteční souřadnice pohybu.
     * @param end Koncová souřadnice pohybu.
     * @param velocity Udává, o kolik jednotek se autobus posune za jednotku času.
     */
    public void go(Point2D start, Point2D end, double velocity){
        this.start = start;
        this.end = end;
        this.distanceToGo = start.distance(end);
        this.distanceDone = 0;
        this.velocity = velocity;
        move(start.getX(),start.getY());
        timer.registerEverySecondEvent(this);
    }

    /**
     * Třída umožnující spuštění metody endStay třídy Bus pomocí Timeru.
     */
    class Executer implements Executable {
        public Object toExec;
        public Executer(Object toExec){
            this.toExec = toExec;
        }
        public void execute(){
            ((Bus)toExec).endStay();
        }
    }

    /**
     * Pozastaví pohyb atobusu na danou doubu.
     * @param secDurance Doba po kterou má autobus stát.
     */
    public void stayAtPosition(int secDurance){
        backupVelocity = velocity;
        velocity = 0;
        Executer executer = new Executer(this);
        timer.registerTimeEvent( executer,new DayTime(timer.getTime()+secDurance));
    }

    /**
     * Opětovně přivede autobus k pohybu.
     */
    private void endStay(){
        velocity = backupVelocity;
        connection.redirectBus();
    }

    /**
     * Vrací Spoj kterému náleží.
     * @return Spoj, kterému autobus náleží.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Realizace rozhraní Comparable
     */
    public int compareTo(Object b){
        return this.toString().compareTo(b.toString());
    }
}