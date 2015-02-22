/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.system.android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import com.google.vrtoolkit.cardboard.CardboardDeviceParams;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.CardboardView.Renderer;
import com.google.vrtoolkit.cardboard.CardboardView.StereoRenderer;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadMountedDisplayManager;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;
import com.jme3.input.android.AndroidInputHandler;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.khronos.egl.EGLConfig;

/**
 *
 * @author reden
 */
public class CardboardContext extends OGLESContext implements CardboardView.Renderer{
    
    private static final Logger logger = Logger.getLogger(CardboardContext.class.getName());
    
    private Quaternion orientation = new Quaternion();
    private Vector3f position = new Vector3f();
    private Matrix4f tempMat = new Matrix4f();
    private float[] temp = new float[16];
    private Eye leftEye;
    private Eye rightEye;
    private HeadMountedDisplayManager mHMDManager;
    private HeadTracker mHeadTracker;
    private HeadTransform ht;
    
    public CardboardContext(){
        
    }
    
    @Override
    public GLSurfaceView createView(Context context) {
        // NOTE: We assume all ICS devices have OpenGL ES 2.0.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // below 4.0, check OpenGL ES 2.0 support.
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            if (info.reqGlEsVersion < 0x20000) {
                throw new UnsupportedOperationException("OpenGL ES 2.0 is not supported on this device");
            }
        }
        CardboardDeviceParams params = new CardboardDeviceParams();
        params.setInterLensDistance(0.45f);
        params.setScreenToLensDistance(0.045f);

        mHMDManager = new HeadMountedDisplayManager(context);
        mHMDManager.getHeadMountedDisplay().setCardboardDeviceParams(params);
//        mHMDManager.getHeadMountedDisplay();
        // Start to set up the view
        CardboardView view = new CardboardView(context);
        if (androidInput == null) {
            androidInput = new AndroidInputHandler();
        }
        androidInput.setView(view);
        androidInput.loadSettings(settings);

        // setEGLContextClientVersion must be set before calling setRenderer
        // this means it cannot be set in AndroidConfigChooser (too late)
        view.setEGLContextClientVersion(2);

        view.setFocusableInTouchMode(true);
        view.setFocusable(true);

        // setFormat must be set before AndroidConfigChooser is called by the surfaceview.
        // if setFormat is called after ConfigChooser is called, then execution
        // stops at the setFormat call without a crash.
        // We look at the user setting for alpha bits and set the surfaceview
        // PixelFormat to either Opaque, Transparent, or Translucent.
        // ConfigChooser will do it's best to honor the alpha requested by the user
        // For best rendering performance, use Opaque (alpha bits = 0).
        int curAlphaBits = settings.getAlphaBits();
        logger.log(Level.FINE, "curAlphaBits: {0}", curAlphaBits);
        if (curAlphaBits >= 8) {
            logger.log(Level.FINE, "Pixel Format: TRANSLUCENT");
            view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            view.setZOrderOnTop(true);
        } else if (curAlphaBits >= 1) {
            logger.log(Level.FINE, "Pixel Format: TRANSPARENT");
            view.getHolder().setFormat(PixelFormat.TRANSPARENT);
        } else {
            logger.log(Level.FINE, "Pixel Format: OPAQUE");
            view.getHolder().setFormat(PixelFormat.OPAQUE);
        }

        AndroidConfigChooser configChooser = new AndroidConfigChooser(settings);
        view.setEGLConfigChooser(configChooser);
        view.setRenderer((Renderer)this);

        // Attempt to preserve the EGL Context on app pause/resume.
        // Not destroying and recreating the EGL context
        // will help with resume time by reusing the existing context to avoid
        // reloading all the OpenGL objects.
        if (Build.VERSION.SDK_INT >= 11) {
            view.setPreserveEGLContextOnPause(true);
        }
        logger.log(Level.INFO, "Cardboard view created ");
        return view;
    }

    @Override
    public void onDrawFrame(HeadTransform ht, Eye eye, Eye eye1) {
        ht.getHeadView(temp, 0);
        this.ht = ht;
        tempMat.set(temp);
        tempMat.toRotationQuat(orientation);
        tempMat.toTranslationVector(position);
        leftEye = eye;
        rightEye = eye1;
        // apply eye view to camera matrix
        
        
        super.onDrawFrame(null);
    }

    @Override
    public void onFinishFrame(Viewport vwprt) {
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(null, width, height);
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglc) {
        super.onSurfaceCreated(null, eglc);
    }

    @Override
    public void onRendererShutdown() {
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Eye getLeftEye() {
        return leftEye;
    }

    public Eye getRightEye() {
        return rightEye;
    }

    public HeadTransform getHt() {
        return ht;
    }

    
}
