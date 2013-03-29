package com.breadcrumbteam.rateagator;
import java.io.Serializable;
import java.util.ArrayList;

/**this class represents the data associated with a professor
 * It includes the name of the professor, a list of the courses
 * that the professor teaches, and a single evaluation object 
 * representing the average evaluations for this professor*/
public class CourseSet implements Serializable{
	
	public enum SetType{
		ProfessorSet,
		CourseSet
	}
	
	private static final long serialVersionUID = 8846792087777183741L;

	protected String setName;
	
	protected ArrayList<Course> courseList;
	
	public SetType type;
	
	public CourseSet(String setName){
		this.setName = setName;
		courseList = new ArrayList<Course>();
	}
	
	/*Adds the given course to the courseList
	 * sets this professor as the course's courseProf
	 */
	public void addCourse(Course c) {
		courseList.add(c);
	}
	

}
