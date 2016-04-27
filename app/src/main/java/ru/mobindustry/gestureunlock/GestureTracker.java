package ru.mobindustry.gestureunlock;

import android.content.Context;

import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Григорий on 23.04.2016.
 */
public class GestureTracker {

    public static final char A_DIV = 'a';

    private float VALUABLE_DELTA = 0.1f;

    private HeadTracker track;
    float [] angles = new float[3];
    private HeadTransform trans;
    private boolean stop = false;
    private float[] lastAngles = new float[3];

    StringBuilder trackXStr;
    StringBuilder trackYStr;
    StringBuilder trackZStr;

    ArrayList<Float> x, y, z;

    public GestureTracker(Context context){
        track = HeadTracker.createFromContext(context);
        trans = new HeadTransform();
        trackXStr = new StringBuilder();
        trackYStr = new StringBuilder();
        trackZStr = new StringBuilder();
        VALUABLE_DELTA = C.VALUABLE_DELTA(context);
    }

    public void startTracking(){
        track.startTracking();
        stop=false;
        trackXStr = new StringBuilder();
        trackYStr = new StringBuilder();
        trackZStr = new StringBuilder();
        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop){
                    track();
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopTracking(){
        track.stopTracking();
        stop=true;
    }

    private void track(){
        track.getLastHeadView(trans.getHeadView(), 0);
        trans.getEulerAngles(angles, 0);
        if(Math.abs(angles[0]-lastAngles[0])>=VALUABLE_DELTA){
            trackXStr.append(angles[0]);
            trackXStr.append(A_DIV);
            lastAngles[0]=angles[0];
            x.add(angles[0]);
        }
        if(Math.abs(angles[1]-lastAngles[1])>=VALUABLE_DELTA){
            trackYStr.append(angles[1]);
            trackYStr.append(A_DIV);
            lastAngles[1]=angles[1];
            y.add(angles[1]);
        }
        if(Math.abs(angles[2]-lastAngles[2])>=VALUABLE_DELTA){
            trackZStr.append(angles[2]);
            trackZStr.append(A_DIV);
            lastAngles[2]=angles[2];
            z.add(angles[2]);
        }
    }

    public String[] getGesture(){
        return new String[]{
                trackXStr.toString(),
                trackYStr.toString(),
                trackZStr.toString()
        };
    }

    public float getLastX(){
        return lastAngles[0];
    }

    public float getLastY(){
        return lastAngles[1];
    }

    public float getLastZ(){
        return lastAngles[2];
    }

    public float[] getX(){
        float[] floatArray = new float[x.size()];
        int i = 0;
        for (Float f : x) {
            floatArray[i++] = (f != null ? f : Float.NaN);
        }
        return floatArray;
    }

    public float[] getY(){
        float[] floatArray = new float[y.size()];
        int i = 0;
        for (Float f : y) {
            floatArray[i++] = (f != null ? f : Float.NaN);
        }
        return floatArray;
    }

    public float[] getZ(){
        float[] floatArray = new float[z.size()];
        int i = 0;
        for (Float f : z) {
            floatArray[i++] = (f != null ? f : Float.NaN);
        }
        return floatArray;
    }

    public static float[] divide(String str){
        String[] val = str.split(A_DIV+"");
        float[] res = new float[val.length];
        for(int i=0; i<res.length; i++){
            res[i]=Float.parseFloat(val[i]);
        }
        return res;
    }

}
