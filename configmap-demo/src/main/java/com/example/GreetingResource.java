package com.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/greeting")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String greeting() {
        String color = System.getenv().getOrDefault("BACKGROUND_COLOR", "white");
        return "<html><body style=\"background-color: " + color + ";\">" +
                "<h1>Hello from Quarkus!</h1>" +
                "</body></html>";
    }
}
