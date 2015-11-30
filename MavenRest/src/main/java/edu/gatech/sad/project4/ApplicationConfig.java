/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author smithda
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
    	NewHibernateUtil.getSessionFactory();
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(edu.gatech.sad.project4.Resources.DeleteClearAllStudentpreferenceCoursesResource.class);
        resources.add(edu.gatech.sad.project4.Resources.DeleteClearProfessorProficiencyResource.class);
        resources.add(edu.gatech.sad.project4.Resources.DeleteRemoveCourseFromStudentpreferencetableResource.class);
        resources.add(edu.gatech.sad.project4.Resources.DeleteRemoveProfessorProficiencyResource.class);
        resources.add(edu.gatech.sad.project4.Resources.DeleteRemoveProfessorResource.class);
        resources.add(edu.gatech.sad.project4.Resources.DeleteRemoveStudentResource.class);
        resources.add(edu.gatech.sad.project4.Resources.DeleteRemoveStudentpreferenceResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetAllCoursesResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetAllPreferenceForStudentResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetAllProfessorsResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetAllTasForCourseResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetAllTasResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetCourseResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetLatestCompleteProcessingstatustableEntryResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetOfferedCoursesResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetProfessorResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetStudentPreference.class);
        resources.add(edu.gatech.sad.project4.Resources.GetStudentResource.class);
        resources.add(edu.gatech.sad.project4.Resources.GetStudentsInCourseResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAddCourseCompleteToStudentResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAddCoursesToStudentpreferencetableResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAddNewProfessorResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAddNewStudentResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAddNewStudentpreferenceResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAddProfessorProficiencyResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAssignEnrollmentLimitToCourseResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutAssignTaWeightingToCourseResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutChangeAdministratorPasswordResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutChangeStudentPasswordResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutChangeStudentPreferenceNumCoursesDesiredResource.class);
        resources.add(edu.gatech.sad.project4.Resources.PutSetStudentTaResource.class);
        resources.add(edu.gatech.sad.project4.Resources.StudentLoginResource.class);
    }
    
}
