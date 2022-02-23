package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurnPosition {
    private static boolean reverseBoolean;
    private static double bestPos;
    public static double getNewTurnPosition(double currentPosition, double newPosition) {
        double intergerCurrentPos = Math.round(currentPosition);
        double regular; 
        double regularPlusOffset;
        double regularMinusOffset;
        double currentDecimal;
        double reverseDecimal;
        double reverse;
        double reversePlusOffset;
        double reverseMinusOffset;
        double reverseBestOffset = 1;
        double regularBestOffset = 1;
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
}
