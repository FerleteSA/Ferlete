package com.ferlete.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Device")
public class Device {

	private String deviceID;
	private String lastDate;
	private Double lastLatitude;
	private Double lastLongitude;
	private List<Atividade> activityDevice;
	private List<Ponto> coordenada;

	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * @param deviceID
	 *            the deviceID to set
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public List<Atividade> getActivityDevice() {
		return activityDevice;
	}

	public void setActivityDevice(List<Atividade> activityDevice) {
		this.activityDevice = activityDevice;
	}

	public List<Ponto> getCoordenada() {
		return coordenada;
	}

	public void setCoordenada(List<Ponto> coordenada) {
		this.coordenada = coordenada;
	}

	
	/**
	 * @return the lastDate
	 */
	public String getLastDate() {
		return lastDate;
	}

	/**
	 * @param lastDate the lastDate to set
	 */
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	/**
	 * @return the lastLatitude
	 */
	public Double getLastLatitude() {
		return lastLatitude;
	}

	/**
	 * @param lastLatitude the lastLatitude to set
	 */
	public void setLastLatitude(Double lastLatitude) {
		this.lastLatitude = lastLatitude;
	}

	/**
	 * @return the lastLongitude
	 */
	public Double getLastLongitude() {
		return lastLongitude;
	}

	/**
	 * @param lastLongitude the lastLongitude to set
	 */
	public void setLastLongitude(Double lastLongitude) {
		this.lastLongitude = lastLongitude;
	}

}
