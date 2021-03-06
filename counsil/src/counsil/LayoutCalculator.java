package counsil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that calculates layout
 *
 * @author Dax
 */
public class LayoutCalculator {

    /**
     * JSON configure file object
     */
    private final JSONObject input;

    /**
     * Role of current Counsil session
     */
    private final String role;

    /**
     * current menu position
     */
    private final Position menuPosition;

    /**
     * Creates layout calculator
     *
     * @param myRole role to create correct layout
     * @param layoutFile
     * @throws java.io.FileNotFoundException
     * @throws org.json.JSONException
     */
    public LayoutCalculator(String myRole, File layoutFile) throws FileNotFoundException, JSONException {
        Scanner scanner = new Scanner(layoutFile);
        String entireFileText = scanner.useDelimiter("\\A").next();
        input = new JSONObject(entireFileText);
        role = myRole;

        menuPosition = new Position();
        menuPosition.x = input.getJSONObject("startingMenu").getInt("x");
        menuPosition.y = input.getJSONObject("startingMenu").getInt("y");
    }

    /**
     * Gets role from configure file
     *
     * @return role of current user
     * @throws JSONException
     */
    public String getMenuRole() throws JSONException {
        return role;
    }

    /**
     * recalculate new layout from JSON layout file and array of nodes or
     * something with specify role return layout with position nad id|name
     *
     * it work with fields with ratio < 1, but result may not be as expected,
     * because it prefer spliting to rows not columbs, if needed can be change
     * in future @param windows
     */
    public void recalculate(List<DisplayableWindow> windows) {
        if (input == null) {
            return;
        }
        Map<String, List<DisplayableWindow>> numRoles;
        numRoles = new HashMap();
        //-----------------
        JSONArray layouts = null;
        try {//load layout
            layouts = input.getJSONArray("layouts");
        } catch (JSONException ex) {
            Logger.getLogger(LayoutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        //-----------------
        Vector<String> roles = new Vector<>();
        roles.add("presentation");
        roles.add("interpreter");
        roles.add("teacher");
        roles.add("student");

        for (int i = 0; i < roles.size(); i++) {
            numRoles.put(roles.get(i), new ArrayList<>());
        }

        //distribute windows by their role
        for (DisplayableWindow win : windows) {
            if (numRoles.containsKey(win.getRole())) {
                numRoles.get(win.getRole()).add(win);
            }
        }

        //choose layout
        JSONObject layout = getCorrectLayout(layouts, numRoles);
        JSONObject windowLayout = null;
        JSONObject menuLayout = null;
        try {
            windowLayout = layout.getJSONObject("windows");
            menuLayout = layout.getJSONObject("menu");
        } catch (JSONException ex) {
            Logger.getLogger(LayoutCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (windowLayout == null || menuLayout == null) {//something wrong with layout, don't move windows
            return;
        }

        //calculate windows position, do it for ecery role
        for (int i = 0; i < roles.size(); i++) {
            JSONArray roleWindowsConfig;
            int windowWidth = 1;
            int windowHeight = 1;
            int windowX = 0;
            int windowY = 0;
            List<DisplayableWindow> winList;
            try {
                if (windowLayout.has(roles.get(i))) {
                    roleWindowsConfig = windowLayout.getJSONArray(roles.get(i));
                } else {
                    //missing array of windows position for this role, go for next role
                    continue;
                }
            } catch (JSONException ex) {
                Logger.getLogger(LayoutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            winList = numRoles.get(roles.get(i));
            for (int j = 0; j < winList.size(); j++) {
                DisplayableWindow win = winList.get(j);
                if (j < roleWindowsConfig.length()) {
                    JSONObject windowConfig;
                    try {
                        windowConfig = roleWindowsConfig.getJSONObject(j);
                        windowWidth = windowConfig.getInt("width");
                        windowHeight = windowConfig.getInt("height");
                        windowX = windowConfig.getInt("x");
                        windowY = windowConfig.getInt("y");
                    } catch (JSONException ex) {
                        Logger.getLogger(LayoutManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    win.setHeight(windowHeight);
                    win.setWidth(windowWidth);
                    win.setPosition(new Position(windowX, windowY));
                }
            }
        }

        //set menu position
        try {
            menuPosition.x = menuLayout.getInt("x");
            menuPosition.y = menuLayout.getInt("y");
        } catch (JSONException ex) {
            Logger.getLogger(LayoutCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Gets menu position from configure file
     *
     * @return menu position
     */
    public Position getMenuPostion() {

        return new Position(menuPosition.x, menuPosition.y);
    }

    /**
     *
     * @param numRoles calculated map of current windows divided to roles
     * @return choose correct layout based on conditions
     */
    private JSONObject getCorrectLayout(JSONArray layouts, Map<String, List<DisplayableWindow>> numRoles) {
        JSONObject ret = null;
        for (int i = 0; i < layouts.length(); i++) {
            JSONObject layout;
            JSONArray conditions;
            boolean correct = true;
            try {
                layout = layouts.getJSONObject(i);
                conditions = layout.getJSONArray("conditions");
                for (int j = 0; j < conditions.length(); j++) {
                    JSONObject condition = conditions.getJSONObject(j);
                    correct = correct && checkConndition(condition, numRoles);
                }
                if (correct) {
                    //found first layout have fullfiled conditions for this number of windows in each role
                    ret = layout;
                    System.out.println("layout no. " + i);
                    break;
                }
            } catch (JSONException ex) {
                Logger.getLogger(LayoutCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (ret == null) {    //if none layout is possible choose first if exist
            if (layouts.length() >= 1) {
                System.out.println("using first layout, none is correct");
                try {
                    ret = layouts.getJSONObject(0);
                } catch (JSONException ex) {
                    Logger.getLogger(LayoutCalculator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return ret;
    }

    private boolean checkConndition(JSONObject condition, Map<String, List<DisplayableWindow>> numRoles) throws JSONException {
        if (condition.has("role")) {
            String conditionRole = condition.getString("role");
            if (condition.has("count")) {
                int count = condition.getInt("count");
                return numRoles.get(conditionRole).size() == count;
            }
            if (condition.has("less")) {
                int less = condition.getInt("less");
                return numRoles.get(conditionRole).size() < less;
            }
            if (condition.has("more")) {
                int more = condition.getInt("more");
                return numRoles.get(conditionRole).size() > more;
            }
        }
        if (condition.has("my role")) {
            String myrole = condition.getString("my role");
            return myrole.equalsIgnoreCase(role);
        }
        //default value is true (incorrectly writen condition is ignored)
        return true;
    }

    /**
     * Saves current layout to file
     *
     * @param fileName name of the new file
     * @param windows current windows
     */
    public void createAndSaveLayoutFile(String fileName, List<DisplayableWindow> windows) {

        File file = new File(fileName);
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(file));

            // add header
            writer.append(String.format("{%n"
                    + "\"layouts\" : [%n"));

            // add layout
            writeOneLayout(writer, windows);

            // add starting menu location
            writer.append(String.format("],%n"
                    + "\"startingMenu\" : {%n"
                    + "\"x\" : \"" + menuPosition.x + "\",%n"
                    + " \"y\" : \"" + menuPosition.y + "\"%n"
                    + "}%n}%n"));

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(LayoutCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Writes one layout to file
     * @param writer file writer
     * @param windows currently available windows
     * @throws IOException 
     */
    private void writeOneLayout(BufferedWriter writer, List<DisplayableWindow> windows) throws IOException {

        writeHeaderElement(writer);
        boolean firstSkipped = false;
        
        for (DisplayableWindow window : windows) {
            String currentRole = window.getRole().toLowerCase();
            if (!"student".equals(currentRole)) {
                if (firstSkipped){
                    writer.append(String.format(",%n"));
                }
                else {
                    firstSkipped = true;
                }
                writeSingleRoleElement(writer, currentRole, window);
            }
        }
        
        writeStudentElements(writer, windows);       
        writeFooterElement(writer);
    }

    /**
     * Writes footer of one layout
     * @param writer file writer
     * @throws IOException 
     */
    private void writeFooterElement(BufferedWriter writer) throws IOException {
        // add menu location
        writer.append(String.format("},%n"
                + "\"menu\" : {%n"
                + "\"x\" : \"" + menuPosition.x + "\",%n"
                + " \"y\" : \"" + menuPosition.y + "\"%n"));
        
        // end element
        writer.append(String.format("}%n}%n"));
    }

    /**
     * Writes header of one layout
     * @param writer file writer
     * @throws IOException 
     */
    private void writeHeaderElement(BufferedWriter writer) throws IOException {
        // add conditions
        writer.append(String.format("{%n"
                + "\"conditions\":[%n"
                + "{\"my role\":\"" + role + "\"}%n"
                + "],"
        ));
        
        // add windows
        writer.append(String.format("\"windows\":{%n"));
    }

    /**
     * Writes element containing only one position and role
     * @param writer file writer 
     * @param currentRole role of current element
     * @param window current window
     * @throws IOException 
     */
    private void writeSingleRoleElement(BufferedWriter writer, String currentRole, DisplayableWindow window) throws IOException {
        writer.append(String.format("\"" + currentRole + "\": [%n"
                + getSingleElementProperties(window) + "%n"
                + "]"));
    }

    /**
     * Writes properties of single window
     * @param window current window
     */
    private String getSingleElementProperties(DisplayableWindow window) {
        return "{\"x\": \"" + window.getPosition().x + "\", "
                + "\"y\": \"" + window.getPosition().y + "\", "
                + "\"width\": \"" + window.getWidth() + "\", "
                + "\"height\": \"" + window.getHeight() + "\"}";
    }

    /**
     * Writes students windows position to configuration file
     * @param writer file writer
     * @param windows currently available windows
     * @throws IOException 
     */
    private void writeStudentElements(BufferedWriter writer, List<DisplayableWindow> windows) throws IOException {
        
        boolean firstSkipped = false;

        for (DisplayableWindow window : windows) {
            String currentRole = window.getRole().toLowerCase();
            if ("student".equals(currentRole)) {

                // add comma at the end of last row
                if (firstSkipped) {
                    writer.append(String.format(",%n"));                    
                }
                else {
                    // add header of element
                    writer.append(String.format("\"student\":[%n"));
                    firstSkipped = true;
                }
                
                writer.append(String.format(getSingleElementProperties(window)));
            }
        }
        
        // if any student was written, end element
        if (firstSkipped) writer.append(String.format("%n]%n"));
    }
}
