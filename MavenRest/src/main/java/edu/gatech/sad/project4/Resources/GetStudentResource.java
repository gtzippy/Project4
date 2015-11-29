/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gatech.sad.project4.InteractionLayer;
import static edu.gatech.sad.project4.Resources.ResourceBase.mapper;
import edu.gatech.sad.project4.entities.Professorstable;
import edu.gatech.sad.project4.entities.Studenttable;
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
 * @author smithda
 */
@Path("/GetStudent")
public class GetStudentResource extends ResourceBase{

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetStudentResource
     */
    public GetStudentResource() {
    }

    /**
     * Retrieves representation of an instance of edu.gatech.sad.project4.GetStudentResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getJson(@PathParam("id") int id) {
        String studentString = null;
        //int intId = Integer.parseInt(id);
        try {
            Studenttable st = iLayer.getStudent(id);
            return Response.ok(mapper.writeValueAsString(st)).build();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GetProfessorResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.noContent().type(ex.getMessage()).build();
        }
    }

    /**
     * PUT method for updating or creating an instance of GetStudentResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
