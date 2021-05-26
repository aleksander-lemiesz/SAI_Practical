package loanclient.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import shared.model.ListViewLine;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import java.net.URL;
import java.util.ResourceBundle;

public class LoanClientController implements Initializable {

    private BankApplicationGateway gateway = null;

    @FXML
    private TextField tfSsn;
    @FXML
    private TextField tfAmount;
    @FXML
    private TextField tfTime;
    @FXML
    private ListView<ListViewLine<LoanRequest, LoanReply>> lvLoanRequestReply= new ListView<>();


    public LoanClientController() {}

    @FXML
    public void btnSendLoanRequestClicked() {
        // create the BankRequest
        int ssn = Integer.parseInt(tfSsn.getText());
        int amount = Integer.parseInt(tfAmount.getText());
        int time = Integer.parseInt(tfTime.getText());
        LoanRequest loanRequest = new LoanRequest(ssn, amount, time);
        ListViewLine<LoanRequest, LoanReply> listViewLine = new ListViewLine<>(loanRequest);

        this.lvLoanRequestReply.getItems().add(listViewLine);

        try {
            gateway.applyForLoan(loanRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    This method is executed by FX after the FX frame is initialized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfSsn.setText("123456");
        tfAmount.setText("80000");
        tfTime.setText("30");

        gateway = new BankApplicationGateway() {
            @Override
            public void onLoanReplyReceived(LoanRequest request, LoanReply reply) {
                // loop through the listview and find line with this request
                System.out.println("request: " + request);
                for (ListViewLine<LoanRequest, LoanReply> list : lvLoanRequestReply.getItems()) {
                //    System.out.println("request: " + list.getRequest());
                    // assign reply to that line
                    if (request == list.getRequest()) {
                        list.setReply(reply);
                    }
                }
                // Refreshing the list
                Platform.runLater(() -> lvLoanRequestReply.refresh());
                //System.out.println("refreshing...");
            }
        };
    }

    void stop() {
        gateway.stop();
    }

}
