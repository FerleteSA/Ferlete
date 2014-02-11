package com.ferlete.database;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.ferlete.model.Account;
import com.ferlete.model.Atividade;
import com.ferlete.model.Device;
import com.ferlete.model.Ponto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class Database extends SQLiteOpenHelper{

	//The Android's default system path of your application database.
	static String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();

	public static String DB_MAIN_PATH = SDCARD_PATH + "/ferlete/data/";
	public static String DB_BCK_PATH = SDCARD_PATH + "/ferlete/databck/";;

	public static final String DB_MAIN_NAME = "ferletedb";
	public static final String DB_BCK_NAME = "ferletedb_bck";

	private static SQLiteDatabase _dataBase; 

	private final Context _context;

	public static final int TYPE_DB_MAIN = 1;
	public static final int TYPE_DB_BCK = 2;

	private static final String TAG_LOGCAT = "Database";

	private int _dbType;
	private String _dbName;
	private String _dbPath;

	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public Database(Context context, String DBName, int dbType) {
		super(context, DBName, null, 33);
		this._context = context;
		_dbType = dbType;
		if(_dbType == TYPE_DB_MAIN){
			_dbName = DB_MAIN_NAME;
			_dbPath = DB_MAIN_PATH;
		} else if(_dbType == TYPE_DB_BCK){
			_dbName = DB_BCK_NAME;
			_dbPath = DB_BCK_PATH;
		}
	}	

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public boolean createDataBase() throws IOException{
		Log.d("Database","createDatabase()");

		boolean dbExist = dbExist();

		if(dbExist){
			//do nothing - database already exist
			return false;
		}else{

			try {

				copyDataBase();
				return true;
			} catch (IOException e) {

				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */

	public boolean dbExist(){
		try {
			File f = new File(_dbPath+_dbName);
			return f.exists();
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	private void copyDataBase() throws IOException{
		Log.d("Database","copyDataBase()");
		try {
			//Open your local db as the input stream
			InputStream myInput = _context.getAssets().open(_dbName);


			// Path to the just created empty db
			String outFileName = _dbPath + _dbName;    	

			//Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			//transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}

			//Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Database","copyDataBase error: " + e.getMessage());
		}

	}

	public boolean openDataBase(){
		Log.d("Database","openDataBase()");
		String myPath = _dbPath + _dbName;

		//Open the database
		try {
			this.getReadableDatabase();
			_dataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	@Override
	public synchronized void close() {
		Log.d("Database","close()");
		try {
			this.getReadableDatabase();
			if(_dataBase != null)
				_dataBase.close();

			super.close();	
		} catch (Exception e) {
			// TODO: handle exception
		}


	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	//Retorna a quantidade de linhas de uma query
	int getQtdRows(String cmdSQL)
	{
		try {
			Cursor cur= _dataBase.rawQuery(cmdSQL, null);
			int x= cur.getCount();
			cur.close();
			return x;			
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}

	}

	//Retorna o cursor de uma query
	public static Cursor getCursor(String cmdSQL)
	{
		Cursor cur = null;
		try {
			cur= _dataBase.rawQuery(cmdSQL, null);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Database", "RetCursor Falha:"+e.getMessage());			
		}
		return cur;			

	}

	public boolean CompactDatabase(){
		try {
			_dataBase.execSQL("VACUUM");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	//Executa uma query. Retorna TRUE em caso de sucesso e FALSE em caso de falha.
	public boolean executeSQL(String cmdSQL){
		try {
			_dataBase.execSQL(cmdSQL);
			Log.d("Database","ExecuteSQL: " + cmdSQL);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}


	//Retorna o primeiro campo do primeiro resultado de uma query. Caso a query não resulte nenhum valor, 
	//o valor pre-definido em valRet é retornado.
	public String getValue(String cmdSQL, String valRet){
		Cursor cur= _dataBase.rawQuery(cmdSQL, null);
		String val;
		if (cur.getCount() > 0){
			cur.moveToFirst();
			val = cur.getString(0);
			cur.close();
			return val;
		} else {
			return valRet;
		}

	}

	public boolean isOpen(){
		return _dataBase.isOpen();
	}

	public long createDeviceGPS(Device device) {
		long device_id = 0;

		try {

			_dataBase.beginTransaction();

			ContentValues values = new ContentValues();

			for (Ponto ponto : device.getCoordenada()) {
				values.put("DEVICEID", device.getDeviceID());
				values.put("DTLEITURA", ponto.getDataLeitura());
				values.put("LATITUDE", ponto.getLatitude());
				values.put("LONGITUDE", ponto.getLongitude());
			}

			device_id = _dataBase.insert("DEVICE", null, values);

			_dataBase.setTransactionSuccessful();
			_dataBase.endTransaction();




		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Database", "insertDevice Falha:"+e.getMessage());			
		}

		return device_id;
	}	

	/**
	 * getting all Pontos do Device
	 * */
	public List<Ponto> getAllPontoDevice() {
		List<Ponto> listaPonto = new ArrayList<Ponto>();
		String selectQuery = "SELECT  * FROM DEVICE";

		Log.e(TAG_LOGCAT, "getAllPontoDevice: "+selectQuery);

		Cursor c = getCursor(selectQuery);

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
	public boolean deleteAllPoints() {
		return executeSQL("DELETE FROM DEVICE");
	}

	// ---------------Metodos para Classe Atividade----------------------
	/*
	 * Creating a Activity
	 */
	public long createAtividade(Atividade atividade) {
		long atividadeId = 0;
		try {

			_dataBase.beginTransaction();

			ContentValues values = new ContentValues();

			values.put("DESCRICAO", atividade.getDescricao());
			values.put("ENDERECO", atividade.getEndereco());
			values.put("LATITUDE", atividade.getLatitude());
			values.put("LONGITUDE", atividade.getLongitude());
			values.put("STATUS", atividade.getStatus());

			atividadeId = _dataBase.insert("ATIVIDADE", null, values);

			_dataBase.setTransactionSuccessful();
			_dataBase.endTransaction();


		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Database", "createAtividade Falha:"+e.getMessage());			
		}

		return atividadeId;
	}


	/*
	 * getting Atividade count
	 */
	public int getAtividadeCount() {
		return Integer.valueOf(getValue("SELECT  * FROM ATIVIDADE", "0"));

	}


	/**
	 * getting all Atividades
	 * */
	public List<Atividade> getAllAtividades() {
		List<Atividade> atividades = new ArrayList<Atividade>();
		String selectQuery = "SELECT  * FROM ATIVIDADE";

		Log.e(TAG_LOGCAT, "getAllAtividades " + selectQuery);

		Cursor c = getCursor(selectQuery);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Atividade atividade = new Atividade();
				atividade.setIdAtividade(c.getInt(c.getColumnIndex("_id")));
				atividade.setDescricao(c.getString(c.getColumnIndex("DESCRICAO")));
				atividade.setEndereco(c.getString(c.getColumnIndex("ENDERECO")));
				atividade.setLatitude(c.getDouble(c.getColumnIndex("LATITUDE")));
				atividade.setLongitude(c.getDouble(c.getColumnIndex("LONGITUDE")));
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
		String selectQuery = "SELECT  * FROM ATIVIDADE";

		Log.e(TAG_LOGCAT, "getListAtividades " + selectQuery);

		Cursor c = getCursor(selectQuery);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Atividade atividade = new Atividade();
				atividade.setIdAtividade(c.getInt(c.getColumnIndex("_id")));
				atividade.setDescricao(c.getString(c.getColumnIndex("DESCRICAO")));
				atividade.setEndereco(c.getString(c.getColumnIndex("ENDERECO")));
				atividade.setLatitude(c.getDouble(c.getColumnIndex("LATITUDE")));
				atividade.setLongitude(c.getDouble(c.getColumnIndex("LONGITUDE")));
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
		int rows = 0;
		_dataBase.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put("IDATIVIDADE", atividade.getIdAtividade());
			values.put("STATUS", status);

			rows = _dataBase.update("ATIVIDADE", values, "_id" + " = ?", new String[] { String.valueOf(atividade.getIdAtividade()) });

			_dataBase.setTransactionSuccessful();
			_dataBase.endTransaction();




		} catch (Exception e) {
			// TODO: handle exception

		}

		return rows;


	}

	/*
	 * Deleting a Activity
	 */
	public boolean deleteActivity(Atividade atividade) {
		return executeSQL("DELETE FROM ATIVIDADE WHERE _id = " + String.valueOf(atividade.getIdAtividade()));
	}

	// ---------------Metodos para Classe Account----------------------
	/*
	 * Creating a Account
	 */
	public long createAccount(Account account) {
		long accountId = 0;

		_dataBase.beginTransaction();
		try {
			ContentValues values = new ContentValues();

			List<Device> listaDevice = new ArrayList<Device>();		
			listaDevice = account.getDevices();


			values.put("EMAIL", account.getLogin());
			values.put("SENHA", account.getSenha());
			values.put("DEVICEID", listaDevice.get(0).getDeviceID());

			Log.e(TAG_LOGCAT, values.toString());


			accountId = _dataBase.insert("ACCOUNT", null, values);

			_dataBase.setTransactionSuccessful();
			_dataBase.endTransaction();

		} catch (Exception e) {
			// TODO: handle exception

		}
		return accountId;

	}

	public List<Account> getAllAccount() {
		List<Account> accounts = new ArrayList<Account>();
		String selectQuery = "SELECT  * FROM ACCOUNT";

		Cursor c = getCursor(selectQuery);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Account account = new Account();
				account.setLogin(c.getString(c.getColumnIndex("EMAIL")));
				account.setSenha(c.getString(c.getColumnIndex("SENHA")));

				// adding Account list
				accounts.add(account);
			} while (c.moveToNext());
		}

		return accounts;
	}		


}