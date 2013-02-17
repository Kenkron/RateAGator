package com.breadcrumbteam.rateagator;

public class Evaluation {
	private int totalResponses;
	private double[] responses = new double[10];
	private int currentResponse = 0;
	public Evaluation(int numOfResponses) {
		this.totalResponses = numOfResponses;
	}
	
	public void addResponseValue(double responseValue) {
		if(currentResponse < 10) {
			responses[currentResponse] = responseValue;
			currentResponse++;
		}
	}
	
	public int getTotalResponses() {
		return totalResponses;
	}
	
	public double[] getResponses() {
		return responses;
	}
}
