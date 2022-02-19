package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.GroupNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UnableToCreateGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UsernameAlreadyExistsException;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Stiliyan Iliev
 */
public class StandardUser implements User {

    private transient static final double FIRST_ADDED_FRIEND_OWES_AMOUNT = 0;
    private transient static final double SPLIT_BETWEEN_TWO_FRIEND = 2.0;

    @SerializedName("Username")
    private final String myUsername;

    @SerializedName("Password")
    private final String password;

    @SerializedName("Friends")
    private final Map<String, StandardPayment> friendsList;

    private final Set<Group> groups;

    public StandardUser(String myUsername, String password) {
        if (myUsername == null || myUsername.isEmpty() || password == null
                || password.isEmpty() || password.isBlank()) {
            throw new IllegalArgumentException("The vaule of argument myUsername in User constructor" +
                    " is invalid!");
        }

        this.myUsername = myUsername;
        this.password = password;

        this.friendsList = new HashMap<>();
        this.groups = new HashSet<>();
    }

    private Group findGroup(String groupName) {
        Group group = null;
        for (Group gr : this.groups) {
            if (gr.getGroupName().equals(groupName)) {
                group = gr;
                break;
            }
        }
        return group;
    }

    public void addFriendToFriendsList(String username) throws UsernameAlreadyExistsException {
        if (username == null || username.isEmpty() || username.isBlank()) {
            throw new IllegalArgumentException("Invalid argument value in method addFriendTOFriendsList!");
        }
        if (friendsList.containsKey(username)) {
            throw new UsernameAlreadyExistsException("The username value in method addFriendToFriendsList" +
                    " already exists!");
        }
        friendsList.put(username, new StandardPayment());
    }

    public void split(double amount, String username, String paymentReason) throws UserNotFoundException {
        if (username == null || username.isEmpty() || username.isBlank() ||
                paymentReason == null || paymentReason.isEmpty() || paymentReason.isBlank()) {
            throw new IllegalArgumentException("Invalid argument value in method split!");
        }

        if (!friendsList.containsKey(username)) {
            throw new UserNotFoundException("There is no user with this username " +
                    "in your friendsList!");
        } else {
            friendsList.replace(username,
                    friendsList.get(username).addPayment(friendsList.get(username).getAmount() +
                                    new BigDecimal(amount / SPLIT_BETWEEN_TWO_FRIEND)
                                            .setScale(2, RoundingMode.HALF_UP).doubleValue(),
                            paymentReason + "-" + amount + "BGN"));
        }
    }

    public void payed(String username, double amount) throws UserNotFoundException {

        if (username == null || username.isEmpty() || username.isBlank()) {
            throw new IllegalArgumentException("Illegal arument value in method played!");
        }

        if (!friendsList.containsKey(username)) {
            throw new UserNotFoundException("There is no user with this username: " + username +
                    ", in your friendsList!");
        } else {
            friendsList.replace(username,
                    friendsList.get(username).addPayment(-amount));
        }
    }

    public void createGroup(String groupName, String... usernames) throws UnableToCreateGroupException {
        if (groupName == null || groupName.isEmpty() || groupName.isBlank()) {
            throw new IllegalArgumentException("The group name cannot be null, empty string, " +
                    "blank space!");
        }

        long matchingName = groups.stream()
                .map(Group::getGroupName)
                .filter(groupName::equals)
                .count();

        if (matchingName > 0) {
            throw new UnableToCreateGroupException("There is already an existing group named: " + groupName);
        }

        this.groups.add(new Group(groupName, usernames));
    }

    public void splitByGroup(String groupName, double amount, String paymentReason) throws GroupNotFoundException {
        if (groupName == null || groupName.isEmpty() || paymentReason == null
                || paymentReason.isEmpty()) {
            throw new IllegalArgumentException("Invalid arguments value in method splitByGroup");
        }

        Group group = findGroup(groupName);
        if (group == null) {
            throw new GroupNotFoundException("There is no such group with this group name: " +
                    groupName);
        } else {
            group.splitByGroup(amount, paymentReason);
        }
    }

