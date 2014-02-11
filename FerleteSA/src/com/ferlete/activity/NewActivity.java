package com.ferlete.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ferlete.R;
import com.ferlete.database.DatabaseHelper;
import com.ferlete.globals.FerleteApp;
import com.ferlete.model.Atividade;

public class NewActivity extends Activity {

	// Database Helper
	EditText edDescricao, edEndereco;
	Double lat, lon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newactivity);
		
		Button btnGravar = (Button) findViewById(R.id.btnGravar);
		edDescricao = (EditText) findViewById(R.id.edDescricao);
		edEndereco = (EditText) findViewById(R.id.edEndereco);
		//edLatitude = (EditText) findViewById(R.id.edLat);
		//edLongitude = (EditText) findViewById(R.id.edLong);

		try {

			Intent it = getIntent();
			Bundle params = it.getExtras();

			if (params != null) {
				String endereco = params.getString("Endereco");
				lat = params.getDouble("latitude");
				lon = params.getDouble("longitude");
				
				edEndereco.setText(endereco);
				//edLatitude.setText(lat.toString());
				//edLongitude.setText(lon.toString());
				
			}
			if (params == null) {
				
				
			}

		} catch (Exception e) {
			mostraMensagem(e.getMessage());
		}

		

		btnGravar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				String descricao = edDescricao.getText().toString();
				String endereco = edEndereco.getText().toString();
				//Double latitude = Double.parseDouble(edLatitude.getText().toString());
				//Double longitude = Double.parseDouble(edLongitude.getText().toString());

				// Cria uma nova atividade
				Atividade atividade = new Atividade();
				atividade.setDescricao(descricao);
				atividade.setEndereco(endereco);
				atividade.setLatitude(lat);
				atividade.setLongitude(lon);
				atividade.setStatus(0);//Nova Atividade

				FerleteApp.getDatabase().createAtividade(atividade);

				//db.close();
				finish();

			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private void mostraMensagem(String msg) {
		AlertDialog.Builder dialogo = new AlertDialog.Builder(NewActivity.this);
		dialogo.setTitle("Ferlete");
		dialogo.setMessage(msg);
		dialogo.setNeutralButton("OK", null);
		dialogo.show();
	}

}
