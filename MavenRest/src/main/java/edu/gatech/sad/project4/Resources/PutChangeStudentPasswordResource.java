/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import edu.gatech.sad.project4.InteractionLayer;
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
@Path("/PutChangeStudentPassword")
public class PutChangeStudentPasswordResource extends ResourceBase{

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PutChangeStudentPasswordResource
     */
    public PutChangeStudentPasswordResource() {
    }

    /**
     * Retrieves representation of an instance of
     * edu.gatech.sad.project4.Resources.PutChangeStudentPasswordResource
     *
     * @return an instance of javax.ws.rs.core.Response
     */
    @GET
    @Path("{studentId}/{newPassword}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson(@PathParam("studentId") int studentId, @PathParam("newPassword") String newPassword) {
        try {
            InteractionLayer iLayer = InteractionLayer.Instance();
            iLayer.changeStudentPassword(studentId, newPassword);
            return Response.ok().build();
        } catch (Throwable ex) {
            Logger.getLogger(PutChangeStudentPasswordResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().status(Response.Status.NOT_FOUND).type(ex.getMessage()).build();
        }
    }

    /**
     * PUT method for updating or creating an instance of
     * PutChangeStudentPasswordResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(Response content) {
    }
}
