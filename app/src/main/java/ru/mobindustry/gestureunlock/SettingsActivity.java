package ru.mobindustry.gestureunlock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Григорий on 26.04.2016.
 */
public class SettingsActivity extends AppCompatActivity {

    EditText valD, corrP, fault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        valD = (EditText) findViewById(R.id.val_d);
        corrP = (EditText) findViewById(R.id.correct);
        fault = (EditText) findViewById(R.id.fault);

        valD.setText(C.VALUABLE_DELTA(this)+"");
        corrP.setText(C.CORRECT_POINTS(this)+"");
        fault.setText(C.FAULT(this)+"");
    }


    public void onSaveButton(View v){
        C.setValuableDelta(Float.parseFloat(valD.getText().toString()), this);
        C.setCorrectPoints(Float.parseFloat(corrP.getText().toString()), this);
        C.setFault(Float.parseFloat(fault.getText().toString()), this);
        finish();
    }

}
