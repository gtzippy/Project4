/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.gatech.sad.project4.InteractionLayer;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Daniel
 */
@Path("/PutAddCoursesToStudentpreferencetable")
public class PutAddCoursesToStudentpreferencetableResource extends ResourceBase{

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PutAddCoursesToStudentpreferencetableResource
     */
    public PutAddCoursesToStudentpreferencetableResource() {
    }

    /**
     * Retrieves representation of an instance of edu.gatech.sad.project4.Resources.PutAddCoursesToStudentpreferencetableResource
     * @return an instance of javax.ws.rs.core.Response
     */
    @GET
    @Path("{courseCodes}/{preferenceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson(@PathParam("courseCodes") String courseCodes, @PathParam("preferenceId") int preferenceId) {
    try{
    	iLayer.addCoursesToStudentpreferencetable(courseCodes, preferenceId);
        return Response.ok().build();
    } catch (Throwable ex) {
    		Logger.getLogger(PutAddCoursesToStudentpreferencetableResource.class.getName()).log(Level.SEVERE, null, ex);
    		return Response.noContent().type(ex.getMessage()).build();
    	}
    }

    /**
     * PUT method for updating or creating an instance of PutAddCoursesToStudentpreferencetableResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(Response content) {
    }
}
