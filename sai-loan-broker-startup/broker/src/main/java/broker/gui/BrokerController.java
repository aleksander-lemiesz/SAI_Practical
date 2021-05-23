package broker.gui;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;

import java.net.URL;
import java.util.ResourceBundle;


public class BrokerController implements Initializable {

    private LoanClientApplicationGateway loanGateway = null;
    private BankApplicationGateway bankGateway = null;

    @FXML
    private ListView<BankRequest> lvBankRequestReply;
    @FXML
    private TextField tfInterest;

    public BrokerController() {
        loanGateway = new LoanClientApplicationGateway() {
            @Override
            public void onBankRequestReceived(BankRequest request) {
                showBankRequest(request);
            }
        };
        bankGateway = new BankApplicationGateway();

    }

    /*
     Use this method to show each bankRequest (upon message arrival) on the frame in a thread-safe way.
     */
    private void showBankRequest(BankRequest bankRequest){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lvBankRequestReply.getItems().add(bankRequest);
            }
        });
    }

    void stop() {
        loanGateway.stop();
        bankGateway.stop();
    }

    /*
    This method is executed by FX after the FX frame is initialized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
