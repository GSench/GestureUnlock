package ru.mobindustry.gestureunlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Григорий on 23.04.2016.
 */
public class UnlockActivity extends AppCompatActivity {

    GestureTracker tracker;
    Button trackBtn;

    private float FAULT = 0.5f;
    private float CORRECT_POINTS = 0.95f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlock_layout);
        tracker = new GestureTracker(this);
        trackBtn = (Button) findViewById(R.id.track_btn);
        trackBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tracker.startTracking();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    tracker.stopTracking();
                    confirmGesture();
                }
                return false;
            }
        });
        FAULT = C.FAULT(this);
        CORRECT_POINTS = C.CORRECT_POINTS(this);
    }

    private void confirmGesture(){
        float[] xCheck = tracker.getX();
        float[] yCheck = tracker.getY();
        float[] zCheck = tracker.getZ();
        SharedPreferences spref = getSharedPreferences(MainActivity.SAVED_GESTURES, MODE_PRIVATE);
        String xStr = spref.getString(MainActivity.UNLOCK_GESTURE_X, "0");
        String yStr = spref.getString(MainActivity.UNLOCK_GESTURE_Y, "0");
        String zStr = spref.getString(MainActivity.UNLOCK_GESTURE_Z, "0");
        float[] x = GestureTracker.divide(xStr);
        float[] y = GestureTracker.divide(yStr);
        float[] z = GestureTracker.divide(zStr);

        boolean accessX = checkGesture(xCheck, x);
        boolean accessY = checkGesture(yCheck, y);
        boolean accessZ = checkGesture(zCheck, z);

        showResult(accessX, accessY, accessZ, xCheck, yCheck, zCheck, x, y, z);
    }

    private boolean checkGesture(float[] cToCheck, float[] cCheck){
        int correctPoints = 0;
        for(int i=0; i<cToCheck.length; i++){
            float compare = getCompareC(i, cToCheck.length, cCheck);
            if(Math.abs(compare-cToCheck[i])<=FAULT) correctPoints++;
        }
        return (float)correctPoints/cToCheck.length>=CORRECT_POINTS;
    }

    private float getCompareC(int i, int length, float[] from){
        if(from.length==length) return from[i];
        float compareI, d;
        compareI = (float)(i+1)/length*from.length-1;
        if((int)compareI==compareI) return from[(int)compareI];
        d = from[((int) compareI)+1]-from[((int) compareI)];
        return (compareI - (int)compareI)*d+from[(int)compareI];
    }

    private void showResult(boolean accessX, boolean accessY, boolean accessZ, float[] xCheck, float[] yCheck, float[] zCheck, float[] x, float[] y, float[] z){
        LinearLayout resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(accessX&&accessY&&accessZ){
            Toast.makeText(this, "ACCESS GRANTED", Toast.LENGTH_LONG).show();
            v.vibrate(400);
        }
        else {
            Toast.makeText(this, "ACCESS DENIED", Toast.LENGTH_LONG).show();
            v.vibrate(new long[]{0, 200, 100, 200}, -1);
        }

        LinearLayout resultView = (LinearLayout) getLayoutInflater().inflate(R.layout.unlock_results, resultLayout, false);

        GraphView xGraph = (GraphView) resultView.findViewById(R.id.graph_x);
        GraphView yGraph = (GraphView) resultView.findViewById(R.id.graph_y);
        GraphView zGraph = (GraphView) resultView.findViewById(R.id.graph_z);

        int i;
        DataPoint[] check, comp;

        LineGraphSeries<DataPoint> xCheckF = new LineGraphSeries<>();
        xCheckF.setColor(Color.parseColor("#F50057"));
        LineGraphSeries<DataPoint> xF = new LineGraphSeries<>();
        xF.setColor(Color.parseColor("#FF80AB"));
        check = new DataPoint[xCheck.length];
        comp = new DataPoint[xCheck.length];
        for(i=0; i<xCheck.length; i++){
            check[i] = new DataPoint(i, xCheck[i]);
            comp[i] = new DataPoint(i, getCompareC(i, xCheck.length, x));
        }
        xCheckF.resetData(check);
        xF.resetData(comp);

        LineGraphSeries<DataPoint> yCheckF = new LineGraphSeries<>();
        yCheckF.setColor(Color.BLUE);
        LineGraphSeries<DataPoint> yF = new LineGraphSeries<>();
        yF.setColor(Color.parseColor("#9FA8DA"));
        check = new DataPoint[yCheck.length];
        comp = new DataPoint[yCheck.length];
        for(i=0; i<yCheck.length; i++){
            check[i] = new DataPoint(i, yCheck[i]);
            comp[i] = new DataPoint(i, getCompareC(i, yCheck.length, y));
        }
        yCheckF.resetData(check);
        yF.resetData(comp);

        LineGraphSeries<DataPoint> zCheckF = new LineGraphSeries<>();
        zCheckF.setColor(Color.GREEN);
        LineGraphSeries<DataPoint> zF = new LineGraphSeries<>();
        zF.setColor(Color.parseColor("#C5E1A5"));
        check = new DataPoint[zCheck.length];
        comp = new DataPoint[zCheck.length];
        for(i=0; i<zCheck.length; i++){
            check[i] = new DataPoint(i, zCheck[i]);
            comp[i] = new DataPoint(i, getCompareC(i, zCheck.length, z));
        }
        zCheckF.resetData(check);
        zF.resetData(comp);

        xGraph.addSeries(xCheckF);
        xGraph.addSeries(xF);
        xGraph.getViewport().setXAxisBoundsManual(true);
        xGraph.getViewport().setMaxX(xCheck.length);
        if(accessX) xGraph.setBackgroundColor(Color.parseColor("#CCFF90")); else xGraph.setBackgroundColor(Color.parseColor("#EF9A9A"));
        xGraph.setTitle("X");

        yGraph.addSeries(yCheckF);
        yGraph.addSeries(yF);
        yGraph.getViewport().setXAxisBoundsManual(true);
        yGraph.getViewport().setMaxX(yCheck.length);
        if(accessY) yGraph.setBackgroundColor(Color.parseColor("#CCFF90")); else yGraph.setBackgroundColor(Color.parseColor("#EF9A9A"));
        yGraph.setTitle("Y");

        zGraph.addSeries(zCheckF);
        zGraph.addSeries(zF);
        zGraph.getViewport().setXAxisBoundsManual(true);
        zGraph.getViewport().setMaxX(zCheck.length);
        if(accessZ) zGraph.setBackgroundColor(Color.parseColor("#CCFF90")); else zGraph.setBackgroundColor(Color.parseColor("#EF9A9A"));
        zGraph.setTitle("Z");

        resultLayout.removeAllViews();
        resultLayout.addView(resultView);
    }

}
