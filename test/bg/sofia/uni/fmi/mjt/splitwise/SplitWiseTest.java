package bg.sofia.uni.fmi.mjt.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.user.StandardUser;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class SplitWiseTest {

    @TempDir
    Path tempDir;

    private SplitWise splitWise;

    private Path path1;
    private File file1;

    @BeforeEach
    void setUp() throws InvalidUsernameException,
            InvalidPasswordException, UsernameAlreadyExistsException {

        try {
            path1 = tempDir.resolve("tempDir");
            file1 = path1.toFile();
            file1.createNewFile();


            file1.setReadable(true);
            file1.setWritable(true);

        } catch (InvalidPathException | IOException ipe) {
            System.err.println(
                    "error creating temporary test file in " +
                            this.getClass().getSimpleName());
        }


        this.splitWise = new DefaultSplitWise(path1);

        this.splitWise.register("Stiliyan00", "password1");
        this.splitWise.register("Kristian00", "password2");
        this.splitWise.register("Aleksandra00", "password3");
        this.splitWise.register("Velina00", "password4");

    }

    @Test
    void testFindUserByUsernameWithInvalidUsernameValue() {
        assertThrows(IllegalArgumentException.class, () -> splitWise.findUserByUsername(null),
                "The value of the username cannot be null");

        assertThrows(IllegalArgumentException.class, () -> splitWise.findUserByUsername(""),
                "The value of the username cannot be an empty string");

        assertThrows(IllegalArgumentException.class, () -> splitWise.findUserByUsername("   "),
                "The value of the username cannot be a blank space");
    }

    @Test
    void testFindUserByUsernameWithNonExistingUser() {
        assertNull(splitWise.findUserByUsername("RussianBear"));
    }

    @Test
    void testFindUserByUsernameWithExistingUser() {
        User user = new StandardUser("Stiliyan00", "password1");
        assertEquals(user, splitWise.findUserByUsername("Stiliyan00"));
    }

    @Test
    void testSplitWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split(null, "any username", 100, "any reason"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("", "any username", 100, "any reason"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("   ", "any username", 100, "any reason"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("any username", null, 100, "any reason"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("any username", "", 100, "any reason"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("any username", " ", 100, "any reason"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("username1", "username2", -100, "any reason"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("username1", "username2", 100, null));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("username1", "username2", 100, ""));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.split("username1", "username2", 100, "  "));
    }

    @Test
    void testSplitWithNonExistingUsernames() {
        assertThrows(UserNotFoundException.class,
                () -> splitWise.split("username1", "Stiliyan00", 100, "reason1"));

        assertThrows(UserNotFoundException.class,
                () -> splitWise.split("Stiliyan00", "username1", 100, "reason1"));
    }

    @Test
    void testSplitWithValidArguments() throws UserNotFoundException, UsernameAlreadyExistsException {
        splitWise.addUserToFriendsList("Stiliyan00","Kristian00");
        splitWise.split("Stiliyan00", "Kristian00", 100, "bills");

        assertEquals(50.0,
                splitWise.findUserByUsername("Kristian00").amountOweFriend("Stiliyan00"));
    }

    @Test
    void testSplitByGroupWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup(null, 100,"group1","reason1"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("", 100,"group1","reason1"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup(" ", 100,"group1","reason1"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("Stiliyan00", -100,"group1","reason1"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("Stiliyan00", 100,null,"reason1"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("Stiliyan00", 100,"","reason1"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("Stiliyan00", 100," ","reason1"));


        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("Stiliyan00", 100,"group1",null));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("Stiliyan00", 100,"group1",""));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.splitByGroup("Stiliyan00", 100,"group1"," "));
    }

    @Test
    void testSplitByGroupWithNonExistingUsername() {
        assertThrows(UserNotFoundException.class,
                () -> splitWise.splitByGroup("username1", 100, "group1", "reason1"));
    }

    @Test
    void testSplitByGroupWithNonExistingGroupName() {
        assertThrows(GroupNotFoundException.class,
                () -> splitWise.splitByGroup("Stiliyan00", 100, "group1", "reason1"));
    }

    @Test
    void testSplitByGroupWithValidArguments() throws UserNotFoundException, UnableToCreateGroupException, GroupNotFoundException {
        splitWise.createGroup("Stiliyan00", "Sushi Gang",
                "Velina00", "Aleksandra00");
        splitWise.splitByGroup("Stiliyan00", 90.0, "Sushi Gang",
                "Yesterday we ate sushi");
        assertEquals(30.0, splitWise.findUserByUsername("Velina00").amountOweFriend("Stiliyan00"));
        assertEquals(30.0, splitWise.findUserByUsername("Aleksandra00").amountOweFriend("Stiliyan00"));
    }

    @Test
    void testCreateGroupWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup("", "groupName1", "Kristian00", "Dimitar00"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup(null, "groupName1", "Kristian00", "Dimitar00"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup("  ", "groupName1", "Kristian00", "Dimitar00"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup("Stiliyan00", null, "Kristian00", "Dimitar00"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup("Stiliyan00", "", "Kristian00", "Dimitar00"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup("Stiliyan00", "   ", "Kristian00", "Dimitar00"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup("Stiliyan00", "group name 1",null));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.createGroup("Stiliyan00", "group name 1", "Kristian00"));
    }

    @Test
    void testCreateGroupWithNonExistingUsernames() {
        assertThrows(UserNotFoundException.class,
                () -> splitWise.createGroup("username1", "groupName1", "Kristian00", "Dimitar00"));

        assertThrows(UserNotFoundException.class,
                () -> splitWise.createGroup("Stiliyan00", "groupName1", "username1", "Dimitar00"));

        assertThrows(UserNotFoundException.class,
                () -> splitWise.createGroup("Stiliyan00", "groupName1", "Kristian00", "Dimitar00"));

    }

    @Test
    void testCreateGroupWithExistingGroupName() throws UserNotFoundException, UnableToCreateGroupException {
        splitWise.createGroup("Stiliyan00", "Hotel", "Aleksandra00", "Velina00");
        assertThrows(UnableToCreateGroupException.class,
                () -> splitWise.createGroup("Stiliyan00", "Hotel", "Kristian00", "Velina00"));
    }

    @Test
    void testPayedWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payed(null, "Kristian00", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payed("", "Kristian00", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payed(" ", "Kristian00", 100));


        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payed("Kristian00",null, 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payed("Kristian00","", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payed("Kristian00","  ", 100));
    }

    @Test
    void testPayedWithNonExistingUsers() {
        assertThrows(UserNotFoundException.class,
                () -> splitWise.payed("username1", "Kristian00", 100));

        assertThrows(UserNotFoundException.class,
                () -> splitWise.payed("Stiliyan00", "username1", 1000));
    }

    @Test
    void testPayedWithValidArguments() throws UserNotFoundException, UsernameAlreadyExistsException {
        splitWise.addUserToFriendsList("Stiliyan00", "Kristian00");
        splitWise.split("Stiliyan00", "Kristian00", 100.0, "restaurant");
        splitWise.payed("Stiliyan00", "Kristian00", 40.0);

        assertEquals(-10.0, splitWise.findUserByUsername("Stiliyan00").amountOweFriend("Kristian00"));
        assertEquals(10.0, splitWise.findUserByUsername("Kristian00").amountOweFriend("Stiliyan00"));
    }

    @Test
    void testPayedFromGroupMemberWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember(null, "Kristian00", "group1", 120));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember("", "Kristian00", "group1", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember(" ", "Kristian00", "group1", 100));


        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember("Kristian00",null, "group1", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember("Kristian00","", "group1", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember("Kristian00","  ", "group1", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember("Kristian00","group1", null, 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember("Kristian00","group1", "", 100));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.payedFromGroupMember("Kristian00","group1", "  ", 100));
    }


    @Test
    void testPayedFromGroupMemberWithNonExistingUser() throws GroupNotFoundException, UserNotFoundException, UnableToCreateGroupException {
        splitWise.createGroup("Stiliyan00", "group1", "Kristian00", "Velina00");
        splitWise.splitByGroup("Stiliyan00", 120, "group1","restaurant");

        assertThrows(UserNotFoundException.class,
                () -> splitWise.payedFromGroupMember("usernam1", "group1", "Stiliyan00", 30));
        assertThrows(UserNotFoundException.class,
                () -> splitWise.payedFromGroupMember("Stiliyan00", "group1", "username1", 30));

        assertThrows(GroupNotFoundException.class,
                () -> splitWise.payedFromGroupMember("Stiliyan00", "group2", "Kristian00", 30));
    }

    @Test
    void testPayedFromGroupMemberWithValidArguments() throws GroupNotFoundException, UserNotFoundException, UnableToCreateGroupException {
        splitWise.createGroup("Stiliyan00", "group1", "Kristian00", "Velina00");
        splitWise.splitByGroup("Stiliyan00", 120, "group1","restaurant");

        splitWise.payedFromGroupMember("Stiliyan00", "group1", "Kristian00", 30);

        assertEquals(10, splitWise.findUserByUsername("Kristian00").amountOweFriend("Stiliyan00"));
    }


    @Test
    void testRegisterWithInvalidUsernameOrPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> splitWise.register(null, "password"));
        assertThrows(InvalidUsernameException.class,
                () -> splitWise.register("", "password"));
        assertThrows(InvalidUsernameException.class,
                () -> splitWise.register("user123", "password"));


        assertThrows(IllegalArgumentException.class,
                () -> splitWise.register("username", null));
        assertThrows(InvalidPasswordException.class,
                () -> splitWise.register("username", ""));
        assertThrows(InvalidPasswordException.class,
                () -> splitWise.register("username", "passwrd"));
    }

    @Test
    void testRegisterWithAlreadyExistingUsername() {
        assertThrows(UsernameAlreadyExistsException.class,
                () -> splitWise.register("Stiliyan00", "password1234"));
    }

    @Test
    void testRegisterWithValidNewUserData() throws InvalidUsernameException, InvalidPasswordException, UsernameAlreadyExistsException {
        splitWise.register("username1", "password123");
        assertNotNull(splitWise.findUserByUsername("username1"));
    }

    @Test
    void testAddUserToFriendsListWithInvalidUsernames() {
        assertThrows(IllegalArgumentException.class,
                () -> splitWise.addUserToFriendsList(null, "Kristian00"));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.addUserToFriendsList("", "Kristian00" ));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.addUserToFriendsList(" ", "Kristian00"));


        assertThrows(IllegalArgumentException.class,
                () -> splitWise.addUserToFriendsList("Kristian00",null));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.addUserToFriendsList("Kristian00",""));

        assertThrows(IllegalArgumentException.class,
                () -> splitWise.addUserToFriendsList("Kristian00","  "));

    }

    @Test
    void testAddUserToFriendsListWithNonExistingUsernames() throws UserNotFoundException, UsernameAlreadyExistsException {
        assertThrows(UserNotFoundException.class,
                () -> splitWise.addUserToFriendsList("username1", "Kristian00"));
        assertThrows(UserNotFoundException.class,
                () -> splitWise.addUserToFriendsList("Kristian00", "username"));

        splitWise.addUserToFriendsList("Stiliyan00", "Kristian00");
        assertThrows(UsernameAlreadyExistsException.class,
                () -> splitWise.addUserToFriendsList("Stiliyan00", "Kristian00"));
    }

    @Test
    void testAddUserToFriendsListWithValidArgumentsValue() throws UserNotFoundException, UsernameAlreadyExistsException {
        splitWise.addUserToFriendsList("Kristian00", "Stiliyan00");

        assertEquals(0.0, Math.abs(splitWise.findUserByUsername("Kristian00").amountOweFriend("Stiliyan00")));
    }
}