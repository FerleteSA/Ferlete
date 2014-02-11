package com.ferlete.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ferlete.model.Account;
import com.ferlete.model.Atividade;
import com.ferlete.model.Device;
import com.ferlete.model.Ponto;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String TAG_LOGCAT = "FerleteSA";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "DBFerleteSA";

	// TableS Name
	private static final String TABLE_DEVICE = "DEVICE";
	private static final String TABLE_ATIVIDADE = "ATIVIDADE";
	private static final String TABLE_ACCOUNT = "ACCOUNT";

	// Table Create Device
	private static final String CREATE_TABLE_GPSDEVICE = "CREATE TABLE DEVICE"
			+ "(DEVICEID TEXT, LATITUDE DOUBLE, LONGITUDE DOUBLE, DTLEITURA DATETIME)";

	// Table Create Atividade
	private static final String CREATE_TABLE_ATIVIDADE = "CREATE TABLE ATIVIDADE"
			+ "(IDATIVIDADE INTEGER PRIMARY KEY AUTOINCREMENT, DESCRICAO TEXT NOT NULL, ENDERECO TEXT, LATITUDE DOUBLE, LONGITUDE DOUBLE, STATUS INTEGER)";

	// Table Create Account
	private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE ACCOUNT"
			+ "(EMAIL TEXT PRIMARY KEY, SENHA TEXT NOT NULL, DEVICEID TEXT NOT NULL)";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_GPSDEVICE);
		db.execSQL(CREATE_TABLE_ATIVIDADE);
		db.execSQL(CREATE_TABLE_ACCOUNT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATIVIDADE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);

		// create new tables
		onCreate(db);
	}

	// ---------------Metodos para Classe Device----------------------
	/*
	 * Insere dados de leitura de GPS para banco
	 */
	public long createDeviceGPS(Device device) {

		long device_id = 0;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		for (Ponto ponto : device.getCoordenada()) {
			values.put("DEVICEID", device.getDeviceID());
			values.put("DTLEITURA", ponto.getDataLeitura());
			values.put("LATITUDE", ponto.getLatitude());
			values.put("LONGITUDE", ponto.getLongitude());
		}

		try {

			// insert row
			device_id = db.insert("DEVICE", null, values);

		} catch (SQLiteException e) {
			e.printStackTrace();

		}

		return device_id;
	}

	/**
	 * getting all Pontos do Device
	 * */
	public List<Ponto> getAllPontoDevice() {
		List<Ponto> listaPonto = new ArrayList<Ponto>();
		String selectQuery = "SELECT  * FROM " + TABLE_DEVICE;

		Log.e(TAG_LOGCAT, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Ponto p = new Ponto();
				p.setDataLeitura(c.getString(c.getColumnIndex("DTLEITURA")));
				p.setLatitude(c.getDouble(c.getColumnIndex("LATITUDE")));
				p.setLongitude(c.getDouble(c.getColumnIndex("LONGITUDE")));
				// adding atividade list
				listaPonto.add(p);
			} while (c.moveToNext());
		}

		return listaPonto;
	}

	/*
	 * Deleting a Ponto
	 */
	public void deleteAllPoints() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DEVICE, null, null);
	}

	// ---------------Metodos para Classe Atividade----------------------
	/*
	 * Creating a Activity
	 */
	public long createAtividade(Atividade atividade) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("DESCRICAO", atividade.getDescricao());
		values.put("ENDERECO", atividade.getEndereco());
		values.put("LATITUDE", atividade.getLatitude());
		values.put("LONGITUDE", atividade.getLongitude());
		values.put("STATUS", atividade.getStatus());
		Log.e(TAG_LOGCAT, values.toString());

		// insert row
		long atividadeId = db.insert("ATIVIDADE", null, values);

		return atividadeId;
	}

	/*
	 * getting Atividade count
	 */
	public int getAtividadeCount() {
		String countQuery = "SELECT  * FROM " + TABLE_ATIVIDADE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/**
	 * getting all Atividades
	 * */
	public List<Atividade> getAllAtividades() {
		List<Atividade> atividades = new ArrayList<Atividade>();
		String selectQuery = "SELECT  * FROM " + TABLE_ATIVIDADE;

		Log.e(TAG_LOGCAT, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Atividade atividade = new Atividade();
				atividade.setIdAtividade(c.getInt(c
						.getColumnIndex("IDATIVIDADE")));
				atividade.setDescricao(c.getString(c
						.getColumnIndex("DESCRICAO")));
				atividade
						.setEndereco(c.getString(c.getColumnIndex("ENDERECO")));
				atividade
						.setLatitude(c.getDouble(c.getColumnIndex("LATITUDE")));
				atividade.setLongitude(c.getDouble(c
						.getColumnIndex("LONGITUDE")));
				atividade.setStatus(c.getInt(c.getColumnIndex("STATUS")));

				// adding atividade list
				atividades.add(atividade);
			} while (c.moveToNext());
		}

		return atividades;
	}

	/**
	 * getting list of activity for listview
	 * */
	public ArrayList<Atividade> getListAtividades() {
		ArrayList<Atividade> atividades = new ArrayList<Atividade>();
		String selectQuery = "SELECT  * FROM " + TABLE_ATIVIDADE;

		Log.e(TAG_LOGCAT, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Atividade atividade = new Atividade();
				atividade.setIdAtividade(c.getInt(c
						.getColumnIndex("IDATIVIDADE")));
				atividade.setDescricao(c.getString(c
						.getColumnIndex("DESCRICAO")));
				atividade
						.setEndereco(c.getString(c.getColumnIndex("ENDERECO")));
				atividade
						.setLatitude(c.getDouble(c.getColumnIndex("LATITUDE")));
				atividade.setLongitude(c.getDouble(c
						.getColumnIndex("LONGITUDE")));
				atividade.setStatus(c.getInt(c.getColumnIndex("STATUS")));

				// adding atividade list
				atividades.add(atividade);
			} while (c.moveToNext());
		}

		return atividades;
	}

	/*
	 * Update Status a Activity
	 */
	public int updateStatusActivity(Atividade atividade, int status) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("IDATIVIDADE", atividade.getIdAtividade());
		values.put("STATUS", status);

		return db.update(TABLE_ATIVIDADE, values, "IDATIVIDADE" + " = ?",
				new String[] { String.valueOf(atividade.getIdAtividade()) });
	}

	/*
	 * Deleting a Activity
	 */
	public void deleteActivity(Atividade atividade) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ATIVIDADE, "IDATIVIDADE" + " = ?",
				new String[] { String.valueOf(atividade.getIdAtividade()) });
	}

	// ---------------Metodos para Classe Account----------------------
	/*
	 * Creating a Account
	 */
	public long createAccount(Account account) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		List<Device> listaDevice = new ArrayList<Device>();		
		listaDevice = account.getDevices();
			

		values.put("EMAIL", account.getLogin());
		values.put("SENHA", account.getSenha());
		values.put("DEVICEID", listaDevice.get(0).getDeviceID());

		Log.e(TAG_LOGCAT, values.toString());

		// insert row
		long accountId = db.insert("ACCOUNT", null, values);

		return accountId;
	}
	
	public List<Account> getAllAccount() {
		List<Account> accounts = new ArrayList<Account>();
		String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Account account = new Account();
				account.setLogin(c.getString(c
						.getColumnIndex("EMAIL")));
				account.setSenha(c.getString(c
						.getColumnIndex("SENHA")));
				

				// adding Account list
				accounts.add(account);
			} while (c.moveToNext());
		}

		return accounts;
	}


	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
