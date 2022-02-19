package bg.sofia.uni.fmi.mjt.splitwise.server;

import java.io.BufferedReader;
import java.io.IOException;

public class UserRunnable implements Runnable {
    private final BufferedReader reader;

    public UserRunnable(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.err.println("Connection is closed, stop waiting for server messages due to: " + e.getMessage());
                System.err.println(e);
                break;
            }
        }
    }
}
