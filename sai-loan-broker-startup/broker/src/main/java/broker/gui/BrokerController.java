package broker.gui;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import javax.jms.Destination;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;


public class BrokerController implements Initializable {

    private LoanClientApplicationGateway loanGateway = null;
    private BankApplicationGateway bankGateway = null;

    // Linking LoanRequests with BankRequests
    private HashMap<BankRequest, LoanRequest> requests = new HashMap<>();

    @FXML
    private ListView<LoanRequest> lvBankRequestReply;
    @FXML
    private TextField tfInterest;

    public BrokerController() {
        loanGateway = new LoanClientApplicationGateway() {
            @Override
            public void onLoanRequestReceived(LoanRequest loanRequest) {
                BankRequest bankRequest = new BankRequest(loanRequest.getAmount(), loanRequest.getTime(), 0, 0);
                bankGateway.sendBankRequest(bankRequest);
                requests.put(bankRequest, loanRequest);
                showBankRequest(loanRequest);
            }
        };
        bankGateway = new BankApplicationGateway() {
            @Override
            public void onBankReplyReceived(BankReply bankReply, BankRequest bankRequest) {
                LoanReply loanReply = new LoanReply(bankReply.getInterest(), bankReply.getBank());
                LoanRequest request = requests.get(bankRequest);
                System.out.println("Controller requests map: " + requests);
                System.out.println("Controller bankRequest: " + bankRequest);
                System.out.println("Controller loanRequest: " + request);
                loanGateway.sendLoanReply(loanReply, request);
            }
        };

    }

    /*
     Use this method to show each bankRequest (upon message arrival) on the frame in a thread-safe way.
     */
    private void showBankRequest(LoanRequest request){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lvBankRequestReply.getItems().add(request);
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
