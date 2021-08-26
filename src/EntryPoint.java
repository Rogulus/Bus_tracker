import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.lang.*;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;

import controller.MainPageController;
import model.MMBTModel;

/**
 * Application for monitoring the movement of buses
 *
 * @author  Marek Stastny, Michael Kinc
 * @version 1.0
 */
public class EntryPoint extends Application{

	/**
	 * This is the main method which binds model controller and view.
	 * It also makes the model run and shows the main window.
	 * @param args Unused.
	 * @return Nothing.
	 */
	public static void main(String[] args){
		launch(args);
    }

    public void start(Stage primaryStage)throws Exception{
		FXMLLoader loadrer = new FXMLLoader();
    	Parent root = loadrer.load(getClass().getResource("view/mainPage.fxml").openStream());

    	Pane rootPane;
		FXMLLoader loader;

		MMBTModel model = new MMBTModel();
		MainPageController mainPageController = loadrer.getController();
		mainPageController.addModel(model);

		mainPageController.updateModelData();

		primaryStage.setOnCloseRequest(e -> {
			e.consume();
			mainPageController.closeProgram();
		});

    	primaryStage.setTitle("MMBT");
    	primaryStage.setScene(new Scene(root, 1000, 600));

		model.run();
    	primaryStage.show();
	}

}
