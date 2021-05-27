package broker.gui;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import shared.model.ListViewLine;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;


public class BrokerController implements Initializable {

    private LoanClientApplicationGateway loanGateway = null;
    private BankApplicationGateway bankGateway = null;

    // Linking LoanRequests with BankRequests
    private HashMap<BankRequest, LoanRequest> requests = new HashMap<>();

    @FXML
    private ListView<ListViewLine<LoanRequest, LoanReply>> lvLoanRequestReply= new ListView<>();

    public BrokerController() {
        loanGateway = new LoanClientApplicationGateway() {
            @Override
            public void onLoanRequestReceived(LoanRequest loanRequest) {
                BankRequest bankRequest = new BankRequest(loanRequest.getAmount(), loanRequest.getTime(), 0, 0);
                bankGateway.sendBankRequest(bankRequest);
                requests.put(bankRequest, loanRequest);

                showLoanRequest(loanRequest);
            }
        };
        bankGateway = new BankApplicationGateway() {
            @Override
            public void onBankReplyReceived(BankReply bankReply, BankRequest bankRequest) {
                LoanReply loanReply = new LoanReply(bankReply.getInterest(), bankReply.getBank());
                LoanRequest request = requests.get(bankRequest);
                loanGateway.sendLoanReply(loanReply, request);

                showAndUpdateLoans(loanReply, request);
            }
        };

    }

    /*
     Use this method to show each bankRequest (upon message arrival) on the frame in a thread-safe way.
     */
    private void showLoanRequest(LoanRequest request){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ListViewLine<LoanRequest, LoanReply> listViewLine = new ListViewLine<>(request);
                lvLoanRequestReply.getItems().add(listViewLine);
            }
        });
    }

    private void showAndUpdateLoans(LoanReply loanReply, LoanRequest request) {
        for (ListViewLine<LoanRequest, LoanReply> list : lvLoanRequestReply.getItems()) {
            // assign reply to that line
            if (request.equals(list.getRequest())) {
                list.setReply(loanReply);
            }
        }
        // Refreshing the list
        Platform.runLater(() -> lvLoanRequestReply.refresh());
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
