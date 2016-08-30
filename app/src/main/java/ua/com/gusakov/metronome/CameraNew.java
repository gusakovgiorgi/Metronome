package ua.com.gusakov.metronome;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasana on 6/28/2016.
 */
public class CameraNew extends Camera{
    Context ctx;
    private CameraManager cameraManager;
    private CameraCharacteristics cameraCharacteristics;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;

    private CaptureRequest.Builder mBuilder;

    Surface mSurface;
    List<Surface> list;
    SurfaceTexture mSurfaceTexture;

    CameraNew(Context ctx){
        this.ctx=ctx;
        init();
    }

    private void init() {
        Log.v("MyTag","camera initialized");
        cameraManager = (CameraManager)ctx.getSystemService(Context.CAMERA_SERVICE);
        try
        {
            String[] id = cameraManager.getCameraIdList();
            if (id != null && id.length > 0)
            {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(id[0]);
                boolean isFlash = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (isFlash)
                {
                    cameraManager.openCamera(id[0], mCameraCallback, null);
                }
            }
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera)
        {
            mCameraDevice = camera;
            // get builder
            try
            {
                mBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                list = new ArrayList<Surface>();
                mSurfaceTexture = new SurfaceTexture(1);
                Size size = getSmallestSize(mCameraDevice.getId());
                mSurfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
                mSurface = new Surface(mSurfaceTexture);
                list.add(mSurface);
                mBuilder.addTarget(mSurface);
                camera.createCaptureSession(list, mSessionCallBack, null);
            }
            catch (CameraAccessException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera)
        {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error)
        {

        }

    };

    private Size getSmallestSize(String cameraId) throws CameraAccessException
    {
        Size[] outputSizes = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(SurfaceTexture.class);
        if (outputSizes == null || outputSizes.length == 0)
        {
            throw new IllegalStateException("Camera " + cameraId + "doesn't support any outputSize.");
        }
        Size chosen = outputSizes[0];
        for (Size s : outputSizes)
        {
            if (chosen.getWidth() >= s.getWidth() && chosen.getHeight() >= s.getHeight())
            {
                chosen = s;
            }
        }
        return chosen;
    }
    private CameraCaptureSession.StateCallback mSessionCallBack = new CameraCaptureSession.StateCallback()
    {
        @Override
        public void onConfigured(CameraCaptureSession session)
        {
            mSession = session;
            try
            {
                mSession.setRepeatingRequest(mBuilder.build(), null, null);
            }
            catch (CameraAccessException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session)
        {

        }
    };
    public void turnOnFlashLight()
    {
        try
        {
            mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            mSession.setRepeatingRequest(mBuilder.build(), null, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void turnOffFlashLight()
    {
        try
        {
            mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
            mSession.setRepeatingRequest(mBuilder.build(), null, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
        Log.v("MyTag","Camera closed");
        if (mCameraDevice == null || mSession == null)
        {
            return;
        }
        mSession.close();
        mCameraDevice.close();
        mCameraDevice = null;
        mSession = null;
    }
}
