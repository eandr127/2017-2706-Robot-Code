package org.usfirst.frc.team2706.robot.subsystems;

import org.usfirst.frc.team2706.robot.RobotMap;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/** 
 * Coordinates commands for the Gear Handler Arm mechanisms. 
 * 
 * @author wakandacat, FilledWithDetermination, Crazycat200
 */
public class GearHandler extends Subsystem {

    private DoubleSolenoid solenoid = new DoubleSolenoid(RobotMap.SOLENOID_FORWARD_CHANNEL, RobotMap.SOLENOID_REVERSE_CHANNEL);
    
    /*
     * some interesting things about the sensor... (measured in flash card lines)
     *  (right on top of sensor = line 1)
     * line 01 = 1.2V ++++++++++++
     * line 02 = 1.4V ++++++++++++++
     * line 03 = 1.7V +++++++++++++++++
     * line 04 = 1.8V ++++++++++++++++++
     * line 05 = 2.4V ++++++++++++++++++++++++
     * line 06 = 2.9V +++++++++++++++++++++++++++++
     * line 07 = 2.9V +++++++++++++++++++++++++++++
     * line 08 = 2.8V ++++++++++++++++++++++++++++
     * line 09 = 2.2V ++++++++++++++++++++++
     * line 10 = 2.2V ++++++++++++++++++++++
     * line 11 = 2.1V +++++++++++++++++++++
     * line 12 = 1.8V ++++++++++++++++++
     * line 13 = 1.7V +++++++++++++++++
     * line 14 = 1.6V ++++++++++++++++
     * line 15 = 1.3V +++++++++++++
     * line 16 = 1.2V ++++++++++++
     * line 17 = 1.1V +++++++++++
     * line 18 = 1.1V +++++++++++
     * line 19 = 1.0V ++++++++++
     * line 20 = 0.9V +++++++++
     * line 21 = 0.9V +++++++++
     */
    private AnalogInput irSensor = new AnalogInput(RobotMap.INFRARED_SENSOR_ANALOG);
    private static final double GEAR_CAPTURED = 1.2;
   
    // Let's use this to keep track of whether the arm is closed :)
    private boolean closed = true;

    public void initDefaultCommand() {}
    
    public void openArm() {
        solenoid.set(DoubleSolenoid.Value.kForward);
        closed = false;
    }
    
    public void closeArm() {
        solenoid.set(DoubleSolenoid.Value.kReverse); 
        closed = true;
    }
    
    public void toggleArm() {
        if (closed) {
            openArm();
        } else {
            closeArm();
        }
    }
    
    public boolean gearCaptured() {
        if (irSensor.getVoltage() >= GEAR_CAPTURED) {
            return true;
        }
        return false;
    }
}

