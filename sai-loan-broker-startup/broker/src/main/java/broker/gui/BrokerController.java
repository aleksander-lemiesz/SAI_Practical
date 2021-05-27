package broker.gui;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import shared.model.ListViewLine;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.broker.BankInterestRequest;
import shared.model.broker.Loan;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;


import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;



public class BrokerController implements Initializable {

    private LoanClientApplicationGateway loanGateway = null;
    private BankApplicationGateway bankGateway = null;

    // Linking LoanRequests with BankRequests
    private HashMap<BankRequest, LoanRequest> requests = new HashMap<>();

    @FXML
    private ListView<ListViewLine<LoanRequest, LoanReply>> lvLoanRequestReply = new ListView<>();

    public BrokerController() {
        loanGateway = new LoanClientApplicationGateway() {
            @Override
            public void onLoanRequestReceived(LoanRequest loanRequest) {
                BankInterestRequest bankInterestRequest = getCreditInfo();
                BankRequest bankRequest = new BankRequest(loanRequest.getAmount(), loanRequest.getTime(),
                        bankInterestRequest.getCreditScore(), bankInterestRequest.getHistory());
                System.out.println(bankInterestRequest);
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

                if (bankReply.getInterest() >= 0) {
                    // Archive the loan
                    archive(loanReply, request);
                }
            }
        };

    }

    private BankInterestRequest getCreditInfo() {

        // Start Connection
        WebTarget serviceTarget = null;
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newBuilder().withConfig(config).build();
        URI baseURI = UriBuilder.fromUri("http://localhost:9091/credit/history/123").build();
        serviceTarget = client.target(baseURI);

        Invocation.Builder requestBuilder = serviceTarget.request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            BankInterestRequest entity = response.readEntity(BankInterestRequest.class);
            System.out.println("The service response is: " + entity);
            return entity;
        } else {
            System.err.println("ERROR: Cannot get path param! " + response);
            String entity = response.readEntity(String.class);
            System.err.println(entity);
            return null;
        }

    }

    private void archive(LoanReply loanReply, LoanRequest request) {

        // Start Connection
        WebTarget serviceTarget = null;
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newBuilder().withConfig(config).build();
        URI baseURI = UriBuilder.fromUri("http://localhost:9090/archive/accepted").build();
        serviceTarget = client.target(baseURI);

        // Create object and send it.
        Loan loan = new Loan(loanReply, request);
        System.out.println(loan);
        Entity<Loan> loanEntity = Entity.entity(loan, MediaType.APPLICATION_JSON);

        Response response = serviceTarget.request().accept(MediaType.TEXT_PLAIN).post(loanEntity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            System.out.println("Successfully archived " + loan + ".");
        } else {
            System.err.println("ERROR: Cannot archive loan " + loan + "!" + response);
            String entity = response.readEntity(String.class);
            System.err.println(entity);
        }

    }

    /*
     Use this method to show each bankRequest (upon message arrival) on the frame in a thread-safe way.
     */
    private void showLoanRequest(LoanRequest request) {
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
