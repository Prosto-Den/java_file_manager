package watchers;

import events.ClipboardEvent;
import events.EventBus;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.Clipboard;
import javafx.util.Duration;

public class ClipboardWatcher
{
    private final Timeline timeline;
    private boolean lastState = false;

    public ClipboardWatcher()
    {
        timeline = new Timeline(new KeyFrame(Duration.millis(300), event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            boolean hasFiles = clipboard.hasFiles();
            if (hasFiles != lastState)
            {
                lastState = hasFiles;
                EventBus.publish(new ClipboardEvent(hasFiles));
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void start()
    {
      timeline.play();
    }

    public void stop()
    {
        if (timeline != null)
            timeline.stop();
    }
}
