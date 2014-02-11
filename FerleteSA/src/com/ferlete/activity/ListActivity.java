package com.ferlete.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.ferlete.R;
import com.ferlete.database.DatabaseHelper;
import com.ferlete.database.MyAdapter;
import com.ferlete.globals.FerleteApp;
import com.ferlete.model.Atividade;

public class ListActivity extends android.app.ListActivity {

	// Database Helper
	int qtdeRegistros = 0;
	private static final int CMD_COMPARTILHAR = 0;	
	private static final int CMD_INICIAR = 1;
	private static final int CMD_CONCLUIR = 2;
	private static final int CMD_EXCLUIR = 3;

	MyAdapter adapter = null;
	ArrayList<Atividade> atividades = new ArrayList<Atividade>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_list);

		try {

			qtdeRegistros = FerleteApp.getDatabase().getAtividadeCount();

			if (qtdeRegistros == 0) {
				Toast.makeText(getApplicationContext(),
						"Nenhuma Atividade Cadastrada!", Toast.LENGTH_SHORT)
						.show();
			} else {
				loadDate();

				ListView listView = getListView();
				listView.setTextFilterEnabled(true);

				registerForContextMenu(listView);
			}
		} catch (SQLiteException e) {
			Log.i("FerleteSA", e.getMessage());
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == getListView()) {
			//menu.setHeaderTitle("Opções da Atividade");
			
			menu.add(0, CMD_COMPARTILHAR, 0, R.string.mmuCompartilhar);			
			menu.add(0, CMD_INICIAR, 0, R.string.mmuIniciar);
			menu.add(0, CMD_CONCLUIR, 0, R.string.mmuConcluir);
			menu.add(0, CMD_EXCLUIR, 0, R.string.mmuDelete);
		}

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case CMD_COMPARTILHAR:
			
			Intent contacts = new Intent(getApplicationContext(),
					ShareContactsActivity.class);
			startActivity(contacts);
			
			return true;

		case CMD_INICIAR:
			Atividade atIniciar = (Atividade) getListAdapter().getItem(
					info.position);
			FerleteApp.getDatabase().updateStatusActivity(atIniciar, 1);

			loadDate();

			return true;

		case CMD_CONCLUIR:
			Atividade atConcluir = (Atividade) getListAdapter().getItem(
					info.position);
			FerleteApp.getDatabase().updateStatusActivity(atConcluir, 2);

			loadDate();

			return true;
		case CMD_EXCLUIR:
			Atividade atExcluir = (Atividade) getListAdapter().getItem(
					info.position);
			FerleteApp.getDatabase().deleteActivity(atExcluir);

			// atualiza adapter
			adapter.remove(adapter.getItem(info.position));

			String txt = "Atividade:'" + atExcluir.getDescricao()
					+ "' Excluida!";
			Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT)
					.show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void loadDate() {
		atividades.clear();
		atividades = FerleteApp.getDatabase().getListAtividades();
		adapter = new MyAdapter(getApplicationContext(), 0, atividades);
		setListAdapter(adapter);
	}

}
