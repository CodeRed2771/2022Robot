package frc.robot;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.vision.*;

public class VisionBall implements VisionRunner.Listener<VisionBallPipeline>
{
    private UsbCamera  camera;
    private int IMG_WIDTH = 640;
    private int IMG_HEIGHT = 480;
    private VisionThread visionThread;
    private final Object imgLock = new Object();
    private double centerX = 0.0;

    public void init() {
        camera = CameraServer.startAutomaticCapture();
        camera.setResolution(IMG_WIDTH, IMG_HEIGHT);

        visionThread = new VisionThread(camera, new VisionBallPipeline(), pipeline -> {
            if (!pipeline.filterContoursOutput().isEmpty()) {
                Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
                synchronized (imgLock) {
                    centerX = r.x + (r.width / 2);
                }
            } else {
                centerX = 0;
            }
        });
        
        visionThread.start();
    }

    public double getBallXOffset() {
        double centerX;
        synchronized (imgLock) {
            centerX = this.centerX;
        }
        return centerX - (IMG_WIDTH / 2);
    }

    @Override
    public void copyPipelineOutputs(VisionBallPipeline pipeline) {
        // TODO Auto-generated method stub
        
    }
}

