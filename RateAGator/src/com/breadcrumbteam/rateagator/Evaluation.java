package com.breadcrumbteam.rateagator;

import java.io.Serializable;
import java.util.ArrayList;

public class Evaluation implements Serializable{
	
	private static final long serialVersionUID = 1626338404173274034L;

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
			"Enthusiasm for subject",
			"Encouragement of independent thinking",
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
	
	public static Evaluation mergeEvaluations(ArrayList<Evaluation> evals) {
		int totalResponses = 0;
		double[] responses = new double[10];
		for(Evaluation e : evals) {
			totalResponses += e.getTotalResponses();
			for(int i = 0;i<10;i++) {
				responses[i] += e.getResponses()[i] * e.getTotalResponses();
			}
		}
		
		Evaluation combinedEval = new Evaluation(totalResponses);
		for(int i = 0;i<10;i++) {
			combinedEval.addResponseValue(responses[i]/totalResponses);
		}
		return combinedEval;
	}
}
