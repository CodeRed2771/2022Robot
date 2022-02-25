package frc.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Calibration {

    /*
     * Drive Train
     */
	
	 // TEST/OLD BOT
	
	// private final static double DT_A_ABS_ZERO_INITIAL = .522; //Practice Robot Calibration
	// private final static double DT_B_ABS_ZERO_INITIAL = .904; //Practice Robot Calibration
	// private final static double DT_C_ABS_ZERO_INITIAL = .793; //Practice Robot Calibration
	// private final static double DT_D_ABS_ZERO_INITIAL = .106; //Practice Robot Calibration

	// PRACTICE
	
	// private final static double DT_A_ABS_ZERO_INITIAL = .228; //Practice Robot Calibration
	// private final static double DT_B_ABS_ZERO_INITIAL = .748; //Practice Robot Calibration
	// private final static double DT_C_ABS_ZERO_INITIAL = .518; //Practice Robot Calibration
	// private final static double DT_D_ABS_ZERO_INITIAL = .292; //Practice Robot Calibration
	
    // COMPETIION

    private final static double DT_A_ABS_ZERO_INITIAL = .376; // OLD BOT (ZUNI)
    private final static double DT_B_ABS_ZERO_INITIAL = .838; 
    private final static double DT_C_ABS_ZERO_INITIAL = .443; 
    private final static double DT_D_ABS_ZERO_INITIAL = .110; 
    
    public final static double DT_NEW_A_ABS_ZERO_INITIAL = 0; // NEW BOT
    public final static double DT_NEW_B_ABS_ZERO_INITIAL = 0; 
    public final static double DT_NEW_C_ABS_ZERO_INITIAL = 0; 
    public final static double DT_NEW_D_ABS_ZERO_INITIAL = 0; 
    
    public static final double SHOOTER_P = 0.00063;
    public static final double SHOOTER_I = 0.0; // not used
    public static final double SHOOTER_D = 0.05;
    public static final double SHOOTER_F = 0.000190;
    public static final int SHOOTER_DEFAULT_SPEED = 2000;
    public static final int FEEDER_DEFAULT_SPEED = 3800
    ;

    public final static double VISION_FWD_P = 0.05;
    public final static double VISION_FWD_I = 0;
    public final static double VISION_FWD_D = 0.15;

    public final static double VISION_STR_P = 0.15;
    public final static double VISION_STR_I = 0;
    public final static double VISION_STR_D = 0.2;

    public final static double VISION_ROT_P = 0.03;
    public final static double VISION_ROT_I = 0;
    public final static double VISION_ROT_D = 0;

    public final static double TURN_P = 20; //was 10 3.10.19
	public final static double TURN_I = 0.00; // was .01
	public final static double TURN_D = 400; // was 400
    public final static double TURN_I_ZONE = 40; // 8/10/21 this was not set, so it's not right
    public final static double TURN_F = 0;    // 8/10/21 this was also not used before but could be helpful

    // Physical Module - A
    public final static int DT_A_DRIVE_ID = 3;
    public final static int DT_A_TURN_ID = 4;
    private static double DT_A_ABS_ZERO = DT_A_ABS_ZERO_INITIAL;

    public static double GET_DT_A_ABS_ZERO() {
        return DT_A_ABS_ZERO;
    }

    // Physical Module - B
    public final static int DT_B_DRIVE_ID = 6;
    public final static int DT_B_TURN_ID = 5;
    private static double DT_B_ABS_ZERO = DT_B_ABS_ZERO_INITIAL;

    public static double GET_DT_B_ABS_ZERO() {
        return DT_B_ABS_ZERO;
    }

    // Physical Module - C
    public final static int DT_C_DRIVE_ID = 2;
    public final static int DT_C_TURN_ID = 1;
    private static double DT_C_ABS_ZERO = DT_C_ABS_ZERO_INITIAL;

    public static double GET_DT_C_ABS_ZERO() {
        return DT_C_ABS_ZERO;
    }

    // Physical Module - D
    public final static int DT_D_DRIVE_ID = 7;
    public final static int DT_D_TURN_ID = 8;
    private static double DT_D_ABS_ZERO = DT_D_ABS_ZERO_INITIAL;

    public static double GET_DT_D_ABS_ZERO() {
        return DT_D_ABS_ZERO;
    }

    // Rot PID - this is for turning the robot, not turning a module
    public final static double DT_ROT_PID_P = .007;
    public final static double DT_ROT_PID_I = .0004;
    public final static double DT_ROT_PID_D = .000;
    public final static double DT_ROT_PID_IZONE = 18;

	public final static int DT_MM_ACCEL = 1000;
	public final static int DT_MM_VELOCITY = 2000;
	
	// COMPETIION AND PRACTICE
	public static final double DRIVE_DISTANCE_TICKS_PER_INCH = .39;  

    // TEST BOT2
    // public static final double DRIVE_DISTANCE_TICKS_PER_INCH = 32.900;

    public static final double AUTO_ROT_P = 0.08; // increased from .03 on 2/9/2019 by CS
    public static final double AUTO_ROT_I = 0.001;
    public static final double AUTO_ROT_D = 0.1; // was 067
    public static final double AUTO_ROT_F = 0.0;

    public static final double AUTO_DRIVE_P = .00115; 
    public static final double AUTO_DRIVE_I = 0;
    public static final double AUTO_DRIVE_D = 0; // was 0
    public static final double AUTO_DRIVE_F = 0.000156;
    public static final int AUTO_DRIVE_IZONE = 50;

    public static final double INTAKE_MAX_CURRENT = 14;

    public static final DigitalInput botIndicator = new DigitalInput(9);

    public static void loadSwerveCalibration() {
        File calibrationFile = new File("/home/lvuser/swerve.calibration");
        if (calibrationFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(calibrationFile));
                DT_A_ABS_ZERO = Double.parseDouble(reader.readLine());
                DT_B_ABS_ZERO = Double.parseDouble(reader.readLine());
                DT_C_ABS_ZERO = Double.parseDouble(reader.readLine());
                DT_D_ABS_ZERO = Double.parseDouble(reader.readLine());
                reader.close();
                SmartDashboard.putBoolean("Using File-based Swerve Calibration", true);
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        SmartDashboard.putBoolean("Using File-based Swerve Calibration", false);
    }

    public static void saveSwerveCalibration(double dt_a, double dt_b, double dt_c, double dt_d) {
        File calibrationFile = new File("/home/lvuser/swerve.calibration");
        try {
            if (calibrationFile.exists()) {
                calibrationFile.delete();
            }
            calibrationFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(calibrationFile));
            writer.write(String.valueOf(dt_a) + "\n");
            writer.write(String.valueOf(dt_b) + "\n");
            writer.write(String.valueOf(dt_c) + "\n");
            writer.write(String.valueOf(dt_d) + "\n");
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println(calibrationFile.getAbsolutePath());

        DT_A_ABS_ZERO = dt_a;
        DT_B_ABS_ZERO = dt_b;
        DT_C_ABS_ZERO = dt_c;
        DT_D_ABS_ZERO = dt_d;
    }

    public static void resetSwerveDriveCalibration() {
        DT_A_ABS_ZERO = DT_A_ABS_ZERO_INITIAL;
        DT_B_ABS_ZERO = DT_B_ABS_ZERO_INITIAL;
        DT_C_ABS_ZERO = DT_C_ABS_ZERO_INITIAL;
        DT_D_ABS_ZERO = DT_D_ABS_ZERO_INITIAL;

        File calibrationFile = new File("/home/lvuser/swerve.calibration");
        calibrationFile.delete();
    }

    public static void initializeSmartDashboard() {
        SmartDashboard.putBoolean("Calibrate Swerve", false);
        SmartDashboard.putBoolean("Reset Swerve Calibration", false);
    }

    public static boolean shouldCalibrateSwerve() {
        boolean calibrateSwerve = SmartDashboard.getBoolean("Calibrate Swerve", false);
        if (calibrateSwerve) {
            SmartDashboard.putBoolean("Calibrate Swerve", false);
            return true;
        }
        return false;
    }

    public static void checkIfShouldResetCalibration() {
        boolean deleteCalibration = SmartDashboard.getBoolean("Reset Swerve Calibration", false);
        if (deleteCalibration) {
            SmartDashboard.putBoolean("Reset Swerve Calibration", false);
            resetSwerveDriveCalibration();
        }
    }

    public static boolean isPracticeBot() {
        return !botIndicator.get();
    }
}
