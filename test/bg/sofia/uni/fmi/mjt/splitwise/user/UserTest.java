package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.GroupNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UnableToCreateGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.user.exceptions.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new StandardUser("Stiliyan00", "password1");
    }

    @Test
    void testOfWithValidInput() throws UserNotFoundException {
        String userString = "{\"Username\":\"Kristian00\",\"Password\":\"password3\",\"Friends\":{\"Stiliyan00\":{\"reasons\":[\"We bought OS\\u0027 tabbac--34.0\"],\"amount\":-17.0}},\"groups\":[]}";
        User user1 = User.of(userString);

        assertEquals(17, user1.amountOweFriend("Stiliyan00"));
        assertEquals("Kristian00", user1.getUsername());
        assertTrue(user1.isValidPassword("password3"));
    }

    @Test
    void testOfWithInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> User.of(null));
    }

    @Test
    void testStandardUserConstructor() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardUser(null,null));
    }

    @Test
    void testAddFriendToFriendsListWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> user.addFriendToFriendsList(null));

        assertThrows(IllegalArgumentException.class,
                () -> user.addFriendToFriendsList(""));

        assertThrows(IllegalArgumentException.class,
                () -> user.addFriendToFriendsList("  "));
    }

    @Test
    void testAddFriendToFriendsListWithAlreadyAddedUser() throws UsernameAlreadyExistsException {
        user.addFriendToFriendsList("Kristian00");

        assertThrows(UsernameAlreadyExistsException.class,
                () -> user.addFriendToFriendsList("Kristian00"));
    }

    @Test
    void testAddFriendToFriendsListWithValidArguments() throws UsernameAlreadyExistsException, UserNotFoundException {
        user.addFriendToFriendsList("Kristian00");

        assertEquals(-0.0, user.amountOweFriend("Kristian00"));
    }

    @Test
    void testSplitWithInvalidArgumentValue() {
        assertThrows(IllegalArgumentException.class,
                () -> user.split(100, null, "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> user.split(100, "", "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> user.split(100, " ", "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> user.split(100, "Kristian00", null));
        assertThrows(IllegalArgumentException.class,
                () -> user.split(100, "Kristian00", ""));
    }

    @Test
    void testSplitWithInvalidUserNotInFriendsList() {
        assertThrows(UserNotFoundException.class,
                () -> user.split(100, "username1", "reason1"));
    }

    @Test
    void testSplitWithValidArguments() throws UsernameAlreadyExistsException, UserNotFoundException {
        user.addFriendToFriendsList("Kristian00");
        user.split(100 ,"Kristian00", "reason1");

        assertEquals(-50.0, user.amountOweFriend("Kristian00"));
    }

    @Test
    void testPayedWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> user.payed(null, 100));
        assertThrows(IllegalArgumentException.class,
                () -> user.payed( "", 100));
        assertThrows(IllegalArgumentException.class,
                () -> user.payed( " ",100));
    }

    @Test
    void testPayedWithUserNotInFriendsList() {
        assertThrows(UserNotFoundException.class,
                () -> user.payed("Kristian00", 100));
    }

    @Test
    void testPayedWithValidInput() throws UserNotFoundException, UsernameAlreadyExistsException {
        user.addFriendToFriendsList("Kristian00");
        user.split(100, "Kristian00", "reason1");

        user.payed("Kristian00", 60);

        assertEquals(10.0, user.amountOweFriend("Kristian00"));
    }

    @Test
    void testCreateGroupWithInvalidArguments() throws UnableToCreateGroupException {
        assertThrows(IllegalArgumentException.class,
                () -> user.createGroup(null, "Kristian00", "Velina00"));
        assertThrows(IllegalArgumentException.class,
                () -> user.createGroup("", "Kristian00", "Velina00"));
        assertThrows(IllegalArgumentException.class,
                () -> user.createGroup(" ", "Kristian00", "Velina00"));

        user.createGroup("group1", "Kristian00", "Dimitar00");
        assertThrows(UnableToCreateGroupException.class,
                () -> user.createGroup("group1", "Kristian00", "Dimitar00"));
    }

    @Test
    void testCreateGroupWithValidArgumentValues() throws UnableToCreateGroupException, GroupNotFoundException {
        user.createGroup("group1", "Kristian00", "Dimitar00");

        Set<String> group = new HashSet<>();
        group.add("Kristian00");
        group.add("Dimitar00");
        assertEquals(group.size(),user.getGroupMembersUsernames("group1").size());
        assertTrue(group.containsAll(user.getGroupMembersUsernames("group1")));
    }

    @Test
    void testSplitByGroupWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> user.splitByGroup(null, 120, "reason1"));
        assertThrows(IllegalArgumentException.class,
                () -> user.splitByGroup("", 120, "reason1"));
        assertThrows(IllegalArgumentException.class,
                () -> user.splitByGroup("group1", 120, null));
        assertThrows(IllegalArgumentException.class,
                () -> user.splitByGroup("group1", 120, ""));

        assertThrows(GroupNotFoundException.class,
                () -> user.splitByGroup("group1", 120, "reason1"));
    }

    @Test
    void testSplitByGroupWithValidArguments() throws UnableToCreateGroupException, GroupNotFoundException, UserNotFoundException {
        user.createGroup("group1", "Kristian00", "Dimitar00");
        user.splitByGroup("group1", 120,"reason1");

        assertEquals(40.0, user.getGroupMemberOweAmount("group1", "Kristian00"));
    }

    @Test
    void getFriendsListToString() {
    }

    @Test
    void getGroupWithInvalidArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> user.getGroup(null));

        assertThrows(IllegalArgumentException.class,
                () -> user.getGroup(""));

        assertThrows(GroupNotFoundException.class,
                () -> user.getGroup("group1"));
    }

    @Test
    void testGetGroup() throws UnableToCreateGroupException, GroupNotFoundException {
        user.createGroup("group1", "Kristian00", "Dimitar00");
        assertEquals(user.getGroup("group1"), "{Dimitar00=0.0, Kristian00=0.0}");
    }

    @Test
    void testPayedFromGroupMemberWithInvalidArguments() throws UnableToCreateGroupException {
        assertThrows(IllegalArgumentException.class,
                () -> user.payedFromGroupMember(null, "username", 10));

        assertThrows(IllegalArgumentException.class,
                () -> user.payedFromGroupMember("", "username", 10));

        assertThrows(IllegalArgumentException.class,
                () -> user.payedFromGroupMember("groupName", null, 10));

        assertThrows(IllegalArgumentException.class,
                () -> user.payedFromGroupMember("groupName", "", 10));

        assertThrows(GroupNotFoundException.class,
                () -> user.payedFromGroupMember("groupName", "username", 10));

        user.createGroup("groupName", "Kristian00", "Dimitar00");

        assertThrows(UserNotFoundException.class,
                () -> user.payedFromGroupMember("groupName", "username", 10));
    }

    @Test
    void testPayedFromGroupMember() throws UnableToCreateGroupException, GroupNotFoundException, UserNotFoundException {
        user.createGroup("groupName", "Kristian00", "Dimitar00");
        user.splitByGroup("groupName", 120, "reason1");

        user.payedFromGroupMember("groupName", "Kristian00",30.0);

        assertEquals(10.0, user.getGroupMemberOweAmount("groupName", "Kristian00"));
    }

    @Test
    void testGetUsername() {
        assertEquals("Stiliyan00", user.getUsername());
    }

    @Test
    void testGetGroupMembersUsernamesWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> user.getGroupMembersUsernames(""));
        assertThrows(IllegalArgumentException.class,
                () -> user.getGroupMembersUsernames(null));


        assertThrows(GroupNotFoundException.class,
                () -> user.getGroupMembersUsernames("groupName"));
    }

    @Test
    void testGetGroupMembersUsernames() throws UnableToCreateGroupException, GroupNotFoundException {
        user.createGroup("groupName", "Kristian00", "Dimitar00");

        Set<String> groupMembers = new HashSet<>();
        groupMembers.add("Kristian00");
        groupMembers.add("Dimitar00");

        assertEquals(groupMembers.size(), user.getGroupMembersUsernames("groupName").size());
        assertTrue(groupMembers.containsAll(user.getGroupMembersUsernames("groupName")));
    }

    @Test
    void testIsValidPassword() {
        assertTrue(user.isValidPassword("password1"));
        assertFalse(user.isValidPassword("username1"));
    }

    @Test
    void testAmountOweFriend() throws UsernameAlreadyExistsException, UserNotFoundException {
        assertThrows(IllegalArgumentException.class,
                () -> user.amountOweFriend(null));
        assertThrows(IllegalArgumentException.class,
                () -> user.amountOweFriend(""));

        user.addFriendToFriendsList("Kristian00");
        user.split(100, "Kristian00", "reason1");

        assertEquals(-50.0, user.amountOweFriend("Kristian00"));
    }

    @Test
    void testGetGroupMemberOweAmount() throws UnableToCreateGroupException, UserNotFoundException, GroupNotFoundException {
        assertThrows(IllegalArgumentException.class,
                () -> user.getGroupMemberOweAmount(null, "username"));
        assertThrows(IllegalArgumentException.class,
                () -> user.getGroupMemberOweAmount("", "username"));
        assertThrows(IllegalArgumentException.class,
                () -> user.getGroupMemberOweAmount("groupName", null));
        assertThrows(IllegalArgumentException.class,
                () -> user.getGroupMemberOweAmount("groupName", ""));

        assertThrows(GroupNotFoundException.class,
                () -> user.getGroupMemberOweAmount("groupName", "username1"));
        user.createGroup("groupName", "Kristian00", "Dimitar00");
        assertThrows(UserNotFoundException.class,
                () -> user.getGroupMemberOweAmount("groupName", "username"));


        assertEquals(0.0, user.getGroupMemberOweAmount("groupName","Kristian00"));
    }

    @Test
    void testGetFriendsListToString() throws UsernameAlreadyExistsException, UserNotFoundException {
        user.addFriendToFriendsList("Kristian00");
        user.addFriendToFriendsList("Dimitar00");

        user.split(100, "Kristian00", "reason1");


        StringBuilder stringBuilder = new StringBuilder("Kristian00 owe you 50.0 [reason1-100.0BGN]");
        stringBuilder.append(System.lineSeparator());
        assertEquals(String.valueOf(stringBuilder), user.getFriendsListToString());
    }

    @Test
    void testGetFriendsListToString1() throws UsernameAlreadyExistsException, UserNotFoundException {
        user.addFriendToFriendsList("Kristian00");
        user.addFriendToFriendsList("Dimitar00");

        user.split(-100, "Kristian00", "reason1");


        StringBuilder stringBuilder = new StringBuilder("Kristian00 you owe 50.0 [reason1--100.0BGN]");
        stringBuilder.append(System.lineSeparator());
        assertEquals(String.valueOf(stringBuilder), user.getFriendsListToString());
    }

    @Test
    void testGetAllUnfinishedGroups() throws GroupNotFoundException, UnableToCreateGroupException {
        user.createGroup("group1", "Kristian00", "Dimitar00");
        user.splitByGroup("group1", 120, "reason1");

        StringBuilder res = new StringBuilder("Group: group1 {Dimitar00=40.0, Kristian00=40.0} [reason1-120.0BGN]");
        res.append(System.lineSeparator());

        assertEquals(String.valueOf(res), user.getAllUnfinishedGroups());
    }
}