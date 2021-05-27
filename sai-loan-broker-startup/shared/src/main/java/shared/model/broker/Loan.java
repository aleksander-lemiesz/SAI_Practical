package shared.model.broker;

import shared.model.client.LoanReply;
import shared.model.client.LoanRequest;

public class Loan {

    private int ssn;
    private int amount;
    private String bank;
    private double interest;
    private int time;

    public Loan(LoanReply loanReply, LoanRequest request) {
        this.ssn = request.getSsn();
        this.amount = request.getAmount();
        this.bank = loanReply.getBank();
        this.interest = loanReply.getInterest();
        this.time = request.getTime();
    }

    public Loan() {
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "ssn=" + ssn +
                ", amount=" + amount +
                ", bank='" + bank + '\'' +
                ", interest=" + interest +
                ", time=" + time +
                '}';
    }
}
