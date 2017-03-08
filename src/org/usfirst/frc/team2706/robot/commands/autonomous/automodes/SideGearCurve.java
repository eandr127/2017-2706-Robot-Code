package org.usfirst.frc.team2706.robot.commands.autonomous.automodes;

import org.usfirst.frc.team2706.robot.commands.autonomous.movements.CurveDrive;
import org.usfirst.frc.team2706.robot.commands.autonomous.movements.RotateDriveWithGyro;
import org.usfirst.frc.team2706.robot.commands.autonomous.movements.StraightDriveWithEncoders;
import org.usfirst.frc.team2706.robot.commands.autonomous.plays.DrivePlaceGear;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SideGearCurve extends CommandGroup {

    /**
     * Start at the side of the field, grab the side gear and head on out to the launch pad
     * 
     * @param speed What speeed are we travelling at?
     * @param xCurve How far right do we go on the turn?
     * @param yCurve How far forward to we go on the turn?
     * @param endAngle What angle do we turn at to face the gear handler?
     * @param reverseDistance How far back do we go after placing the gear?
     * @param toLaunchPadDistance How far is it to the launch pad from our location?
     */
    public SideGearCurve(double speed, double xCurve, double yCurve, double endAngle,
                    double reverseDistance, double toLaunchPadDistance) {
        this.addSequential(new CurveDrive(xCurve, yCurve, endAngle, speed));
        this.addSequential(new StraightDriveWithEncoders(0.4,3,0.2),2);
        
        this.addSequential(new DrivePlaceGear(0.5,0,4));
        this.addSequential(new RotateDriveWithGyro(0.6, -endAngle));
        this.addSequential(new StraightDriveWithEncoders(0.5, toLaunchPadDistance, 0.2));
    }
}
