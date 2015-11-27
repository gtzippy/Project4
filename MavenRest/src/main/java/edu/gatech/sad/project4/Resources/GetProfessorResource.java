/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gatech.sad.project4.InteractionLayer;
import edu.gatech.sad.project4.entities.Professorstable;
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
@Path("/getProfessor")
public class GetProfessorResource extends ResourceBase{

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetProfessorResource
     */
    public GetProfessorResource() {
    }

    /**
     * Retrieves representation of an instance of
     * edu.gatech.sad.project4.GetProfessorResource
     *
     * @param id
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getJson(@PathParam("id") int id) {
        String professorString = null;
        //int intId = Integer.parseInt(id);
        try {
            InteractionLayer iLayer = InteractionLayer.Instance();            
            Professorstable pt = iLayer.getProfessor(id);
            return Response.ok(mapper.writeValueAsString(pt)).build();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GetProfessorResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.noContent().type(ex.getMessage()).build();
        }
    }

    /**
     * PUT method for updating or creating an instance of GetProfessorResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
