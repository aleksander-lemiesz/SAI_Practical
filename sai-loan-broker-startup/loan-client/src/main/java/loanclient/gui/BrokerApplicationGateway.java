package loanclient.gui;

import com.google.gson.Gson;
import shared.model.ListViewLine;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import javax.jms.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public abstract class BrokerApplicationGateway {

    private MessageSenderGateway msgSenderGateway = null;
    private MessageReceiverGateway msgReceiverGateway = null;

    // Storing the requests
    private HashMap<String, LoanRequest> requests = new HashMap<>();
    private final String replyQueue = "bankReplyQueue";

    public BrokerApplicationGateway() {
        // start connection
        //msgSenderGateway = new MessageSenderGateway("bankRequestQueue");
        msgSenderGateway = new MessageSenderGateway("brokerRequestQueue");

        msgReceiverGateway = new MessageReceiverGateway(replyQueue);
        msgReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {

                    TextMessage textMessage = (TextMessage) message;

                    // Deserialize
                    String split[] = textMessage.getText().split(" & ");
                    LoanReply reply = deserializeLoanReply(split[0]);
                    LoanRequest request = deserializeLoanRequest(split[1]);

                    System.out.println("On message Client reply: " + reply);
                    System.out.println("On message Client request: " + request);

                    // Assign the reply to the request
                    onLoanReplyReceived(request, reply);

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void applyForLoan(LoanRequest request) throws Exception {

        // Create JSON
        Gson gson = new Gson();
        String json = gson.toJson(request);

        // Create the message from JSON
        Message message = msgSenderGateway.createTextMessage(json);

        // Set the receiver destination
        Destination replyDest = msgReceiverGateway.getDestination();
        message.setJMSReplyTo(replyDest);

        // send message
        msgSenderGateway.send(message);

        // Put the messageId into the hashMap to be able to assign the reply
        String messageId = message.getJMSMessageID();
        requests.put(messageId, request);

    }

    public abstract void onLoanReplyReceived(LoanRequest request, LoanReply reply);

    public LoanReply deserializeLoanReply(String body) {
        return new Gson().fromJson(body, LoanReply.class);
    }

    public LoanRequest deserializeLoanRequest(String body) {
        return new Gson().fromJson(body, LoanRequest.class);
    }

    public ListViewLine<LoanRequest, LoanReply> deserializeLoanReplyAndRequest(String body) {
        String split[] = body.split(" & ");
        LoanReply reply = deserializeLoanReply(split[0]);
        LoanRequest request = deserializeLoanRequest(split[1]);
        ListViewLine<LoanRequest, LoanReply> listViewLine = new ListViewLine<>(request);
        listViewLine.setReply(reply);
        return listViewLine;
    }

    public void stop() {
        msgSenderGateway.stop();
    }

}
