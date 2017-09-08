package com.example.hammad.instanthelp.sevices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.activity.HomeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Coder on 14/05/2017.
 */

public class DefaultFallService extends Service implements SensorEventListener {

	SensorManager senSensorManager;
	Sensor senAccelerometer;
	//    float lowerThreshold;
	float resultant;
	float upperThreshold;
	final String TAG = "DEBUGGING";
	//    private long millis;
	int initCounter = 0;
	public boolean fall = false;
	public boolean isActivityRunning;

//    File file;
//    FileOutputStream outputStream;
//    String data ;
//    FileWriter writer;





	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {



//         File root = new File(Environment.getExternalStorageDirectory(), "DatA");
//        if (!root.exists()) {
//            root.mkdirs();
//        }
//        file = new File(root, "myfile");
//        try {
//            writer = new FileWriter(new File(root,"myfile"),true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
		Toast.makeText(this, "Sensor Enabled", Toast.LENGTH_SHORT).show();
		senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);


//        final AlertDialog.Builder builder = new AlertDialog.Builder(DefaultFallService.this);
//        builder.setMessage("Fall Detected. Are you fine?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i){
//                        stopPlaying();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(DefaultFallService.this, "Inform Emergency service", Toast.LENGTH_SHORT).show();
//                        stopPlaying();
//                    }
//                }).create();
		return START_STICKY;
	}



	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {


		if(fall){
			if(!isActivityRunning){
				Intent intent = new Intent(DefaultFallService.this, HomeActivity.class);
				intent.putExtra("FALL", true);
				intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				Log.d(TAG, "startActivity");
			}
			fall = false;


//
		}




		Sensor mySensor = sensorEvent.sensor;

		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = sensorEvent.values[0] / 9.8f;
			float y = sensorEvent.values[1] / 9.8f;
			float z = sensorEvent.values[2] / 9.8f;
			resultant = (float) Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));


//            String value = String.format("%.2f",resultant);

//            data +=  value +" ";
//            try {
//                writer.append(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
			if(resultant <=0.4){
				initCounter++;
				if(initCounter == 1){

					Log.d(TAG, "lower threshold cross");
					upperThreshold = 0;
					new Task().execute();
				}
			}
		}
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}

	@Override
	public void onDestroy() {

		senSensorManager.unregisterListener(this, senAccelerometer);
		Toast.makeText(this, "Sensor Disabled", Toast.LENGTH_SHORT).show();
//        try {
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
	}

	class Task extends AsyncTask<Void,Void,Boolean>{


		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			final CountDownTimer countDownTimer = new CountDownTimer(2000,5) {
				@Override
				public void onTick(long l) {
					if (resultant > 3.0 ){
						upperThreshold = resultant;
//						Toast.makeText(DefaultFallService.this, "upper threshold reaches", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFinish() {


					Log.d(TAG, "First onFinish  "+ upperThreshold);
					if(upperThreshold > 3.0){
						new CountDownTimer(15000, 100) {
							int  counter;
							@Override
							public void onTick(long l) {
								if(resultant > 1.2|| resultant < 0.8){
									counter++;
								}
							}

							@Override
							public void onFinish() {

								if (counter < 3){
									fall = true;
								}else{
									fall = false;
								}
								initCounter = 0;
							}
						}.start();
					}else{
						initCounter = 0;
					}
				}
			};

			countDownTimer.start();
		}


		@Override
		protected Boolean doInBackground(Void... voids) {


			publishProgress();


			return fall;
		}

		@Override
		protected void onPostExecute(Boolean b) {
			Log.d(TAG, "onPostExecute   "+ b);
		}
	}


}
