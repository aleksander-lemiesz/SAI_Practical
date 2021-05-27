package broker.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankRequest;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import javax.jms.*;

public abstract class LoanClientApplicationGateway {

    // private MessageSenderGateway toBankGateway = null;
    private MessageSenderGateway toClientGateway = null;
    private MessageReceiverGateway fromClientGateway = null;

    public LoanClientApplicationGateway() {
        fromClientGateway = new MessageReceiverGateway("brokerRequestQueue");
        toClientGateway = new MessageSenderGateway();

        fromClientGateway.setListener( new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {

                    // Set correlation ID
                    msg.setJMSCorrelationID(msg.getJMSMessageID());
                    String corId = msg.getJMSMessageID();
                    Destination replyTo = msg.getJMSReplyTo();

                    // TODO: add enricher here

                    //toClientGateway.send(msg);

                    TextMessage textMessage = (TextMessage) msg;
                    var deserialized = deserializeBankRequest(textMessage.getText());

                    onLoanRequestReceived(deserialized, corId, replyTo);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public abstract void onLoanRequestReceived(LoanRequest request, String corId, Destination replyTo);

    public void stop() {
        toClientGateway.stop();
        fromClientGateway.stop();
    }

    public LoanRequest deserializeBankRequest(String body) {
        return new Gson().fromJson(body, LoanRequest.class);
    }

    public String serializeLoanReply(LoanReply reply) {
        return new Gson().toJson(reply);
    }

    public void sendLoanReply(LoanReply reply, Destination destination, String corID) {
        try {
            Message msg = toClientGateway.createTextMessage(serializeLoanReply(reply));
            msg.setJMSCorrelationID(corID);
            toClientGateway.send(msg, destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
