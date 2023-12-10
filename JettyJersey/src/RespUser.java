// package com.mkyong;

//Class that represents an User related error 
//Used in UserConnection.java
public class RespUser {
    // Make atributos public (for easy acess)
    public String status;

    public String error;

    RespUser(){
        
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
