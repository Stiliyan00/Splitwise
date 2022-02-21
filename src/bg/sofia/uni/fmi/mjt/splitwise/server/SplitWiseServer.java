package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.DefaultSplitWise;
import bg.sofia.uni.fmi.mjt.splitwise.SplitWise;
import bg.sofia.uni.fmi.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.command.DefaultCommand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Stiliyan Iliev
 * */
public class SplitWiseServer {
    private static final int SERVER_PORT = 7777;
    private static final int BUFFER_SIZE = 1024;
    private static final String SERVER_HOST = "localhost";

    private boolean isStarted = true;

    private final int port;
    private final ByteBuffer messageBuffer;

    private final SplitWise splitWise;
    private Command userCommands;

    private static final Path DATASET_PATH = Path.of("database.txt");

    public SplitWiseServer(int port) {
        this.port = port;
        this.messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.splitWise = new DefaultSplitWise(DATASET_PATH);
        this.userCommands = new DefaultCommand(this.splitWise);
    }

    public static void main(String[] args) {
        new SplitWiseServer(SERVER_PORT).start();
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, port));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (isStarted) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        messageBuffer.clear();
                        int r = socketChannel.read(messageBuffer);
                        if (r <= 0) {
                            System.out.println("Nothing to read, closing channel");
                            socketChannel.close();
                            continue;
                        }

                        handleKeyIsReadable(key, messageBuffer);
                    } else if (key.isAcceptable()) {

                        handleKeyIsAcceptable(selector, key);
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            this.stop();
            splitWise.storeUsersData();
            System.err.println("There is a problem with the server socket: " + e.getMessage());
            System.err.println(e);
        }

        System.out.println("Server stopped");
    }

    /**
     * Stop the server
     *
     * @throws IOException
     */
    public void stop() {
        isStarted = false;
    }

    private void handleKeyIsReadable(SelectionKey key, ByteBuffer buffer) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).trim();

        System.out.println("Message [" + message + "] received from client " + socketChannel.getRemoteAddress());

        String command = message.split(" ")[0];
        String arguments = message.substring(message.indexOf(" ") + 1);

        String response = null;

        switch (command) {
            case "signup" -> response = userCommands.signup(arguments.split(" "));

            case "login" -> response = userCommands.login(arguments.split(" "));

            case "get-status" -> response = userCommands.getStatus(arguments.split(" "));

            case "split" -> response = userCommands.split(arguments.split(" "));

            case "payed" -> response = userCommands.payed(arguments.split(" "));

            case "split-group" -> response = userCommands.splitGroup(arguments.split(","));

            case "create-group" -> response = userCommands.createGroup(arguments.split(" "));

            case "add-friend" -> response = userCommands.addFriend(arguments.split(" "));

            case "get-groups" -> response = userCommands.getGroups(arguments.split(" "));

            case "disconnect" -> {
                splitWise.storeUsersData();
                disconnect(key);
            }

            case "help" -> response = Command.help();

            case "payed-group-member" -> response = userCommands.payedGroupMember(arguments.split(" "));

            default -> response = "[ Unknown command ]";
        }

        if (response != null) {
            System.out.println("Sending response to client: " + response);
            response += System.lineSeparator();
            buffer.clear();
            buffer.put(response.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
        }
    }

    private void handleKeyIsAcceptable(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);

        System.out.println("Connection accepted from client " + accept.getRemoteAddress());
    }

    private void disconnect(SelectionKey key) throws IOException {
        key.channel().close();
        key.cancel();
    }

}
