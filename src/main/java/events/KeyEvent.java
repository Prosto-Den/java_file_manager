package events;


import javafx.scene.input.KeyCode;


public class KeyEvent 
{
    private final KeyCode code;
    private final boolean pressed;

    public KeyEvent(KeyCode code, boolean isPressed)
    {
        this.code = code;
        pressed = isPressed;
    }

    public KeyCode getCode() { return code; }
    public boolean isPressed() { return pressed; }
}
