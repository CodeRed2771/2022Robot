package frc.robot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.vision.*;

public class VisionBall implements VisionRunner.Listener<VisionBallPipelineRed>
{
    private static UsbCamera  camera;
    private static int IMG_WIDTH = 640;
    private static int IMG_HEIGHT = 480;
    private static VisionThread visionThread;
    private static final Object imgLock = new Object();
    private static double centerX = 0.0;
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static double centerY = 0.0;
    private static AtomicBoolean foundBall = new AtomicBoolean(false);
    private static int closestBall = 0;
    private static int ballAmount = 0;
    private static double bestScore = 0;
    private static double currentScore = 0;

    

    public static double distance(double xPosition, double yPosition) {
        return Math.sqrt(Math.pow(xPosition, 2)+Math.pow(yPosition,2));
    }
    
    public static double algorithim(Rect re) {
        double cenX, cenY, dis, width;
        cenX = re.x + (re.width / 2);
        cenY = re.y + (re.height/2);
        width = re.width;
        dis = distance(cenX, cenY);
        currentScore = (width+cenY)/2-dis;
        return currentScore;
    }

    public static Mat findClosestBall(ArrayList<MatOfPoint> ballsFound) {
            if (ballsFound.size() <= 1) {
                closestBall = 0;
                algorithim(Imgproc.boundingRect(ballsFound.get(0)));
            } else {
                for (int i = 0; ballsFound.size() > i; i ++) {
                    algorithim(Imgproc.boundingRect(ballsFound.get(i))); // Determining Closest Ball; Change as needed
                    if (bestScore < currentScore) {
                        bestScore = currentScore;
                        closestBall = i;
                    }
                }

            }
        ballAmount = ballsFound.size();
        return ballsFound.get(closestBall);
    }

    public static int getBallIndex() {
        return closestBall;
    }

    public static void init(String allianceColor) {
        camera = CameraServer.startAutomaticCapture();
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);

        if (allianceColor == "R") {
            visionThread = new VisionThread(camera, new VisionBallPipelineRed(), pipeline -> {
                if (!pipeline.filterContoursOutput().isEmpty()) {

                    Rect r = Imgproc.boundingRect(VisionBall.findClosestBall(pipeline.filterContoursOutput()));
                    synchronized (imgLock) {
                        centerX = r.x + (r.width / 2);
                        centerY = r.y + (r.height/2);
                    }
                    foundBall.set(true);
                } else {
                    centerX = IMG_WIDTH / 2; // default to being centered
                    centerY = IMG_HEIGHT / 2;
                    ballAmount = 0;
                    foundBall.set(false);
                    bestScore = 0;
                }
            });
        } else {
            visionThread = new VisionThread(camera, new VisionBallPipelineBlue(), pipeline -> {
                if (!pipeline.filterContoursOutput().isEmpty()) {
                    Rect r = Imgproc.boundingRect(VisionBall.findClosestBall(pipeline.filterContoursOutput()));
                    synchronized (imgLock) {
                        centerX = r.x + (r.width / 2);
                        centerY = r.y + (r.height/2);
                    }
                    foundBall.set(true);
                } else {
                    centerX = IMG_WIDTH / 2; // default to being centered
                    centerY = IMG_HEIGHT / 2;
                    ballAmount = 0;
                    foundBall.set(false);
                }
            });
        }
       
    }

    public static void start() {
        visionThread.start();
    }

    public static void stop() {
        visionThread.interrupt();  // not sure this really does any good
    }

    public static int getBallNumber() {
        return ballAmount;
    }

    public static double getBallXOffset() {
        return centerX - (IMG_WIDTH / 2);
    }
    public static double getBallYOffset() {
        return centerY - (IMG_HEIGHT / 2);
    }
    @Override
    public void copyPipelineOutputs(VisionBallPipelineRed pipeline) {
        // TODO Auto-generated method stub
        
    }

    public static boolean ballInView() {
        return foundBall.compareAndSet(true, true);
    }

    public static double getBallScore() {
        return bestScore;
    }

    public static double degreesToBall() {
        double degrees;
        if (centerY/centerX > 0) {
            degrees = Math.atan(centerY/centerX);
        } else {
            degrees = Math.abs(Math.atan(centerY/centerX)) + 180;
        }
        return degrees;
    }

    public static double distnaceToBall() {
        return distance(centerX, centerY);
    }
}