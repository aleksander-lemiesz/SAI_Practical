package shared.model.bank;

/**
 *
 * This class stores all information about an request from a bank to offer
 * a loan to a specific client.
 */
public class BankRequest {

    private int amount = 0; // the amount to borrow
    private int time = 0; // the time-span of the loan in years

    private int credit = 0;
    private int history = 0;

    public BankRequest() {
    }

    public BankRequest(int amount, int time, int credit, int history) {
        this.amount = amount;
        this.time = time;
        this.credit = credit;
        this.history = history;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return " amount=" + amount + " time=" + time + " credit=" + credit+ " history=" + history;
    }

}
