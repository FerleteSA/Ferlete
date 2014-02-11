package com.ferlete.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Ponto")
public class Ponto {
	Double latitude;
	Double longitude;
	String dataLeitura;

	public String getDataLeitura() {
		return dataLeitura;
	}

	public void setDataLeitura(String dataLeitura) {
		this.dataLeitura = dataLeitura;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}
