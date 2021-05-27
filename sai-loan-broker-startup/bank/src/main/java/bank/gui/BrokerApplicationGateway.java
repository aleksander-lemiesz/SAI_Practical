package bank.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanRequest;

import javax.jms.*;
import java.util.HashMap;

public abstract class BrokerApplicationGateway {

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
            TextMessage message = (TextMessage) msgSenderGateway.createTextMessage(json);

            // Get correlation ID
            String corId = requests.get(request);
            System.out.println("CorID after: " + corId);

            // Assign the correlation ID
            message.setJMSCorrelationID(corId);

            // Decide where to send the message
            Destination returnAddress = destinations.get(request);
            System.out.println("ReplyDestination after: " + returnAddress);

            // Because the message is send to the broker the return address is saved in reply to
            message.setJMSReplyTo(returnAddress);

            // Send the reply message
            msgSenderGateway.send(message);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public BrokerApplicationGateway() {
        msgReceiverGateway = new MessageReceiverGateway("bankRequestQueue");
        msgSenderGateway = new MessageSenderGateway("brokerReplyQueue");

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
                    String messageId = message.getJMSCorrelationID();
                    System.out.println("CorID before: " + messageId);

                    // Put the id and request in the hashMap
                    requests.put(bankRequest, messageId);

                    // Put the bankRequest and returnAddress
                    Destination destination = message.getJMSReplyTo();
                    System.out.println("ReplyDestination before: " + destination);
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