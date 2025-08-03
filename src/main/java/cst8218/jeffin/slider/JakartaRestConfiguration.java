package cst8218.jeffin.slider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.PasswordHash;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configures Jakarta RESTful Web Services for the application.
 *
 * This class sets up authentication mechanisms and defines database identity store configurations.
 * The configurations allow users to authenticate using basic authentication and validate credentials 
 * against a database.
 *
 * @author leoje
 */
@ApplicationPath("resources")

/**
 * 
 * It specifies the login and error pages to be used for user authentication.
 *
 * @FormAuthenticationMechanismDefinition(
 *         loginToContinue = @LoginToContinue(
 *                 loginPage = "/login.html",
 *                 errorPage = "/login.html"))
 */

@BasicAuthenticationMechanismDefinition
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "${'java:comp/DefaultDataSource'}",
        callerQuery = "#{'select password from app.appuser where userid = ?'}",
        groupsQuery = "select groupname from app.appuser where userid = ?",
        hashAlgorithm = PasswordHash.class,
        priority = 10
)
@Named
@ApplicationScoped
public class JakartaRestConfiguration extends Application {
    /**
     * Default constructor for JakartaRestConfiguration.
     */
    public JakartaRestConfiguration() {
        // Default constructor
    }
}
