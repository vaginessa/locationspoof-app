package ninja.tjaden.pokemonspoof;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void toggleService(View view){
        Switch startSwitch = (Switch) findViewById(R.id.startswitch);
        EditText username = (EditText) findViewById(R.id.username);
        EditText server = (EditText) findViewById(R.id.serverAddr);
        startSwitch.toggle();
        Log.d(TAG,"View init");
        Intent mServiceIntent = new Intent(this, LocationService.class);
        String s;
        if(server.getText().toString().equals("")){
            s = "192.168.43.12:8000";
        }
        else{
            s = server.getText().toString();
        }
        mServiceIntent.setData(Uri.parse("http://"+s+"/get_location.py"));
        //mServiceIntent.setData(Uri.parse("http://192.168.43.12:8000/get_location.py"));
        Log.d(TAG,"Data set");
        try{
            String user = username.getText().toString();
            Log.d(TAG,"Username capture: " + user);
            Boolean checked = startSwitch.isChecked();
            Log.d(TAG,"Is checked: " + checked.toString());
            if(user.equals(""))
                Log.d(TAG, "Set checked false");
                startSwitch.setChecked(false);

            //LocationService.shouldContinue = checked;

            Log.d(TAG, "Should continue: " + LocationService.shouldContinue);

            mServiceIntent.putExtra("username", user);
            mServiceIntent.putExtra("checked", checked);
            Log.d(TAG, "Service starting");
            this.startService(mServiceIntent);
            Log.d(TAG, "Service started");
        } catch(NullPointerException e){
            startSwitch.setChecked(false);
            LocationService.shouldContinue = false;

        }


    }
}
