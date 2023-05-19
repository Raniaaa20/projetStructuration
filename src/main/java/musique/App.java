package musique;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class App extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
	    Parent root = loader.load();
	    Scene scene = new Scene(root);
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
	

	public static void main(String[] args) {
		launch(args);
	}
}
