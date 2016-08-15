package counsil;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import wddman.UnsupportedOperatingSystemException;
import wddman.WDDMan;
import wddman.WDDManException;

/**
 * Represents window attributes
 * @author xdaxner
 */
class DisplayableWindow {

    /**
     * Height of the window
     */
    private int height;

    /**
     * Width of the window
     */
    private int width;

    /**
     * Position of the window
     */
    private Position position;

    /**
     * Role of the window in layout
     */
    private final String role;

    /**
     * Window title
     */
    private final String title;

    /**
     * Wddman instance
     */
    private final WDDMan wd;

    /**
     * Wddman window instance
     */
    private wddman.Window window;

    /**
     * Initializes arguments
     *
     * @param title title of new window
     * @param role role of window
     * @throws WDDManException
     * @throws UnsupportedOperatingSystemException
     */
    DisplayableWindow(WDDMan wddman, String title, String role) throws WDDManException, UnsupportedOperatingSystemException {
        this.wd = wddman;
        this.title = title;
        this.role = role;
        this.position = new Position();
    }

    /**
     * Applies DisplayableWindow parameters to actual windows
     *
     * @throws WDDManException
     */
    public void adjustWindow() throws WDDManException {
        getWindowInstance();
        if (window != null) {
            window.resize(position.x, position.y, width, height);
        }
    }

    /**
     * Checks if DisplayableWindow is associated with argument title
     *
     * @param title
     * @return true, if Displayable window contains wddman window
     */
    public Boolean contains(String title) {
        return this.title.equals(title);

    }
    
    /**
     * Gets current information about window from wddman
     */
    public final void loadCurrentInfo() {
        try {
            getWindowInstance();
            if (window != null) {
                position.x = window.getLeft();
                position.y = window.getTop();
                width = window.getWidth();
                height = window.getHeight();
            }
        } catch (WDDManException ex) {
            Logger.getLogger(DisplayableWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Closes wddman window instance
     * @throws WDDManException 
     */
    public void close() throws WDDManException {
        getWindowInstance();
        if (window != null) {
            window.close();
        }
    }
    
    public Position getPosition() {
        return position;
    }

    void setPosition(Position position) {
        this.position = position;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getRole() {
        return role;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DisplayableWindow other = (DisplayableWindow) obj;
        return Objects.equals(this.title, other.title);
    }

    private void getWindowInstance() throws WDDManException {
        window = wd.getWindowByTitle(title);
    }
}
