package shared.model.client;

/**
 * This class stores all information about a bank offer
 * as a response to a client loan request.
 */
public class LoanReply {

    private double interest; // the interest that the bank offers for the requested loan
    private String bank; // the unique quote identification of the bank which makes the offer

    public LoanReply() {
        super();
        this.interest = 0;
        this.bank = "";
    }

    public LoanReply(double interest, String quoteID) {
        super();
        this.interest = interest;
        this.bank = quoteID;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return " interest=" + interest + " bank=" + bank;
    }

    public boolean isAccepted(){
        return interest > 0;
    }
}
