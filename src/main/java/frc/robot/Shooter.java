/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Shooter {

    private static CANSparkMax shooterMotor = new CANSparkMax(Wiring.SHOOTER_MOTOR_ID, MotorType.kBrushless);
    private static CANSparkMax feederMotor = new CANSparkMax(Wiring.FEEDER_MOTOR_ID, MotorType.kBrushless);

    private static boolean isInitialized = false;
    private static boolean isEnabled = false;
    private static boolean isGateOpen = false;
    private static boolean oneShot = false;
    private static boolean continuousShooting = false;
    private static int timer = 0;
    private static int liftTimer = 0;
    private static double targetSpeed = Calibration.SHOOTER_DEFAULT_SPEED;
    private static double adjustmentFactor = 1;
    private static SparkMaxPIDController shooterPID;
    private static SparkMaxPIDController feederPID;
    private static DoubleSolenoid ballLiftSolenoid;
    private static DoubleSolenoid shooterPositionSolenoid_Stage1;
    private static DoubleSolenoid shooterPositionSolenoid_Stage2;
    public static enum ShooterPosition {
        Low,
        Medium,
        High
    }
    private static ShooterPosition curShooterPosition;

    public static void init() {
        shooterMotor.restoreFactoryDefaults();
        shooterMotor.setOpenLoopRampRate(.1);
        shooterMotor.setSmartCurrentLimit(40);
        shooterMotor.setIdleMode(IdleMode.kCoast);
        shooterMotor.setInverted(true);
        
        shooterPID = shooterMotor.getPIDController();
        shooterPID.setP(Calibration.SHOOTER_P);
        shooterPID.setI(0);
        shooterPID.setD(Calibration.SHOOTER_D);
        shooterPID.setFF(Calibration.SHOOTER_F);
        shooterPID.setOutputRange(0, 1);

        shooterPID.setReference(0, ControlType.kVelocity);  // set to 0 speed

        feederMotor.restoreFactoryDefaults();
        feederMotor.setOpenLoopRampRate(.1);
        feederMotor.setSmartCurrentLimit(40);
        feederMotor.setIdleMode(IdleMode.kCoast);
        feederMotor.setInverted(true);

        feederPID = feederMotor.getPIDController();
        feederPID.setP(Calibration.SHOOTER_P);
        feederPID.setI(0);
        feederPID.setD(Calibration.SHOOTER_D);
        feederPID.setFF(Calibration.SHOOTER_F);
        feederPID.setOutputRange(0, 1);

        feederPID.setReference(0, ControlType.kVelocity);  // set to 0 speed
		 	
		SmartDashboard.putNumber("Shoot P", Calibration.SHOOTER_P);
		SmartDashboard.putNumber("Shoot D", Calibration.SHOOTER_D);
        SmartDashboard.putNumber("Shoot F", Calibration.SHOOTER_F);
        SmartDashboard.putNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED);
        SmartDashboard.putNumber("Feeder Setpoint", Calibration.FEEDER_DEFAULT_SPEED);
        SmartDashboard.putBoolean("Shooter TUNE", false);

        isInitialized = true;

        ballLiftSolenoid = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.BALLLIFT_FOWARD, Wiring.BALLLIFT_REVERSE);
        shooterPositionSolenoid_Stage1 = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.SHOOTERPOSITION_STAGE1_FOWARD, Wiring.SHOOTERPOSITION_STAGE1_REVERSE);
        shooterPositionSolenoid_Stage1 = new DoubleSolenoid(PneumaticsModuleType.REVPH, Wiring.SHOOTERPOSITION_STAGE2_FOWARD, Wiring.SHOOTERPOSITION_STAGE2_REVERSE);
    }

    public static void tick() {
        double shooterVelocityTarget = 0;

        if (!isInitialized) 
            return;

        shooterVelocityTarget = SmartDashboard.getNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED);

            if (isEnabled) {
                shooterPID.setReference(shooterVelocityTarget, ControlType.kVelocity);
                feederPID.setReference(SmartDashboard.getNumber("Feeder Setpoint", Calibration.FEEDER_DEFAULT_SPEED), ControlType.kVelocity);

                if (SmartDashboard.getBoolean("Shooter TUNE", true)) {
                    shooterPID.setFF(SmartDashboard.getNumber("Shoot F", 0));
                    shooterPID.setP(SmartDashboard.getNumber("Shoot P", 0));
                    shooterPID.setD(SmartDashboard.getNumber("Shoot D", 0));
                   
                    feederPID.setFF(SmartDashboard.getNumber("Shoot F", 0));
                    feederPID.setP(SmartDashboard.getNumber("Shoot P", 0));
                    feederPID.setD(SmartDashboard.getNumber("Shoot D", 0));
               }

                SmartDashboard.putNumber("SHOOTER VELOCITY", shooterMotor.getEncoder().getVelocity());
                SmartDashboard.putNumber("SHOOTER ERROR", shooterVelocityTarget - shooterMotor.getEncoder().getVelocity());
                
                System.out.println(timer);

                if (oneShot) {
                    timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                    setBallLiftUp();
                    if (timer >= 25) {
                        setBallLiftDown();
                        resetTimer();
                        oneShot = false;
                    }
                }

                if (continuousShooting) {
                    timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                    setBallLiftUp();
                    if (timer == 25) {
                        setBallLiftDown();
                    }
                    if (timer == 50) {
                        Intake.retractIntake();
                    }
                    if (timer == 100) {
                        setBallLiftUp();
                    }
                    if (timer == 125) {
                        setBallLiftDown();
                        resetTimer();
                        continuousShooting = false;
                    }
                }
                
            }
        
        SmartDashboard.putBoolean("Is At Speed", isAtSpeed());
    }

    public static void StartShooter() {
        isEnabled = true;
        oneShot = false;
        continuousShooting = false;
    }

    public static boolean isShooterEnabled () {
        return isEnabled;
    }

    public static void StopShooter() {
        isEnabled = false;
        
        if (!isInitialized) return;

        oneShot = false;
        continuousShooting = false;
        resetTimer();
        shooterPID.setReference(0, ControlType.kVelocity);  // set to 0 speed
        feederPID.setReference(0, ControlType.kVelocity);
    }

    public static void setAdjustmentFactor (double adjustmentFactor) {
        adjustmentFactor = (((adjustmentFactor + 1) / 2) + 2) * 0.4;
        Shooter.adjustmentFactor = adjustmentFactor;
        System.out.println(Shooter.adjustmentFactor);
    } 

    public static double getShooterSpeed() {
        if (!isInitialized) 
            return 0;
        else
            return shooterMotor.getEncoder().getVelocity();
    }

    public static boolean isAtSpeed() {
        if (!isInitialized) 
            return true;
        else
            return (getShooterSpeed() > 0 && Math.abs(getShooterSpeed() - targetSpeed) < 300);
    }

    public static void oneShot () {
        oneShot = true;
        continuousShooting = false;
    }

    public static void continuousShooting () {
        continuousShooting = true;
        oneShot = false;
    }

    public static boolean isGateOpen () {
        return isGateOpen;
    }

    public static void resetTimer () {
        timer = 0;
    }

    public static void stopTimer () {
        
    }

    public static void setShooterPosition(ShooterPosition position) {
        switch (position) {
            case Low:
                shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kReverse);
                shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kReverse);
                break;
            case Medium:
                shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kForward);
                shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kReverse);
                break;
            case  High:
                shooterPositionSolenoid_Stage1.set(DoubleSolenoid.Value.kForward);
                shooterPositionSolenoid_Stage2.set(DoubleSolenoid.Value.kForward);
                break;
        }
        curShooterPosition = position;
    }
    
    public static void setBallLiftUp() {
        ballLiftSolenoid.set(DoubleSolenoid.Value.kForward);
    }

    public static void setBallLiftDown() {
        ballLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    public static ShooterPosition getShooterPosition() {
        return curShooterPosition;
    }
}

