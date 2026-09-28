package events;


import javafx.scene.Node;


public class NewTabEvent 
{
    private final Node tabOwner;

    public NewTabEvent(Node tabOwner)
    {
        this.tabOwner = tabOwner;
    }

    public Node getTabOwner() { return tabOwner; }
}
