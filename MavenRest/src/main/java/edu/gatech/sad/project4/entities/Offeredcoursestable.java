package edu.gatech.sad.project4.entities;
// Generated Nov 26, 2015 12:46:12 AM by Hibernate Tools 4.3.1



/**
 * Offeredcoursestable generated by hbm2java
 */
public class Offeredcoursestable  implements java.io.Serializable {


     private int processingStatusId;
     private String courseCode;

    public Offeredcoursestable() {
    }

	
    public Offeredcoursestable(int processingStatusId) {
        this.processingStatusId = processingStatusId;
    }
    public Offeredcoursestable(int processingStatusId, String courseCode) {
       this.processingStatusId = processingStatusId;
       this.courseCode = courseCode;
    }
   
    public int getProcessingStatusId() {
        return this.processingStatusId;
    }
    
    public void setProcessingStatusId(int processingStatusId) {
        this.processingStatusId = processingStatusId;
    }
    public String getCourseCode() {
        return this.courseCode;
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }




}


