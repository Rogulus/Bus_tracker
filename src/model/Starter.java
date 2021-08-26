package model;

import model.Interfaces.Executable;

import java.lang.*;
import java.util.*;

/**
 * Třía která je spouštěna časovačem a obstarává volání zaregistrovaných objektů.
 *
 * @author Marek Šťastný
 */
public class Starter extends TimerTask {

     StarterData data;

    public Starter(StarterData data){
        this.data = data;
    }

    /**
     *Metoda volaná každou vteřinu modelového času. Spouští zaregistrované objekty.
     */
    @Override
    public void run() {
        data.executablesOnEvery.removeAll(data.executablesToUnregister);
        data.executablesToUnregister.clear();

        data.executablesOnEvery.addAll(data.executablesToRegister);
        data.executablesToRegister.clear();
        for(Executable e: data.executablesOnEvery){
            e.execute();
        }
        List<Executable> thisSecEvents = data.executablesAtTime.get(data.timer.getDayTime());
        if(thisSecEvents != null) {
            for (Executable event : thisSecEvents) {
                if (event != null) {
                    event.execute();
                }
            }
        }
        data.timer.incrementTime();
    }

    /**
     * Zaregistruje objekt ke spuštění každou sekudnu modelového času.
     * @param executableToExecute Objekt k zaregistrování.
     */
    public void registerEverySecondEvent(Executable executableToExecute){
        data.executablesToRegister.add(executableToExecute);
    }

    /**
     * Odregistruje objekt ke spuštění každou sekudnu modelového času.
     * @param executableTounregister Objekt k odregistrování.
     */
    public void unregisterEverySecondEvent(Executable executableTounregister){
        data.executablesToUnregister.add(executableTounregister);
    }

    /**
     * Zaregistruje objekt ke spuštění v daný čas.
     * @param executable Objekt k Zaregistrování.
     * @param time Čas, kdy má být objekt spuštěn.
     */
    public void registerTimeEvent(Executable executable, DayTime time){
        List<Executable> listAtTime =data.executablesAtTime.get(time);
        if(listAtTime != null){
            listAtTime.add(executable);
        }
        else{
            listAtTime = new ArrayList<>();
            listAtTime.add(executable);
            data.executablesAtTime.put(new DayTime(time), listAtTime);
        }
    }
}
