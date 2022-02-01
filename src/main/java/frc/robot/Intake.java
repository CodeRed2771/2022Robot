package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.libs.CurrentBreaker;

public class Intake {

    private static CANSparkMax intakeMotor;
    public static CurrentBreaker currentBreaker;
    private static boolean running = false;

    public static void init() {
        intakeMotor = new CANSparkMax(Wiring.INTAKE_MOTOR_ID, MotorType.kBrushless);
        intakeMotor.restoreFactoryDefaults(); 
        intakeMotor.setClosedLoopRampRate(0.5);
        intakeMotor.setSmartCurrentLimit(20);
        intakeMotor.setIdleMode(IdleMode.kBrake);
    }

    public static void startIntake() {
        intakeMotor.set(.5);
        running = true; 
    }
    
    public static void reverseIntake() {
        intakeMotor.set(-.5);
        running = true; 
    }

    public static void stopIntake() {
        intakeMotor.set(0);
        running = false; 
    }

    public static boolean isRunning() {
        return running;
    }

}