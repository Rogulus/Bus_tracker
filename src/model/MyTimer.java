package model;

import java.lang.*;
import java.util.Timer;
import model.Interfaces.*;



/**
 * Třída která implementuje práci s modelovým časem. Každý objekt třídy implementující rozhraní Executable může být
 * zavolán v konkrétní čas nebo každou sekundu modelového času. Třída využívá návrhový vzor Singleton.
 *
 * @author Marek Šťastný
 */
//During runnig of this class every second run method of the Starter class is executed.
public class MyTimer implements Executable{
    private static MyTimer instance = null;
    private DayTime innerTime;
    private Timer timer;
    DayTime lastTime;
    boolean isRunning = false;
    int lastSecDurance = 1000;
    int actSecDurance = 1000;
    StarterData starterData = new StarterData(this);
    Starter starter = new Starter(starterData);
    Listener listener;

    /**
     * Vytváří jedinou instanci Třídy MyTimer.
     */
    private MyTimer(){
        innerTime = new DayTime();
        lastTime = new DayTime(0);
        starter.registerEverySecondEvent(this);
    }

    /**
     * Vrací jedinou instanci třídy MyTimer
     * @return Jedniná instance třídy MyTimer
     */
    public static MyTimer getInstance(){
        if(instance == null){
            instance = new MyTimer();
        }
        return instance;
    }

    /**
     * Vrátí aktuální čas.
     * @return aktuální čas.
     */
    public int getTime(){
        return this.innerTime.getTime();
    }

    /**
     * Vrátí aktuální čas
     * @return aktuální čas.
     */
    public DayTime getDayTime(){
        return this.innerTime;
    }

    /**
     * Vrátí aktuální čas
     * @param time čas na který má být timer nastaven
     */
    public void setTime(DayTime time){
        this.innerTime.setTime(time.getTime());
        starterData.clear();
        starter.registerEverySecondEvent(this);
    }

    /**
     * Inkrementuje čas o sekundu.
     */
    void incrementTime(){
        this.innerTime.incrementTime();
    }

    /**
     * Spusí čas, pokud už čas běží, neudělá nic.
     */
    public void run() {
        if(! isRunning) {
            if(actSecDurance < 1){
                actSecDurance = lastSecDurance;
            }
            starter = new Starter(starterData);
            timer = new Timer();
            timer.schedule(starter, actSecDurance, actSecDurance);
            isRunning = true;
        }
    }

    /**
     * Mění dobu trvaní modelové sekundy.
     * @param secondDurance Počet milisekund v jedné sekundě modelového času.
     */
    public void modifySecDurance(int secondDurance){
        if(secondDurance < 10){
            secondDurance = 10;
        }
        if(isRunning) {
            this.stop();
            actSecDurance = secondDurance;
            this.run();
        }
    }

    /**
     * Zastaví modelový čas. Pokud je čas už zastaven, neudělá nic.
     */
    public void stop(){
        if(isRunning) {
            lastTime.setTime(innerTime);
            timer.cancel();
            starter.cancel();
            lastSecDurance = actSecDurance;
            actSecDurance = 0;
            isRunning = false;
        }
    }

    /**
     * Vrátí kolik milisekund reálného času trvá modelová sekunda.
     * @return Počet milisekund v jedné sekudně modelového času.
     */
    public int getSecDurance(){
       return actSecDurance;
    }

    /**
     * Zaregistruje objekt ke spuštění každou sekudnu modelového času.
     * @param event Objekt k zaregistrování.
     */
    public void registerEverySecondEvent(Executable event){
        starter.registerEverySecondEvent(event);
    }

    /**
     * Odregistruje objekt ke spuštění každou sekudnu modelového času.
     * @param event Objekt k odregistrování.
     */
    public void unregisterEverySecondEvent(Executable event){
        starter.unregisterEverySecondEvent(event);
    }

    /**
     * Zaregistruje objekt ke spuštění v daný čas.
     * @param event Objekt k Zaregistrování.
     * @param time Čas, kdy má být objekt spuštěn.
     */
    public void registerTimeEvent(Executable event, DayTime time){
        starter.registerTimeEvent(event,time);
    }

    /**
     * Zaregistruje právě jeden objekt k oznamování o změně času.
     * @param listener Objekt bude volán jako první při změně času.
     */
    public void setListener(Listener listener){
        this.listener = listener;
    }

    /**
     * Volá listener.
     */
    public void execute(){
        listener.call();
    }
}

