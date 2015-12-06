/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jme3.state.CardboardState;
import com.jme3.system.JmeSystem;
import com.jme3.system.android.CardboardContext;
import com.jme3.system.android.JmeAndroidCardboardSystem;
import com.jme3.system.android.OGLESContext;
import java.util.logging.Logger;

/**
 *
 * @author reden
 */
public class CardboardHarnessFragment extends AndroidHarnessFragment {
    
    private CardboardState vrAppState;
    private static final Logger logger = Logger.getLogger(AndroidHarnessFragment.class.getName());
    private static String APP_CLASS_TAG = "appClass";
    
    public static CardboardHarnessFragment newInstance(String appClass){
        CardboardHarnessFragment cardboardHarness = new CardboardHarnessFragment();
        Bundle b = new Bundle();
        b.putString(APP_CLASS_TAG, appClass);
        cardboardHarness.setArguments(b);
        return cardboardHarness;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger.fine(getClass().getSimpleName() + " onCreate ");
        JmeSystem.setSystemDelegate(new JmeAndroidCardboardSystem());
        Bundle b = getArguments();
        if(b != null){
            appClass = b.getString(APP_CLASS_TAG);
        }
        super.onCreate(savedInstanceState);
        
        vrAppState = new CardboardState((CardboardContext)getJmeApplication().getContext());
        (getJmeApplication()).getStateManager().attach(vrAppState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logger.fine(getClass().getSimpleName() + " onCreateView ");
        // Create the GLSurfaceView for the application
        view = ((OGLESContext) getJmeApplication().getContext()).createView(getActivity());
//        ((CardboardView) view).setOnCardboardTriggerListener(new Runnable() {
//
//            public void run() {
//                onCardboardTrigger();
//            }
//        });
        JmeAndroidCardboardSystem.setView(view);
        createLayout();
        view.addOnLayoutChangeListener(this);
        return frameLayout;
    }

    @Override
    public void onDestroyView() {
        JmeAndroidCardboardSystem.setView(null);
        super.onDestroyView(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void onCardboardTrigger(){
        System.out.println("onCardboardTrigger");
    }
    
}
