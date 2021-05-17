package shared.model.bank;

/**
 * This class stores information about the bank reply
 *  to a loan request of the specific client
 * 
 */
public class BankReply {

    private double interest; // the interest that the bank offers for the requested loan
    private String bank; // the unique quote identification of the bank which makes the offer

    public BankReply() {
        this.interest = 0;
        this.bank = "";
    }
    
    public BankReply(double interest, String quoteId) {
        this.interest = interest;
        this.bank = quoteId;
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
//
//    public String toString() {
//        return "bank=" + this.bank + " interest=" + this.interest;
//    }


    @Override
    public String toString() {
        return "BankReply{" +
                "interest=" + interest +
                ", bank='" + bank + '\'' +
                '}';
    }
}
