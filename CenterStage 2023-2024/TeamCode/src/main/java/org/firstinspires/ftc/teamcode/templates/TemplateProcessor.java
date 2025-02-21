package org.firstinspires.ftc.teamcode.templates;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.concurrent.atomic.AtomicReference;

public class TemplateProcessor implements VisionProcessor, CameraStreamSource {

    Telemetry telemetry;

    //Streaming
    private final AtomicReference<Bitmap> lastFrame =
            new AtomicReference<>(Bitmap.createBitmap(1,1,Bitmap.Config.RGB_565));

    public TemplateProcessor(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        // Executed before the first call to processFrame

        //Streaming
        lastFrame.set(Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565));
    }

    @Override
    public Mat processFrame(Mat input, long captureTimeNanos) {
        // Executed every time a new frame is dispatched


        //Streaming
        Bitmap b = Bitmap.createBitmap(input.width(),input.height(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(input,b);
        lastFrame.set(b);
        return input; // Return the image that will be displayed in the viewport
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight,
                            float scaleBmpPxToCanvasPx, float scaleCanvasDensity,
                            Object userContext) {

    }

    //Streaming
    @Override
    public void getFrameBitmap(Continuation<? extends Consumer<Bitmap>> continuation) {
        continuation.dispatch(bitmapConsumer -> bitmapConsumer.accept(lastFrame.get()));
    }
}