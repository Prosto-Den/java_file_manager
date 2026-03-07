package monitors;

import events.ClipboardEvent;
import events.EventBus;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.Clipboard;
import javafx.util.Duration;

public class ClipboardMonitor
{
    public static void start()
    {
        Timeline checker = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            boolean hasFiles = clipboard.hasFiles();
            EventBus.publish(new ClipboardEvent(hasFiles));
        }));

        checker.setCycleCount(Timeline.INDEFINITE);
        checker.play();
    }
}
