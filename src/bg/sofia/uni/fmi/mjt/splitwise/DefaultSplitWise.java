package bg.sofia.uni.fmi.mjt.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.user.StandardUser;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Stiliyan Iliev
 */
public class DefaultSplitWise implements SplitWise {

    private static final int MINIMAL_USERNAME_AND_PASSWORD_LENGTH = 8;
    private final Path datasetFileName;
    private final Set<User> userSet;

    public DefaultSplitWise(Path path) {

        try (Stream<String> stream = Files.lines(path)) {

            this.userSet = stream.map(User::of).collect(Collectors.toSet());

        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file");
        }
        this.datasetFileName = path;
    }

    public User findUserByUsername(String username) {
        if (username == null || username.isEmpty() || username.isBlank()) {
            throw new IllegalArgumentException("The value of the username cannot be " +
                    "null, empty string or blank space!");
        }
        User user = null;
        for (User usr : this.userSet) {
            if (usr.getUsername().equals(username)) {
                user = usr;
                break;
            }
        }
        return user;
    }

    public void split(String usernamePayed, String usernameHasToPay, double amount, String reason)
            throws UserNotFoundException {
        if (usernamePayed == null || usernamePayed.isEmpty() || usernamePayed.isBlank() ||
                usernameHasToPay == null || usernameHasToPay.isEmpty() || usernameHasToPay.isBlank() ||
                reason == null || reason.isEmpty() || reason.isBlank() || amount <= 0.0) {
            throw new IllegalArgumentException("Invalid argument value in method split in class DefaultSplitWise!");
        }

        User userPayed = findUserByUsername(usernamePayed);
        User userHasToPay = findUserByUsername(usernameHasToPay);

        if (userPayed == null) {
            throw new UserNotFoundException("There is no user with username: " + usernamePayed);
        } else if (userHasToPay == null) {
            throw new UserNotFoundException("There is no user with username: " + usernameHasToPay);
        } else {

            userPayed.split(amount, usernameHasToPay, reason);

            try {
                userHasToPay.split(-amount, usernamePayed, reason);
            } catch (UserNotFoundException e) {
                try {
                    userHasToPay.addFriendToFriendsList(usernamePayed);
                    userHasToPay.split(-amount, usernamePayed, reason);
                } catch (UsernameAlreadyExistsException usernameAlreadyExistsException) {
                    ///impossible case
                    usernameAlreadyExistsException.printStackTrace();
                }
            }
        }
    }

    public void splitByGroup(String usernamePayed, double amount, String groupName, String reason)
            throws GroupNotFoundException, UserNotFoundException {
        if (usernamePayed == null || usernamePayed.isEmpty() || usernamePayed.isBlank() ||
                groupName == null || groupName.isEmpty() || groupName.isBlank() ||
                reason == null || reason.isEmpty() || reason.isBlank() || amount <= 0.0) {
            throw new IllegalArgumentException("Invalid argument value in method split in class DefaultSplitWise!");
        }

        User userPayed = findUserByUsername(usernamePayed);
        if (userPayed == null) {
            throw new UserNotFoundException("There is no such user with username: " + usernamePayed);
        }

        userPayed.splitByGroup(groupName, amount, reason);

        Set<String> usersHaveToPay = userPayed.getGroupMembersUsernames(groupName);

        for (String username : usersHaveToPay) {

            User user = findUserByUsername(username);

            try {
                user.addFriendToFriendsList(usernamePayed);
            } catch (UsernameAlreadyExistsException ignored) {
                ///
            }
            user.split((2 * (-amount)) / (usersHaveToPay.size() + 1), usernamePayed, reason);
        }
    }

