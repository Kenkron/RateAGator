package com.breadcrumbteam.rateagator;

/**this is an object representing a particular course under
 * a particular professor.  It contains the name of the course,
 * the number of the course, the name of the professor, and 
 * a list of the evaluations for this professor-specific course*/
public class Course {
	
	/**The human readable name of the course*/
	protected String courseName;
	
	/**The cryptic number assigned to this course
	 * usually three letters followed by four numbers*/
	protected String courseNum;
	
	/*The professor teaching this particular course*/
	protected Professor courseProf;
	
	public Course(String courseName, String courseNum){
		this.courseName = courseName;
		this.courseNum = courseNum;
	}
	
	/*Associates the given professor with this course
	 * called when this course is added to the professor's courseList
	 */
	public void setCourseProf(Professor p){
		courseProf = p;
	}
}
