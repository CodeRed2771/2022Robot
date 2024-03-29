package frc.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse.PushPromiseHandler;

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
	
    // PRACTICE // OLD BOT (ZUNI)
    private final static double DT_PRACT_A_ABS_ZERO_INITIAL = .376; // PRACTICE BOT
    private final static double DT_PRACT_B_ABS_ZERO_INITIAL = .352; 
    private final static double DT_PRACT_C_ABS_ZERO_INITIAL = .443; 
    private final static double DT_PRACT_D_ABS_ZERO_INITIAL = .098; 
    
    // COMPETITION
    public final static double DT_COMP_A_ABS_ZERO_INITIAL = .548; // COMPETITION BOT
    public final static double DT_COMP_B_ABS_ZERO_INITIAL = .230; 
    public final static double DT_COMP_C_ABS_ZERO_INITIAL = .187; 
    public final static double DT_COMP_D_ABS_ZERO_INITIAL = .332; 

    public static final double AUTO_ROT_P = 0.08; 
    public static final double AUTO_ROT_I = 0.001;
    public static final double AUTO_ROT_D = 0.1; 
    public static final double AUTO_ROT_F = 0.0;

    public static final double INTAKE_MAX_CURRENT = 14;

    public static final DigitalInput botIndicator = new DigitalInput(Wiring.PRACTICE_BOT_INDICATOR);

    // Physical Module - A
    public final static int DT_A_DRIVE_ID = 3;
    public final static int DT_A_TURN_ID = 4;
    private static double DT_A_ABS_ZERO = getInitialTurnZeroPos('A');
    // Physical Module - B
    public final static int DT_B_DRIVE_ID = 6;
    public final static int DT_B_TURN_ID = 5;
    private static double DT_B_ABS_ZERO = getInitialTurnZeroPos('B');
    // Physical Module - C
    public final static int DT_C_DRIVE_ID = 2;
    public final static int DT_C_TURN_ID = 1;
    private static double DT_C_ABS_ZERO = getInitialTurnZeroPos('C');
    // Physical Module - D
    public final static int DT_D_DRIVE_ID = 7;
    public final static int DT_D_TURN_ID = 8;
    private static double DT_D_ABS_ZERO = getInitialTurnZeroPos('D');

    public final static int INTAKE_DEPLOY_ZERO_INITIAL = 200;
    
    public static final double SHOOTER_P = 0.3;
    public static final double SHOOTER_I = 0.0;
    public static final double SHOOTER_D = 0;
    public static final double SHOOTER_F = 0.0505;
    public static final int SHOOTER_DEFAULT_SPEED = 13000;
    public static final int FEEDER_DEFAULT_SPEED = 13000;

    public final static double VISION_FWD_P = 0.05;
    public final static double VISION_FWD_I = 0;
    public final static double VISION_FWD_D = 0.15;

    public final static double VISION_STR_P = 0.15;
    public final static double VISION_STR_I = 0;
    public final static double VISION_STR_D = 0.2;

    public final static double VISION_ROT_P = 0.03;
    public final static double VISION_ROT_I = 0;
    public final static double VISION_ROT_D = 0;

    public final static double VISION_DISTANCE_A = 0.935049;
    public final static double VISION_DISTANCE_B = -90.5271;
    public final static double VISION_DISTANCE_C = 0.563257;

    public static final double CLIMBER_MOTOR_P = .07;
	public static final double CLIMBER_MOTOR_I = 0;
	public static final double CLIMBER_MOTOR_D = 0;
	public static final double CLIMBER_MOTOR_IZONE = 40;
	
    public static double getInitialTurnZeroPos(char moduleLetter) {
        double zeroPos = 0;
        if (isPracticeBot()) {
            switch (moduleLetter) {
                case 'A':
                    zeroPos = DT_PRACT_A_ABS_ZERO_INITIAL;
                    break;
                case 'B':
                    zeroPos = DT_PRACT_B_ABS_ZERO_INITIAL;
                    break;
                case 'C':
                    zeroPos = DT_PRACT_C_ABS_ZERO_INITIAL;
                    break;
                case 'D':
                    zeroPos = DT_PRACT_D_ABS_ZERO_INITIAL;
                    break;
            }
        } else {
            switch (moduleLetter) {
                case 'A':
                    zeroPos = DT_COMP_A_ABS_ZERO_INITIAL;
                    break;
                case 'B':
                    zeroPos = DT_COMP_B_ABS_ZERO_INITIAL;
                    break;
                case 'C':
                    zeroPos = DT_COMP_C_ABS_ZERO_INITIAL;
                    break;
                case 'D':
                    zeroPos = DT_COMP_D_ABS_ZERO_INITIAL;
                    break;
            }
        }
        return zeroPos;
    }

    public static double getTurnZeroPos(char moduleLetter) {
        // note that the Practice bot selection has already
        // occurred at startup.
        double zeroPos = 0;
        switch (moduleLetter) {
            case 'A':
                zeroPos = DT_A_ABS_ZERO;
                break;
            case 'B':
                zeroPos = DT_B_ABS_ZERO;
                break;
            case 'C':
                zeroPos = DT_C_ABS_ZERO;
                break;
            case 'D':
                zeroPos = DT_D_ABS_ZERO;
                break;
        }
        return zeroPos;
    }

    public static double getTurnP() { 
        if (isPracticeBot())
            return 8;  // zuni
        else    
            return 20;  // competition
    }
    public static double getTurnI() { 
        if (isPracticeBot())
            return 0.01;  
        else    
            return 0;  // competition
    }
    public static double getTurnIZone() { 
        if (isPracticeBot())
            return 40;  
        else    
            return 0;  // competition
    }
    public static double getTurnD() { 
        if (isPracticeBot())
            return 400;  
        else    
            return 0;  // competition
    }    
    public static double getTurnF() { 
        if (isPracticeBot())
            return 0;  
        else    
            return 0;  // competition
    }    
    
    /********************** DRIVE PID ******************/
    
    public static double getDriveP() { 
        if (isPracticeBot())
            return .00115;  // zuni
        else    
            return .3;  // competition
    }
    public static double getDriveI() { 
        if (isPracticeBot())
            return 0.01;  
        else    
            return 0;  // competition
    }
    public static double getDriveIZone() { 
        if (isPracticeBot())
            return 50;  
        else    
            return 0;  // competition
    }
    public static double getDriveD() { 
        if (isPracticeBot())
            return 0;  
        else    
            return 0;  // competition
    }    
    public static double getDriveF() { 
        if (isPracticeBot())
            return 0.000156;  
        else    
            return 0;  // competition
    }   

    // Rot PID - this is for turning the robot, not turning a module
    public final static double DT_ROT_PID_P = .007;
    public final static double DT_ROT_PID_I = .0004;
    public final static double DT_ROT_PID_D = .000;
    public final static double DT_ROT_PID_IZONE = 18;

	public final static int DT_MM_ACCEL = 8000; //(distance units per 100 ms) per second
	public final static int DT_MM_VELOCITY = 35000;  //distance units per 100 ms
	
	// COMPETIION AND PRACTICE
    public final static double getDriveTicksPerInch() {
        if (isPracticeBot()) 
            return(.39);  
        else
            return(1230);
    }

    // Loads the calibration numbers from the stored file which overrides the programmed values
    // This is read during robotInit, so if you make changes to the stored calibration, either
    // reboot the robot, or do a restart Code.
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

    public static void initializeSmartDashboard() {
        SmartDashboard.putBoolean("Calibrate Swerve", false);
        SmartDashboard.putBoolean("Delete Swerve Calibration", false);
    }

    public static boolean shouldCalibrateSwerve() {
        boolean calibrateSwerve = SmartDashboard.getBoolean("Calibrate Swerve", false);
        if (calibrateSwerve) {
            SmartDashboard.putBoolean("Calibrate Swerve", false);
            return true;
        }
        return false;
    }

    public static void checkIfShouldDeleteCalibration() {
        boolean deleteCalibration = SmartDashboard.getBoolean("Delete Swerve Calibration", false);
        if (deleteCalibration) {
            SmartDashboard.putBoolean("Delete Swerve Calibration", false); // turn switch back off
            deleteSwerveDriveCalibration();  
        }
    }

    public static void deleteSwerveDriveCalibration() {
        DT_A_ABS_ZERO = getInitialTurnZeroPos('A');;
        DT_B_ABS_ZERO = getInitialTurnZeroPos('B');;
        DT_C_ABS_ZERO = getInitialTurnZeroPos('C');;
        DT_D_ABS_ZERO = getInitialTurnZeroPos('D');;

        File calibrationFile = new File("/home/lvuser/swerve.calibration");
        calibrationFile.delete();
    }

    public static boolean isPracticeBot() {
        return !botIndicator.get();
    }
}
