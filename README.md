# Splitwise
Course Project for Modern Java Technologies 2022 at FMI Sofia


## Brief Description:
A client-server application with functionality resembling that of [Splitwise](https://www.splitwise.com/) that accepts user commands, sends them to the server for processing, accepts its response, and provides it to the user in a readable format.

Splitwise aims to make it easier to share bills between friends and roommates and reduce "only I buy beer in this dorm" type arguments.

### Functionalities

- User registration with username and password; Registered users are stored in a file at the server - it serves as a database. On shutdown and restart, the server can load the already registered users into its memory.
        
        $ signup <username> <password>
       

- Login:
        
        $ login <username> <password>
              
- For the convenience of the user, a command is also provided to show him all available commands he can use at that particular moment:
        
        $ help
 
- A registered user can:
    - add already registered users to the Friend List based on their username. For example:
        ```bash
        $ add-friend <username>
        ```
    - creates a group consisting of several, already registered users:

        ```bash
        $ create-group <group_name> <username> <username> ... <username>
        ```
        Groups are created by a single user, and each group includes three or more users.

    - adds the amount paid by him to the liabilities of:
        - another user from his friend list:
        ```bash
        $ split <amount> <username> <reason_for_payment>
        ```
        - a group in which he participates:

        ```bash
        $ split-group <amount> <group_name> <reason_for_payment>
        ```

    - gets status - the amounts he owes his friends and in his groups and the amounts they owe him. For example:
        ```bash
        $ get-status
        Friends:
        * Pavel Petrov (pavel97): Owes you 10 LV

        Groups
        * 8thDecember
        - Pavel Petrov (pavel97): Owes you 25 LV
        - Hristo Hristov (ico_h): Owes you 25 LV
        - Harry Gerogiev (harryharry): You owe 5 LV
        ```
        Groups with "outstanding accounts" are visualized.


- A newly entered amount is divided equally between all group members or in half if shared with a Friend List user.

- When one user A owes money to another user B, the debt can only be "settled" by user B.
    ```bash
    $ payed <amount> <username>
    ```
    For example:
    ```bash
    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 10 LV
    * Hristo Hristov (ico_h): You owe 5 LV

    $ payed 5 pavel97
    Pavel Petrov (pavel97) payed you 5 LV.
    Current status: Owes you 5 LV

    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 5 LV
    * Hristo Hristov (ico_h): You owe 5 LV
    ```

- When user A owes user B an amount (e.g. $5), but adds another amount that he paid (e.g. $5) before returning it to B, then the amounts owed to both are recalculated (the amount owed to A will become $2.50, B still owes nothing but has $2.50 to collect).
    ```bash
    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 10 LV
    * Hristo Hristov (ico_h): You owe 5 LV

    $ split 5 ico_h limes and oranges
    Splitted 5 LV between you and Hristo Hristov.
    Current status: You owe 2.50 LV

    $ get-status
    Friends:
    * Pavel Petrov (pavel97): Owes you 5 LV
    * Hristo Hristov (ico_h): You owe 2.50 LV
    ```

- Each time a user logs in, they receive notifications if their friends have added amounts or "paid off" debts.
For example:
    ```bash
    $ login alex alexslongpassword
    Successful login!
    No notifications to show.
    ```
    или
    ```bash
    $ login alex alexslongpassword
    You successfully logged in!
    => Notifications: 
    ***************************
    Misho approved your payment 10 LV [Mixtape beers].
    
    Groups:
    * Roomates:
    You owe Gery 20 LV [Tanya Bday Present].

    * Family:
    You owe Alex 150 LV [Surprise trip for mom and dad]
    ***************************
    ```
    
   ## File Architecture:
    ```bash
           src
            └─ bg.sofia.uni.fmi.mjt.splitwise.
                ├─ client
                |     ├─ SplitWiseUser.java
                |     └─ UserRunnable.java
                |       
                ├─ command
                |     ├─ Command.java
                |     └─ DefaultCommand.java
                |
                ├─ server
                |     └─ SplitWiseServer.java
                |
                ├─ user
                |     ├─ exceptions
                |     |       ├─ GroupNotFoundException.java
                |     |       ├─ InvalidPasswordException.java
                |     |       ├─ InvalidUsernameException.java
                |     |       ├─ UnableToCreateGroupException.java
                |     |       ├─ UsernameAlreadyExistsException.java
                |     |       └─ UserNotFoundException.java
                |     |
                |     ├─ Group.java
                |     ├─ StandardPayment.java
                |     ├─ StandardUser.java
                |     └─ User.java
                |
                ├─ SplitWise.java
                └─ DefaultSplitWise.java
            test
            └─ bg.sofia.uni.fmi.mjt.splitwise
                ├─ user
                |    └─ UserTest.java
                |
                └─ SplitWiseTest.java
    ```

