package com.breadcrumbteam.rateagator;

import java.io.Serializable;

/**this is an object representing a particular course under
 * a particular professor.  It contains the name of the course,
 * the number of the course, the name of the professor, and 
 * a list of the evaluations for this professor-specific course*/
public class Course implements Serializable{
	
	private static final long serialVersionUID = 7514124260204175686L;

	/**The human readable name of the course*/
	public String courseName;
	
	/**The cryptic number assigned to this course
	 * usually three letters followed by four numbers*/
	public String courseNum;
	
	/**The professor teaching this particular course*/
	public String professorFirstName, professorLastName;
	
	public Course(String courseName, String courseNum){
		this.courseName = courseName;
		this.courseNum = courseNum;
	}
	
	/**Associates the given professor with this course
	 * called when this course is added to the professor's courseList
	 */
	public void setCourseProfessor(String fName,String lName){
		professorFirstName = fName;
		professorLastName = lName;
	}
}
