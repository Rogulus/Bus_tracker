package controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.DayTime;
import model.Street;


/**
 * Třída obsluhující jednu ulici na mapě
 * @author Marek Šťastný
 */
public class LineController {
    private Line meStreetLine;
    private Street street;
    private Pane rootPane;
    private PaneMap map;
    private BusLine  actLine;
    private HBox lineBox = new HBox();
    private MainPageController mainPageController;
    private LineClickHandler lineClickHandler;
    private boolean isSetAlternativeRouteMode = false;
    private boolean isSetPossibleAddToRouteMode = false;
    private boolean isSetViewMode = true;
    private boolean isSetInLineMode = false;
    IntegerProperty streetMaxSpeed;

    @FXML
    private Label streetName;
    @FXML
    private Label maxSpeed;
    @FXML
    private Slider speedSetter;
    @FXML
    private ListView<model.Line> lines;
    @FXML
    private VBox vbox;

    private VBox setAlternativeRouteVBox;


    /**
     * Vytvoří reprezentaci ulice a zobrazí ji v mapě, včetně informačního panelu ulice.
     * @param map mapa, do které má být ulice vykreslena.
     * @param streetLines svázané modelové ulice s odpovídajícími přímkami na mapě.
     * @param street modelová ulice, která má být reprezentována.
     * @param mainPageController kontroller pod který tento kontroller spadá.
     */
    public LineController(PaneMap map,PairBinder<model.Street, Line> streetLines, Street street, MainPageController mainPageController)  {
        this.street = street;
        this.map = map;
        this.mainPageController = mainPageController;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/streetView.fxml"));
            fxmlLoader.setController(this);
                rootPane = (Pane) fxmlLoader.load();
        }
        catch (java.io.IOException e){
        }

        if(rootPane != null){

        }

        rootPane.setStyle("-fx-background-color: #ffe140");

        //setting street info panel
        streetName.setText(street.getName());
        streetMaxSpeed = new SimpleIntegerProperty((int) Math.round(street.getMaxSpeed()));
        maxSpeed.setText(String.valueOf(streetMaxSpeed.getValue()));

        setAlternativeRouteVBox = new VBox();
        vbox.getChildren().add(setAlternativeRouteVBox);
        setBusLineButtons();

        //creating street line
        meStreetLine = new Line(map.convertX(street.getStart().getX()), map.convertY(street.getStart().getY()), map.convertX(street.getEnd().getX()), map.convertY(street.getEnd().getY()));
        streetLines.bindTogether(street, meStreetLine);


        this.lineClickHandler = new LineClickHandler();
        meStreetLine.setOnMouseClicked(lineClickHandler);

        meStreetLine.setStrokeWidth(6);

        meStreetLine.setOnMouseEntered(new LineMouseEnteredHandler());
        meStreetLine.setOnMouseExited(new LineMouseExitedHandler() );

