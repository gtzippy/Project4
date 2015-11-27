/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.core.Response;

/**
 *
 * @author Daniel
 */
public abstract class ResourceBase {
    public static ObjectMapper mapper;
    
    public static void setObjectMapper(ObjectMapper mapper){
        ResourceBase.mapper = mapper;
    }
}
