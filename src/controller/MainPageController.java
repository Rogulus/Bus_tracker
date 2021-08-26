package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import model.*;
import model.Interfaces.Listener;

import java.util.ArrayList;
import java.util.List;


/**
 * Třída obsluhující hlavní okno aplikace.
 * @author Marek Šťastný
 */
public class MainPageController implements Listener {
    MMBTModel model;
    private boolean wasNotified = false ;
    private PaneMap myMap;
    private  boolean settingTimeInProgress = false;


    private PairBinder<model.Line, TreeItem<String>> shownLines = new PairBinder<>();
    private PairBinder<Connection, TreeItem<String> > connections = new PairBinder<>();
    public PairBinder<Line, TreeItem<String>> delayBinder = new PairBinder<>();


    @FXML
    private Button setTimeButton;

    @FXML
    private HBox controlsPanel;

    @FXML
    private Label timeSpeedLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Pane map;


    @FXML
    private Slider zoomer;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private void initialize(){
        map.setStyle("-fx-background-color: #282828");
        setMapRefresher();
    }


    /**
     * Přidá si model, se ktrým má interagovat
     * @param model Model se kterým bude interagovat.
     */
    public void addModel(MMBTModel model){
        this.model = model;
        model.setListener(this);

        this.myMap = new PaneMap(map, model.getMapWidth(), model.getMapHight(), model, this);

        myMap.drawStreets(new ArrayList<>(model.getAllStreets().values()));
        myMap.setBusLines();

        for(BusStop stop: model.getAllStops()){
            new BusStopController(stop, myMap);
        }
        printTreeView();
    }

    /**
     * Naplní boční panel informacemi o linkách.
     */
    public void printTreeView() {
        //treeView setting
        TreeItem<String> root = new TreeItem<>();
        treeView.setRoot(root);

        treeView.setShowRoot(false);

        int lineDelay = -1;
        DayTime lineDelayDT  = new DayTime();

        List<Line> lines = model.getAllLines();
        for (Line l : lines) {
            TreeItem<String> tI = new TreeItem<>(l.getName());
            TreeItem<String> delay = new TreeItem<>();
            TreeItem<String> stops = new TreeItem<>("Stops");
            TreeItem<String> cs = new TreeItem<>("Connections");
            root.getChildren().add(tI);
            tI.getChildren().add(delay);
            this.delayBinder.bindTogether(l, delay);
            tI.getChildren().add(stops);
            tI.getChildren().add(cs);
            shownLines.bindTogether(l, tI);

            List<Connection> c = l.getConnections();
            for (Connection c1 : c) {
                TreeItem<String> TIconnection = new TreeItem<>((c1.getStringStartTime()));
                lineDelay = model.getDelayOfConnection(l, c1);
                connections.bindTogether(c1, TIconnection);
                cs.getChildren().add(TIconnection);
            }
            List<BusStop> s = l.getStops();
            for (BusStop s1 : s) {
                TreeItem<String> busStop = new TreeItem<>(s1.getName());
                stops.getChildren().add(busStop);
            }
            lineDelayDT.setTime(lineDelay);
            String lineDelayStr = "Delay: " + lineDelayDT.toString();
            delay.setValue(lineDelayStr);
        }
    }

    /**
     * Updatuje zobrazení rychlosti času modelu.
     */
    public void updateModelData(){
        timeSpeedLabel.setText(String.valueOf(model.getSecondDurance()));
    }


