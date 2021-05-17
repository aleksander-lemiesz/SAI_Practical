package bank.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanRequest;

import javax.jms.*;
import java.util.HashMap;

public abstract class LoanClientApplicationGateway {

    private final MessageReceiverGateway msgReceiverGateway;
    private final MessageSenderGateway msgSenderGateway;

    // Storing the requests
    private HashMap<BankRequest, String> requests = new HashMap<>();

    // Storing the destinations
    private HashMap<BankRequest, Destination> destinations = new HashMap<>();

    public abstract void onBankRequestReceived(BankRequest request);

    public void sendBankReply(BankRequest request, BankReply reply) {
        try {
            // Turn reply into string
            String json = serializeBankReply(reply);

            // Create the message
            Message message = msgSenderGateway.createTextMessage(json);

            // Get correlation ID
            String corId = requests.get(request);

            // Assign the correlation ID
            message.setJMSCorrelationID(corId);

            // Decide where to send the message
            Destination returnAddress = destinations.get(request);

            // Send the reply message
            msgSenderGateway.send(message, returnAddress);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public LoanClientApplicationGateway() {
        msgReceiverGateway = new MessageReceiverGateway("bankRequestQueue");
        //msgSenderGateway = null;
        msgSenderGateway = new MessageSenderGateway("bankReplyQueue");

        msgReceiverGateway.setListener( new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {
                    // get JSON from the message text
                    TextMessage message = (TextMessage) msg;
                    String json = message.getText();

                    //deserialize json
                    BankRequest bankRequest = deserializeBankRequest(json);

                    // Get the id of the message
                    String messageId = message.getJMSMessageID();

                    // Put the id and request in the hashMap
                    requests.put(bankRequest, messageId);

                    // Put the bankRequest and returnAddress
                    Destination destination = message.getJMSReplyTo();
                    destinations.put(bankRequest, destination);

                    //call abstr. meth. to pass the bankRequest
                    onBankRequestReceived(bankRequest);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public BankRequest deserializeBankRequest(String body) {
        return new Gson().fromJson(body, BankRequest.class);
    }

    public String serializeBankReply(BankReply reply) {
        return new Gson().toJson(reply);
    }

    public void stop() {
        msgReceiverGateway.stop();
        msgSenderGateway.stop();
    }

}
