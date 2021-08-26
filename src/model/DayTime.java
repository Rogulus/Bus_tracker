package model;

/**
 * Třída reprezentující čas ve dni
 * @author Michael Kinc
 */

public class DayTime implements Comparable{
    private int timeInSeconds=0;

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
    private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

    /**
     * Kontruktor třídy DayTime
     */

    public DayTime(){
        this.timeInSeconds=0;
    }

    /**
     * Konstruktor třídy DayTime
     * @param time čas ve dni
     */

    public DayTime(DayTime time) {
            this.timeInSeconds = time.getTime();
    }

    /**
     * Konstruktor třídy DayTime
     * @param timeInSeconds čas v sekundách
     */

    public DayTime(int timeInSeconds){
            this.timeInSeconds = timeInSeconds % SECONDS_IN_DAY;
    }

    /**
     * Nataví čas
     * @param time čas ve dni
     */

    public void setTime(DayTime time){
        this.timeInSeconds = time.getTime();
    }

    /**
     * Nastaví čas
     * @param timeInSeconds čas v sekundách
     * @throws IllegalArgumentException v případě chybně zadaného argumentu timeInSeconds
     */

    public void setTime(int timeInSeconds) throws IllegalArgumentException{
        if(isInSecInterval(timeInSeconds)) {
            this.timeInSeconds = timeInSeconds;
        }
        else {
            throw new IllegalArgumentException("Seconds out of range");
        }
    }

    /**
     * Vrátí čas v sekundách.
     * @return čas v sekundách
     */

    public int getTime(){
        return this.timeInSeconds;
    }

    /**
     * Inkrementuje čas
     */

    public void incrementTime(){
        this.timeInSeconds = ((this.timeInSeconds + 1) % SECONDS_IN_DAY);
    }

    /**
     * Převede hpdiny, minuty a sekundy na sekundy.
     * @param hours hodiny
     * @param minutes minuty
     * @param seconds sekundy
     * @return vrací počet sekund
     * @throws IllegalArgumentException v případě, že je některý z parametr funkce nesprávně zadaný
     */

    public static int toSec(int hours, int minutes, int seconds) throws IllegalArgumentException {
        int secondTime;
        if (0 > hours || hours >= 24 ||
                0 > minutes || minutes >= 60 ||
                0 > seconds || seconds >= 60) {
            System.out.println(String.valueOf(hours) + String.valueOf(minutes) + String.valueOf(seconds));
            throw new IllegalArgumentException();
        }
        secondTime = hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE + seconds;

        return secondTime;
    }

    /**
     * Metoda, která kontroluje, zda jsou sekundy ve správném intervalu.
     * @param sec sekundy
     * @return True v případě úspěchu operace, False v případě neúspěchu.
     */

    private boolean isInSecInterval(int sec){
        if (0 <= sec && sec < SECONDS_IN_DAY){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Přetížená metoda compareTo
     * @param o porovnávaný objekt
     * @return Vrací kladnou hodnotu v případě, že první objekt je lexikograficky větší, a zápornou hodnotu, pokud je to naopak.
     */

    public int compareTo(Object o){
        if(o == null){
            throw new NullPointerException();
        }
        else if(o instanceof DayTime) {
            DayTime t = (DayTime) o;
            if(this.timeInSeconds < t.getTime()){
                return -1;
            }
            else if(this.timeInSeconds == t.getTime())
                return 0;
            else{
                return 1;
            }
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Přetížená metoda toString
     * @return vrací řetězec (hodiny, minuty, sekundy)
     */

    public String toString(){
        return "" + this.timeInSeconds/3600 + ":" +timeInSeconds%3600/60 + ":" + timeInSeconds%60;
    }
}