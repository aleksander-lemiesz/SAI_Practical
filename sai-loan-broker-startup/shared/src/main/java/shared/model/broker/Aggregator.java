package shared.model.broker;

import shared.model.bank.BankReply;
import shared.model.bank.BankRequest;
import shared.model.client.LoanReply;

import java.util.ArrayList;
import java.util.Objects;

public class Aggregator {

    private int aggregationID = 0;
    private int numberOfRepliesExpected = 0;
    private BankRequest request = null;
    private int numberOfReplies = 0;
    private ArrayList<BankReply> replies = new ArrayList<>();

    public Aggregator() {
    }

    public Aggregator(int aggregationID, BankRequest request) {
        this.aggregationID = aggregationID;
        this.request = request;
    }

    public int getAggregationID() {
        return aggregationID;
    }

    public void setAggregationID(int aggregationID) {
        this.aggregationID = aggregationID;
    }

    public int getNumberOfRepliesExpected() {
        return numberOfRepliesExpected;
    }

    public void setNumberOfRepliesExpected(int numberOfRepliesExpected) {
        this.numberOfRepliesExpected = numberOfRepliesExpected;
    }

    public BankRequest getRequest() {
        return request;
    }

    public void setRequest(BankRequest request) {
        this.request = request;
    }

    public int getNumberOfReplies() {
        return numberOfReplies;
    }

    public ArrayList<BankReply> getReplies() {
        return new ArrayList<>(replies);
    }

    public void AddReply(BankReply reply) {
        if (replies.isEmpty()) {
            System.out.println("Reply to be added: " + reply);
            replies.add(reply);
            System.out.println("After adding the reply: " + this);
            numberOfReplies = 1;
        } else {
            if (!isReadyForFinalReply()) {
                for (BankReply r : replies) {
                    if (!r.getBank().equals(reply.getBank())) {
                        replies.add(reply);
                        numberOfReplies++;
                        return;
                    } else {
                        r.setInterest(reply.getInterest());
                    }
                }
            }
        }
    }

    public boolean isReadyForFinalReply() {
        return numberOfReplies == numberOfRepliesExpected;
    }

    public BankReply getBestBankReply() {
        if (replies.isEmpty()) {
            System.out.println("The list is empty!");
            return null;
        } else {
            double minInterest = replies.get(0).getInterest();
            BankReply toReturn = new BankReply();
            for (BankReply reply : replies) {
                if (minInterest > reply.getInterest()) {
                    minInterest = reply.getInterest();
                    toReturn.setInterest(reply.getInterest());
                    toReturn.setBank(reply.getBank());
                }
            }
            return toReturn;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aggregator that = (Aggregator) o;
        return aggregationID == that.aggregationID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregationID);
    }

    @Override
    public String toString() {
        return "Aggregator{" +
                "aggregationID=" + aggregationID +
                ", numberOfRepliesExpected=" + numberOfRepliesExpected +
                ", request=" + request +
                ", numberOfReplies=" + numberOfReplies +
                ", replies=" + replies +
                '}';
    }
}
