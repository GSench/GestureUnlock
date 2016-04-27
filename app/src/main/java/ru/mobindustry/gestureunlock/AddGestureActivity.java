package ru.mobindustry.gestureunlock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.concurrent.TimeUnit;

/**
 * Created by Григорий on 23.04.2016.
 */
public class AddGestureActivity extends AppCompatActivity {

    GestureTracker tracker;
    GraphView graphView;
    Button trackBtn;
    AppCompatActivity act;

    private LineGraphSeries<DataPoint> fX;
    private LineGraphSeries<DataPoint> fY;
    private LineGraphSeries<DataPoint> fZ;
    private volatile int time = 0;
    private final int TIME_STEP = 50;
    private volatile boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gesture_activity);
        act=this;
        tracker = new GestureTracker(this);
        graphView = (GraphView) findViewById(R.id.graph_view);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        trackBtn = (Button) findViewById(R.id.track_btn);
        trackBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tracker.startTracking();
                    startTrackView();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    tracker.stopTracking();
                    stopTrackView();
                }
                return false;
            }
        });
    }

    private void startTrackView(){
        stop = false;
        graphView.removeAllSeries();
        fX = new LineGraphSeries<DataPoint>();
        fX.setColor(Color.RED);
        fY = new LineGraphSeries<DataPoint>();
        fY.setColor(Color.BLUE);
        fZ = new LineGraphSeries<DataPoint>();
        fZ.setColor(Color.GREEN);
        graphView.addSeries(fX);
        graphView.addSeries(fY);
        graphView.addSeries(fZ);
        new Thread(new Runnable() {
            @Override
            public void run() {
                time=0;
                while (!stop){
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            time++;
                            graphView.getViewport().setMaxX(time*TIME_STEP/1000f);
                            fX.appendData(new DataPoint(time*TIME_STEP/1000f, tracker.getLastX()), true, time);
                            fY.appendData(new DataPoint(time*TIME_STEP/1000f, tracker.getLastY()), true, time);
                            fZ.appendData(new DataPoint(time*TIME_STEP/1000f, tracker.getLastZ()), true, time);
                        }
                    });
                    try {
                        TimeUnit.MILLISECONDS.sleep(TIME_STEP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void stopTrackView(){
        stop=true;
    }

    public void saveGesture(View v){
        String[] gesture = tracker.getGesture();
        if(gesture[0].equals("")){
            Toast.makeText(this, "Write gesture first!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.putExtra(MainActivity.UNLOCK_GESTURE, tracker.getGesture());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_CANCELED);
        finish();
    }

}
