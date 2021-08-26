package controller;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.Bus;
import model.MMBTModel;
import model.PointEntity;
import model.Street;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída sloužící k ovládání mapy a plnění GUI daty
 * @author Michael Kinc
 */

public class PaneMap {
    Pane map;
    double zoom = 1;
    double width;
    double height;
    private MMBTModel model;
    private MainPageController mainPageController;


    private PairBinder<Street, Text> streetNames = new PairBinder<>();
    private PairBinder<model.Street, Line> streetLines = new PairBinder<>();
    private PairBinder<Bus, Circle> drawnBuses = new PairBinder<>();

    private PairBinder<Street, LineController> lineControllers = new PairBinder<>();
    private PairBinder<model.Line, BusLine> busLinesControllers = new PairBinder<>();
    private List<BusLine> busLines = new ArrayList<>();


    private PairBinder<Circle, PointEntity> circles = new PairBinder<>();

    /**
     * Konstruktor třídy PaneMap
     * @param map mapa
     * @param width šířka mapy
     * @param height výška mapy
     * @param model model
     * @param mainPageController controller hlavního okna aplikace
     */

    public PaneMap(Pane map, double width, double height, MMBTModel model, MainPageController mainPageController) {
        this.map = map;
        this.width = width;
        this.height = height;
        map.setMinWidth(convertX(width));
        map.setMinHeight(convertY(height));
        this.model = model;
        this.mainPageController = mainPageController;
    }

    /**
     * Vrátí linky.
     * @return linky
     */

    public List<Line> getLines(){
        return streetLines.getSecondList();
    }

    /**
     * Vrátí autobusovou linku
     * @param busLine autobusová linka
     * @return vrací linku
     */

    public BusLine getBusLine(model.Line busLine){
        return busLinesControllers.getSecond(busLine);
    }


    /**
     * Nastaví přiblížení nebo oddálení mapy.
     * @param zoom přiblížení/oddálení
     */

    public void setZoom(double zoom) {
        this.zoom = zoom;

        //resizing map
        map.setMinHeight(height * zoom);
        map.setMinWidth(width * zoom);

        reDrawBuses(drawnBuses.getFirstList());

        //Streets scaling
        for (model.Street s : streetLines) {
            Line line = streetLines.getSecond(s);
            line.setStartX(convertX(s.getStart().getX()));
            line.setStartY(convertY((s.getStart().getY())));
            line.setEndX(convertX(s.getEnd().getX()));
            line.setEndY(convertY((s.getEnd().getY())));

            //Street names moving
            Text name = streetNames.getSecond(s);
            name.setX(convertX(s.getHalfPointX()));
            name.setY(convertY(s.getHalfPointY()));
        }

        //StopsScaling
        for(Circle c: circles){
            c.setCenterX(convertX(circles.getSecond(c).getX()));
            c.setCenterY(convertY(circles.getSecond(c).getY()));
        }
    }

    /**
     * Vykreslí ulice do mapy
     * @param streets list ulice
     */

    public void drawStreets(List<Street> streets) {

        //Streets setting
        for (Street street : streets) {

            lineControllers.bindTogether(street, new LineController(this, streetLines, street, this.mainPageController));

            //Writing names
            Text nameText = new Text(street.getName());
            streetNames.bindTogether(street, nameText);
            nameText.setX(convertX(street.getHalfPointX()));
            nameText.setY(convertY(street.getHalfPointY()));
            nameText.setFill(Color.CYAN);
            this.map.getChildren().add(nameText);
        }
    }

    /**
     * Nastaví kontrolery autobusových linek
     */

    public void setBusLines(){
        for(model.Line busLine: model.getAllLines()){
            BusLine bl =new BusLine(busLine, model, this, lineControllers ,busLine.getColor());
            busLines.add(bl);
            busLinesControllers.bindTogether(busLine,bl);
        }
    }

    /**
     * Překreslí autobusy
     * @param buses list autobusů
     */

    public void reDrawBuses(List<Bus> buses) {
        Circle circle;
        for (Bus bus : buses) {
            //bus is on the map and must be moved
            if (drawnBuses.firstContains(bus)) {
                circle = drawnBuses.getSecond(bus);
                circle.setCenterX(convertX(bus.getX()));
                circle.setCenterY(convertY(bus.getY()));
            } else {
                //Bus is new and must be created new visualization
                circle = new Circle(convertX(bus.getX()), convertY(bus.getY()), 6, Color.web(bus.getConnection().getLine().getColor()));
                this.map.getChildren().add(circle);
                drawnBuses.bindTogether(bus, circle);
            }
        }

        //bus circle does not exist in the model and visualization must be removed
        ArrayList<Bus> busesToRemove = new ArrayList<>();
        for (Bus bus : drawnBuses) {
            if (!buses.contains(bus)) {
                busesToRemove.add(bus);
            }
        }

        for (Bus b : busesToRemove) {
            map.getChildren().remove(drawnBuses.getSecond(b));
            drawnBuses.removeBothViaFirst(b);
        }
    }

    /**
     * Přidá kolečko reprezentující autobus na mapu
     * @param circle kolečko
     */

    public void addCircle(Circle circle){
        circles.bindTogether(circle, new PointEntity((int)circle.getCenterX(), (int)circle.getCenterY()));
        circle.setCenterX(convertX(circle.getCenterX()));
        circle.setCenterY(convertY(circle.getCenterY()));
        addNode(circle);
    }

    /**
     * Odstraní kolečko z mapy
     * @param circle kolečko
     */

    public void removeCircle(Circle circle){
        circles.removeBothViaFirst(circle);
        removeNode(circle);
    }

    /**
     * Přídává prvek do mapy.
     * @param pane prvek
     */

    public void addNode(Node pane) {
        map.getChildren().add(pane);
    }

    /**
     * Odebere prvek z mapy.
     * @param pane prvek
     */

    public void removeNode(Node pane) {
        map.getChildren().remove(pane);
    }

    /**
     * Změní barvu ulic v mapě
     * @param streets ulice
     * @param color barva
     */

    public void changeLinesColor(List<Street> streets, Color color) {
        for (Street street : streets) {
            Line line = streetLines.getSecond(street);
            if (line != null) {
                line.setStroke(color);
            }
        }
    }

    /**
     * Provádí grafickou transformaci osy x
     * @param x hodnota na ose x
     * @return vrací transformovaný bod x
     */

    public double convertX(double x) {
        return x * zoom;
    }

    /**
     * Provádí grafickou transformaci osy y
     * @param y hodnota na ose y
     * @return vrací transformovaný bod y
     */

    public double convertY(double y) {
        return Math.abs(y * zoom - (height * zoom));
    }
}
        