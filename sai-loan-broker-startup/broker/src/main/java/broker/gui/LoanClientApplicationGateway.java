package broker.gui;

import com.google.gson.Gson;
import shared.model.ListViewLine;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import javax.jms.*;
import java.util.HashMap;

public abstract class LoanClientApplicationGateway {

    private MessageSenderGateway toClientGateway = null;
    private MessageReceiverGateway fromClientGateway = null;

    // Storing the requests
    private HashMap<LoanRequest, Destination> requests = new HashMap<>();

    public LoanClientApplicationGateway() {
        fromClientGateway = new MessageReceiverGateway("brokerRequestQueue");
        toClientGateway = new MessageSenderGateway();

        fromClientGateway.setListener( new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {

                    TextMessage textMessage = (TextMessage) msg;
                    var request = deserializeBankRequest(textMessage.getText());
                    var replyTo = msg.getJMSReplyTo();

                    System.out.println("Client gateway On message Request: " + request);
                    System.out.println("Client gateway On message ReplyTo: " + replyTo);

                    requests.put(request, replyTo);

                    onLoanRequestReceived(request);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public abstract void onLoanRequestReceived(LoanRequest request);

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

    public String serializeLoanRequest(LoanRequest request) {
        return new Gson().toJson(request);
    }

    public String serializeLoanReplyAndRequest(LoanReply reply, LoanRequest request) {
        return serializeLoanReply(reply) + " & " + serializeLoanRequest(request);
    }

    public void sendLoanReply(LoanReply reply, LoanRequest request) {
        try {
            Message msg = toClientGateway.createTextMessage(serializeLoanReplyAndRequest(reply, request));
            var replyTo = requests.get(request);
            System.out.println("Client gateway Send request: " + request);
            System.out.println("Client gateway Send ReplyTo: " + replyTo);
            toClientGateway.send(msg, replyTo);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
