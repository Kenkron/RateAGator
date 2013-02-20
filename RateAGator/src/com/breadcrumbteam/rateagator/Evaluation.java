package com.breadcrumbteam.rateagator;

public class Evaluation {
	
	/**an ordered list of the labels for the fields*/
	public static final String[] FIELD_NAMES=
		{
			"Describing objectives and assignments",
			"Communicating ideas and information",
			"Expressing expectations for performance",
			"Availability to assist students",
			"Respect and concern for students",
			"Stimulation of interest in course",
			"Facilitation of learning",
			"",
			"",
			"Overall assessment of instructor"
		};
	
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
