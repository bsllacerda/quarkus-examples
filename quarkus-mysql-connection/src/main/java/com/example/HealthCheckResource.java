package com.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import io.quarkus.logging.Log;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Path("")
public class HealthCheckResource {    

    @Inject
    DataSource dataSource;
	
	private final LocalDateTime startTime = LocalDateTime.now();
	
	/**
	 * Neste código, ao instanciar StartupProbeResource, a hora atual é registrada.
	 * Cada vez que o endpoint /startup é acessado, calculamos quantos segundos se passaram desde a inicialização.
	 * Durante os primeiros 30 segundos, o endpoint retorna um status HTTP 503 (Service Unavailable).
	 * Após 60 segundos, ele retorna um status HTTP 200 com a mensagem "Service ready!".
	 */
	@GET
    @Produces(MediaType.TEXT_PLAIN)
	@Path("/startup")
    public Response startupCheck() {		

        Log.info("Executando startupCheck");
		
		LocalDateTime now = LocalDateTime.now();
        long secondsSinceStart = ChronoUnit.SECONDS.between(startTime, now);

        if (secondsSinceStart < 30) {
			Log.warn("Not Started Yet! A Aplicação ainda está inicializando...");
            // Return a 503 Service Unavailable status for the first 60 seconds
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Application is still warming up!")
                    .build();
        }
		
		Log.info("Startup OK! Aplicação inicializada!");
		
        return Response.ok("Application started!").build();

    }

	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/healthz")
    public ConnectionTestResult healthzCheck() {   
	
		Log.info("Executando healthCheck");

		ConnectionTestResult result = new ConnectionTestResult();        
        result.setStatus("SUCCESS");
        result.setMessage("OK");

        Log.info("Health: OK");            

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/ready")
    public ConnectionTestResult readyCheck() {

        Log.info("Executando readyCheck");

		ConnectionTestResult result = new ConnectionTestResult();

        try (Connection connection = dataSource.getConnection()) {
			
            if (connection.isValid(1000)) {
				
                result.setStatus("SUCCESS");
                result.setMessage("OK");
				
                Log.info("Ready: OK");
				
            } else {
				
                result.setStatus("FAILURE");
                result.setMessage("Failed");
				
                Log.warn("Not Ready! Connection is not valid.");
            }
        } catch (SQLException exception) {
			
            result.setStatus("ERROR");
            result.setMessage("Failed to create a connection. \nMessage: " + exception.getMessage());
			
            Log.error("Not Ready! Failed to create a connection.", exception);
        }

        return result;
    }	

    public static class ConnectionTestResult {
        private String status;
        private String message;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
