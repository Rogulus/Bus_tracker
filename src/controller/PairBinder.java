package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Třída která vytváří relacie, mezi dvěma objekty různých tříd.
 * @author Marek Šťastný
 */
public class PairBinder  <T1 , T2> implements Iterable<T1>  {

    private T1 firstType;
    private T2 secondType;

    private List<T1> firstList;
    private List<T2> secondList;

    public PairBinder() {
        firstList = new ArrayList<>();
        secondList = new ArrayList<>();
    }

    public Iterator<T1> iterator(){
        return firstList.listIterator();
    }

    /**
     * Sváže dva objěkty k sobě
     */
    void bindTogether(T1 first, T2 second){
        firstList.add(first);
        secondList.add(second);
    }

    /**
     * Na základě druhého prvku relace vrací první.
     */
    public T1 getFirst(T2 second){
        return firstList.get(secondList.indexOf(second));
    }

    /**
     * Vrací seznam prvních prvků relací.
     */
    public List<T1> getFirstList(){
        return new ArrayList<>(firstList);
    }

    /**
     * Na základě prvního prvku relace vrací druhý.
     */
    public T2 getSecond(T1 first){
        int i = firstList.indexOf(first);
        if(i != -1) {
            return secondList.get(i);
        }
        else{
            return null;
        }
    }

    /**
     * Vrací seznam druhých prvků relací.
     */
    public List<T2> getSecondList(){
        return new ArrayList<>(secondList);
    }

    /**
     * Vrací true pokud je hledaný prvek uložen v relaci
     */
    public boolean firstContains(T1 first){
        return firstList.contains(first);
    }

    /**
     * Vrací true pokud je hledaný prvek uložen v relaci
     */
    public boolean secondContains(T2 second){
        return secondList.contains(second);
    }

    /**
     * Odstraní oba prvky z relace pomocí prvního
     */
    public void removeBothViaFirst(T1 first){
        int i = firstList.indexOf(first);
        firstList.remove(i);
        secondList.remove(i);
    }

    /**
     * Odstraní oba prvky z relace pomocí druhého
     */
    public void removeBothViaSecond(T2 second){
        int i = secondList.indexOf(second);
        firstList.remove(i);
        secondList.remove(i);
    }
}