    public void createGroup(String usernamePayed, String groupName, String... otherUsernames)
            throws UnableToCreateGroupException, UserNotFoundException {
        if (usernamePayed == null || usernamePayed.isEmpty() || usernamePayed.isBlank() ||
                groupName == null || groupName.isEmpty() || groupName.isBlank() || otherUsernames == null ||
                otherUsernames.length < 2) {
            throw new IllegalArgumentException("Invalid argument value in method createGroup in class" +
                    "DefaultSplitWise!");
        }

        User user = findUserByUsername(usernamePayed);
        if (user == null) {
            throw new UserNotFoundException("There is no such user with this username: " + usernamePayed);
        }

        for (String usr : otherUsernames) {
            if (findUserByUsername(usr) == null) {
                throw new UserNotFoundException("There is no such user with this username: " + usr);
            }
        }
        user.createGroup(groupName, otherUsernames);
    }

    public void payed(String usernamePayed, String usernameHasToPay, double amount)
            throws UserNotFoundException {
        if (usernamePayed == null || usernamePayed.isEmpty() || usernamePayed.isBlank() ||
                usernameHasToPay == null || usernameHasToPay.isEmpty() || usernameHasToPay.isBlank()) {
            throw new IllegalArgumentException("Invalid argument value in method payed in class " +
                    "DefaultSplitWise!");
        }

        User user = findUserByUsername(usernamePayed);

        if (user == null) {
            throw new UserNotFoundException("There is no user with this username: " + usernamePayed);
        }

        user.payed(usernameHasToPay, amount);

        User userHasToPay = findUserByUsername(usernameHasToPay);
        userHasToPay.payed(usernamePayed, -amount);
    }

    public void payedFromGroupMember(String usernamePayed, String groupName, String username, double amount)
            throws UserNotFoundException, GroupNotFoundException {
        if (usernamePayed == null || usernamePayed.isEmpty() || usernamePayed.isBlank() ||
                username == null || username.isEmpty() || username.isBlank() ||
                groupName == null || groupName.isEmpty() || groupName.isBlank()) {
            throw new IllegalArgumentException("Invalid argument value in method payed in class " +
                    "DefaultSplitWise!");
        }

        User user = findUserByUsername(usernamePayed);
        User userHasToPay = findUserByUsername(username);

        if (user == null) {
            throw new UserNotFoundException("There is no user with this username: " + usernamePayed);
        }
        user.payedFromGroupMember(groupName, username, amount);
        userHasToPay.payed(usernamePayed, -amount);

    }

    public void register(String username, String password)
            throws InvalidUsernameException, InvalidPasswordException, UsernameAlreadyExistsException {
        if (username == null) {
            throw new IllegalArgumentException("The value of the username cannot be null");
        } else if (username.length() < MINIMAL_USERNAME_AND_PASSWORD_LENGTH) {
            throw new InvalidUsernameException("The username should be at least 8 characters!");
        } else if (password == null) {
            throw new IllegalArgumentException("The value of the password cannot be null!");
        } else if (password.length() < MINIMAL_USERNAME_AND_PASSWORD_LENGTH) {
            throw new InvalidPasswordException("The password should be at least 8 characters!");
        }

        User user = findUserByUsername(username);

        if (user != null) {
            throw new UsernameAlreadyExistsException("There is already a user with this username!");
        }

        userSet.add(new StandardUser(username, password));

    }

    public void addUserToFriendsList(String username1, String username2)
            throws UserNotFoundException, UsernameAlreadyExistsException {
        if (username1 == null || username1.isEmpty() || username1.isBlank()) {
            throw new IllegalArgumentException("Invalid argument value!");
        }

        User user = findUserByUsername(username1);

        User userFriend = findUserByUsername(username2);

        if (user == null) {
            throw new UserNotFoundException("There is no user with this username: " + username1);
        } else if (userFriend == null) {
            throw new UserNotFoundException("There is no user with this username: " + username2);
        }

        user.addFriendToFriendsList(username2);
    }

    public void storeUsersData() {
        Gson gson = new Gson();

        try (var bufferedWriter = Files.newBufferedWriter(datasetFileName)) {
            for (User user : userSet) {
                bufferedWriter.write(gson.toJson(user));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file");
        }
    }
}
