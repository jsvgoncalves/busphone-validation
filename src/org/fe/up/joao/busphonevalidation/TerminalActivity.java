package org.fe.up.joao.busphonevalidation;

import org.fe.up.joao.busphonevalidation.helper.CameraHelper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class TerminalActivity extends Activity
{
	QRCodeReader qrReader;

	static {
		System.loadLibrary("iconv");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		qrReader = new QRCodeReader(this);
		String busMessage = getString(R.string.bus) + " " + V.busNumber;

		setContentView(R.layout.activity_terminal);

		((TextView) findViewById(R.id.terminal_bus_label)).setText(busMessage);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
		preview.addView(qrReader.mPreview);
	}

	public void onPause() {
		super.onPause();
		qrReader.releaseCamera();
	}



	/**
	 * Implements everything needed
	 * to show the camera preview.
	 *
	 */
	private class QRCodeReader {

		protected Camera mCamera;
		protected CameraHelper mPreview;
		protected Handler autoFocusHandler;
		protected AutoFocusCallback autoFocusCB;

		ImageScanner scanner;

		protected boolean barcodeScanned = false;
		protected boolean previewing = true;

		/**
		 * Constructor
		 * Instanciates camera stuff
		 */
		public QRCodeReader(Context context){
			PreviewCallback previewCb = new PreviewCallback() {
				public void onPreviewFrame(byte[] data, Camera camera) {
					Camera.Parameters parameters = camera.getParameters();
					Size size = parameters.getPreviewSize();

					Image barcode = new Image(size.width, size.height, "Y800");
					barcode.setData(data);

					int result = scanner.scanImage(barcode);

					if (result != 0) {
						previewing = false;
						mCamera.setPreviewCallback(null);
						mCamera.stopPreview();
						
						/*
						 * The scanned data is handled here!
						 * syms = list of symbols read.
						 */
						SymbolSet syms = scanner.getResults();
						for (Symbol sym : syms) {
							Log.v("MyLog", "Read symbol:" + sym.getData());
							(new ValidateCode()).validate(sym.getData());
							barcodeScanned = true;
							return;
						}
					}
				}
			};

			// Mimic continuous auto-focusing
			autoFocusCB = new AutoFocusCallback() {
				public void onAutoFocus(boolean success, Camera camera) {
					autoFocusHandler.postDelayed(doAutoFocus, 1000);
				}
			};

			autoFocusHandler = new Handler();
			mCamera = getCameraInstance();

			/* Instance barcode scanner */
			scanner = new ImageScanner();
			scanner.setConfig(0, Config.X_DENSITY, 3);
			scanner.setConfig(0, Config.Y_DENSITY, 3);
			mPreview = new CameraHelper(context, mCamera, previewCb, autoFocusCB);
		}

		/**
		 * Called by activity to realease the camera for other apps
		 */
		private void releaseCamera() {
			if (mCamera != null) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.release();
				mCamera = null;
			}
		}

		/**
		 * Does auto focus.
		 */
		private Runnable doAutoFocus = new Runnable() {
			public void run() {
				if (previewing)
					mCamera.autoFocus(autoFocusCB);
			}
		};

		/** A safe way to get an instance of the Camera object. */
		public Camera getCameraInstance(){
			int cameraCount = 0;
			Camera cam = null;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			cameraCount = Camera.getNumberOfCameras();
			for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
				Camera.getCameraInfo( camIdx, cameraInfo );
				if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
					try {
						cam = Camera.open( camIdx );
					} catch (RuntimeException e) {
						Log.e("Camera Failed", "Camera failed to open: " + e.getLocalizedMessage());
					}
				}
			}

			return cam;
		}
	}


	private class ValidateCode extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * Receives a String with data from a QRCode that
		 * should contain a valid ticket and sends it
		 * to the server for validation.
		 * The ticket should be accompanied by a
		 * userID.
		 * @param data
		 */
		public void validate(String data) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
