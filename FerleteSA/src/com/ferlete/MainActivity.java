package com.ferlete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;


import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.ferlete.activity.AboutActivity;
import com.ferlete.activity.ListActivity;
import com.ferlete.activity.LoginActivity;
import com.ferlete.activity.NewActivity;
import com.ferlete.database.DatabaseHelper;
import com.ferlete.globals.FerleteApp;
import com.ferlete.model.Atividade;
import com.ferlete.prefs.UserSettingActivity;
import com.ferlete.services.GPSTracker;
import com.ferlete.services.GPSTrackerImplementation;
import com.ferlete.services.GetGPSSendWS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;

public class MainActivity extends ActionBarActivity implements		LoaderCallbacks<Cursor> {

	public static final String PREFS_NAME = "PrefFerleteSA";
	private static final int RESULT_SETTINGS = 1;

	GoogleMap mGoogleMap;
	final LatLng CENTER = new LatLng(-20.44, -54.62);

	// Database Helper

	ArrayList<Atividade> atividades = new ArrayList<Atividade>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent loginUser = new Intent(getApplicationContext(), LoginActivity.class);
		startService(new Intent(this, GetGPSSendWS.class));
		
		//startActivity(loginUser);


		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mGoogleMap = fragment.getMap();

		// Mostra minha localizacao
		mGoogleMap.setMyLocationEnabled(true);

		// Move a camara para posicao inicial
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(CENTER));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

		// You can disable zooming gesture functionality by calling
		// setZoomGesturesEnabled()
		mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

		// My rotate gesture can be enabled or disabled by calling
		// setRotateGesturesEnabled() method
		mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);

		// Setting click event handler for InfoWIndow
		mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				// Remove the marker
				// marker.remove();

				Intent newActivity = new Intent(getApplicationContext(), NewActivity.class);
				Bundle params = new Bundle();
				params.putDouble("latitude",marker.getPosition().latitude);
				params.putDouble("longitude",marker.getPosition().longitude);
				params.putString("Endereco", marker.getTitle());
				newActivity.putExtras(params);
				startActivity(newActivity);

			}

		});

		// Setting OnClickEvent listener for the GoogleMap
		mGoogleMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latlng) {
				addMarker(latlng);

			}
		});

		handleIntent(getIntent());

		
		// Starting locations retrieve task
		new RetrieveTask().execute();

	}

	private void handleIntent(Intent intent) {
		try {
			if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
				doSearch(intent.getStringExtra(SearchManager.QUERY));
			} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
				getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("FerleteSA", e.getMessage());
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(0, data, this);
	}

	private void getPlace(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(1, data, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

	
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		  

		  return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_atividades:
			Intent listActivity = new Intent(getApplicationContext(),ListActivity.class);
			startActivity(listActivity);
			return true;
		case R.id.action_configuracoes:
			Intent config = new Intent(getApplicationContext(),UserSettingActivity.class);
			startActivityForResult(config, RESULT_SETTINGS);
			return true;
		case R.id.action_infoDevice:
			Intent infoDevice = new Intent(getApplicationContext(),AboutActivity.class);
			startActivity(infoDevice);
			return true;
		case R.id.action_sair:
			stopService(new Intent(this, GetGPSSendWS.class));
			super.finish();
			return true;
		}
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		CursorLoader cLoader = null;
		if (arg0 == 0)
			cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[] { query.getString("query") }, null);
		else if (arg0 == 1)
			cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null,	new String[] { query.getString("query") }, null);
		return cLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		showLocations(c);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
	}

	private void showLocations(Cursor c) {
		MarkerOptions markerOptions = null;
		LatLng position = null;
		mGoogleMap.clear();
		while (c.moveToNext()) {
			markerOptions = new MarkerOptions();
			position = new LatLng(Double.parseDouble(c.getString(1)),
					Double.parseDouble(c.getString(2)));

			markerOptions.draggable(true);
			markerOptions.position(position);
			markerOptions.title(c.getString(0));
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ico_pink));

			// Setting snippet for the InfoWindow
			markerOptions
			.snippet("Pressione aqui para adicionar uma Atividade");
			mGoogleMap.addMarker(markerOptions);

		}
		if (position != null) {

			CameraPosition cameraP = new CameraPosition.Builder()
			.target(position).zoom(17).build();
			mGoogleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraP));

		}
	}

	// Adding marker on the GoogleMaps
	private void addMarker(LatLng latlng) {

		String filterAddress = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

		try {
			List<Address> addresses = geoCoder.getFromLocation(latlng.latitude,
					latlng.longitude, 1);

			if (addresses.size() > 0) {
				for (int index = 0; index < addresses.get(0)
						.getMaxAddressLineIndex(); index++)
					filterAddress += addresses.get(0).getAddressLine(index)
					+ " ";
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e2) {
			// TODO: handle exception

			e2.printStackTrace();
		}

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latlng);
		markerOptions.title(filterAddress);
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_pink));
		markerOptions.snippet("Pressione aqui para adicionar uma Atividade");
		mGoogleMap.addMarker(markerOptions);
	}

	// Background task to retrieve locations from SQLlitle
	private class RetrieveTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// new ParserTask().execute(result);

			atividades = FerleteApp.getDatabase().getListAtividades();
			addMarkerActivityMaps(atividades);

		}

		private void addMarkerActivityMaps(ArrayList<Atividade> atividades) {
			LatLng position = null;

			for (Atividade atividade : atividades) {

				MarkerOptions markerOptions = new MarkerOptions();
				position = new LatLng(atividade.getLatitude(),
						atividade.getLongitude());

				markerOptions.draggable(false);
				markerOptions.position(position);

				switch (atividade.getStatus()) {

				case 0:// Nova
					markerOptions.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ico_pink));
					break;

				case 1:// Iniciada
					markerOptions.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ico_azure));
					break;

				case 2:// Concluida
					markerOptions.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ico_verde));
					break;

				default:

				}

				markerOptions.title(atividade.getDescricao());
				markerOptions.snippet(atividade.getEndereco());
				mGoogleMap.addMarker(markerOptions);

			}

		}

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setMessage("Deseja realmente sair?")
		.setCancelable(false)
		.setPositiveButton("Sim",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				MainActivity.this.finish();
			}
		}).setNegativeButton("Não", null).show();
	}

	@Override
	public void onResume() {
		super.onResume(); // Always call the superclass method first

		mGoogleMap.clear();

		// Starting locations retrieve task
		new RetrieveTask().execute();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_SETTINGS:
			// showConfig();
			break;
		}
	}

	private void showConfig() {

		SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);

		StringBuilder builder = new StringBuilder();
		builder.append("\npref_visibilidade:"
				+ sharedPref.getBoolean("pref_visibilidade", false));
		builder.append("\npref_timesync:"
				+ sharedPref.getInt("pref_timesync", 30));
		builder.append("\npref_email:" + sharedPref.getString("pref_email", ""));

		Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
		Log.i("FerleteSA", "key:" + builder.toString());

	}

}