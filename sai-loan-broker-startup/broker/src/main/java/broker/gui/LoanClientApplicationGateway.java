package broker.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankRequest;
import shared.model.client.LoanReply;

import javax.jms.*;

public abstract class LoanClientApplicationGateway {

    private MessageSenderGateway toClientGateway = null;
    private MessageReceiverGateway fromClientGateway = null;

    public LoanClientApplicationGateway() {
        fromClientGateway = new MessageReceiverGateway("brokerRequestQueue");
        toClientGateway = new MessageSenderGateway();

        fromClientGateway.setListener( new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {

                    toClientGateway.send(msg, msg.getJMSReplyTo());

                    TextMessage textMessage = (TextMessage) msg;
                    var deserialized = deserializeBankRequest(textMessage.getText());

                    onBankRequestReceived(deserialized);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public abstract void onBankRequestReceived(BankRequest request);

    public void stop() {
        toClientGateway.stop();
        fromClientGateway.stop();
    }

    public BankRequest deserializeBankRequest(String body) {
        return new Gson().fromJson(body, BankRequest.class);
    }

}
