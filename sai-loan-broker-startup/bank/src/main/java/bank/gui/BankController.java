package bank.gui;


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
import java.util.ResourceBundle;


public class BankController implements Initializable {

    private BrokerApplicationGateway gateway = null;

    private static String bankName = null;
    private static String queueName = null;

    @FXML
    private ListView<ListViewLine<BankRequest, BankReply>> lvBankRequestReply;
    @FXML
    private TextField tfInterest;

    public BankController(String queue, String name) {
        bankName = name;
        queueName = queue;
        gateway = new BrokerApplicationGateway(queueName) {
            @Override
            public void onBankRequestReceived(BankRequest request) {
                showBankRequest(request);
            }
        };
    }

    /*
     Use this method to show each bankRequest (upon message arrival) on the frame in a thread-safe way.
     */
    private void showBankRequest(BankRequest bankRequest){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ListViewLine<BankRequest, BankReply> listViewLine = new ListViewLine<>(bankRequest);
                lvBankRequestReply.getItems().add(listViewLine);
            }
        });
    }

    void stop() {
        gateway.stop();
    }

    /*
    This method is executed by FX after the FX frame is initialized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void btnSendBankInterestReplyClicked() {

        var line = this.lvBankRequestReply.getSelectionModel().getSelectedItem();
        BankRequest bankRequest = line.getRequest();

        //BankReply bankReply = new BankReply(0, "ING");
        BankReply bankReply = new BankReply(0, bankName);

        int interest = Integer.parseInt(tfInterest.getText());
        bankReply.setInterest(interest);

        gateway.sendBankReply(bankRequest, bankReply);

        line.setReply(bankReply);
        Platform.runLater(() -> lvBankRequestReply.refresh());
    }

}
