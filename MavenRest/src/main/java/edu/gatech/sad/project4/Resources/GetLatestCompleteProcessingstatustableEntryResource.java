/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.gatech.sad.project4.InteractionLayer;
import edu.gatech.sad.project4.entities.Processingstatustable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Daniel
 */
@Path("/GetLatestCompleteProcessingstatustableEntry")
public class GetLatestCompleteProcessingstatustableEntryResource extends ResourceBase{

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of
     * GetLatestCompleteProcessingstatustableEntryResource
     */
    public GetLatestCompleteProcessingstatustableEntryResource() {
    }

    /**
     * Retrieves representation of an instance of
     * edu.gatech.sad.project4.Resources.GetLatestCompleteProcessingstatustableEntryResource
     *
     * @return an instance of javax.ws.rs.core.Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson() {
        try {
            Processingstatustable pst = iLayer.getLatestCompleteProcessingstatustableEntry();
            if(pst == null){
            	Response.ok("DB is null").header("Access-Control-Allow-Origin", "*").build();
            }
            return Response.ok(mapper.writeValueAsString(pst)).header("Access-Control-Allow-Origin", "*").build();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(GetLatestCompleteProcessingstatustableEntryResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.noContent().type(ex.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        }        
    }

    /**
     * PUT method for updating or creating an instance of
     * GetLatestCompleteProcessingstatustableEntryResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(Response content) {
    }
}
