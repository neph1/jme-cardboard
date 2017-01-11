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
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadMountedDisplayManager;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;
import com.jme3.input.android.AndroidInputHandler;
import com.jme3.input.android.AndroidInputHandler14;
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
    private HeadTransform mHeadTransform;
    private CardboardView mView;
    private CardboardDeviceParams mParams;
    
    public CardboardContext(CardboardDeviceParams params){
        mParams = params;
    }
    
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
        } else if (Build.VERSION.SDK_INT < 9){
            throw new UnsupportedOperationException("jME3 requires Android 2.3 or later");
        }
//        if(mParams == null){
//            mParams = new CardboardDeviceParams();
//            mParams.setInterLensDistance(0.45f);
//            mParams.setScreenToLensDistance(0.045f);
//        }
//        
//        mHMDManager = new HeadMountedDisplayManager(context);
//        mHMDManager.getHeadMountedDisplay().setCardboardDeviceParams(mParams);
        // Start to set up the view
        mView = new CardboardView(context);
        if (androidInput == null) {
            if (Build.VERSION.SDK_INT >= 14) {
                androidInput = new AndroidInputHandler14();
            } else if (Build.VERSION.SDK_INT >= 9){
                androidInput = new AndroidInputHandler();
            }
        }
        androidInput.setView(mView);
        androidInput.loadSettings(settings);

	mView.setFocusable(true);

//        int curAlphaBits = settings.getAlphaBits();
//        logger.log(Level.FINE, "curAlphaBits: {0}", curAlphaBits);
//        if (curAlphaBits >= 8) {
//            logger.log(Level.FINE, "Pixel Format: TRANSLUCENT");
//            mView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//            mView.setZOrderOnTop(true);
//        } else if (curAlphaBits >= 1) {
//            logger.log(Level.FINE, "Pixel Format: TRANSPARENT");
//            mView.getHolder().setFormat(PixelFormat.TRANSPARENT);
//        } else {
//            logger.log(Level.FINE, "Pixel Format: OPAQUE");
//            mView.getHolder().setFormat(PixelFormat.OPAQUE);
//        }

        AndroidConfigChooser configChooser = new AndroidConfigChooser(settings);
        mView.setEGLConfigChooser(configChooser);
        mView.setRenderer((Renderer)this);
        
        if (Build.VERSION.SDK_INT >= 11) {
            mView.setPreserveEGLContextOnPause(true);
        }
        logger.log(Level.INFO, "Cardboard view created ");
        return mView;
    }

    
    @Override
    public void onDrawFrame(HeadTransform ht, Eye eye, Eye eye1) {
        ht.getHeadView(temp, 0);
        this.mHeadTransform = ht;
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
        return mHeadTransform;
    }

    public void resetHeadtracker(){
        mView.resetHeadTracker();
    }

    public CardboardDeviceParams getCardboardDeviceParams() {
        return mParams;
    }

    public void setCardboardDeviceParams(CardboardDeviceParams mParams) {
        this.mParams = mParams;
    }
    
    
}
