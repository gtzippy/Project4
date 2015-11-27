/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gatech.sad.project4.InteractionLayer;
import edu.gatech.sad.project4.entities.Studentpreferencestable;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Path("/GetStudentPreference")
public class GetStudentPreference extends ResourceBase{

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetStudentPreference
     */
    public GetStudentPreference() {
    }

    /**
     * Retrieves representation of an instance of edu.gatech.sad.project4.Resources.GetStudentPreference
     * @return an instance of javax.ws.rs.core.Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{studentPreferenceId}")
    public Response getJson(@PathParam("studentPreferenceId") int studentPreferenceId) {
        try {
            InteractionLayer iLayer = InteractionLayer.Instance();
            Studentpreferencestable spt = iLayer.getStudentPreference(studentPreferenceId);
            return Response.ok(mapper.writeValueAsString(spt)).build();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GetStudentPreference.class.getName()).log(Level.SEVERE, null, ex);
            return Response.noContent().type(ex.getMessage()).build();
        }
    }

    /**
     * PUT method for updating or creating an instance of GetStudentPreference
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(Response content) {
    }
}
