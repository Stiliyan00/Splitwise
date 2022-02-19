package bg.sofia.uni.fmi.mjt.splitwise.user;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Stiliyan Iliev
 * @apiNote the purpose of this class is to make the process of containing and maintaining
 * of the friends list in class StandardUser much easier.
 * */
class StandardPayment {
    private transient static final double FIRST_ADDED_FRIEND_OWES_AMOUNT = 0.0;
    private final List<String> reasons;
    private double amount;

    public StandardPayment() {
        this.reasons = new LinkedList<>();
        this.amount = FIRST_ADDED_FRIEND_OWES_AMOUNT;
    }

    /**
     * Allows the user to easily change the owe amount without adding any new reason for this payment
     *
     * @return the updated StandardPayment
     * */
    public StandardPayment addPayment(double amount) {
        this.amount += amount;
        return this;
    }

    /**
     * Allows the user to easily change the owe amount and adding the reason for this change
     *
     * @return the updated StandardPayment
     * */
    public StandardPayment addPayment(double amount, String reason) {
        this.amount += amount;
        reasons.add(reason);
        return this;
    }

    /**
     * @return the current amount of the StandardPayment
     * */
    public double getAmount() {
        return amount;
    }

    /**
     * @return a list of all the reason for the StandardPayment and its changes
     * */
    public List<String> getReasons() {
        return reasons;
    }
}
