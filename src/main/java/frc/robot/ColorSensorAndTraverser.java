package frc.robot;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DriverStation;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;

public class ColorSensorAndTraverser {

    private static final TalonSRX spinMotor = new TalonSRX(Wiring.COLOR_WHEEL_SPINNER_AND_TRAVERSER_ID);
    private static final I2C.Port i2cPort = I2C.Port.kOnboard;
    private static final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
    private static final ColorMatch colorMatcher = new ColorMatch();
    private static final Color BlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429); // NEED TO BE CALIBRATED
    private static final Color GreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240); // NEED TO BE CALIBRATED
    private static final Color RedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114); // NEED TO BE CALIBRATED
    private static final Color YellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113); // NEED TO BE CALIBRATED
    private static boolean isSpinning = false;
    private static ColorSensorAndTraverser instance;
    private static int timesColorPassed = 0;
    private static String colorString;
    private static Color lastColorSeen = BlueTarget;
    private static boolean spinningCompleted = false;
    private static String gameData = DriverStation.getInstance().getGameSpecificMessage();
    private static boolean isRunning = false;
    private static boolean runTrue = false;

    public ColorSensorAndTraverser() {
        colorMatcher.addColorMatch(BlueTarget);
        colorMatcher.addColorMatch(GreenTarget);
        colorMatcher.addColorMatch(RedTarget);
        colorMatcher.addColorMatch(YellowTarget);
    }

    public static ColorSensorAndTraverser getInstance() {
        if (instance == null) {
            instance = new ColorSensorAndTraverser();
        }
        return instance;
    }

    public static void start3To5TimesSpinning() {
        isSpinning = true;
        timesColorPassed = 0;
        spinMotor.set(ControlMode.PercentOutput, -.5);
        lastColorSeen = colorSensor.getColor();
        spinningCompleted = false;
    }

    public static void tick() {
        // SPINMOTOR NEEDS TO SPIN 131072 TICKS TO SPIN CONTROL PANEL 4 TIMES WITHOUT
        // SLIPPAGE
        if (isSpinning == true) {
            Color currentColor = colorSensor.getColor();
            ColorMatchResult match = colorMatcher.matchClosestColor(currentColor);
            if (match != null && match.color == RedTarget && lastColorSeen != RedTarget) {
                timesColorPassed++;
                lastColorSeen = match.color;
            }
            lastColorSeen = match.color;

            if (match.color == BlueTarget) {
                colorString = "Blue";
            } else if (match.color == RedTarget) {
                colorString = "Red";
            } else if (match.color == GreenTarget) {
                colorString = "Green";
            } else if (match.color == YellowTarget) {
                colorString = "Yellow";
            } else {
                colorString = "Unknown";
            }

            // SmartDashboard.putNumber("Red", currentColor.red);
            // SmartDashboard.putNumber("Green", currentColor.green);
            // SmartDashboard.putNumber("Blue", currentColor.blue);
            SmartDashboard.putNumber("Confidence", match.confidence);
            SmartDashboard.putNumber("colorROT", timesColorPassed);
            SmartDashboard.putString("Detected Color", colorString);

            if (timesColorPassed >= 7) {
                spinMotor.set(ControlMode.PercentOutput, 0);
                isSpinning = false;
                currentColor = null;
                spinningCompleted = true;
            }
        }
    }

    public boolean isSpinningCompleted() {
        return spinningCompleted;
    }

    public void stopSpinning() {
        spinMotor.set(ControlMode.PercentOutput, 0);
    }

    public static void startMatchColorSpinning() {
        spinMotor.set(ControlMode.PercentOutput, .5);
        isRunning = true;
    }

    public static void matchColor() {
        if (isRunning == true) {
            if (gameData.length() == 0) {
                gameData = DriverStation.getInstance().getGameSpecificMessage();
            }
            if (gameData.length() > 0) {
                switch (gameData.charAt(0)) {
                case 'B':
                    Color blue = colorSensor.getColor();
                    ColorMatchResult Blue = colorMatcher.matchClosestColor(blue);
                    if (Blue.color == RedTarget) {
                        spinMotor.set(ControlMode.PercentOutput, 0);
                        isRunning = false;
                    }
                    break;
                case 'G':
                    Color green = colorSensor.getColor();
                    ColorMatchResult Green = colorMatcher.matchClosestColor(green);
                    if (Green.color == YellowTarget) {
                        spinMotor.set(ControlMode.PercentOutput, 0);
                        isRunning = false;
                    }
                    break;
                case 'R':
                    Color red = colorSensor.getColor();
                    ColorMatchResult Red = colorMatcher.matchClosestColor(red);
                    if (Red.color == BlueTarget) {
                        spinMotor.set(ControlMode.PercentOutput, 0);
                        isRunning = false;
                    }
                    break;
                case 'Y':
                    Color yellow = colorSensor.getColor();
                    ColorMatchResult Yellow = colorMatcher.matchClosestColor(yellow);
                    if (Yellow.color == GreenTarget) {
                        spinMotor.set(ControlMode.PercentOutput, 0);
                        isRunning = false;
                    }
                    break;
                default:
                    System.out.println("ERROR STATE");
                    break;
                }
            } else {
                // NOTHING RECEIVED
            }
        }
    }

    public static void levelScale () {
        if (runTrue) {
            // THIS IS THE ACTUAL FUNCTION THAT LEVELS THE SCALE
        }
    }

    public static void runTrue (boolean runTrue) {
        ColorSensorAndTraverser.runTrue = runTrue;
    }
}