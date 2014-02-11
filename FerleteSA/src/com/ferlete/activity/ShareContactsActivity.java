package com.ferlete.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ferlete.R;

public class ShareContactsActivity extends Activity {
	ArrayAdapter<String> listAdapter;
	ArrayList<String> contactList = new ArrayList<String>();
	ArrayList<String> emailList = new ArrayList<String>();
	ListView listView;
	ProgressDialog dialog;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		// Find the ListView resource.
		listView = (ListView) findViewById(R.id.mainListView);
	

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				//final String item = (String) parent.getItemAtPosition(position);
				//contactList.remove(item);
				listAdapter.notifyDataSetChanged();
				return ;
				
			}

		});

		Button btnShare = (Button) findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}

		});

		dialog = ProgressDialog.show(this, "Aguarde!",
				"Carregando Contatos...", true);

		new LoadContacts().execute(0);

	}

	public void showContactsByName(ArrayList<String> conts) {

		// get list contact by name
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, conts);

		// Set the ArrayAdapter as the ListView's adapter.
		listView.setAdapter(listAdapter);

	}

	public void showContactsByEmail(ArrayList<String> conts) {

		// get list contact by name
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				conts);

		// Set the ArrayAdapter as the ListView's adapter.
		listView.setAdapter(listAdapter);

	}

	/*
	 * private Uri getPhotoUriFromID(String id) { try { Cursor cur =
	 * getContentResolver() .query(ContactsContract.Data.CONTENT_URI, null,
	 * ContactsContract.Data.CONTACT_ID + "=" + id + " AND " +
	 * ContactsContract.Data.MIMETYPE + "='" +
	 * ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
	 * null); if (cur != null) { if (!cur.moveToFirst()) { return null; // no
	 * photo } } else { return null; // error in cursor process } } catch
	 * (Exception e) { e.printStackTrace(); return null; } Uri person =
	 * ContentUris.withAppendedId( ContactsContract.Contacts.CONTENT_URI,
	 * Long.parseLong(id)); return Uri.withAppendedPath(person,
	 * ContactsContract.Contacts.Photo.CONTENT_DIRECTORY); }
	 */
	public ArrayList<String> getListContactByName() {
		ArrayList<String> names = new ArrayList<String>();

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				// String id = cur.getString(cur
				// .getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					names.add(name);
					// Log.i("FerleteSA", "name : " + name + ", ID : " + id);

				}
			}
		}
		// Sort ArrayList
		Collections.sort(names);

		return names;

	}

	public ArrayList<String> getListContactByEmail() {
		ArrayList<String> names = new ArrayList<String>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor cur1 = cr.query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " = ?", new String[] { id }, null);
				while (cur1.moveToNext()) {
					// to get the contact names
					String name = cur1
							.getString(cur1
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					// Log.i("FerleteSA", "name : " + name);
					String email = cur1
							.getString(cur1
									.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

					// Log.i("FerleteSA", "email : " + email);
					if (email != null) {
						names.add(name);
					}
				}
				cur1.close();
			}
		}
		// Sort ArrayList
		Collections.sort(names);

		return names;
	}

	private class LoadContacts extends AsyncTask<Integer, Void, Boolean> {

		protected Boolean doInBackground(Integer... position) {
			try {
				contactList = getListContactByName();
				return true;
			} catch (Exception e) {
				return false;
			}

		}

		protected void onPostExecute(Boolean bol) {
			if (bol) {
				// sucesso
				showContactsByName(contactList);
				dialog.dismiss();
			} else {
				// falha
				// tratar o erro
				dialog.dismiss();
			}
		}

	}
}