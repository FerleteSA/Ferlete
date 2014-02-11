package com.ferlete.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.ClientResource;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ferlete.database.DatabaseHelper;
import com.ferlete.globals.FerleteApp;
import com.ferlete.model.Device;
import com.ferlete.model.Ponto;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetGPSSendWS extends Service implements Runnable {

	private TelephonyManager telephonyManager;
	private Double latAtual, longAtual;
	protected int count = 0;
	private static String TAG_LOGCAT = "GetGPSSendWS";
	private static String deviceID;
	private boolean ativo;
	private int timeSync=10000;//10 segundos
	

	// Database Helper

	// Class Device
	Device device = new Device();

	// List of points
	List<Ponto> coordLidas = new ArrayList<Ponto>();
	
	// Connection detector class
	ConnectionDetector cd;
	
	// flag for Internet connection status
	Boolean isInternetPresent = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		//Toast.makeText(this, "The new Service GPS<->WS was Created",
		//		Toast.LENGTH_LONG).show();
		Log.i(TAG_LOGCAT, "AVISO: The new Service GPS<->WS was Created");
		ativo = true;

		
		// get device ID
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceID = telephonyManager.getDeviceId();


		// Delega para uma thread
		new Thread(this).start();

	}

	@Override
	public void onStart(Intent intent, int startId) {
		// For time consuming an long tasks you can launch a new thread here...		
		Log.i(TAG_LOGCAT, "AVISO: Service GPS<->WS Started");

		readGPS();

	}

	@Override
	public void onDestroy() {
		//Toast.makeText(this, "Service GPS<->WS Destroyed", Toast.LENGTH_LONG)
		//		.show();
		Log.i(TAG_LOGCAT, "AVISO: Service GPS<->WS Destroyed");
		ativo = false;

	}

	@Override
	public void run() {
		// Metodo run() - Padrao Runnable

		while (ativo) {
			try {
				Log.i(TAG_LOGCAT,"Thread Acordou....");
				// Fazer metodo!!!
				Log.i(TAG_LOGCAT, "Quantidade Registro a Enviar:" + FerleteApp.getDatabase().getAllPontoDevice().size());
				
				List<Ponto> pontos = new ArrayList<Ponto>();
				pontos=FerleteApp.getDatabase().getAllPontoDevice();
				
				Log.d(TAG_LOGCAT, "run pontos: " + pontos.size());
				
				if (pontos.size() > 0) {
					
					// creating connection detector class instance
	    			cd = new ConnectionDetector(getApplicationContext());

	    			// get Internet status
					isInternetPresent = cd.isConnectingToInternet();
					
					ClientResource deviceResource;				    	    	
	    	    	java.util.Date agora = new java.util.Date();
	    			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    			
					if (isInternetPresent){
						
						try{
							
						
						
						Device device = new Device();
						device.setDeviceID(deviceID);
						device.setLastLatitude(pontos.get(pontos.size()-1).getLatitude());
						device.setLastLongitude(pontos.get(pontos.size()-1).getLongitude());
						device.setLastDate(formatador.format(agora));
						device.setCoordenada(pontos);
						
						XStream xstream = new XStream(new DomDriver());
				        xstream.processAnnotations(Device.class);
				        
				        deviceResource = new ClientResource("http://200.212.248.135:8100/WSferlete/device");
				        deviceResource.put(xstream.toXML(device));	
				        
				        count++;
				        Log.i(TAG_LOGCAT, "Servico Ferlete Transmitiu " + count + " vezes");
				        FerleteApp.getDatabase().deleteAllPoints();
				   
						}catch(Exception ex){
							ex.printStackTrace();
							Log.i(TAG_LOGCAT, "ERRO Transmissao:" + ex.getMessage());
						}
				        
				        
						
					}else{
						Log.i(TAG_LOGCAT, "Internet nao esta presente");
					}
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(timeSync);// faz a thread dormir por X seg
				// configurado
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		stopSelf();
	}

	public void readGPS() {

		device.setDeviceID(deviceID);

		new GPSTracker(getBaseContext(), new GPSTrackerImplementation() {

					@Override
					public void onLocationChange(GPSTracker tracker,
							Location location) {
						// This will remove the gps liteners
						// tracker.stopUsingGPS();

						try {

							latAtual = location.getLatitude();
							longAtual = location.getLongitude();							
							Log.i(TAG_LOGCAT, "Novas Coordenadas \n Lat:"+ latAtual.toString() + "\nlong:"+ longAtual.toString());

							//Cria um novo ponto
							Ponto ponto = new Ponto();
							ponto.setLatitude(latAtual);
							ponto.setLongitude(longAtual);
							ponto.setDataLeitura(null);
							coordLidas.add(ponto);
							
							//Armazena a coordenada
							device.setCoordenada(coordLidas);
							storeCoordLidas(device);
							coordLidas.clear();


						} catch (Exception e) {
							Log.i(TAG_LOGCAT, e.getMessage());
						}

					}

				});

		// If gps isn't enabled show dialog
		// if (!gps.canGetLocation()) gps.showSettingsAlert();
	}

	protected void storeCoordLidas(Device device) {

		try {
			FerleteApp.getDatabase().createDeviceGPS(device);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

	}
}
