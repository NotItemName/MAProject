package com.epam.preprod.ma.bean;

public class ChartsData {

	private MotivationMapBean motivationMap;

	private DetailedChartBean details;

	public ChartsData() {
	}

	public ChartsData(MotivationMapBean motivationMap, DetailedChartBean details) {
		super();
		this.motivationMap = motivationMap;
		this.details = details;
	}

	public MotivationMapBean getMotivationMap() {
		return motivationMap;
	}

	public DetailedChartBean getDetails() {
		return details;
	}

	public void setMotivationMap(MotivationMapBean motivationMap) {
		this.motivationMap = motivationMap;
	}

	public void setDetails(DetailedChartBean details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "ChartsData [" + motivationMap + ", \n	" + details + "]";
	}

}
