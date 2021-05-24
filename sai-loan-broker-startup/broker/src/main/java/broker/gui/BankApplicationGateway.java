package broker.gui;

import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class BankApplicationGateway {

    private MessageSenderGateway toClientGateway = null;
    private MessageReceiverGateway fromBankGateway = null;

    public BankApplicationGateway() {
        fromBankGateway = new MessageReceiverGateway("brokerReplyQueue");
        toClientGateway = new MessageSenderGateway();

        fromBankGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {

                    toClientGateway.send(msg, msg.getJMSReplyTo());

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void stop() {
        toClientGateway.stop();
        fromBankGateway.stop();
    }

}
