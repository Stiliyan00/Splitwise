package bg.sofia.uni.fmi.mjt.splitwise.client;

import bg.sofia.uni.fmi.mjt.splitwise.command.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SplitWiseUser {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;

    private String username;

    public static void main(String[] args) {
        new SplitWiseUser().start();
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println("Connected to the server.");

            logging(reader, writer, scanner);

            new Thread(new UserRunnable(reader)).start();

            while (true) {
                System.out.print("=> ");
                String message = scanner.nextLine();
                if ("disconnect".equals(message)) {
                    break;
                } else if (message.split(" ")[0].equals("split")) {
                    writer.println(message.split(" ")[0] + " " + username + " "
                            + message.substring(message.indexOf(" ") + 1));
                } else if (message.split(" ")[0].equals("split-group")) {
                    writer.println(message.split(" ")[0] + " " + username + ","
                            + message.substring(message.indexOf(" ") + 1));
                } else {
                    writer.println(message + " " + username);
                }
            }

            System.out.println("[ Disconnected ]");
        } catch (IOException e) {
            System.err.println("An error occurred in the client I/O: " + e.getMessage());
            System.err.println(e);
        }
    }

    private void logging(BufferedReader reader, PrintWriter writer, Scanner scanner) {
        boolean loggedIn = false;

        try {
            while (!loggedIn) {
                System.out.print("=> ");
                String message = scanner.nextLine();
                String command = message.split(" ")[0];

                if ("signup".equals(command)) {
                    writer.println(message);
                    writer.flush();

                    String responseMessage = reader.readLine();

                    if (responseMessage.equals("successful registration")) {
                        loggedIn = true;
                        String[] info = message.split(" ");
                        username = info[1];
                        System.out.println("Your registration was successful!");
                    } else {
                        System.out.println(responseMessage + "\nPlease try again: ");
                    }

                } else if ("login".equals(command)) {

                    writer.println(message);

                    String responseMessage = null;
                    responseMessage = reader.readLine();


                    switch (responseMessage) {
                        case "Login was successful" -> {
                            String[] info = message.split(" ");
                            username = info[1];
                            loggedIn = true;
                            System.out.println("You successfully logged in!");
                        }
                        case "Invalid username" ->
                                System.out.println("There is no user with this username!\n" +
                                        "Please try again: ");
                        case "Invalid password" -> System.out.println("The password is incorrect!\nPlease try again: ");
                        default -> System.out.println("Invalid command!");
                    }
                } else if ("help".equals(command)) {
                    System.out.println(Command.helpLogging());
                } else {
                    System.out.println("You should first login!");
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred in the client I/O while logging: " + e.getMessage());
        }
    }

}
