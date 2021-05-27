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
import java.util.ResourceBundle;


public class BrokerController implements Initializable {

    private LoanClientApplicationGateway loanGateway = null;
    private BankApplicationGateway bankGateway = null;

    @FXML
    private ListView<LoanRequest> lvBankRequestReply;
    @FXML
    private TextField tfInterest;

    public BrokerController() {
        loanGateway = new LoanClientApplicationGateway() {
            @Override
            public void onLoanRequestReceived(LoanRequest request, String corId, Destination replyTo) {
                BankRequest bankRequest = new BankRequest(request.getAmount(), request.getTime(), 0, 0);
                System.out.println("corID: " + corId);
                System.out.println("replyTo: " + replyTo);
                System.out.println("Request: " + bankRequest);
                bankGateway.sendBankRequest(bankRequest, corId, replyTo);
                showBankRequest(request);
            }
        };
        bankGateway = new BankApplicationGateway() {
            @Override
            public void onBankReplyReceived(BankReply reply, Destination destination, String corID) {
                LoanReply loanReply = new LoanReply(reply.getInterest(), reply.getBank());
                System.out.println("Destination after: " + destination);
                System.out.println("Reply: " + loanReply);
                loanGateway.sendLoanReply(loanReply, destination, corID);
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
