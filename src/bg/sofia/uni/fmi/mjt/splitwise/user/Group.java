package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UsernameAlreadyExistsException;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Stiliyan Iliev
 */
class Group {
    private transient static final double FIRST_AMOUNT = 0.0;

    @SerializedName("Group")
    private final String groupName;
    @SerializedName("Users")
    private final Map<String, Double> groupOfUsers;
    private transient final int numberOfGroupMembers;
    private final List<String> reasons;

    public Group(String groupName, String... usernames) {
        this.reasons = new LinkedList<>();

        this.groupName = groupName;

        this.groupOfUsers = new HashMap<>();
        this.numberOfGroupMembers = usernames.length + 1;

        for (String username : usernames) {
            this.groupOfUsers.put(username, FIRST_AMOUNT);
        }
    }

    public void splitByGroup(double amount, String reason) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("The value of argument amount in method splitByGroup" +
                    " is invalid!");
        }
        double amountToPay = amount / numberOfGroupMembers;

        groupOfUsers.replaceAll((username, oldValue) -> oldValue + amountToPay);
        this.reasons.add(reason + "-" + amount + "BGN");
    }

    public void addUserToGroup(String username) throws UsernameAlreadyExistsException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("The value of argument username in method addUserToGroup" +
                    "is invalid!");
        }
        if (groupOfUsers.containsKey(username)) {
            throw new UsernameAlreadyExistsException("This user is already in this group!");
        } else {
            groupOfUsers.put(username, FIRST_AMOUNT);
        }
    }

    public void payed(String username, double amount) throws UserNotFoundException {
        if (username == null || username.isEmpty() || amount <= 0.0) {
            throw new IllegalArgumentException("The value of the arguments in method payed " +
                    "in class Group are invalid!");
        }

        if (!groupOfUsers.containsKey(username)) {
            throw new UserNotFoundException("There is no such user in this group!");
        } else {
            groupOfUsers.replace(username, groupOfUsers.get(username) - amount);
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public Map<String, Double> getGroupOfUsers() {
        return groupOfUsers;
    }

    public boolean isFinished() {
        for (Map.Entry<String, Double> gr : groupOfUsers.entrySet()) {
            if (gr.getValue() != 0.0) {
                return false;
            }
        }
        return true;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
