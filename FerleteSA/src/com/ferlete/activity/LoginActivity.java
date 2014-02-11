package com.ferlete.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ferlete.R;
import com.ferlete.database.DatabaseHelper;
import com.ferlete.globals.FerleteApp;
import com.ferlete.model.Account;
import com.ferlete.model.Device;

public class LoginActivity extends Activity {

	private EditText usuario;
	private EditText senha;
	private Button btnCreateAcount;
	private TelephonyManager telephonyManager;
	private static String TAG_LOGCAT = "FerleteSA";

	List<Device> listDevices = new ArrayList<Device>();
	List<Account> listAccounts = new ArrayList<Account>();

	// Database Helper

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// make conection database
		
		usuario = (EditText) findViewById(R.id.login);
		senha = (EditText) findViewById(R.id.senha);

		btnCreateAcount = (Button) findViewById(R.id.btnCreateAcount);
		
		
		
		listAccounts = FerleteApp.getDatabase().getAllAccount();
		
		if (listAccounts.size() > 0){
			btnCreateAcount.setVisibility(0);
		}else{
			btnCreateAcount.setVisibility(1);
		}
				

		btnCreateAcount.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				String usuarioInformado = usuario.getText().toString();
				String senhaInformada = senha.getText().toString();

				if (usuarioInformado.equals("") || senhaInformada.equals("")) {
					// Deve preecher usuario e Senha

					// 1. Instantiate an AlertDialog.Builder with its
					// constructor
					AlertDialog.Builder builder = new AlertDialog.Builder(v
							.getContext());

					// 2. Chain together various setter methods to set the
					// dialog characteristics
					builder.setMessage("Informe E-mail e Nova Senha Acesso")
							.setTitle("Nova Conta");
					builder.setCancelable(true);

					// 3. Get the AlertDialog from create()
					final AlertDialog dialog = builder.create();
					dialog.setButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									dialog.dismiss();

								}
							});

					dialog.show();

				} else {

					// dialog.setTitle("Wait");
					// dialog.setCancelable(false);
					// dialog.show();

					new CreateAccount().execute(0);

				}

			}
		});

	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void entrarOnClick(View v) {

		try {
			String usuarioInformado = usuario.getText().toString();
			String senhaInformada = senha.getText().toString();
			if ("1".equals(usuarioInformado) && "1".equals(senhaInformada)) {

				// Fecha atividade
				finish();

			} else {
				// mostra uma mensagem de erro
				String mensagemErro = getString(R.string.msgErroAutenticacao);

				Toast toast = Toast.makeText(this, mensagemErro,
						Toast.LENGTH_SHORT);
				toast.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class CreateAccount extends AsyncTask<Integer, Void, Boolean> {

		protected Boolean doInBackground(Integer... position) {
			try {

				
				Device device = new Device();
				device.setDeviceID(telephonyManager.getDeviceId());
				listDevices.add(device);

				Account account = new Account();
				account.setLogin(usuario.getText().toString());
				account.setLogin(senha.getText().toString());
				account.setDevices(listDevices);
				Log.i(TAG_LOGCAT, account.getLogin());
				FerleteApp.getDatabase().createAccount(account);
				return true;
			} catch (Exception ex) {				
				return false;

			}

		}

		protected void onPostExecute(Boolean bol) {
			if (bol) {
				// sucesso
				Log.i(TAG_LOGCAT, "Conta Criada com Sucesso");
				// dialog.dismiss();
			} else {
				// falha
				// tratar o erro
				Log.i(TAG_LOGCAT, "Falha na Criacao da Conta");
				// dialog.dismiss();
			}
		}
	}

}
