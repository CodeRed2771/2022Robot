package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.libs.CurrentBreaker;

public class Intake {

    private static Intake instance;
    private static CANSparkMax intakeMotor;
    private static DoubleSolenoid pistonArm1;
    private static Compressor compressor;
    private static int BallCount = 0;
    public static CurrentBreaker currentBreaker;

    private static DoubleSolenoid queuer;

    public Intake() {

        pistonArm1 = new DoubleSolenoid(0, 1);
        intakeMotor = new CANSparkMax(Wiring.INTAKE_MOTOR_ID, MotorType.kBrushless);
        compressor = new Compressor();
        intakeMotor.setClosedLoopRampRate(0.5);
        intakeMotor.setSmartCurrentLimit(20);

        queuer = new DoubleSolenoid(2, 3);
    }

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake();
        }
        return instance;
    }

    public static boolean isIntakeDown() {
        return (pistonArm1.get() == DoubleSolenoid.Value.kReverse);
    }

    public static void moveIntakeDown() {
        pistonArm1.set(DoubleSolenoid.Value.kReverse);
    }

    public static void moveIntakeUp() {
        pistonArm1.set(DoubleSolenoid.Value.kForward);
    }

    public static void moveQueuerDown() {
        queuer.set(DoubleSolenoid.Value.kForward);
    }

    public static void moveQueruerUp() {
        queuer.set(DoubleSolenoid.Value.kReverse);
    }

    public static void runIntakeForwards() {
        intakeMotor.set(-.6);
    }

    public static void stopIntake() {
        intakeMotor.set(0);
    }

    public static void runIntakeBackwards() {
        intakeMotor.set(.5);
    }
}