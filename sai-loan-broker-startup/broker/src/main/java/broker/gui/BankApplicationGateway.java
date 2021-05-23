package broker.gui;

import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class BankApplicationGateway {

    private MessageSenderGateway toBankGateway = null;
    private MessageReceiverGateway fromBankGateway = null;

    public BankApplicationGateway() {
        fromBankGateway = new MessageReceiverGateway("brokerReplyQueue");
        toBankGateway = new MessageSenderGateway("bankRequestQueue");

        fromBankGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {

                    toBankGateway.send(msg);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void stop() {
        toBankGateway.stop();
        fromBankGateway.stop();
    }

}
