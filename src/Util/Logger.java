package Util;

import java.awt.*;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple on-screen message logger with fixed capacity.
 * Stores messages in a FIFO deque and provides methods
 * to log new messages and retrieve the current log.
 */
public class Logger {

    private static final int MAX_MESSAGES = 8;
    private static final long DISPLAY_TIME_MS = 3000; // 3 seconds display duration
    private static final Deque<LogEntry> messages = new LinkedList<>();

    private Logger() { /* Prevent instantiation */ }

    /**
     * Represents a log entry with message text and creation timestamp.
     */
    private static class LogEntry {
        final String msg;
        final long timestamp;

        LogEntry(String msg) {
            this.msg = msg;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Adds a new message to the log. Cleans up expired entries and enforces capacity.
     * @param msg the message to log
     */
    public static void log(String msg) {
        cleanupExpired();
        if (messages.size() >= MAX_MESSAGES) {
            messages.removeFirst();
        }
        messages.addLast(new LogEntry(msg));
    }

    /**
     * Removes messages that have exceeded DISPLAY_TIME_MS.
     */
    private static void cleanupExpired() {
        long now = System.currentTimeMillis();
        while (!messages.isEmpty() && now - messages.getFirst().timestamp > DISPLAY_TIME_MS) {
            messages.removeFirst();
        }
    }

    /**
     * Renders the log messages onto the given Graphics context.
     * To be called in GamePanel.paintComponent().
     * @param g the Graphics context
     */
    public static void render(Graphics g) {
        cleanupExpired();
        List<LogEntry> snapshot = List.copyOf(messages);
        if (snapshot.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Consolas", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();

        int x = 10;
        int y = g2.getClipBounds().height - 10;

        // Semi-transparent background box
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(
                x - 5,
                y - lineHeight * snapshot.size() - 5,
                g2.getClipBounds().width / 2,
                lineHeight * snapshot.size() + 10
        );

        // Draw each message, newest at bottom
        g2.setColor(Color.WHITE);
        for (int i = snapshot.size() - 1; i >= 0; i--) {
            g2.drawString(snapshot.get(i).msg, x, y);
            y -= lineHeight;
        }
    }
}
