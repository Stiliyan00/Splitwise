package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.GroupNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UnableToCreateGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UsernameAlreadyExistsException;
import com.google.gson.Gson;

import java.util.Set;

public interface User {
    /**
     * Allows 1 line of our database to be parsed to a User object.
     */
    static User of(String line) {
        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("The value of argument line in static method of in" +
                    "class User is invalid!");
        }

        Gson gson = new Gson();

        return gson.fromJson(line, StandardUser.class);
    }

    /**
     * Allows the current user to add another user to his friends list.
     *
     * @param username the username of the user that should be added to the friends list
     * @throws IllegalArgumentException       if {@code username} is a null, empty string or blank space
     * @throws UsernameAlreadyExistsException if the user with username {@code username} is already
     *                                        added to the friends list
     */
    void addFriendToFriendsList(String username) throws UsernameAlreadyExistsException;

    /**
     * Allows the current user to split a bill with the user with username {@code username}
     *
     * @param username      the username of the user which has not payed
     * @param amount        the amount of the bill
     * @param paymentReason the reason for the payment
     * @throws IllegalArgumentException if {@code username} is null, empty string or blank space
     * @throws UserNotFoundException    if the user with username {@code username} is not found in
     *                                  our database
     */
    void split(double amount, String username, String paymentReason) throws UserNotFoundException;

    /**
     * Allows the current user to note that the user with username {@code username} has return him {@code amount}
     *
     * @param username the username of the user who has return the certain amount {@code amount}
     * @param amount   the amount of the bill
     * @throws IllegalArgumentException if {@code username} is null, empty string or blank space
     */
    void payed(String username, double amount) throws UserNotFoundException;

    /**
     * Allows the current user to create a group named {@code groupName} with the users with usernames
     * {@code usernames}.
     *
     * @param groupName the name of the group the current user wants to create
     * @param usernames the usernames of the other participants in the group named {@code groupName}
     * @throws UnableToCreateGroupException if a problem occurred while creating the group
     */
    void createGroup(String groupName, String... usernames) throws UnableToCreateGroupException;

    /**
     * Allows the current user to split a bill among the him and the other participant in the group
     * named {@code groupName}, if the group exists.
     *
     * @param groupName     tha name of the group
     * @param amount        the amount of the bill
     * @param paymentReason the reason of the payment
     * @throws IllegalArgumentException if {@code groupName} or if {@code paymentReason} is null or
     *                                  an empty string.
     * @throws GroupNotFoundException   if the group named {@code groupName} does not exist in our database
     */
    void splitByGroup(String groupName, double amount, String paymentReason) throws GroupNotFoundException;

    /**
     * @return a string representation of all the users from the current user's friend list
     * which owe him money
     */
    String getFriendsListToString();

    /**
     * Allows the current user to get a particular group named {@code groupName}
     */
    String getGroup(String groupName) throws GroupNotFoundException;

    /**
     * Allows the current user to note that a member in group {@code groupName} whose username is
     * {@code username} has return {@code amount}
     *
     * @param groupName the name of the group both the users participate in.
     * @param amount    the amount the second user owes the first one
     * @param username  the username of the user who owes money
     * @throws IllegalArgumentException if {@code groupName} is null or empty string
     * @throws UserNotFoundException    if the user with username {@code username} is not found in our database.
     * @throws GroupNotFoundException   if the group named {@code groupName} is not found in our database.
     */
    void payedFromGroupMember(String groupName, String username, double amount)
            throws UserNotFoundException, GroupNotFoundException;

    /**
     * @return the username of the current user
     */
    String getUsername();

    /**
     * @param groupName the name of the group we are searching for
     * @return a set of the usernames of all participant in this particular group named {@code groupName}
     * @throws IllegalArgumentException if {@code groupName} is null, empty string or blank space
     * @throws GroupNotFoundException   if the group named {@code groupName} is not found in our database
     */
    Set<String> getGroupMembersUsernames(String groupName) throws GroupNotFoundException;

    /**
     * @return true if the value of password1 equals the current user's password value
     */
    boolean isValidPassword(String password1);

    /**
     * @return a string representation of all the groups of the current user, which have
     * at least one participant who owes the current user money
     */
    String getAllUnfinishedGroups();

    /**
     * @return a string representation of all the users which the current user owes money to.
     */
    String getAllFriendIOweMoneyMessage();

    /**
     * Allows the current user to get the amount he owes to the user with username {@code username}
     *
     * @param username the username of the user which the current user owes money to
     * @return the amount the current user owes the user with username {@code username}
     * */
    double amountOweFriend(String username) throws UserNotFoundException;

    double getGroupMemberOweAmount(String groupName, String username)
            throws GroupNotFoundException, UserNotFoundException;
}
