package com.ferlete.database;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ferlete.R;
import com.ferlete.model.Atividade;

public class MyAdapter extends ArrayAdapter<Atividade>{

	private final Context context;	
	private ArrayList<Atividade> atividades;

	public MyAdapter(Context context, int textViewResourceId, ArrayList<Atividade> atividades) {
		//super(context, R.layout.rowlayout, atividades);
		super(context, textViewResourceId, atividades);
        this.context = context;
        this.atividades = atividades;
    }
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_listactivity, parent, false);
		
		TextView tvAtividade = (TextView) rowView.findViewById(R.id.tvAtividade);
		TextView tvEndereco = (TextView) rowView.findViewById(R.id.tvEndereco);
		ImageView iconStatus = (ImageView) rowView.findViewById(R.id.icon);
		
		Integer status = atividades.get(position).getStatus();		
		
		
		switch(status){
			case 0:	//Nova			
				iconStatus.setImageResource(R.drawable.ico_pink);
				break;
			case 1:	//Iniciada	
				iconStatus.setImageResource(R.drawable.ico_azure);
				break;
			case 2:	//Finalizada			
				iconStatus.setImageResource(R.drawable.ico_verde);
				break;
			default:
				iconStatus.setImageResource(R.drawable.ico_verde);
			
			}
		
		tvAtividade.setText(atividades.get(position).getDescricao());
		tvEndereco.setText(atividades.get(position).getEndereco());
		
		return rowView;
		
	}
	

	@Override
	public int getCount() {
		return atividades.size();
	}



	@Override
	public Atividade getItem(int position) {
		 return atividades.get(position);
	}



	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
