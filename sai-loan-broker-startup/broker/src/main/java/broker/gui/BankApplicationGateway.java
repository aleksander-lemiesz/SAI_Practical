package broker.gui;

import com.google.gson.Gson;
import shared.model.ListViewLine;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanReply;
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

                    TextMessage textMessage = (TextMessage) msg;

                    String split[] = textMessage.getText().split(" & ");
                    BankReply reply = deserializeBankReply(split[0]);
                    BankRequest request = deserializeBankRequest(split[1]);

                    onBankReplyReceived(reply, request);

                } catch (JMSException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public abstract void onBankReplyReceived(BankReply reply, BankRequest bankRequest);

    public void stop() {
        toBankGateway.stop();
        fromBankGateway.stop();
    }

    public BankReply deserializeBankReply(String body) {
        return new Gson().fromJson(body, BankReply.class);
    }

    public BankRequest deserializeBankRequest(String body) {
        return new Gson().fromJson(body, BankRequest.class);
    }

    public String serializeBankRequest(BankRequest request) {
        return new Gson().toJson(request);
    }

    public void sendBankRequest(BankRequest bankRequest) {
        try {
            Message msg = toBankGateway.createTextMessage(serializeBankRequest(bankRequest));
            toBankGateway.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
