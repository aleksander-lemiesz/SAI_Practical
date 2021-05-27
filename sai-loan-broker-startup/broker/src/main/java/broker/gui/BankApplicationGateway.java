package broker.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanRequest;

import javax.jms.*;

public abstract class BankApplicationGateway {

    // private MessageSenderGateway toClientGateway = null;
    private MessageSenderGateway toBankGateway = null;
    private MessageReceiverGateway fromBankGateway = null;

    public BankApplicationGateway() {
        toBankGateway = new MessageSenderGateway("bankRequestQueue");
        fromBankGateway = new MessageReceiverGateway("brokerReplyQueue");

        fromBankGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {

                    // TODO: add router here

                    TextMessage textMessage = (TextMessage) msg;
                    var deserialized = deserializeBankReply(textMessage.getText());
                    //toBankGateway.send(msg, msg.getJMSReplyTo());
                    var destination = msg.getJMSReplyTo();
                    //System.out.println(destination);
                    var corID = msg.getJMSCorrelationID();

                    onBankReplyReceived(deserialized, destination, corID);

                } catch (JMSException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public abstract void onBankReplyReceived(BankReply reply, Destination destination, String corID);

    public void stop() {
        toBankGateway.stop();
        fromBankGateway.stop();
    }

    public BankReply deserializeBankReply(String body) {
        return new Gson().fromJson(body, BankReply.class);
    }

    public String serializeBankRequest(BankRequest request) {
        return new Gson().toJson(request);
    }

    public void sendBankRequest(BankRequest bankRequest, String corId, Destination replyTo) {
        try {
            Message msg = toBankGateway.createTextMessage(serializeBankRequest(bankRequest));
            msg.setJMSCorrelationID(corId);
            msg.setJMSReplyTo(replyTo);
            toBankGateway.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
