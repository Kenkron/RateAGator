package com.breadcrumbteam.rateagator;
import java.util.ArrayList;

/**this class represents the data associated with a professor
 * It includes the name of the professor, a list of the courses
 * that the professor teaches, and a single evaluation object 
 * representing the average evaluations for this professor*/
public class Professor {
	
	protected String fName;
	protected String lName;
	
	protected ArrayList<Course> courseList;
	
	public Professor(String fName, String lName){
		this.fName = fName;
		this.lName = lName;
		courseList = new ArrayList<Course>();
	}
	
	/*Adds the given course to the courseList
	 * sets this professor as the course's courseProf
	 */
	public void addCourse(Course c) {
		courseList.add(c);
		c.setCourseProf(this);
	}
	

}
