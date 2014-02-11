package com.ferlete.globals;

import java.io.File;
import java.io.IOException;

import com.ferlete.database.Database;

import android.app.Application;
import android.content.Context;
import android.database.SQLException;
import android.os.Environment;
import android.util.Log;

public class FerleteApp extends Application{
	private static Database database;//database principal
	private static Database databasebck;//database de backup
	private static String sdcard_path;
	private static Context mContext; 
	private static boolean canUseDB = false; 

	@Override
	public void onCreate() {
		mContext = this.getApplicationContext();
		sdcard_path = Environment.getExternalStorageDirectory().getPath();
		
		if(isSdPresent()){
			//Cria as pastas dos databases
			new File(Environment.getExternalStorageDirectory().getPath() +"/ferlete/data/").mkdirs();	
			new File(Environment.getExternalStorageDirectory().getPath() +"/ferlete/databck/").mkdirs();
			
			//Cria e abre os databases
			createOpenDatabase();
			createOpenDatabaseBck();
		}
	}

	@Override
	public void onTerminate() {
		database.close();
		database = null;
		
		databasebck.close();
		databasebck = null;
	}
	

    public static Database getDatabase() {
    	if(isSdPresent()){
    		if(database == null){
    			createOpenDatabase();//Cria e abre o database.
    		} else {
    			if(!database.isOpen()){
        			createOpenDatabase();
        		}
    		}
    	}
    	
    	return database;	//Retorna o database.
    }
    
    public static Database getDatabaseBck() {
    	if(isSdPresent()){
    		if(databasebck == null){
    			createOpenDatabaseBck();
    		} else {
    			if(!databasebck.isOpen()){
    				createOpenDatabaseBck();
        		}
    		}
    	}
    	
    	return databasebck;
    }
    
    public static String getSdCardPath(){
    	return sdcard_path;
    }
    
  //Verifica se existe uma mídia de armazenamento montada
	private static boolean isSdPresent() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	private static boolean createOpenDatabase(){
		database = new Database(mContext,Database.DB_MAIN_NAME,Database.TYPE_DB_MAIN);
		try {
			database.createDataBase();
		} catch (IOException ioe) {
			Log.e("FerleteApp", "Fail to create database.");
			return false;
		}
		
		try {
			database.openDataBase();
			return true;
		}catch(SQLException sqle){
			Log.e("FerleteApp", "Fail to open database.");
			return false;
		}
	}
	
	private static boolean createOpenDatabaseBck(){
		databasebck = new Database(mContext,Database.DB_BCK_NAME,Database.TYPE_DB_BCK);
		try {
			databasebck.createDataBase();
		} catch (IOException ioe) {
			Log.e("FerleteApp", "Fail to create databasebck.");
			return false;
		}
		
		try {
			databasebck.openDataBase();
			return true;
		}catch(SQLException sqle){
			Log.e("FerleteApp", "Fail to open databasebck.");
			return false;
		}

	}
	
}
