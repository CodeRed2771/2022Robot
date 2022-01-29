/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

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
    private static Servo gate = new Servo(Wiring.SERVO_PWM_ID);

    private static boolean isEnabled = false;
    private static boolean isGateOpen = false;
    private static boolean oneShot = false;
    private static boolean continuousShooting = false;
    private static int timer = 0;
    private static double targetSpeed = Calibration.SHOOTER_DEFAULT_SPEED;
    private static double adjustmentFactor = 1;
    private static SparkMaxPIDController shooterPID;
    private static SparkMaxPIDController feederPID;

    public static void init() {
        shooterMotor.restoreFactoryDefaults();
        shooterMotor.setOpenLoopRampRate(.1);
        shooterMotor.setSmartCurrentLimit(40);
        shooterMotor.setIdleMode(IdleMode.kCoast);
        
        shooterPID = shooterMotor.getPIDController();
        shooterPID.setP(Calibration.SHOOTER_P);
        shooterPID.setI(0);
        shooterPID.setD(Calibration.SHOOTER_D);
        shooterPID.setFF(Calibration.SHOOTER_F);
        shooterPID.setOutputRange(-1, 1);

        shooterPID.setReference(0, ControlType.kSmartVelocity);  // set to 0 speed

        feederMotor.restoreFactoryDefaults();
        feederMotor.setOpenLoopRampRate(.1);
        feederMotor.setSmartCurrentLimit(40);
        feederMotor.setIdleMode(IdleMode.kCoast);
        
        feederPID = shooterMotor.getPIDController();
        feederPID.setP(Calibration.SHOOTER_P);
        feederPID.setI(0);
        feederPID.setD(Calibration.SHOOTER_D);
        feederPID.setFF(Calibration.SHOOTER_F);
        feederPID.setOutputRange(-1, 1);

        feederPID.setReference(0, ControlType.kSmartVelocity);  // set to 0 speed
		 	
		SmartDashboard.putNumber("Shoot P", Calibration.SHOOTER_P);
		SmartDashboard.putNumber("Shoot D", Calibration.SHOOTER_D);
        SmartDashboard.putNumber("Shoot F", Calibration.SHOOTER_F);
        SmartDashboard.putNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED);

        closeGate();
    }

    public static void tick() {

        if (SmartDashboard.getBoolean("Shooter TUNE", true)) {
			shooterPID.setFF(SmartDashboard.getNumber("Shoot F", 0));
			shooterPID.setP(SmartDashboard.getNumber("Shoot P", 0));
            shooterPID.setD(SmartDashboard.getNumber("Shoot D", 0));
            
            feederPID.setFF(SmartDashboard.getNumber("Shoot F", 0));
            feederPID.setP(SmartDashboard.getNumber("Shoot P", 0));
            feederPID.setD(SmartDashboard.getNumber("Shoot D", 0));

        }

            if (isEnabled) {
                shooterPID.setReference(SmartDashboard.getNumber("Shoot Setpoint", Calibration.SHOOTER_DEFAULT_SPEED), ControlType.kSmartVelocity);
                SmartDashboard.putNumber("SHOOTER VELOCITY", shooterMotor.getEncoder().getVelocity());

                System.out.println(timer);

                if (oneShot) {
                    timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                    openGate();
                    if (timer >= 3) {
                        closeGate();
                        resetTimer();
                        oneShot = false;
                    }
                }

                if (continuousShooting) {
                    timer += 1; // ONE TIMER UNIT EQUALS ABOUT 20 MILLISECONDS
                    openGate();
                    if (timer >= 200) {
                        closeGate();
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
        closeGate();
        oneShot = false;
        continuousShooting = false;
        resetTimer();
        shooterPID.setReference(0, ControlType.kSmartVelocity);  // set to 0 speed
    }

    public static void setAdjustmentFactor (double adjustmentFactor) {
        adjustmentFactor = (((adjustmentFactor + 1) / 2) + 2) * 0.4;
        Shooter.adjustmentFactor = adjustmentFactor;
        System.out.println(Shooter.adjustmentFactor);
    } 

    public static double getShooterSpeed() {
        return shooterMotor.getEncoder().getVelocity();
    }

    public static boolean isAtSpeed() {
        return (getShooterSpeed() > 0 && Math.abs(getShooterSpeed() - targetSpeed) < 300);
    }

    public static void closeGate () {
       
        if (Calibration.isPracticeBot()) {
            gate.set(0.8); // PRACTICE
        } else {
            gate.set(0.45); // COMP BOT
        }
       
        isGateOpen = false;
    }

    public static void openGate () {
        if (Calibration.isPracticeBot()) {
            gate.set(0.50); // PRACTICE
        } else {
            gate.set(0.31); // COMP BOT
        }
       
        isGateOpen = true;
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



}

