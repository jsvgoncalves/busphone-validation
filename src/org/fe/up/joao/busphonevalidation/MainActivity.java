package org.fe.up.joao.busphonevalidation;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		Log.v("mylog", "Bus Validator started.");
	}

	/**
	 * Método chamado pelo seletor de numero de autocarro
	 */
	public void showBusDialog(View v){
		BusDialog bd = new BusDialog();
		bd.show(getFragmentManager(), "tag");
	}

	/**
	 * Método chamado pelo botão de iniciar
	 */
	public void startTrip(View v){
		if (V.busNumber == -1){
			((Button) findViewById(R.id.busNumberButton)).setBackgroundResource(R.drawable.button_selector);
		} else {
			Intent intent = new Intent(this, TerminalActivity.class);
			startActivity(intent);
		}
	}


	public static class BusDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Log.v("mylog", "Bus Dialog Created");
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.app_name);
			builder.setItems(V.buses, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					V.busNumber = Integer.valueOf(V.buses[item]);
					((Button) getActivity().findViewById(R.id.busNumberButton)).setText(V.buses[item]);
				}
			});
			return builder.create();
		}

	}
}