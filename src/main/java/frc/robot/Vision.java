package frc.robot;

import java.lang.Math;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.*;

public class Vision {

    private static double CameraHeight = 21.625;
    private static double TargetHeight = 89.75; 
    private static double cameraDistanceFromCenterOfRobot = 4.875; 
    private static double CameraAngle = 29.5;
    private static NetworkTable table = null;
    private static double ty = 0;
    private static double degreesTargetOffGround = 0;
    private static double distance = 0;
    public static Vision instance;

    public static Vision getInstance() {
        if (instance == null)
            instance = new Vision();
        return instance;
    }

    public Vision() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
        SmartDashboard.putNumber("Adjust Val:", 1);
        SmartDashboard.putNumber("ShooterPivoterAdjust", 0.5);
    }

    public static double getAngleOffset() {
        return table.getEntry("tx").getDouble(0);
    }

    public static double getDistanceAdjustedAngle () {

        // return getAngleOffset()*SmartDashboard.getNumber("Adjust Val:", 1);
                                                                                           
        double distance = getDistanceFromTarget();
        double originalDistance = distance;                                                                                                                        
        double upperVal = 0;
        double adjustFactorOne = 1;                                                    
        double adjustFactorTwo = 1;                                                    
        double averageAdjustFactorPerInch = 0;
        double finalAdjustedFactor = 1;
                                   
        distance = Math.floor(distance/12);                                 // THIS IS THE CODE WE ARE GOING TO USE TO GET
        upperVal = distance + 1;                                            // THE DISTANCE ADJUSTED FACTOR - IS

        if (distance > VisionCalibration.turnAdjustmentArray.length - 1) {
            upperVal = VisionCalibration.turnAdjustmentArray.length - 1;
            distance = upperVal;
        }

        if (distance != upperVal) {
            adjustFactorOne = VisionCalibration.turnAdjustmentArray[(int) distance];
            adjustFactorTwo = VisionCalibration.turnAdjustmentArray[(int) upperVal];
            averageAdjustFactorPerInch = (adjustFactorTwo - adjustFactorOne) / 12;
            finalAdjustedFactor = (averageAdjustFactorPerInch * (originalDistance - (distance * 12))) + adjustFactorOne;
        } else {
            finalAdjustedFactor = VisionCalibration.turnAdjustmentArray[(int)distance];
        }
        // adjustFactorOne = VisionCalibration.turnAdjustmentArray[(int) distance];
        // adjustFactorTwo = VisionCalibration.turnAdjustmentArray[(int) upperVal];
        // averageAdjustFactorPerInch = (adjustFactorTwo - adjustFactorOne) / 12;
        // finalAdjustedFactor = (averageAdjustFactorPerInch * (originalDistance - (distance * 12))) + adjustFactorOne;
        SmartDashboard.putNumber("FINAL ADJUSTED FACTOR", finalAdjustedFactor);
        SmartDashboard.putNumber("VISION Angle Offset", getAngleOffset());
        
        return finalAdjustedFactor * getAngleOffset();
    }

    public static double getShooterPivoterDesiredPosition () {
        // return SmartDashboard.getNumber("ShooterPivoterAdjust", 0.5);

        double distance = getAdjustedDistanceFromTarget();
        SmartDashboard.putNumber("Adjusted Distance", getAdjustedDistanceFromTarget());
        double originalDistance = distance;                                                                                                                        
        double upperVal = 0;
        double desiredShaftPositionOne = 1;                                                    
        double desiredShaftPositionTwo = 1;                                                    
        double averageDesiredShaftPositionPerInch = 0;
        double finalShaftPosition = 1;

        distance = Math.floor(distance/12);
        upperVal = (float) distance + 1;

        if (distance > ShooterCalibration.shooterPivoterArray.length - 1) {
            upperVal = ShooterCalibration.shooterPivoterArray.length - 1;
            distance = upperVal;
        }

        if (distance != upperVal) {
            desiredShaftPositionOne = ShooterCalibration.shooterPivoterArray[(int)distance];
            desiredShaftPositionTwo = ShooterCalibration.shooterPivoterArray[(int)upperVal];
            averageDesiredShaftPositionPerInch = (desiredShaftPositionTwo - desiredShaftPositionOne) / 12;
            finalShaftPosition = (averageDesiredShaftPositionPerInch * (originalDistance - ((float) distance * 12))) + desiredShaftPositionOne;
        } else {
            finalShaftPosition = ShooterCalibration.shooterPivoterArray[(int)distance];
        }

        // desiredShaftPositionOne = ShooterCalibration.shooterPivoterArray[(int)distance];
        // desiredShaftPositionTwo = ShooterCalibration.shooterPivoterArray[(int)upperVal];
        // averageDesiredShaftPositionPerInch = (desiredShaftPositionTwo - desiredShaftPositionOne) / 12;
        // finalShaftPosition = (averageDesiredShaftPositionPerInch * (originalDistance - ((float) distance * 12))) + desiredShaftPositionOne;
        return finalShaftPosition;
    }

    public static boolean seesTarget() {
        return table.getEntry("tv").getDouble(0) > 0; 
    }

    public static boolean onTarget() {
        return (seesTarget() && (Math.abs(getAngleOffset()) <= 1));
    }

    public static double getDistanceFromTarget () {
        ty = table.getEntry("ty").getDouble(0);
        degreesTargetOffGround = CameraAngle + ty;
        distance = (TargetHeight - CameraHeight) / Math.tan(Math.toRadians(degreesTargetOffGround));
        SmartDashboard.putNumber("Distance:", distance);
        return distance;
    }

    public static double getAdjustedDistanceFromTarget () {
        
        double originalDistance = getDistanceFromTarget(); 
        double angleFromTarget = getAngleOffset();

        return originalDistance - (cameraDistanceFromCenterOfRobot - ((Math.cos(Math.toRadians(angleFromTarget))) * cameraDistanceFromCenterOfRobot));
    }

    public static void setLED(boolean turnOn) {
        table.getEntry("ledMode").forceSetNumber(turnOn ? 3 : 1); // 3 - on, 1 = off, 2 - blink
    }

    public static void flashLED() {
        table.getEntry("ledMode").forceSetNumber(2);

        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setLED(false);
        }).start();
    }

    public static void setDriverMode() {
        setLED(false);
        table.getEntry("camMode").forceSetNumber(1);
    }

    public static void setVisionTrackingMode() {
        setLED(true);
        table.getEntry("camMode").forceSetNumber(0);
    }

    public static void setVisionToActiveTrackingMode() {
        table.getEntry("snapshot").forceSetNumber(1);
    }

    public static void stopActiveVisionMode() {
        table.getEntry("snapshot").forceSetNumber(0);
    }

    public static void setTargetForShooting() {
        table.getEntry("pipeline").forceSetNumber(0);
    }

    public static void setTargetForFuelCell() {
        table.getEntry("pipeline").forceSetNumber(1);
    }
}