package com.example.hammad.instanthelp.sevices;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.activity.HomeActivity;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;

import java.io.FileWriter;
import java.util.Collection;
import java.util.Timer;

/**
 * Created by Coder on 26/08/2017.
 */

public class WearableFallService extends Service {


	Timer mTimer;
	Shimmer mShimmerDevice1 = null;
	double resultant = 0;
	double upperThreshold;
	int initCounter = 0;
	boolean fall = false;
	String TAG = "DebuGGing";
	FileWriter writer;
	String data;
	MediaPlayer mp;
	ImageView imageView;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mp = MediaPlayer.create(WearableFallService.this, R.raw.alrm);
		mp.setVolume(100,100);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mShimmerDevice1 = new Shimmer(this, mHandler,"Stomach", 51.2, 0, 0, Shimmer.SENSOR_ACCEL|Shimmer.SENSOR_GYRO|Shimmer.SENSOR_MAG, false);
		String bluetoothAddress="00:06:66:66:94:9A";
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

		mShimmerDevice1.connect(bluetoothAddress,"default");
		Log.d("ConnectionStatus","Trying");
		return START_STICKY;
	}

	public boolean isActivityRunning;
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) { // handlers have a what identifier which is used to identify the type of msg
				case Shimmer.MESSAGE_READ:
					if ((msg.obj instanceof ObjectCluster)){	// within each msg an object can be include, objectclusters are used to represent the data structure of the shimmer device
						ObjectCluster objectCluster =  (ObjectCluster) msg.obj;
//            	    Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Accelerometer X");  // first retrieve all the possible formats for the current sensor device
//					FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
//					if (formatCluster!=null){
//						Log.d("CalibratedData",objectCluster.mMyName + " AccelX: " + formatCluster.mData + " "+ formatCluster.mUnits);
//					}
//					Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
//					formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
//					if (formatCluster!=null){
//						Log.d("CalibratedData",objectCluster.mMyName + " AccelY: " + formatCluster.mData + " "+formatCluster.mUnits);
//					}
//					Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
//					formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
//					if (formatCluster!=null){
//						Log.d("CalibratedData",objectCluster.mMyName + " AccelZ: " + formatCluster.mData + " "+formatCluster.mUnits);
//					}

						if(fall) {
							if (!isActivityRunning) {
								Intent intent = new Intent(WearableFallService.this, HomeActivity.class);
								intent.putExtra("FALL", true);
								intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								Log.d(TAG, "startActivity");
							}
							fall = false;
						}

						double 	acclX = 0,
								acclY = 0,
								acclZ = 0,
								gyroX = 0,
								gyroY = 0,
								gyroZ = 0;


						Collection<FormatCluster> accelXFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer X");  // first retrieve all the possible formats for the current sensor device
						FormatCluster formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelXFormats,"CAL")); // retrieve the calibrated data
						if (formatCluster!=null){
							acclX = formatCluster.mData / 9.8;
//						Log.d("CalibratedData",objectCluster.mMyName + " AccelLNX: " + formatCluster.mData + " "+ formatCluster.mUnits);
						}
						Collection<FormatCluster> accelYFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Y");  // first retrieve all the possible formats for the current sensor device
						formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelYFormats,"CAL")); // retrieve the calibrated data
						if (formatCluster!=null){
							acclY = formatCluster.mData / 9.8;

//						Log.d("CalibratedData",objectCluster.mMyName + " AccelLNY: " + formatCluster.mData + " "+formatCluster.mUnits);
						}
						Collection<FormatCluster> accelZFormats = objectCluster.mPropertyCluster.get("Low Noise Accelerometer Z");  // first retrieve all the possible formats for the current sensor device
						formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(accelZFormats,"CAL")); // retrieve the calibrated data
						if (formatCluster!=null){
							acclZ = formatCluster.mData / 9.8;
//						Log.d("CalibratedData",objectCluster.mMyName + " AccelLNZ: " + formatCluster.mData + " "+formatCluster.mUnits);
						}
						Collection<FormatCluster> gyroXFormats = objectCluster.mPropertyCluster.get("Gyroscope X");  // first retrieve all the possible formats for the current sensor device
						formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gyroXFormats,"CAL")); // retrieve the calibrated data
						if (formatCluster!=null){
							gyroX = formatCluster.mData;
							Log.d("CalibratedData",objectCluster.mMyName + " gyroX: " + formatCluster.mData + " "+formatCluster.mUnits);
						}
						Collection<FormatCluster> gyroYFormats = objectCluster.mPropertyCluster.get("Gyroscope Y");  // first retrieve all the possible formats for the current sensor device
						formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gyroYFormats,"CAL")); // retrieve the calibrated data
						if (formatCluster!=null){
							gyroY = formatCluster.mData;
							Log.d("CalibratedData",objectCluster.mMyName + " gyroY: " + formatCluster.mData + " "+formatCluster.mUnits);
						}
						Collection<FormatCluster> gyroZFormats = objectCluster.mPropertyCluster.get("Gyroscope Z");  // first retrieve all the possible formats for the current sensor device
						formatCluster = ((FormatCluster)ObjectCluster.returnFormatCluster(gyroZFormats,"CAL")); // retrieve the calibrated data
						if (formatCluster!=null){
							gyroZ = formatCluster.mData;
							Log.d("CalibratedData",objectCluster.mMyName + " gyroZ: " + formatCluster.mData + " "+formatCluster.mUnits);
						}

						resultant = Math.abs(Math.sqrt(Math.pow(acclX, 2) + Math.pow(acclY, 2) + Math.pow(acclZ, 2)));

//						String value = String.format("%.2f",resultant);
//
//						data +=  "\n"+acclX + "\t"+ acclY +"\t"+ acclZ+"\t" + value+"\t" + gyroX + "\t"+gyroY+"\t"+gyroZ+" ";
//						try {
//							writer.append(data);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					Log.d(TAG, resultant + " G");

						if(resultant <= 0.4){
							initCounter++;
							if(initCounter == 1){

								upperThreshold = 0;
								new Task().execute();
							}
						}
					}
					break;
				case Shimmer.MESSAGE_TOAST:
					Log.d("toast",msg.getData().getString(Shimmer.TOAST));
					Toast.makeText(getApplicationContext(), msg.getData().getString(Shimmer.TOAST),
							Toast.LENGTH_SHORT).show();
					break;

				case Shimmer.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case Shimmer.MSG_STATE_FULLY_INITIALIZED:
							if (mShimmerDevice1.getShimmerState()==Shimmer.STATE_CONNECTED){
								Log.d("ConnectionStatus","Successful");
								mShimmerDevice1.startStreaming();

//                    	        shimmerTimer(10); //Disconnect in 30 seconds
							}
							break;
						case Shimmer.STATE_CONNECTING:
							Log.d("ConnectionStatus","Connecting");
							break;
						case Shimmer.STATE_NONE:
							Log.d("ConnectionStatus","No State");
							break;
					}
					break;

			}
		}
	};

	private void stopPlaying() {
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
	}

	class Task extends AsyncTask<Void,Void,Boolean> {


		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {
			final CountDownTimer countDownTimer = new CountDownTimer(2000,5) {
				@Override
				public void onTick(long l) {
					if (resultant > 3.3){
						upperThreshold = resultant;
//						Toast.makeText(WearableFallService.this, "upper threshold reaches", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFinish() {


					Log.d(TAG, "First onFinish  "+ upperThreshold);
					if(upperThreshold > 3.3){
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		mShimmerDevice1.stop();

	}
}
