package database;
import javax.inject.*;

import play.db.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JavaApplicationDatabase {

    private Database db;

    //@Inject
    public JavaApplicationDatabase(Database db) {
        this.db = db;
    }

   public double insertRow(String user_name, double latitude, double longitude){
	   double cum_distance = getCumulativeDistance(user_name, latitude, longitude);
	   String current_timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	   String sql = "INSERT INTO UserLocation(user, latitude, longitude, timestamp, cumulative_dist)" + "VALUES(?, ?, ?, ?, ?);";
	   try( Connection connection = db.getConnection();
			   PreparedStatement pstmt = connection.prepareStatement(sql);) {
		   pstmt.setString(1,user_name);
		   pstmt.setDouble(2, latitude);
		   pstmt.setDouble(3, longitude);
		   pstmt.setString(4, current_timestamp);
		   pstmt.setDouble(5, cum_distance);
		   
		   
		   
	   pstmt.executeUpdate();
	   pstmt.close();
	   connection.close();
	   }
	   catch (SQLException e) {
		   System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	   }
	   System.out.println("Record created successfully");
	   return cum_distance;
   }
   
   public double getCumulativeDistance(String user, double latitude, double longitude) {
	   double cumulativeDistance = 0;
	   double prevLatitude = 0;
	   double prevLongitude = 0;
	   double cumDistanceTillNow = 0;
	   String sql = "SELECT * FROM UserLocation ORDER BY id DESC LIMIT 1";
	   try{ 
		   Connection connection = db.getConnection();
		   Statement pstmt = connection.createStatement();
		   ResultSet rs    = pstmt.executeQuery(sql); 
		   System.out.println("In try");
		   //means it is first entry
		   if(!rs.next()) {
			   cumulativeDistance = 0;
		   }
		   else {
			   prevLatitude = rs.getDouble("latitude");
			   System.out.println("calc prev lat" + prevLatitude);
			   prevLongitude = rs.getDouble("longitude");
			   cumDistanceTillNow = rs.getDouble("cumulative_dist");
			   cumulativeDistance = cumDistanceTillNow + findDistance(prevLatitude, prevLongitude, latitude, longitude);
		   }
		   rs.close();
		   pstmt.close();
		   connection.close();
	   }
	   catch (SQLException e) {
           System.out.println(e.getMessage());
       }
	   return cumulativeDistance;
   }
   
   // Source for finding distance : https://stackoverflow.com/questions/18861728/calculating-distance-between-two-points-represented-by-lat-long-upto-15-feet-acc
   public double findDistance(double prevLatitude, double prevLongitude, double latitude, double longitude) {
	   double R = 6371; // Radius of the earth in km
	   double dLat = Math.toRadians(latitude-prevLatitude);  // deg2rad below
	   double dLon = Math.toRadians(longitude-prevLongitude); 
	   double a = 
	     Math.sin(dLat/2) * Math.sin(dLat/2) +
	     Math.cos(Math.toRadians(prevLatitude)) * Math.cos(Math.toRadians(latitude)) * 
	     Math.sin(dLon/2) * Math.sin(dLon/2)
	     ; 
	   double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	   double d = R * c; // Distance in km
	   System.out.println("Distance caluclated "+ d);
	   return d*1000;
   }


}
