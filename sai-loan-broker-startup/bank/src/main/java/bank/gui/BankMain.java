package bank.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class BankMain extends Application {

    private static String queueName = null;
    private static String bankName = null;

    @Override
    public void start(final Stage primaryStage) throws IOException {
        final Logger logger = LoggerFactory.getLogger(BankMain.class);

        final String fxmlFileName = "bank.fxml";
        URL url = getClass().getClassLoader().getResource(fxmlFileName);
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            BankController controller = new BankController(queueName, bankName);
            loader.setController(controller);
            Parent root = loader.load();

            // EXIT this application when this stage is closed
            primaryStage.setOnCloseRequest(t -> {
                logger.info("Closing bank .....");
                controller.stop();
                Platform.exit();
                System.exit(0);
            });
            // set the stage title, icon and size
            primaryStage.setTitle("BANK - "+ bankName);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/bank.png")));
            primaryStage.setScene(new Scene(root, 500, 300));
            // show the stage
            primaryStage.show();


        } else {
            logger.error("Could not load frame from "+fxmlFileName);
        }
    }

    public static void main(String[] args) {

        if (args.length < 2 ){
            throw new IllegalArgumentException("Arguments are missing. You must provide two arguments: BANK_REQUEST_QUEUE and BANK_NAME");
        }
        if (args[0] == null){
            throw new IllegalArgumentException("Please provide BANK_NAME.");
        }
        if (args[1] == null){
            throw new IllegalArgumentException("Please provide BANK_REQUEST_QUEUE.");
        }

        bankName =args[0];
        queueName = args[1];

        launch(args);
    }
}
