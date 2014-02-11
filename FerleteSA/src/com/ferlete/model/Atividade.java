package com.ferlete.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Atividade")
public class Atividade {

	private int idAtividade;
	private int status;
	private String descricao;
	private String endereco;
	private Double latitude;
	private Double longitude;

	// Contrutores
	public Atividade() {
	}

	public Atividade(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public int getIdAtividade() {
		return idAtividade;
	}

	public void setIdAtividade(int idDescricao) {
		this.idAtividade = idDescricao;
	}

	@Override
	public String toString() {
		return descricao;
	}

	/**
	 * @return the endereco
	 */
	public String getEndereco() {
		return endereco;
	}

	/**
	 * @param endereco the endereco to set
	 */
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

}
