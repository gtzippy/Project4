/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4;

/**
 *
 * @author Daniel
 */
public class ServerResponse {
    public String json;
    public boolean success;
    
    public ServerResponse(){
        
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getJson() {
        return json;
    }

    public boolean isSuccess() {
        return success;
    }
    
    
}
