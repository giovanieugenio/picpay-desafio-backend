package com.ms.picpaydesafiobackend.authorization;

public record Authorization(
        String message) {

    public boolean isAuthorized(){
        return this.message.equals("Autorizado");
    }
}