        setViewMode();
        this.map.addNode(meStreetLine);
    }

    /**
     * Nastaví v informačním panelu ulice tlačítka 'setup alternative route' pro každou linku projíždějící danou ulicí .
     */
    public void setBusLineButtons( ) {
        setAlternativeRouteVBox.getChildren().clear();
        for(model.Line line : street.getLines()) {
            Label lineName = new Label(line.getName());
            Button setAlternativeRoute = new Button("set alternative route");
            setAlternativeRoute.setOnAction(e -> {map.getBusLine(line).setAlternativeRoute();});

            setAlternativeRouteVBox.getChildren().addAll(lineName, setAlternativeRoute);
        }
    }

    /**
     * Zavře informační panel
     */
    public void close(){
        map.removeNode(rootPane);
    }

    /**
     * Nastaví novou barvu ulice
     * @param color Hexadecimální string nové barvy ulice např. "AABB15".
     */
    public void setColor(String color){
        meStreetLine.setStroke(Color.web(color));
    }

    /**
     * Reakce na změnu posuvníku v informačním panelu ulice. Změní rychlost v ulici a aktualizuje informační panel.
     */
    public void onSpeedShift(){
        street.setMaxSpeed(speedSetter.getValue());
        streetMaxSpeed.set((int) Math.round(street.getMaxSpeed()));
        maxSpeed.setText(String.valueOf(streetMaxSpeed.getValue()));
        refreshTreeView();
    }

    /**
     * Přepíše hodnoty zpoždění na hlavní stránce v TreeView.
     */
    public void refreshTreeView() {
        TreeItem<String> dl;
        for (model.Line l : this.mainPageController.delayBinder.getFirstList()) {
            dl = this.mainPageController.delayBinder.getSecond(l);
            model.Connection connection = l.getConnections().get(0);
            dl.setValue("Delay: " + new DayTime(this.mainPageController.model.getDelayOfConnection(l, connection)).toString());
        }
    }

    /**
     * Nastavení zobrazení ulice na prohlížecí mód. Na ulici lze kliknout a zobrazí informační panel.
     * Po najetí zobrazí barvu linky, která přes ní vede.
     */
    public void setViewMode() {
        isSetViewMode = true;
        isSetAlternativeRouteMode = false;
        isSetPossibleAddToRouteMode = false;
        isSetInLineMode = false;
        meStreetLine.setStroke(Color.web("585858"));

    }

    /**
     * Nastavení zobrazení ulice na mód nastavování nové trasy. Ulice má žlutou barvu, po najetí se nic nestane.
     */
    public void setSetAlternativeRouteMode(BusLine busLine){
        actLine = busLine;
        isSetAlternativeRouteMode = true;
        isSetPossibleAddToRouteMode = false;
        isSetViewMode = false;
        isSetInLineMode = false;
        meStreetLine.setStroke(Color.web("FFFF00"));
    }

    /**
     * Nastavení zobrazení ulice na mód že je možno tuto ulici přidat do trasy. Ulice má tyrkysovou barvu a po kliknuí se přidá do alternativní trasy
     */
    public void setPossibleToAddToRoute(){
        isSetPossibleAddToRouteMode = true;
        isSetAlternativeRouteMode = false;
        isSetViewMode = false;
        isSetInLineMode = false;
        meStreetLine.setStroke(Color.web("00FFFF"));
    }

    /**
     * Nastavení zobrazení ulice na mód ulice je přidána do alternativní trasy. Ulice má barvu linky do které byla přidána. Po kliknutí se nic nestane.
     * @param color barva linky do které byla přidána.
     */
    public void setInLineMode(String color){
        isSetInLineMode= true;
        isSetAlternativeRouteMode = false;
        isSetPossibleAddToRouteMode = false;
        isSetViewMode = false;
        meStreetLine.setStroke(Color.web(color));
    }


    private class LineClickHandler implements EventHandler {
        @Override
        public void handle(Event event) {
                if(isSetViewMode){
                    rootPane.setLayoutX(map.convertX(street.getHalfPointX()));
                    rootPane.setLayoutY(map.convertY(street.getHalfPointY()));
                    setBusLineButtons();
                    map.addNode(rootPane);
                }
                else if(isSetAlternativeRouteMode){


                }
                else if(isSetInLineMode){


                }
                else if(isSetPossibleAddToRouteMode){
                    actLine.addedToAlternativeRoute(street);
                }
        }
    }


    public class LineMouseEnteredHandler implements EventHandler {
        @Override
        public void handle(Event event) {
                if(isSetViewMode){
                    meStreetLine.setStroke(Color.web("B8B8B8"));

                        for (model.Line busLine : street.getLines()) {
                            map.changeLinesColor(busLine.getStreets(), Color.web(busLine.getColor()));
                        }
                }
                else if(isSetAlternativeRouteMode){


                }
                else if(isSetInLineMode){


                }
                else if(isSetPossibleAddToRouteMode){
                }
        }
    }


    public class LineMouseExitedHandler implements EventHandler {
        @Override
        public void handle(Event event) {
            if (isSetViewMode) {
                meStreetLine.setStroke(Color.web("585858"));
                if (street.isPartOfLine()) {
                    for (model.Line busLine : street.getLines()) {
                        map.changeLinesColor(busLine.getStreets(), Color.web("585858"));
                    }
                }
            } else if (isSetAlternativeRouteMode) {


            } else if (isSetInLineMode) {


            } else if (isSetPossibleAddToRouteMode) {
            }
        }
    }
}