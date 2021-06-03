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

    //private MessageSenderGateway toBankGateway = null;

    //Senders
    private MessageSenderGateway toINGGateway = null;
    private MessageSenderGateway toAMROGateway = null;
    private MessageSenderGateway toRABOGateway = null;

    private MessageReceiverGateway fromBankGateway = null;

    public BankApplicationGateway() {
        //toBankGateway = new MessageSenderGateway("ingRequestQueue");
        fromBankGateway = new MessageReceiverGateway("brokerReplyQueue");

        toINGGateway = new MessageSenderGateway("ingRequestQueue");
        toAMROGateway = new MessageSenderGateway("abnRequestQueue");
        toRABOGateway = new MessageSenderGateway("raboRequestQueue");

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
        toAMROGateway.stop();
        toINGGateway.stop();
        toRABOGateway.stop();
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

    public void sendBankRequestToAMRO(BankRequest bankRequest) {
        try {
            Message msg = toAMROGateway.createTextMessage(serializeBankRequest(bankRequest));
            toAMROGateway.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public void sendBankRequestToING(BankRequest bankRequest) {
        try {
            Message msg = toINGGateway.createTextMessage(serializeBankRequest(bankRequest));
            toINGGateway.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendBankRequestToRABO(BankRequest bankRequest) {
        try {
            Message msg = toRABOGateway.createTextMessage(serializeBankRequest(bankRequest));
            toRABOGateway.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
