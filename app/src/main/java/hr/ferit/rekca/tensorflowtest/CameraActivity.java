package hr.ferit.rekca.tensorflowtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Collections;

public class CameraActivity extends AppCompatActivity {

    //TODO: Clean this code, put it an another class

    static String TAG = "Davidgay";

    static int lowestCamResolution = 400;

    int cameraFacing;
    CameraManager cameraManager;
    TextureView.SurfaceTextureListener surfaceTextureListener;
    static int CAMERA_REQUEST_CODE = 101;
    String cameraId;
    Size previewSize;
    CameraDevice.StateCallback stateCallback;
    CameraDevice cameraDevice;
    HandlerThread backgroundThread;
    Handler backgroundHandler;
    CameraCaptureSession cameraCaptureSession;
    CaptureRequest captureRequest;
    CaptureRequest.Builder captureRequestBuilder;

    public static Bundle pictureStorage;
    public static String pictureKey = "screenshot1";

    TextureView previewScreen;
    Button takePicture;
    Bitmap picture;

    Intent intent;

    Size[] testtest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Log.d(TAG, "created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_screen);

        previewScreen = findViewById(R.id.texVCameraPreview);
        takePicture = findViewById(R.id.btnTakePicture);
        takePicture.setEnabled(false);

        initListeners();

        intent = new Intent(this  , MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;


        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                Log.d(TAG, "Opened");
                CameraActivity.this.cameraDevice = cameraDevice;
                createPreviewSession();

            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                Log.d(TAG, "dc");
                cameraDevice.close();
                CameraActivity.this.cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                Log.d(TAG, "error");
                cameraDevice.close();
                CameraActivity.this.cameraDevice = null;
            }
        };
    }

    private void initListeners() {
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    takePicture.setEnabled(false);
                    Log.d("click", "clickCAM");
                    lock();
                    try {
                        createImageFromBitmap(previewScreen.getBitmap());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        unlock();
                        startActivityIfNeeded(intent,0);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public Bitmap GetPicture(){
        return picture;
    }



    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "TensorFlowTestImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }


    @Override
    protected void onResume() {

        takePicture.setEnabled(true);

        Log.d(TAG, "resume");

        super.onResume();
        openBackgroundThread();
        if (previewScreen.isAvailable()) {

            setUpCamera();
            openCamera();


        } else {
            previewScreen.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onStop() {

        Log.d(TAG, "stop");
        super.onStop();
        closeCamera();
        closeBackgroundThread();
    }

    class OpenCameraTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            setUpCamera();
            openCamera();
            return null;
        }
    }

    class CloseCameraTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            closeCamera();
            closeBackgroundThread();
            return null;
        }
    }

    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
    


    private void setUpCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    this.cameraId = cameraId;

                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    Size[] camResolutions = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                    for(int i=0;i<camResolutions.length; i++){
                        if((camResolutions[i].getHeight() == camResolutions[i].getWidth()) && camResolutions[i].getWidth() > lowestCamResolution){
                            previewSize = camResolutions[i];
                        }

                    }
                    Log.d(TAG, String.valueOf(previewSize.getHeight()) + " " + String.valueOf(previewSize.getWidth()));
                    adjustAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = previewScreen.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (cameraDevice == null) {
                                return;
                            }

                            try {
                                captureRequest = captureRequestBuilder.build();
                                CameraActivity.this.cameraCaptureSession = cameraCaptureSession;
                                CameraActivity.this.cameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void lock() {
        try {
            cameraCaptureSession.capture(captureRequestBuilder.build(),
                    null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void unlock() {
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
                    null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = previewScreen.getWidth();
        int viewHeight = previewScreen.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Log.v(TAG, "video=" + videoWidth + "x" + videoHeight +
                " view=" + viewWidth + "x" + viewHeight +
                " newView=" + newWidth + "x" + newHeight +
                " off=" + xoff + "," + yoff);

        Matrix txform = new Matrix();
        previewScreen.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff, yoff);
        previewScreen.setTransform(txform);
    }

}
