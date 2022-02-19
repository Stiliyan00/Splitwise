# Splitwise
Course Project for Modern Java Technologies 2022 at FMI Sofia


## Кратко описание:
Клиент-сървър приложение с функционалност, наподобяваща тази на [Splitwise](https://www.splitwise.com/), което приема потребителски команди, изпраща ги за обработка на сървъра, приема отговора му и го предоставя на потребителя в четим формат.

Splitwise цели улесняване на поделянето на сметки между приятели и съквартиранти и намаляване на споровете от тип "само аз купувам бира в това общежитие".

### Функционалности

- Регистрация на потребител с username и password; Регистрираните потребители се пазят във файл при сървъра - той служи като база от данни. При спиране и повторно пускане, сървърът може да зареди в паметта си вече регистрираните потребители.
        ```bash
        $ signup <username> <password>
        ```

- Login:
        ```bash
        $ login <username> <password>
        ```
      
- За удобството на потребителя е предоставена и команда, която да му показва всички налични команди, който може да използва в конкретния момент:
        ```bash
        $ help
        ``` 
 
- Регистриран потребител може да:
    - добавя вече регистрирани потребители във Friend List на база техния username. Например:
        ```bash
        $ add-friend <username>
        ```
    - създава група, състояща се от няколко, вече регистрирани, потребители:

        ```bash
        $ create-group <group_name> <username> <username> ... <username>
        ```
        Групите се създават от един потребител, като всяка група включва трима или повече потребители. Можете да си представяте, че "приятелските" отношения са група от двама човека.

    - добавя сума, платена от него, в задълженията на:
        - друг потребител от friend list-a му:
        ```bash
        $ split <amount> <username> <reason_for_payment>
        ```
        - група, в която участва:

        ```bash
        $ split-group <amount> <group_name> <reason_for_payment>
        ```

    - получава своя статус - сумите, които той дължи на приятелите си и в групите си и сумите, които дължат на него. Например:
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
        Визуализират се групите, при които има "неуредени сметки".


- Нововъведена сума се дели поравно между всички участници в групата или наполовина, ако се дели с потребител от Friend List-a.

- Когато един потребител А дължи пари на друг потребител B, задължението може да бъде "погасено" само от потребител B.
    ```bash
    $ payed <amount> <username>
    ```
    Например:
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

- Когато един потребител А дължи на потребител B сума (например 5$), но преди да ги върне на B добави друга сума, която той е платил (например 5$), тогава дължимите суми и на двамата се преизчисляват (дължимата сума на А ще стане 2.50$, B все още не дължи нищо, но има да взима 2.50$).
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

- При всяко влизане на потребителя в системата, той получава известия, ако негови приятели са добавяли суми или "погасявали" дългове.
Например:
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
