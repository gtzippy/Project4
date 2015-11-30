/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.gatech.sad.project4.entities.Studenttable;

/**
 * REST Web Service
 *
 * @author Daniel
 */
@Path("/GetAllTasForCourse")
public class GetAllTasForCourseResource extends ResourceBase {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetAllTasForCourseResource
     */
    public GetAllTasForCourseResource() {
    }

    /**
     * Retrieves representation of an instance of
     * edu.gatech.sad.project4.Resources.GetAllTasForCourseResource
     *
     * @return an instance of javax.ws.rs.core.Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{courseCode}")
    public Response getJson(@PathParam("courseCode") String courseCode) {
        try {
            List<Studenttable> tas = iLayer.getAllTasForCourse(courseCode);
            return Response.ok(mapper.writeValueAsString(tas)).header("Access-Control-Allow-Origin", "*").build();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GetAllTasForCourseResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.noContent().type(ex.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        } 
    }

    /**
     * PUT method for updating or creating an instance of
     * GetAllTasForCourseResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(Response content) {
    }
}
