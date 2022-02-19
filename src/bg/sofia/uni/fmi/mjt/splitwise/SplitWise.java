package bg.sofia.uni.fmi.mjt.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.*;

/**
 * @author Stiliyan Iliev
 * */
public interface SplitWise {

    /**
     * @param username the user's username we want to find in our database of username
     * @return the user with this username
     * @throws IllegalArgumentException if {@code username} is null, empty or blank space
     */
    User findUserByUsername(String username);

    /**
     * Splits a particular bill between the user with username usernamePayed
     * and the user with username usernameHasToPay and saves the reason for this payment.
     *
     * @param usernamePayed    the username of the user who payed the particular bill
     * @param usernameHasToPay the username of the user who owes money
     * @param amount           the amount of the particular bill
     * @param reason           the reason of the bill
     * @throws UserNotFoundException    if the user with username {@code usernamePayed} or
     *                                  the user with username {@code usernameHasToPay} is not found
     *                                  in our database
     * @throws IllegalArgumentException if {@code usernamePayed} or if {@code usernameHasToPay}
     *                                  is null, empty string or blank space
     */
    void split(String usernamePayed, String usernameHasToPay, double amount, String reason)
            throws UserNotFoundException;

    /**
     * Splits the particular bill among the user with username usernamePayed and the users from
     * group named {@code groupName}.
     *
     * @param usernamePayed the username of the user who payed the particular bill
     * @param amount        the amount of the particular bill
     * @param groupName     the name of the group of users we want to split the bill with
     * @param reason        the reason for this bill
     * @throws IllegalArgumentException if {@code usernamePayed} or if {@code groupName}
     *                                  is null, empty string or blank space and
     *                                  if {@code amount} is a negative number
     * @throws UserNotFoundException    if the user with username {@code usernamePayed} is not found
     *                                  in our database
     * @throws GroupNotFoundException   if the group with name {@code groupName} is not found in our database
     */
    void splitByGroup(String usernamePayed, double amount, String groupName, String reason)
            throws GroupNotFoundException, UserNotFoundException;

    /**
     * Allows the user with username {@code usernamePayed} to create a group named {@code groupName}
     * of user with username {@code otherUsernames}.
     *
     * @param usernamePayed  the username of the user who wants to create the group
     * @param groupName      the name of the group of users we want to create
     * @param otherUsernames the username of the other members of group {@code groupName}
     *                       besides the user with username {@code usernamePayed}
     * @throws IllegalArgumentException     if {@code usernamePayed} or if {@code groupName}
     *                                      is null, empty string or blank space
     * @throws UnableToCreateGroupException if the {@code groupName} is already existing or is not a valid
     *                                      group name or
     *                                      if the {@code otherUsernames} contains either a not valid
     *                                      username, or a non-existing username
     */
    void createGroup(String usernamePayed, String groupName, String... otherUsernames)
            throws UnableToCreateGroupException, UserNotFoundException;

    /**
     * Allows the user with username {@code usernamePayed} to note that the user with
     * username {@code usernameHasToPay} has return {@code amount} he owes.
     *
     * @param usernamePayed    the username of the user who payed the particular bill
     * @param usernameHasToPay the username of the user who owes money and has return the particular
     *                         amount {@code amount}
     * @param amount           the amount of money the user with username {@code usernameHasToPay} has
     *                         return to the user with username {@code usernamePayed}
     * @throws IllegalArgumentException if {@code usernamePayed} or if {@code usernameHasToPay}
     *                                  is null, empty string or blank space
     * @throws UserNotFoundException    if the user with username {@code usernamePayed} or
     *                                  the user with username {@code usernameHasToPay} is not found
     *                                  in our database
     */
    void payed(String usernamePayed, String usernameHasToPay, double amount) throws UserNotFoundException;

    /**
     * Allows the user with username {@code usernamePayed} to note that the user with
     * username {@code usernameHasToPay} from the group named {@code groupName}
     * has return {@code amount} he owes.
     *
     * @param usernamePayed the username of the user who payed the particular bill
     * @param username      the username of the user who owes money and has return the particular
     *                      amount {@code amount}
     * @param amount        the amount of money the user with username {@code usernameHasToPay} has
     *                      return to the user with username {@code usernamePayed}
     * @param groupName     the name of the group from which the user with username {@code username}
     *                      owes money to the user with username {@code usernamePayed}
     * @throws IllegalArgumentException if {@code usernamePayed} or if {@code username} or if {@code groupName}
     *                                  is null, empty string or blank space
     * @throws UserNotFoundException    if the user with username {@code usernamePayed} or
     *                                  the user with username {@code username} is not found
     *                                  in our database
     * @throws GroupNotFoundException   if the group named {@code groupName} is not found in our database
     */
    void payedFromGroupMember(String usernamePayed, String groupName, String username, double amount)
            throws UserNotFoundException, GroupNotFoundException;

    /**
     * Allow a new user to make a registration in SplitWise with username {@code username} and
     * with password {@code passoword}.
     *
     * @param username the username which the new user has chosen
     * @param password the password which the new user has chosen
     * @throws IllegalArgumentException       if {@code username} or if {@code password} is null, empty string
     *                                        or blank space.
     * @throws InvalidUsernameException       if {@code username} is less than 8 characters
     * @throws InvalidPasswordException       if {@code password} is less than 8 characters
     * @throws UsernameAlreadyExistsException if there is another user with username {@code username}
     */
    void register(String username, String password) throws InvalidUsernameException,
            InvalidPasswordException, UsernameAlreadyExistsException;

    /**
     * Allows the user with username {@code username1} to add to his friends' list the user with
     * username {@code username2}.
     *
     * @param username1 the username of the user who want to add the user with username {@code username2}
     *                  to his friends list
     * @param username2 the username of the user which {@code username1} wants to add to his friends list
     * @throws IllegalArgumentException       if {@code username1} or if {@code username2} is null,empty string or
     *                                        bank space
     * @throws UserNotFoundException          if the user with {@code username1} or the username with username
     *                                        {@code username2} is not found in our database
     * @throws UsernameAlreadyExistsException if the user with username {@code username2} is already added
     *                                        to the user with username {@code username1} friends list
     */
    void addUserToFriendsList(String username1, String username2)
            throws UserNotFoundException, UsernameAlreadyExistsException;

    /**
     * Stores the current data in our database.
     *
     * @throws IllegalStateException if a problem occurred while writing to our database
     */
    void storeUsersData();
}
