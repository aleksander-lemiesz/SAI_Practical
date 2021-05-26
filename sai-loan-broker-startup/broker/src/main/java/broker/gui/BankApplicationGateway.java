package broker.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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

    public BankReply deserializeBankReply(String body) {
        return new Gson().fromJson(body, BankReply.class);
    }

}
