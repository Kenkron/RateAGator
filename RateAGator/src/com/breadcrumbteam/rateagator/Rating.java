package com.breadcrumbteam.rateagator;

import java.io.Serializable;
import java.util.ArrayList;

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

public class Rating implements Serializable{
	
	private static final long serialVersionUID = -560649625967547531L;
	
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
	private double[] responses = new double[FIELD_NAMES.length];
	
	/**a counter for adding responses one at a time*/
	private int currentResponse = 0;
	
	public Rating(int numOfResponses) {
		this.totalResponses = numOfResponses;
	}
	
	public void addResponseValue(double responseValue) {
		if(currentResponse < FIELD_NAMES.length) {
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
	
	/**creates an average of all of the ratings in the given
	 * list.  totalResponses will be set to the total number of
	 * responses in all the ratings collectively.  The averages
	 * will be based on the number of responses, so the rating in
	 * a course without many responses will influence the average
	 * less*/
	public static Rating merge(ArrayList<Rating> ratings){
		int totalResponses=0;
		double[] averageResponses=new double[FIELD_NAMES.length];
		for (Rating r:ratings){
			totalResponses+=r.totalResponses;
			for (int i=0;i<FIELD_NAMES.length;i++){
				averageResponses[i]+=r.responses[i]*r.totalResponses;
			}
		}
		for (int i=0;i<FIELD_NAMES.length;i++){
			averageResponses[i]/=totalResponses;
		}
		Rating average=new Rating(totalResponses);
		average.responses=averageResponses;
		return average;
	}
}
