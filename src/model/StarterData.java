package model;

import model.Interfaces.Executable;

import java.util.*;

/**
 * Třída, která obsahuje data časovače.
 *
 * @author Marek Šťastný
 */
public class StarterData {
    Set<Executable> executablesOnEvery;
    List<Executable> executablesToUnregister;
    List<Executable> executablesToRegister;
    SortedMap<DayTime, List<Executable>> executablesAtTime;
    MyTimer timer;

    /**
     * Vytvoří instanci Třídy StarterData.
     * @param timer časovač, který bude nad těmito daty operovat.
     */
    public StarterData(MyTimer timer){
        executablesOnEvery = new LinkedHashSet<>();//retains insertion order
        executablesToUnregister = new ArrayList<>();
        executablesToRegister = new ArrayList<>();
        executablesAtTime = new TreeMap<>();
        this.timer = timer;
    }

    /**
     * Vymaže všechny registrace k volání.
     */
    public void clear(){
        executablesOnEvery.clear();
        executablesToUnregister.clear();
        executablesToRegister.clear();
        executablesAtTime.clear();
    }
}
