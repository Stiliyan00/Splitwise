package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.SplitWise;
import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Stiliyan Iliev
 * */
public class DefaultCommand implements Command {

    private final SplitWise splitWise;

    public DefaultCommand(SplitWise splitWise) {
        this.splitWise = splitWise;
    }

    public static final int NUMBER_OF_ARGUMENTS_3 = 3;
    public static final int NUMBER_OF_ARGUMENTS_4 = 4;

    @Override
    public String signup(String... args) {
        if (args.length != 2) {
            return "[ Unknown command ]";
        } else {
            try {
                splitWise.register(args[0], args[1]);
                return "successful registration";
            } catch (InvalidUsernameException e) {
                return "invalid username";
            } catch (InvalidPasswordException e) {
                return "invalid password";
            } catch (UsernameAlreadyExistsException e) {
                return "username already exists";
            }
        }
    }

    @Override
    public String login(String... args1) {
        if (args1.length != 2) {
            return "[ Unknown command ]";
        } else {

            User user = splitWise.findUserByUsername(args1[0]);

            if (user == null) {
                return "Invalid username";
            } else if (user.isValidPassword(args1[1])) {
                return "Login was successful" + System.lineSeparator() +
                        "Notifications: " + System.lineSeparator() +
                        user.getAllFriendIOweMoneyMessage();
            } else {
                return "Invalid password";
            }
        }
    }

    @Override
    public String getStatus(String... args) {
        if (args.length != 1) {
            return "[ Unknown command ]";
        }
        User user = splitWise.findUserByUsername(args[args.length - 1]);

        String status = "Friends list:\n" + user.getFriendsListToString() +
                user.getAllUnfinishedGroups();
        return status;
    }

    @Override
    public String split(String... args) {
        if (args.length < NUMBER_OF_ARGUMENTS_4) {
            return "[ Invalid number of arguments in command split ]";
        }

        final int numberOfArgumentsToSkip = 3;

        String reason = Arrays.stream(args).sequential()
                .skip(numberOfArgumentsToSkip)
                .collect(Collectors.joining(" "));

        try {
            splitWise.split(args[0], args[1], Double.parseDouble(args[2]), reason);
        } catch (UserNotFoundException e) {
            return e.getMessage();
        }

        return "You successfully split the bill!";
    }

    @Override
    public String payed(String... args) {
        if (args.length != NUMBER_OF_ARGUMENTS_3) {
            return "[ Invalid number of arguments in command payed ]";
        }

        try {
            splitWise.payed(args[2], args[0], Double.parseDouble(args[1]));
        } catch (UserNotFoundException e) {
            return e.getMessage();
        }

        return "You successfully noted the payment of amount: " + args[1] + " of user: " + args[0];
    }

    @Override
    public String splitGroup(String... args) {

        if (args.length != NUMBER_OF_ARGUMENTS_4) {
            return "[ Invalid number of arguments in command split-group ]";
        }

        try {
            final int userWhoPayIndex = 0;
            final int groupNameIndex = 1;
            final int billAmountIndex = 2;
            final int reasonIndex = 3;

            this.splitWise.splitByGroup(args[userWhoPayIndex], Double.parseDouble(args[billAmountIndex]),
                    args[groupNameIndex], args[reasonIndex]);
        } catch (GroupNotFoundException | UserNotFoundException e) {
            return e.getMessage();
        }

        return "You successfully split the amount: " + args[2] + " with the members of " +
                "group: " + args[1];
    }

    @Override
    public String createGroup(String... args) {
        if (args.length < NUMBER_OF_ARGUMENTS_4) {
            return "[ Invalid number of arguments in command create-group ]";
        }

        String[] groupMembers = args[args.length - 2].split(",");

        String groupName = Arrays.stream(args).sequential()
                .limit(args.length - 2)
                .collect(Collectors.joining(" "));

        try {
            splitWise.createGroup(args[args.length - 1], groupName, groupMembers);
        } catch (UserNotFoundException | UnableToCreateGroupException e) {
            return e.getMessage();
        }

        return "You successfully created the group: " + groupName;
    }

    @Override
    public String addFriend(String... args) {
        if (args.length != 2) {
            return "[ Invalid number of arguments in command add-friend ]";
        } else {
            try {
                splitWise.addUserToFriendsList(args[1], args[0]);
                return "You successfully added user: " + args[0] + " to your friends list";
            } catch (UserNotFoundException | UsernameAlreadyExistsException e) {
                return e.getMessage();
            }
        }

    }

    @Override
    public String payedGroupMember(String... args) {

        if (args.length < NUMBER_OF_ARGUMENTS_4) {
            return "[ Invalid number of arguments in command payed-group-member ]";
        }

        final int last3Arguments = 3;

        String groupName = Arrays.stream(args).sequential()
                .limit(args.length - last3Arguments)
                .collect(Collectors.joining(" "));

        try {
            final int usernameIndex = args.length - 3;

            splitWise.payedFromGroupMember(args[args.length - 1], groupName,
                    args[usernameIndex], Double.parseDouble(args[args.length - 2]));
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return e.getMessage();
        }

        return "You successfully noted the payment of: " + args[2] + " from user: " + args[1]
                + " in group: " + args[0];

    }

    @Override
    public String getGroups(String... args) {
        if (args.length != 1) {
            return "Unknown command";
        }
        User user = splitWise.findUserByUsername(args[args.length - 1]);

        return user.getAllUnfinishedGroups();
    }


}
