package shared.model.broker;

public class BankInterestRequest {
    private int creditScore;
    private int history;

    public BankInterestRequest() {
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return "BankInterestRequest{" +
                "creditScore=" + creditScore +
                ", history=" + history +
                '}';
    }
}
