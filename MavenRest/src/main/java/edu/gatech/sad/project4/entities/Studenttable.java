package edu.gatech.sad.project4.entities;
// Generated Nov 26, 2015 12:46:12 AM by Hibernate Tools 4.3.1



/**
 * Studenttable generated by hbm2java
 */
public class Studenttable  implements java.io.Serializable {


     private int id;
     private String name;
     private String password;
     private boolean ta;
     private int creditHoursCompleted;
     private String takenCourses;
     private String email;

    public Studenttable() {
    }

	
    public Studenttable(String name, String password, boolean ta, int creditHoursCompleted, String email) {
        this.name = name;
        this.password = password;
        this.ta = ta;
        this.creditHoursCompleted = creditHoursCompleted;
        this.email = email;
    }
    public Studenttable(String name, String password, boolean ta, int creditHoursCompleted, String takenCourses, String email) {
       this.name = name;
       this.password = password;
       this.ta = ta;
       this.creditHoursCompleted = creditHoursCompleted;
       this.takenCourses = takenCourses;
       this.email = email;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isTa() {
        return this.ta;
    }
    
    public void setTa(boolean ta) {
        this.ta = ta;
    }
    public int getCreditHoursCompleted() {
        return this.creditHoursCompleted;
    }
    
    public void setCreditHoursCompleted(int creditHoursCompleted) {
        this.creditHoursCompleted = creditHoursCompleted;
    }
    public String getTakenCourses() {
        return this.takenCourses;
    }
    
    public void setTakenCourses(String takenCourses) {
        this.takenCourses = takenCourses;
    }


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}




}


