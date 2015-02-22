/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.state;

import com.google.vrtoolkit.cardboard.Viewport;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.android.CardboardContext;
import com.jme3.util.TempVars;

/**
 *
 * @author reden
 */
public class CardboardState extends AbstractAppState{

    private SimpleApplication app;
    Camera camLeft,camRight;
    ViewPort viewPortLeft, viewPortRight;
    private CardboardContext context;
    private Matrix4f transformMatrix;
    private Quaternion directionQuat;
    
    private boolean viewPortSet = false;
    public CardboardState(CardboardContext hmd){
        this.context = hmd;
        transformMatrix = new Matrix4f();
        directionQuat = new Quaternion();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        
        viewPortLeft = app.getViewPort();
        camLeft = app.getCamera();
        camLeft.setLocation(Vector3f.ZERO);
        camLeft.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        setupFiltersAndViews();
        directionQuat.lookAt(Vector3f.UNIT_Z.negate(), Vector3f.UNIT_Y);
        transformMatrix.setRotationQuaternion(directionQuat);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(context.getLeftEye() == null){
            return;
        }
        TempVars tempVars = TempVars.get();
        
        
        if(!viewPortSet){
            Viewport v = context.getLeftEye().getViewport();
            
            
            camLeft.setViewPort((float)v.x/ context.getSettings().getWidth(), (float)v.width / context.getSettings().getWidth(), (float)v.y/ context.getSettings().getHeight(), (float)v.height / context.getSettings().getHeight());
            
            v = context.getRightEye().getViewport();
            camRight.setViewPort((float)v.x/ context.getSettings().getWidth(), (float)(v.x + v.width) / context.getSettings().getWidth(), (float)v.y/ context.getSettings().getHeight(), (float)v.height / context.getSettings().getHeight());
            viewPortSet = true;
        }
        // left eye
        tempVars.tempMat4.set(context.getLeftEye().getEyeView());
        tempVars.tempMat4.multLocal(transformMatrix);
        tempVars.tempMat4.toTranslationVector(tempVars.vect1);
        tempVars.tempMat4.toRotationQuat(tempVars.quat1);
        camLeft.setFrame(tempVars.vect1, tempVars.quat1);
        
        // right eye
        tempVars.tempMat4.set(context.getRightEye().getEyeView());
        tempVars.tempMat4.multLocal(transformMatrix);
        tempVars.tempMat4.toTranslationVector(tempVars.vect1);
        tempVars.tempMat4.toRotationQuat(tempVars.quat1);
        
        camRight.setFrame(tempVars.vect1, tempVars.quat1);
        tempVars.release();
    }

    @Override
    public void render(RenderManager rm) {
        super.render(rm);
        
    }
    
    
    
    private void setupFiltersAndViews() {
        camRight = camLeft.clone();
        
        viewPortRight = app.getRenderManager().createPreView("Right viewport", camRight);
        viewPortRight.setClearFlags(true, true, true);
        viewPortRight.attachScene(this.app.getRootNode());

        camLeft.setViewPort(0.0f, 0.5f, 0.0f, 1.0f);
        camRight.setViewPort(0.5f, 1f, 0.0f, 1f);
        
    }
    
}
