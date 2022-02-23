package frc.robot;

import java.util.Scanner;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnPosition {
    private static boolean reverseBoolean;
    private static double bestPos;
    private static double intergerCurrentPos;
    private static double regular; 
    private static double regularPlusOffset;
    private static double regularMinusOffset;
    private static double currentDecimal;
    private static double reverseDecimal;
    private static double reverse;
    private static double reversePlusOffset;
    private static double reverseMinusOffset;
    private static double reverseBestOffset = 1;
    private static double regularBestOffset = 1;
    public static double getNewTurnPosition(double currentPosition, double newPosition) {
        intergerCurrentPos = Math.round(currentPosition);
        if (currentPosition >= 0) {
            regular = intergerCurrentPos + newPosition;
            regularPlusOffset = (regular + 1) - currentPosition;
            regularMinusOffset = Math.abs(currentPosition - regular);
            currentDecimal = currentPosition - intergerCurrentPos;
            reverseDecimal = 1 - currentDecimal;
            reverse = intergerCurrentPos + reverseDecimal;
            reversePlusOffset = (reverse+1) - currentPosition;
            reverseMinusOffset = Math.abs(currentPosition - reverse);
        } else {
            regular = intergerCurrentPos - newPosition;
            regularPlusOffset = Math.abs((regular - 1) + currentPosition);
            regularMinusOffset = Math.abs(currentPosition + regular);
            currentDecimal = Math.abs(currentPosition) + intergerCurrentPos;
            reverseDecimal = 1 - currentDecimal;
            reverse = intergerCurrentPos - reverseDecimal;
            reversePlusOffset = (reverse-1) + currentPosition;
            reverseMinusOffset = Math.abs(currentPosition + reverse);
        }
        double regularBestPos;
        double reverseBestPos;

        
        // Regular
        if (regularPlusOffset <= regularMinusOffset) {
            if (currentPosition >= 0 ) {
                regularBestPos = regular + 1;
            } else {
                regularBestPos = regular - 1;
            }
            regularBestOffset = regularPlusOffset; 
        } else {
            regularBestPos =  regular; 
            reverseBestOffset = regularMinusOffset;
        }

        // Reverse 
        if (reversePlusOffset <= reverseMinusOffset) {
            if (currentPosition <= 0) {
                reverseBestPos = reverse + 1;
            } else {
                reverseBestPos = reverse -1;
            }
            reverseBestOffset = reversePlusOffset; 
            
        } else {
            reverseBestPos = reverse; 
            reverseBestOffset = reverseMinusOffset;
        }

        // Regular vs. Reverse
        if (regularBestOffset <= reverseBestOffset) {
            bestPos = regularBestPos;
            reverseBoolean = false;
        } else {
            bestPos = reverseBestPos;
            reverseBoolean = true;
        }

        SmartDashboard.putNumber("Optomized Encoder Position", bestPos);
        return bestPos; 
    }
    public static double getBestPosition() {
        return bestPos;
    }
    public static void main(String[] args) {
        Scanner variablesSanner = new Scanner(System.in);
        String variables = variablesSanner.nextLine();
        while (true) {
            boolean returnValues;
            switch (variables) {
                case "All": 
                    returnValues = true;
                    Scanner currentValueScanner = new Scanner(System.in);
                    double currentValue = currentValueScanner.nextDouble();
                    Scanner newValueScanner = new Scanner(System.in);
                    double newValue =  newValueScanner.nextDouble();
                    //System.in.println("Value" + TurnPosition.getNewTurnPosition(currentValue, newValue));
                    break;
                default: 
                    returnValues = false;

            }
        }
    }
}
