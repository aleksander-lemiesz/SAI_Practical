package broker.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankRequest;

import javax.jms.*;

public abstract class LoanClientApplicationGateway {

    private MessageSenderGateway toBankGateway = null;
    private MessageReceiverGateway fromClientGateway = null;

    public LoanClientApplicationGateway() {
        fromClientGateway = new MessageReceiverGateway("brokerRequestQueue");
        toBankGateway = new MessageSenderGateway("bankRequestQueue");

        fromClientGateway.setListener( new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {

                    // Set correlation ID
                    msg.setJMSCorrelationID(msg.getJMSMessageID());

                    toBankGateway.send(msg);

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
        toBankGateway.stop();
        fromClientGateway.stop();
    }

    public BankRequest deserializeBankRequest(String body) {
        return new Gson().fromJson(body, BankRequest.class);
    }

}
