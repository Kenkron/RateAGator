package com.breadcrumbteam.rateagator;

/**
 * 
 * @author westwiatt
 *
 *Types of Ratings:
 *Responded
 *OverallRating
 *Language
 *Work
 *Difficulty
 *Book
 *Hotness
 *Clarity Helpfulness
 *Course Relevance
 *
 */

public class Rating {
	public static final String[] DB_FIELD_NAMES = 
		{
			"OverallRating",
			"Language",
			"Work",
			"Difficulty",
			"Book",
			"Hotness",
			"Clarity",
			"Helpfulness",
			"Relevance"
		};
	public static final String[] FIELD_NAMES=
		{
			"Overall Rating",
			"Language Proficiency",
			"Work Load",
			"Difficulty",
			"Book",
			"Hotness",
			"Content Clarity",
			"Helpfulness",
			"Course Relevance"
		};
	
	private int totalResponses;
	private double[] responses = new double[8];
	private int currentResponse = 0;
	
	public Rating(int numOfResponses) {
		this.totalResponses = numOfResponses;
	}
	
	public void addResponseValue(double responseValue) {
		if(currentResponse < 8) {
			responses[currentResponse] = responseValue;
			currentResponse++;
		}
	}
	
	public int getTotalRatingResponses() {
		return totalResponses;
	}
	
	public double[] getRatingResponses() {
		return responses;
	}
}
