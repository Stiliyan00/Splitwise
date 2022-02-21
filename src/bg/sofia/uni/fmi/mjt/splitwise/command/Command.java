package bg.sofia.uni.fmi.mjt.splitwise.command;

/**
 * @author Stiliyan Iliev
 * Note: the username and the password of every user should be one word
 */
public interface Command {
    /**
     * @return the allowed commands to a certain user before logging in the app.
     */
    static String helpLogging() {
        return """
                you can enter commands:
                disconnect
                signup username password
                login username password""";
    }

    /**
     * @return the allowed command to a certain user who has already logged in the app
     */
    static String help() {
        return """
                you can enter commands:
                disconnect
                get-status
                add-friend friendUsername
                split username amount reason
                payed username amount
                create-group groupName username1,username2,username3,...
                payed-group-member groupName username amount
                split-group groupName,amount,reason
                get-groups""";
    }

    /**
     * Allows a certain user to signup to our SplitWise app.
     *
     * @param args An array of argument which contains 2 elements: the username and the password
     *             which the user has chosen to sing up with.
     * @return [Unknown command] if the number of argument is not 2 or
     * "successful registration" if the singing up is successful or
     * "invalid username" if the chosen username is less than 8 characters or
     * "invalid password" if the chosen password is less than 8 characters or
     * "username already exists" if there is another user with the chosen username
     */
    String signup(String... args);

    /**
     * @param args An array of argument which contains 2 elements: the username and the password
     *             which the user has chosen to log in with.
     * @return [Unknown command] if the number of argument is not 2 or
     * "Invalid username" if there is no user with the chosen username or
     * an appropriate message for the successful logging in
     */
    String login(String... args);

    /**
     * @param args An array of argument which contains 1 element: the username of the user
     *             who want to see his current status
     * @return the current status of the particular user
     * */
    String getStatus(String... args);

    /**
     * @param args An array of argument which contains 4 elements: the username of the user how want to
     *             split a certain bill, the username of the other user who has to pay the bill,
     *             the amount of the bill and at least 1 word for the reason of payment
     *
     * @return "[ Invalid number of arguments in command split ]" if the number of argument is less than 4
     * "You successfully split the bill!" if the splitting of the bill was successful
     * */
    String split(String... args);

    /**
     * @param args An array of argument which contains 3 elements: the username of the user who has payed
     *             the bill, the username of the user who has return the certain amount of money,
     *             the amount the second user has returned
     * @return "[ Invalid number of arguments in command payed ]" if the number of argument is not 3 or
     * an appropriate message for the state of the payment
     * */
    String payed(String... args);

    /**
     * @param args an array of argument which contains 4 elements: the username of the user
     *             who has payed the bill, the name of the group the certain user wants to split
     *             the bill with, the amount of the bill, the reason of the payment
     *
     * @return "[ Invalid number of arguments in command split-group ]" if the number of arguments
     * is not 4 or an appropriate message for the state of the splitting the bill
     * */
    String splitGroup(String... args);

    /**
     * @param args an array of argument which contains at least 4 elements: the username of the user
     *             who wants to create the group, the name of the group and at least 2 other username
     *             of users who will participate in the group.
     * @return an appropriate message for the state of the creation of the group
     * */
    String createGroup(String... args);

    /**
     * @param args an array of argument which contains 2 elements: the username of the user who
     *             want to add a user to his friends list, the username of the user which should be
     *             add to the friends list
     * @return an appropriate message for the state of adding a user to friends list
     * */
    String addFriend(String... args);

    /***
     * @param args an array of argument which contains at least 4 elements: the username of the user
     *             who want to note that a certain member of a group has payed his debt
     *
     * @return an appropriate message for the state of notting a payment from a particular
     * member of a certain group
     */
    String payedGroupMember(String... args);

    /**
     * @param args n array of argument which contains 1 element: the username of the user who wants
     *             to get a string of all the groups in which at least one user has not payed him
     * @return an appropriate message for the state of the process
     * */
    String getGroups(String... args);
}