    /**
     * Ukončí program
     */
    public void closeProgram(){
        model.stop();
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    /**
     * Zastaví čas modelu
     */
    public void stopButtonClicked(){
        model.stop();
        timeSpeedLabel.setText(String.valueOf(model.getSecondDurance()));
    }

    /**
     * Uvede čas modelu do provozu
     */
    public void resumeButtonClicked(){
        model.run();
        timeSpeedLabel.setText(String.valueOf(model.getSecondDurance()));
    }

    /**
     * Zpomalí čas modelu.
     */
    public void plusButtonClicked(){
        int secondDurance = model.getSecondDurance();
        if(secondDurance < 2){
            model.modifySecondDurance(secondDurance +1);
            timeSpeedLabel.setText(String.valueOf(model.getSecondDurance()));
        }
        else {
            secondDurance += (float) secondDurance / 3;
            model.modifySecondDurance(secondDurance);
            timeSpeedLabel.setText(String.valueOf(model.getSecondDurance()));
        }
    }

    /**
     * Zrychlí čas modelu.
     */
    public void minusButtonClicked(){
        int secondDurance = model.getSecondDurance();
        secondDurance -= (float)secondDurance / 3;
        model.modifySecondDurance(secondDurance);
        timeSpeedLabel.setText(String.valueOf(model.getSecondDurance()));
    }

    /**
     * Poznamená si, že čas modelu byl pozměněn.
     */
    public void call(){
        wasNotified = true;
    }


    /**
     * Periodicky zkouší, jestli byl čas modelu změněn. Pokud ano provede aktualizaci.
     */
    private void setMapRefresher() {
        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(16), e -> {

                if(wasNotified) {
                    wasNotified = false;
                    refreshMap();

            }
        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
    }

    /**
     * Upraví zobrazení autobusů a času.
     */
    public void refreshMap(){
        myMap.reDrawBuses(model.getAllBuses());
        timeLabel.setText(model.getStringTime());
    }


    /**
     * Změní zoom mapy.
     */
    public void onZoomShift(){
        myMap.setZoom(zoomer.getValue());
    }

    /**
     * Zkontroluje, jestli byl požadovaný čas validní. Pokud ano, nastaví novvý čas modelu, jinak uživateli ukáže, která kolonka času je nevalidní.
     */
    public void onSetTime() {
        if (!settingTimeInProgress) {
            settingTimeInProgress = true;
            Label colon = new Label(":");
            colon.setPrefHeight(25);
            Label colon2 = new Label(":");
            colon.setPrefHeight(25);
            int time = model.getTime();
            TextField hours = new TextField(Integer.toString(time / 3600));
            TextField minutes = new TextField(Integer.toString(time % 3600 / 60));
            TextField seconds = new TextField(Integer.toString(time % 60));
            hours.setPrefHeight(25);
            hours.setPrefWidth(40);
            minutes.setPrefHeight(25);
            minutes.setPrefWidth(40);
            seconds.setPrefHeight(25);
            seconds.setPrefWidth(40);
            Button okButton = new Button("ok");
            okButton.setPrefHeight(25);
            controlsPanel.getChildren().addAll(hours, colon, minutes, colon2, seconds, okButton);

            okButton.setOnAction(e -> {
                boolean ok = true;
                int intHour = -1;
                int intMinute = -1;
                int intSec = -1;
                try {
                    intHour = Integer.parseInt(hours.getText());
                } catch (NumberFormatException exc) {
                }

                try {
                    intMinute = Integer.parseInt(minutes.getText());
                } catch (NumberFormatException exc) {
                }

                try {
                    intSec = Integer.parseInt(seconds.getText());
                } catch (NumberFormatException exc) {
                }


                if (intHour < 0 || intHour > 23) {
                    ok = false;
                    hours.setText("##");
                }

                if (intMinute < 0 || intMinute > 59) {
                    ok = false;
                    minutes.setText("##");
                }

                if (intSec < 0 || intSec > 59) {
                    ok = false;
                    seconds.setText("##");
                }

                if (ok) {
                    int newTime = intHour * 3600 + intMinute * 60 + intSec;
                    System.out.println(newTime);
                    model.setTime(newTime);
                    controlsPanel.getChildren().removeAll(hours, colon, minutes, colon2, seconds, okButton);
                    settingTimeInProgress = false;
                }
            });
        }
    }
}
