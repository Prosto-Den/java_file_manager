package events;


import javafx.scene.input.KeyCode;


/**
 * Событие нажатия на клавишу. В целом для отслеживания нажатий на клавиши хватает и
 * встроенный в JavaFX средств, но для передачи данных между виджетами удобнее 
 * использовать этот
 * KeyEvent
 */
public class KeyEvent 
{
    private final KeyCode code;
    private final boolean pressed;

    public KeyEvent(KeyCode code, boolean isPressed)
    {
        this.code = code;
        pressed = isPressed;
    }

    /**
     * Получить код нажатой клавиши
     * @return код нажатой клавиши
     */
    public KeyCode getCode() { return code; }

    /**
     * Была ли клавиша нажата? (имеется в виду, клавишу только что нажали или уже отпустили)
     * @return true, если клавишап нажата, иначе false
     */
    public boolean isPressed() { return pressed; }
}
