package controller;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.BusStop;

/**
 * Třída reptezentující autobusovou zastávku
 * @author Marek Šťastný
 */
public class BusStopController {
    private BusStop meStop;
    private Circle stopCircle;
    private PaneMap paneMap;


    /**
     * Vytvoří reprezentaci zastávky a zobrazí ji v mapě.
     * @param stop instance zastávky z modelu.
     * @param paneMap mapa, do které má být zastávka vykreslena.
     */
    public BusStopController(BusStop stop, PaneMap paneMap ){
        meStop = stop;
        this.paneMap = paneMap;
        stopCircle = new Circle(stop.getX(), stop.getY(),
                7,Color.web("7E481C"));
        paneMap.addCircle(stopCircle);
    }
}
