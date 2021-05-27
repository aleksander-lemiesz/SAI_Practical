package bank.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import javax.jms.*;
import java.util.HashMap;

public abstract class BrokerApplicationGateway {

    private final MessageReceiverGateway msgReceiverGateway;
    private final MessageSenderGateway msgSenderGateway;

    public abstract void onBankRequestReceived(BankRequest request);

    public void sendBankReply(BankRequest request, BankReply reply) {
        try {

            // Serialize
            String serialized = serializeBankReplyAndRequest(reply, request);

            // Create the message
            TextMessage message = (TextMessage) msgSenderGateway.createTextMessage(serialized);

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

    public String serializeBankRequest(BankRequest request) {
        return new Gson().toJson(request);
    }

    public String serializeBankReplyAndRequest(BankReply reply, BankRequest request) {
        return serializeBankReply(reply) + " & " + serializeBankRequest(request);
    }

    public void stop() {
        msgReceiverGateway.stop();
        msgSenderGateway.stop();
    }

}