    public String getFriendsListToString() {

        StringBuilder friendsListString = new StringBuilder();

        for (Map.Entry<String, StandardPayment> friendsEntry : friendsList.entrySet()) {
            if (friendsEntry.getValue().getAmount() < 0.0) {
                friendsListString.append(friendsEntry.getKey()).append(" you owe ")
                        .append(-friendsEntry.getValue().getAmount()).append(" ")
                        .append(friendsEntry.getValue().getReasons())
                        .append(System.lineSeparator());
            } else if (friendsEntry.getValue().getAmount() > 0.0) {
                friendsListString.append(friendsEntry.getKey()).append(" owe you ")
                        .append(friendsEntry.getValue().getAmount()).append(" ")
                        .append(friendsEntry.getValue().getReasons())
                        .append(System.lineSeparator());
            }
        }

        return String.valueOf(friendsListString);
    }

    public String getGroup(String groupName) throws GroupNotFoundException {
        if (groupName == null || groupName.isEmpty()) {
            throw new IllegalArgumentException("The value of the group name is invalid!");
        }

        Group gr = findGroup(groupName);

        if (gr == null) {
            throw new GroupNotFoundException("There is no group with this group name");
        }

        return gr.getGroupOfUsers().toString();
    }

    public void payedFromGroupMember(String groupName, String username, double amount)
            throws UserNotFoundException, GroupNotFoundException {
        if (groupName == null || groupName.isEmpty() ||
                username == null || username.isEmpty()) {
            throw new IllegalArgumentException("The value of argument groupName in method " +
                    "payedFromGroupMember cannot be null or empty string!");
        }

        Group group1 = findGroup(groupName);

        if (group1 == null) {
            throw new GroupNotFoundException("There is no group by name: " + groupName);
        }
        group1.payed(username, amount);
    }

    public String getUsername() {
        return myUsername;
    }

    public Set<String> getGroupMembersUsernames(String groupName) throws GroupNotFoundException {
        if (groupName == null || groupName.isEmpty() || groupName.isBlank()) {
            throw new IllegalArgumentException("Invalid argument value in method " +
                    "getGroupMembersUsernames!");
        }

        Group gr = findGroup(groupName);
        if (gr == null) {
            throw new GroupNotFoundException("There is no such group with name: " + groupName);
        }

        return gr.getGroupOfUsers().keySet();
    }

    public boolean isValidPassword(String password1) {
        return password.equals(password1);
    }

    @Override
    public String getAllUnfinishedGroups() {

        StringBuilder result = new StringBuilder();
        for (Group gr : groups) {
            if (!gr.isFinished()) {
                result.append("Group: ").append(gr.getGroupName()).append(" ")
                        .append(gr.getGroupOfUsers().toString()).append(" ")
                        .append(gr.getReasons()).append(System.lineSeparator());
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String getAllFriendIOweMoneyMessage() {

        StringBuilder friendIOweMoney = new StringBuilder("***************************\n");

        for (Map.Entry<String, StandardPayment> fr : friendsList.entrySet()) {
            if (fr.getValue().getAmount() < 0.0) {
                friendIOweMoney.append("You owe ").append(fr.getKey()).append(" ")
                        .append(-fr.getValue().getAmount()).append("BGN")
                        .append(fr.getValue().getReasons()).append(System.lineSeparator());
            }
        }

        friendIOweMoney.append("***************************");

        return String.valueOf(friendIOweMoney);
    }

    @Override
    public double amountOweFriend(String username) throws UserNotFoundException {
        if (username == null || username.isEmpty() || username.isBlank()) {
            throw new IllegalArgumentException("The value of the argument in method amountOweFriend" +
                    "is invalid!");
        }

        if (!friendsList.containsKey(username)) {
            throw new UserNotFoundException("There is no user with username: " + username);
        }

        return -friendsList.get(username).getAmount();
    }

    @Override
    public double getGroupMemberOweAmount(String groupName, String username)
            throws GroupNotFoundException, UserNotFoundException {
        if (groupName == null || groupName.isEmpty() ||
                username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument value in method " +
                    "getGroupMemberOweAmount");
        }
        Group group = findGroup(groupName);

        if (group == null) {
            throw new GroupNotFoundException("There is no group with this name: " + groupName);
        }

        if (!group.getGroupOfUsers().containsKey(username)) {
            throw new UserNotFoundException("There is no user with this username is group: " +
                    groupName);
        }

        return group.getGroupOfUsers().get(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardUser that = (StandardUser) o;
        return Objects.equals(myUsername, that.myUsername) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myUsername, password);
    }
}
