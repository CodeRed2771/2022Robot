package frc.robot;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public class DriveTrain {

    public static SwerveModule moduleA, moduleB, moduleC, moduleD;

    public static void init(String driveMotorType) {
        
        if (driveMotorType.equals("NEO")) {
            moduleA = new SwerveModuleNEO(Calibration.DT_A_DRIVE_ID, Calibration.DT_A_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_A_ABS_ZERO(), 'A'); // Front right
            moduleB = new SwerveModuleNEO(Calibration.DT_B_DRIVE_ID, Calibration.DT_B_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_B_ABS_ZERO(), 'B'); // Back left
            moduleC = new SwerveModuleNEO(Calibration.DT_C_DRIVE_ID, Calibration.DT_C_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_C_ABS_ZERO(),'C'); // Back right
            moduleD = new SwerveModuleNEO(Calibration.DT_D_DRIVE_ID, Calibration.DT_D_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_D_ABS_ZERO(),'D'); // Front left
        } else {
            moduleA = new SwerveModuleFalcon(Calibration.DT_A_DRIVE_ID, Calibration.DT_A_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_A_ABS_ZERO(), 'A'); // Front right
            moduleB = new SwerveModuleFalcon(Calibration.DT_B_DRIVE_ID, Calibration.DT_B_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_B_ABS_ZERO(), 'B'); // Back left
            moduleC = new SwerveModuleFalcon(Calibration.DT_C_DRIVE_ID, Calibration.DT_C_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_C_ABS_ZERO(),'C'); // Back right
            moduleD = new SwerveModuleFalcon(Calibration.DT_D_DRIVE_ID, Calibration.DT_D_TURN_ID, Calibration.AUTO_DRIVE_P,
            Calibration.AUTO_DRIVE_I, Calibration.AUTO_DRIVE_D, Calibration.AUTO_DRIVE_IZONE, Calibration.TURN_P,
            Calibration.TURN_I, Calibration.TURN_D, 200, Calibration.GET_DT_D_ABS_ZERO(),'D'); // Front left
        } 

        SmartDashboard.putNumber("TURN P", Calibration.TURN_P);
        SmartDashboard.putNumber("TURN I", Calibration.TURN_I);
        SmartDashboard.putNumber("TURN D", Calibration.TURN_D);
     //   SmartDashboard.putNumber("TURN I ZONE", Calibration.TURN_I_ZONE);
     //   SmartDashboard.putNumber("TURN F", Calibration.TURN_F);
    }

    // define robot dimensions. L=wheel base W=track width
    private static final double l = 29, w = 18, r = Math.sqrt((l * l) + (w * w));

    public static void setDrivePower(double modAPower, double modBPower, double modCPower, double modDPower) {
        moduleA.setDrivePower(modAPower);
        moduleB.setDrivePower(modBPower);
        moduleC.setDrivePower(modCPower);
        moduleD.setDrivePower(modDPower);
    }

    public static void setDriveMMAccel(int accel) {
        moduleA.setDriveMMAccel(accel);
        moduleB.setDriveMMAccel(accel);
        moduleC.setDriveMMAccel(accel);
        moduleD.setDriveMMAccel(accel);
    }

    public static void setDriveMMVelocity(int velocity) {
        moduleA.setDriveMMVelocity(velocity);
        moduleB.setDriveMMVelocity(velocity);
        moduleC.setDriveMMVelocity(velocity);
        moduleD.setDriveMMVelocity(velocity);
    }

    public static boolean hasDriveCompleted(double allowedError) {
        // just checking two of the modules to see if they are done moving
        return moduleB.hasDriveCompleted(allowedError) && moduleA.hasDriveCompleted(allowedError);
    }

    public static boolean hasDriveCompleted() {
        return hasDriveCompleted(0);
    }

    public static void setTurnPower(double modAPower, double modBPower, double modCPower, double modDPower) {
        moduleA.setTurnPower(modAPower);
        moduleB.setTurnPower(modBPower);
        moduleC.setTurnPower(modCPower);
        moduleD.setTurnPower(modDPower);
    }

    public static void setTurnOrientation(double modAPosition, double modBPosition, double modCPosition,
            double modDPosition) {
        setTurnOrientation(modAPosition, modBPosition, modCPosition, modDPosition, false);
    }

    public static void setTurnOrientation(double modAPosition, double modBPosition, double modCPosition,
            double modDPosition, boolean optimizeTurn) {

        // position is a value from 0 to 1 that indicates
        // where in the rotation of the module the wheel should be set.
        // e.g. a value of .5 indicates a half turn from the zero position

        moduleA.setTurnOrientation(modAPosition, optimizeTurn);
        moduleB.setTurnOrientation(modBPosition, optimizeTurn);
        moduleC.setTurnOrientation(modCPosition, optimizeTurn);
        moduleD.setTurnOrientation(modDPosition, optimizeTurn);

        SmartDashboard.putNumber("A pos call", modAPosition);
        SmartDashboard.putNumber("B pos call", modBPosition);
        SmartDashboard.putNumber("C pos call", modCPosition);
        SmartDashboard.putNumber("D pos call", modDPosition);
    }

    public static void setAllTurnOrientation(double position) {
        setTurnOrientation(position, position, position, position, true);
    }

    /**
     * @param position     - position, 0 to 1, to turn to.
     * @param optimizeTurn - allow turn optimization
     */
    public static void setAllTurnOrientation(double position, boolean optimizeTurn) {
        setTurnOrientation(position, position, position, position, optimizeTurn);
    }

    public static void setAllDrivePosition(int position) {
        setDrivePosition(position, position, position, position);
    }

    public static void setDrivePosition(double modAPosition, double modBPosition, double modCPosition,
            double modDPosition) {
        moduleA.setDrivePIDToSetPoint(modAPosition);
        moduleB.setDrivePIDToSetPoint(modBPosition);
        moduleC.setDrivePIDToSetPoint(modCPosition);
        moduleD.setDrivePIDToSetPoint(modDPosition);
    }

    public static void addToAllDrivePositions(double ticks) {
        setDrivePosition(moduleA.getDriveEnc() + ((moduleA.modulesReversed() ? -1 : 1) * ticks),
                moduleB.getDriveEnc() + ((moduleB.modulesReversed() ? -1 : 1) * ticks),
                moduleC.getDriveEnc() + ((moduleC.modulesReversed() ? -1 : 1) * ticks),
                moduleD.getDriveEnc() + ((moduleD.modulesReversed() ? -1 : 1) * ticks));
    }

    public static double getDriveEnc() {
        return (moduleA.getDriveEnc() + moduleB.getDriveEnc() + moduleC.getDriveEnc() + moduleD.getDriveEnc()) / 4;
    }

    public static void autoSetRot(double rot) {
        swerveDrive(0, 0, rot);
    }

    public static void setAllTurnPower(double power) {
        setTurnPower(power, power, power, power);
    }

    public static void setAllDrivePower(double power) {
        setDrivePower(power, power, power, power);
    }

    public static boolean isModuleATurnEncConnected() {
        return moduleA.isTurnEncConnected();
    }

    public static boolean isModuleBTurnEncConnected() {
        return moduleB.isTurnEncConnected();
    }

    public static boolean isModuleCTurnEncConnected() {
        return moduleC.isTurnEncConnected();
    }

    public static boolean isModuleDTurnEncConnected() {
        return moduleD.isTurnEncConnected();
    }

    public static void resetDriveEncoders() {
        moduleA.resetDriveEnc();
        moduleB.resetDriveEnc();
        moduleC.resetDriveEnc();
        moduleD.resetDriveEnc();
    }

    public static void stopDriveAndTurnMotors() {
        moduleA.stopDriveAndTurnMotors();
        moduleB.stopDriveAndTurnMotors();
        moduleC.stopDriveAndTurnMotors();
        moduleD.stopDriveAndTurnMotors();
    }

    public static void stopDrive() {
        moduleA.stopDrive();
        moduleB.stopDrive();
        moduleC.stopDrive();
        moduleD.stopDrive();
    }

    public static double angleToPosition(double angle) {
        if (angle < 0) {
            return .5d + ((180d - Math.abs(angle)) / 360d);
        } else {
            return angle / 360d;
        }
    }

    private static boolean allowTurnEncoderReset = false;

    public static void allowTurnEncoderReset() {
        allowTurnEncoderReset = true;
    }

    // OLD
    public static void resetTurnEncoders() {
        if (allowTurnEncoderReset) {
            double modAOff = 0, modBOff = 0, modCOff = 0, modDOff = 0;

            moduleA.setTurnPower(0);
            moduleC.setTurnPower(0);
            moduleB.setTurnPower(0);
            moduleD.setTurnPower(0);
            Timer.delay(.1);
            // first find the current absolute position of the turn encoders
            modAOff = DriveTrain.moduleA.getTurnAbsolutePosition();
            modBOff = DriveTrain.moduleB.getTurnAbsolutePosition();
            modCOff = DriveTrain.moduleC.getTurnAbsolutePosition();
            modDOff = DriveTrain.moduleD.getTurnAbsolutePosition();

            // now use the difference between the current position and the
            // calibration zero
            // position
            // to tell the encoder what the current relative position is
            // (relative to the
            // zero pos)
			moduleA.setEncPos((int) (calculatePositionDifference(modAOff, Calibration.GET_DT_A_ABS_ZERO()) * 4096d));
			moduleB.setEncPos((int) (calculatePositionDifference(modBOff, Calibration.GET_DT_B_ABS_ZERO()) * 4096d));
			moduleC.setEncPos((int) (calculatePositionDifference(modCOff, Calibration.GET_DT_C_ABS_ZERO()) * 4096d));
			moduleD.setEncPos((int) (calculatePositionDifference(modDOff, Calibration.GET_DT_D_ABS_ZERO()) * 4096d));

            // Lamprey version
            // moduleA.setEncPos((int) (calculatePositionDifference(modAOff, Calibration.GET_DT_A_ABS_ZERO()) * 1024d));
            // moduleB.setEncPos((int) (calculatePositionDifference(modBOff, Calibration.GET_DT_B_ABS_ZERO()) * 1024d));
            // moduleC.setEncPos((int) (calculatePositionDifference(modCOff, Calibration.GET_DT_C_ABS_ZERO()) * 1024d));
            // moduleD.setEncPos((int) (calculatePositionDifference(modDOff, Calibration.GET_DT_D_ABS_ZERO()) * 1024d));

            allowTurnEncoderReset = false;
        }
    }

    private static double calculatePositionDifference(double currentPosition, double calibrationZeroPosition) {
        if (currentPosition - calibrationZeroPosition >= 0) {
            return currentPosition - calibrationZeroPosition;
        } else {
            return (1 - calibrationZeroPosition) + currentPosition;
        }
    }

    public static void setDriveBrakeMode(boolean b) {
        moduleA.setBrakeMode(b);
        moduleB.setBrakeMode(b);
        moduleC.setBrakeMode(b);
        moduleD.setBrakeMode(b);
    }

    public static double getAverageTurnError() {
        return (Math.abs(moduleA.getTurnError()) + Math.abs(moduleB.getTurnError()) + Math.abs(moduleC.getTurnError())
                + Math.abs(moduleD.getTurnError())) / 4d;
    }

    /*
     * 
     * Drive methods
     */
    public static void swerveDrive(double fwd, double strafe, double rot) {
        double a = strafe - (rot * (l / r));
        double b = strafe + (rot * (l / r));
        double c = fwd - (rot * (w / r));
        double d = fwd + (rot * (w / r));

        double ws1 = Math.sqrt((b * b) + (c * c)); // front_right (CHECK THESE
                                                   // AGAINST OUR BOT)
        double ws2 = Math.sqrt((b * b) + (d * d)); // front_left
        double ws3 = Math.sqrt((a * a) + (d * d)); // rear_left
        double ws4 = Math.sqrt((a * a) + (c * c)); // rear_right

        double wa1 = Math.atan2(b, c) * 180 / Math.PI;
        double wa2 = Math.atan2(b, d) * 180 / Math.PI;
        double wa3 = Math.atan2(a, d) * 180 / Math.PI;
        double wa4 = Math.atan2(a, c) * 180 / Math.PI;

        double max = ws1;
        max = Math.max(max, ws2);
        max = Math.max(max, ws3);
        max = Math.max(max, ws4);
        if (max > 1) {
            ws1 /= max;
            ws2 /= max;
            ws3 /= max;
            ws4 /= max;
        }

        DriveTrain.setTurnOrientation(angleToPosition(wa4), angleToPosition(wa2), angleToPosition(wa1),
                angleToPosition(wa3), true);
        DriveTrain.setDrivePower(ws4, ws2, ws1, ws3);
    }

    public static void showDriveEncodersOnDash() {
        SmartDashboard.putNumber("Mod A Drive Enc", moduleA.getDriveEnc());
        SmartDashboard.putNumber("Mod B Drive Enc", moduleB.getDriveEnc());
        SmartDashboard.putNumber("Mod C Drive Enc", moduleC.getDriveEnc());
        SmartDashboard.putNumber("Mod D Drive Enc", moduleD.getDriveEnc());

        SmartDashboard.putNumber("Mod A Drive Setpt", moduleA.getCurrentDriveSetpoint());
        SmartDashboard.putNumber("Mod B Drive Setpt", moduleB.getCurrentDriveSetpoint());
        SmartDashboard.putNumber("Mod C Drive Setpt", moduleC.getCurrentDriveSetpoint());
        SmartDashboard.putNumber("Mod D Drive Setpt", moduleD.getCurrentDriveSetpoint());
    }

    public static void showTurnEncodersOnDash() {
        SmartDashboard.putNumber("TURN A RAW", round(moduleA.getTurnAbsolutePosition(), 3));
        SmartDashboard.putNumber("TURN B RAW", round(moduleB.getTurnAbsolutePosition(), 3));
        SmartDashboard.putNumber("TURN C RAW", round(moduleC.getTurnAbsolutePosition(), 3));
        SmartDashboard.putNumber("TURN D RAW", round(moduleD.getTurnAbsolutePosition(), 3));

        SmartDashboard.putNumber("TURN A ENC", moduleA.getTurnRelativePosition());
        SmartDashboard.putNumber("TURN B ENC", moduleB.getTurnRelativePosition());
        SmartDashboard.putNumber("TURN C ENC", moduleC.getTurnRelativePosition());
        SmartDashboard.putNumber("TURN D ENC", moduleD.getTurnRelativePosition());

        SmartDashboard.putNumber("TURN A POS", round(moduleA.getTurnPosition(), 2));
        SmartDashboard.putNumber("TURN B POS", round(moduleB.getTurnPosition(), 2));
        SmartDashboard.putNumber("TURN C POS", round(moduleC.getTurnPosition(), 2));
        SmartDashboard.putNumber("TURN D POS", round(moduleD.getTurnPosition(), 2));

        SmartDashboard.putNumber("TURN A ANGLE", round(moduleA.getTurnAngle(), 0));
        SmartDashboard.putNumber("TURN B ANGLE", round(moduleB.getTurnAngle(), 0));
        SmartDashboard.putNumber("TURN C ANGLE", round(moduleC.getTurnAngle(), 0));
        SmartDashboard.putNumber("TURN D ANGLE", round(moduleD.getTurnAngle(), 0));

        SmartDashboard.putNumber("TURN A ERR", moduleA.getTurnError());
        SmartDashboard.putNumber("TURN B ERR", moduleB.getTurnError());
        SmartDashboard.putNumber("TURN C ERR", moduleC.getTurnError());
		SmartDashboard.putNumber("TURN D ERR", moduleD.getTurnError());
		
		//SmartDashboard.putNumber("TURN A SETPOINT", moduleA.getTurnSetpoint());
    }

    public static void humanDrive(double fwd, double str, double rot) {
        if (Math.abs(rot) < 0.01)
            rot = 0;

        if (Math.abs(fwd) < .15 && Math.abs(str) < .15 && Math.abs(rot) < 0.01) {
            setDriveBrakeMode(true);
            stopDrive();
        } else {
            setDriveBrakeMode(false);
            swerveDrive(fwd, str, rot);
        }
    }

    public static void fieldCentricDrive(double fwd, double strafe, double rot) {
        double temp = (fwd * Math.cos(RobotGyro.getGyroAngleInRad()))
                + (strafe * Math.sin(RobotGyro.getGyroAngleInRad()));
        strafe = (-fwd * Math.sin(RobotGyro.getGyroAngleInRad()))
                + (strafe * Math.cos(
                    RobotGyro.getGyroAngleInRad()));
        fwd = temp;
        humanDrive(fwd, strafe, rot);
    }

    public static void tankDrive(double left, double right) {
        setAllTurnOrientation(0);
        setDrivePower(right, left, right, left);
    }

    public static double[] getAllAbsoluteTurnOrientations() {
        return new double[] { moduleA.getTurnAbsolutePosition(), moduleB.getTurnAbsolutePosition(),
                moduleC.getTurnAbsolutePosition(), moduleD.getTurnAbsolutePosition() };
    }

    public static void setDrivePIDValues(double p, double i, double d, double f) {
        moduleA.setDrivePIDValues(p, i, d, f);
        moduleB.setDrivePIDValues(p, i, d, f);
        moduleC.setDrivePIDValues(p, i, d, f);
        moduleD.setDrivePIDValues(p, i, d, f);
    }

    public static void setTurnPIDValues(double p, double i, double d, double iZone, double f) {
        moduleA.setTurnPIDValues(p, i, d, iZone, f);
        moduleB.setTurnPIDValues(p, i, d, iZone, f);
        moduleC.setTurnPIDValues(p, i, d, iZone, f);
        moduleD.setTurnPIDValues(p, i, d, iZone, f);
    }

    public static Double round(Double val, int scale) {
        return new BigDecimal(val.toString()).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
