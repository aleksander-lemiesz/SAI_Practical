package loanclient.gui;

import com.google.gson.Gson;
import shared.model.MessageReceiverGateway;
import shared.model.MessageSenderGateway;
import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

import javax.jms.*;
import java.util.HashMap;

public abstract class BankApplicationGateway {

    private MessageSenderGateway msgSenderGateway = null;
    private MessageReceiverGateway msgReceiverGateway = null;

    // Storing the requests
    private HashMap<String, LoanRequest> requests = new HashMap<>();
    private final String replyQueue = "bankReplyQueue2";

    public BankApplicationGateway() {
        // start connection
        msgSenderGateway = new MessageSenderGateway("bankRequestQueue");

        msgReceiverGateway = new MessageReceiverGateway(replyQueue);
        msgReceiverGateway.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    // Get the loan request via correlation ID in the hashMap
                    String corId = message.getJMSCorrelationID();
                    LoanRequest request = requests.get(corId);

                    // Get the LoanReply form the bank message
                    TextMessage textMessage = (TextMessage) message;
                    LoanReply reply = deserializeLoanReply(textMessage.getText());

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

    public void stop() {
        msgSenderGateway.stop();
    }

}
