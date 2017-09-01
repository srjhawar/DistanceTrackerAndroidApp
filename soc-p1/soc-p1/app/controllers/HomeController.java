package controllers;
import java.sql.Timestamp;

import database.JavaApplicationDatabase;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import play.libs.Json;
import play.libs.Json.*;
//import org.codehaus.jackson.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;
import play.db.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
	@Inject
	Database d; 
    public Result index() {
    	System.out.println("in index");
        return ok(views.html.index.render());
    }
    
    @Inject 
    FormFactory formFactory;
    
    public Result handleupdates() {
    	System.out.println("In handle updates");
    	DynamicForm requestData = formFactory.form().bindFromRequest();
    	
        
        String user = requestData.get("user");
        System.out.println(requestData.get("latitude"));
        double latitude = Double.parseDouble(requestData.get("latitude"));
        double longitude = Double.parseDouble(requestData.get("longitude"));
        //TODO : Timestamp
        JavaApplicationDatabase jd = new JavaApplicationDatabase(d);
        double distance = jd.insertRow(user, latitude, longitude);
        System.out.println("Inside handle updates");
        ObjectNode result = Json.newObject();
        result.put("distance", distance);
        return ok(result);
        //return ok();
    }
    
    
    
    

}
