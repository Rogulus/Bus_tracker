package controller;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;

import model.Crossroad;
import model.MMBTModel;
import model.RouteElement;
import model.Street;

/**
 * Třída uchovávající informace o autobusové lince.
 * @author Marek Šťastný
 */
public class BusLine {
    String lineColor;
    MMBTModel model;
    PaneMap map;
    model.Line meBusLine;
    private Circle startPoint;
    private Circle endPoint;
    private Crossroad lastCrossroad;
    private Street lastStreet;
    private List<Street> addedStreets = new ArrayList<>();
    private Crossroad endAlternativeRoadPoint;
    private PairBinder<model.Street, LineController> lineControllers;

    /**
     * Třída reptezentující autobusovou zastávku
     * @param busLine Linka z modelu, které odpovídá tato linka.
     * @param model modelovaný model.
     * @param map mapa na kterou se vykresluje linka.
     * @param lineControllers svázané modelové ulice s odpovídajícími přímkami na mapě.
     * @param lineColor Barva Linky.
     */
    public BusLine(model.Line busLine, MMBTModel model, PaneMap map, PairBinder<Street, LineController> lineControllers , String lineColor){
        meBusLine = busLine;
        this.lineColor = lineColor;
        this.model = model;
        this.map = map;
        this.lineControllers = lineControllers;
    }

    /**
     * Nastaví mapu do módu nastavování alternativní trasy.
     * Mód skončí po úspěšném nastavení trasy
     */
    public void setAlternativeRoute(){
        addedStreets.clear();
        for(model.Street street : lineControllers){
            lineControllers.getSecond(street).setSetAlternativeRouteMode(this);
        }

        RouteElement start = meBusLine.getFirstRouteElement();
        RouteElement end = meBusLine.getLastRouteElement();
        endAlternativeRoadPoint = (Crossroad) end.getEnd();
        startPoint= new Circle(start.getStart().getX(), start.getStart().getY(), 10, Color.web(meBusLine.getColor()));
        endPoint= new Circle(end.getEnd().getX(), end.getEnd().getY(), 10, Color.web(meBusLine.getColor()));
        map.addCircle(startPoint);
        map.addCircle(endPoint);

        setPossibleToAdd((Crossroad)start.getStart());
    }

    /**
     * přidání ulice do pokračování alternativní trasy
     * @param addedStreet ulice, o kterou se má prodloužit alternativní trasa
     */
    public void addedToAlternativeRoute(Street addedStreet){
        meBusLine.addStreetToAlternativeRoute(addedStreet);
        addedStreets.add(addedStreet);

        addedStreets.forEach(e -> lineControllers.getSecond(e).setInLineMode(meBusLine.getColor()));

        for(Street street : lastCrossroad.getAdjacentStreets()){
            if(street != lastStreet ) {
                   lineControllers.getSecond(street).setSetAlternativeRouteMode(this);
            }
        }

        Crossroad end = addedStreet.getEnd();
        if(end == lastCrossroad){
            end = addedStreet.getStart();
        }
        if(end == endAlternativeRoadPoint){
            for(Street street : lineControllers){
                lineControllers.getSecond(street).setViewMode();
            }
            map.removeCircle(startPoint);
            map.removeCircle(endPoint);
            meBusLine.setUpAlternativeLine();

        }
        else {
            setPossibleToAdd(end);
        }
        lastStreet = addedStreet;
    }

    /**
     * Zobrazí ulice, které je možné přidat do trasy
     * @param crossroad křižovatka, od které zobrazí přiléhající ulice jako možné k přidání do trasy.
     */
    private void setPossibleToAdd(Crossroad crossroad){
        lastCrossroad = crossroad;
        for(Street street : crossroad.getAdjacentStreets()){
            lineControllers.getSecond(street).setPossibleToAddToRoute();
        }
    }
}
