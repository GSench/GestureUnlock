package ru.mobindustry.gestureunlock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String SAVED_GESTURES = "saved_gestures";
    public static final String UNLOCK_GESTURE = "unlock_gesture";
    public static final String UNLOCK_GESTURE_X = "unlock_gesture_x";
    public static final String UNLOCK_GESTURE_Y = "unlock_gesture_y";
    public static final String UNLOCK_GESTURE_Z = "unlock_gesture_z";
    public static final int ADD_GESTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addGesture(View v){
        Intent intent = new Intent(this, AddGestureActivity.class);
        startActivityForResult(intent, ADD_GESTURE, null);
    }

    public void unlock(View v){
        SharedPreferences spref = getSharedPreferences(SAVED_GESTURES, MODE_PRIVATE);
        if(spref.getString(UNLOCK_GESTURE_X, null)==null){
            Toast.makeText(this, "Add new gesture first.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, UnlockActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if((requestCode==ADD_GESTURE)&&(resultCode==RESULT_OK)){
            String[] gesture = data.getStringArrayExtra(UNLOCK_GESTURE);
            SharedPreferences spref = getSharedPreferences(SAVED_GESTURES, MODE_PRIVATE);
            spref
                    .edit()
                    .putString(UNLOCK_GESTURE_X, gesture[0])
                    .putString(UNLOCK_GESTURE_Y, gesture[1])
                    .putString(UNLOCK_GESTURE_Z, gesture[2])
                    .apply();
        }
    }

    public void settings(View v){
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
