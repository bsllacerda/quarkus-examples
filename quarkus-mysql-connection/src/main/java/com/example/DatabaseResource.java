package com.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.jboss.logging.Logger;
import io.quarkus.logging.Log;

@Path("/db")
public class DatabaseResource {

    //private static final Logger LOG = Logger.getLogger(DatabaseResource.class);

    @Inject
    DataSource dataSource;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ConnectionTestResult testConnection() {
        Log.info("Executando endpoint db");
        ConnectionTestResult result = new ConnectionTestResult();

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1000)) {
                result.setStatus("SUCCESS");
                result.setMessage("Connection is valid! 🙂");
                Log.info("Connection is valid!");
            } else {
                result.setStatus("FAILURE");
                result.setMessage("Connection is not valid. 😢");
                Log.warn("Connection is not valid.");
            }
        } catch (SQLException exception) {
            result.setStatus("ERROR");
            result.setMessage("Failed to create a connection. 😢" + exception.getMessage());
            Log.error("Failed to create a connection.", exception);
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
