package frc.robot;

import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.vision.*;

public class VisionBall implements VisionRunner.Listener<VisionBallPipeline>
{
    private static UsbCamera  camera;
    private static int IMG_WIDTH = 640;
    private static int IMG_HEIGHT = 480;
    private static VisionThread visionThread;
    private static final Object imgLock = new Object();
    private static double centerX = 0.0;
    private static AtomicBoolean running = new AtomicBoolean(false);

    public static void init() {
        camera = CameraServer.startAutomaticCapture();
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);

        visionThread = new VisionThread(camera, new VisionBallPipeline(), pipeline -> {
            if (!pipeline.filterContoursOutput().isEmpty()) {
                Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
                synchronized (imgLock) {
                    centerX = r.x + (r.width / 2);
                }
            } else {
                centerX = IMG_WIDTH / 2; // default to being centered
            }
        });
    }

    public static void start() {
        visionThread.start();
    }

    public static void stop() {
        visionThread.interrupt();  // not sure this really does any good
    }

    public static double getBallXOffset() {
        return centerX - (IMG_WIDTH / 2);
    }

    @Override
    public void copyPipelineOutputs(VisionBallPipeline pipeline) {
        // TODO Auto-generated method stub
        
    }
}

