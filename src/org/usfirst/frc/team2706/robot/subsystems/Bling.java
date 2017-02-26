package org.usfirst.frc.team2706.robot.subsystems;


import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2706.robot.Robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * 
 * @author eAUE (Kyle Anderson)
 * @see <a href = "https://docs.google.com/drawings/d/1JQYcLj3Sdf0h_-DD0J7ceG14csBJ6LMhIWoIXoaeYVE/edit?usp=sharing"> 
 * Explanation of the light patterns</a> 
 */
public class Bling extends Subsystem {
    
    /* Will be true if the bling system is working properly 
     * (so if the arduino is not plugged in, it will be false).
     defaults to false to keep everything working. */
    public static boolean connected = false;

    public static SerialPort blingPort;
    
    // Will tell us which teleop thing we're displaying
    private static String teleopDisplayState = "";

    // The number of pixels on one LED strip
    int pixels = 120; 

    /*
     * Will be true if the battery level is critical, in which case it will override all other
     * signals to display the critical battery warning
     */
    public static boolean batCritical = false;
    
    // Let's make the colour and command codes
    Map<String, String> colours = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put("RED", "16711680");
            put("GREEN", "32768");
            put("YELLOW", "16775680");
            put("PURPLE", "8388736");
            put("ORANGE", "16753920");
            put("BLUE", "255");
            put("VIOLET", "15631086");
            put("MERGE", "6160762");
            put("TAN", "16767411");
            put("PINK", "14027935");
            put("WHITE", "16777215");
            put("TURQUOISE", "65535");
            put("BLACK", "0");
            put("GOLD", "16766720");
            put("SILVER", "12632256");
        }

    };

    public Bling() {
        
        try {
            blingPort = new SerialPort(9600, SerialPort.Port.kMXP);
            blingPort.setTimeout(0.8); // Will wait a max of half a second.
            blingPort.writeString("I"); // Tell arduino we're sending a command.
            blingPort.writeString("E0Z"); // Clear the LED strip.
            connected = true;
        }
        
        catch (Exception e){} 
        

    }

    /**
     * This function should be run at the beginning of autonomous to get the proper light pattern.
     */
    public void auto() { // Will run during autonomous period

        // IF THE BLINGPORT FAILED, DON'T CAUSE MORE ERRORS
        if (!connected) return; 
        
        blingPort.writeString("I");
        blingPort.writeString("E0Z"); // Clear the LED strip
        blingPort.writeString("F1C" + colours.get("MERGE") + "D150E1Z");
        
    }

    /** This function initializes teleop period
     * 
     */
    public void teleopInit(){
        blingPort.writeString("I");
        blingPort.writeString("F7C" + colours.get("YELLOW") + "B100D100E7Z");
    }
    
    /**
     * This command should be run with the scheduler during teleop
     * to decide what to display, either distance to gear delivery,
     * if we're ready to get another gear or if it is time to climb/
     * we're ready to climb.
     */
    public void teleopPeriodic(){
        //Get the average distance from whatever obstacle.
        double distance = (Robot.driveTrain.getRightDistanceToObstacle() + 
                           Robot.driveTrain.getLeftDistanceToObstacle()) / 2;
        
        boolean gearCaptured = Robot.gearHandler.gearCaptured();
        
        // Need this to determine if we're ready to climb
        double timeLeft = 150 - Timer.getMatchTime(); 
        
        // We use the teleopDisplayState to make sure we only call each of these once.
        if (distance < 3 && gearCaptured && teleopDisplayState != "distance") {
            showDistance(distance);
            teleopDisplayState = "distance";
        }
        else if (!gearCaptured && teleopDisplayState != "gear") {
            showReadyToReceiveGear(true);
            teleopDisplayState = "gear";
        }
        else if (timeLeft <= 30 && teleopDisplayState != "climb") {
            showReadyToClimb(true);
            teleopDisplayState = "climb";
        }
        
    }
    
    /**
     * This command just quickly clear the LED Strip.
     */
    public void clear() {

        if (!connected) return;
        
        blingPort.writeString("I");
        blingPort.writeString("E0Z"); // Clear the LED strip
    }

    /**
     * This function will run whenever we want to display the battery voltage output. Will run
     * automatically at startup.
     * 
     * @param percent : The current voltage percent reading from the battery.
     * @param criticalStatus : Needs to be true if the battery level is below 20%.
     */
    public void batteryInd(double percent, boolean criticalStatus) {
        
        if (!connected) return;

        batCritical = criticalStatus;
        blingPort.writeString("I"); // Let them know we need to send another command
        String bColour;
        if (percent <= 0.25)
            bColour = colours.get("RED");
        else if (percent <= 0.5)
            bColour = colours.get("YELLOW");
        else if (percent <= 0.75)
            bColour = colours.get("PURPLE");
        else
            bColour = colours.get("GREEN");

        // Use multi-colour display
        blingPort.writeString("F12C" + bColour + "D" + Math.round(percent * 100) + "E12Z");
    }

    /**
     * Show a distance indication on the LED strip out of 3 metres.
     * 
     * @param distance : The current distance
     */
    public void showDistance(double distance) {

        if (!connected) return;
        
        blingPort.writeString("I");
        blingPort.writeString("E0Z"); // Clear the LED strip
        if (distance > 3.0)
            return; // Only showing 3 metres from the object.
        double percentDist = distance / 3;
        System.out.println(Math.round(percentDist * pixels));
        String dColour;
        if (distance > 2)
            dColour = colours.get("RED");
        else if (distance > 1)
            dColour = colours.get("YELLOW");
        else
            dColour = colours.get("GREEN");

        // Colour flash
        blingPort.writeString(
                        "F7C" + dColour + "P0" + "Q" + Math.round(percentDist * pixels) + "E7Z");

    }

    /**
     * This function lets you show whether or not the robot is ready to receive a gear.
     * 
     * @param ready : A boolean that indicates whether or not the robot is ready. True if yes.
     */
    public void showReadyToReceiveGear(boolean ready) {
        
        if (!connected) return;

        // Do not interfere with critical battery warning.
        // Show a theatre chase
        if (ready && !batCritical)
            customDisplay("Orange", 3, -1, 100, 0, 100);

    }
    
    /**
     * This function lets you show whether or not the robot is ready to climb.
     * @param ready : A boolean that indicate true for ready or false for not ready.
     */
    public void showReadyToClimb(boolean ready){
        
        if (ready) customDisplay("White", 11, 250, 60, 0, 100);
        
    }

    /**
     * This is used to display a basic pattern on the bling LED lights.
     * Note that for functions above 9, pixelStart and pixelEnd and delay will do nothing.
     * 
     * @param pattern : The type of motion or animation pattern you would like to display. Patterns range from 1-12.
     * 1 : Color wipe
     * 2 : Colour wipe with blank period
     * 3 : Theatre chase
     * 4 : Rainbow
     * 5 : Theatre chase rainbow
     * 6 : Color bar
     * 7 : Color bar flash
     * 8 : Bounce
     * 9 : Bounce wipe
     * 10: Multi bounce
     * 11: Multi bouce wipe
     * 12: Multi colour wipe
     * @param colour : Colour, either as a preset such as "RED", "GREEN", "WHITE" (either caps or
     *        no caps) or in decimal format. Use a programmer calculator to determine decimal
     *        format.
     * 
     *        Presets: GREEN, RED, BLUE, YELLOW, ORANGE, PURPLE, TAN, VIOLET, MERGE, PINK, WHITE,
     *        TURQUOISE, BLACK, GOLD, SILVER
     * @param delay : The delay between animation segments in seconds, if applicable.
     * @param brightness : The brightness of the LED pattern as an integer between 0 and 100.
     * @param pixelStart : The percent of the bar where the pixel pattern will start in decimal format.
     * @param pixelEnd : The percent of the bar where the pattern will end in decimal format.
     */
    public void customDisplay(String colour, int pattern, double delay,
                    int brightness, int pixelStart, int pixelEnd) {
        
        if (!connected) return;

        String gColour = colour.replace(" ", ""); // Get rid of all the spaces
        gColour = gColour.toUpperCase(); // Make sure that any letters are uppercase.

        if ((gColour.charAt(0)) != '0') { // Preset colour that we need to convert to RGB888.
            gColour = colours.get(gColour);
        }

        int startPixel = Math.round(pixelStart * pixels);
        int endPixel = Math.round(pixelEnd * pixels);

        blingPort.writeString("I");
        
        if (pattern <= 9)
            blingPort.writeString("F" + pattern + "C" + gColour + "B" + brightness + "D" + delay
                                   + "P" + startPixel + "Q" + endPixel + "E" + pattern + "Z");
        
        else 
            blingPort.writeString("F" + pattern + "C" + gColour + "B" + brightness + "E" + 
                                   pattern + "Z");
    }

    @Override
    protected void initDefaultCommand() {}
}
