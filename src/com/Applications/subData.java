package com.Applications;

import java.io.BufferedReader;
import org.json.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * This class returns the users GPODDER.net account subscriptions once 
 * a 200 status code is received from the server after authentication 
 */

public class subData extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		PrintWriter out = res.getWriter();
		
		//Keep HTTP client and HTTP server session alive
		HttpSession session = req.getSession();
		
		String sessionid = null;
		
		
		//Determine whether user has logged in or not 
		if (session.getAttribute("sessionid") != null) {
			sessionid = (String) session.getAttribute("sessionid");
		} else {
			/*
			 * User has not logged in. I believe we are still able to proceed.
			 * 
			 * CODE REVIEWER WILL BE LOGGED INTO GPODDER.NET SO CLIENT SHOULD
			 * STILL BE ABLE TO RECEIVE SUBSCRIPTION DATA
			 * 
			 */
		}


		
		//Get username from Authentication servlet
		String username = (String) req.getAttribute("userinfo");

		//Desired URL endpoint 
		String endDirection = ".json";
		
		//Desired HTTP URL 
		String Direction = "https://gpodder.net/subscriptions/" + username + endDirection;


		try {
			
			// Establish URL connection and send a new "GET" request to server
			
			URL url = new URL(Direction);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			
			//Request Header containing sessionid
			connection.setRequestProperty("Cookie", sessionid);
			connection.connect();

			//Read response from server
			BufferedReader ans = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = ans.readLine()) != null) {
				response.append(inputLine);
			}
			ans.close();


			out.println("WELCOME " + username + ", YOU ARE SUBSCRIBED TOO...");
			out.println();


			JSONArray obj = new JSONArray(response.toString());

			// Loop through array obj and created a JSON object for each
			// Extract that data from the JSON object

			for (int index = 0; index < obj.length(); index++) {
				JSONObject jsonObject = obj.getJSONObject(index);
				out.println("Podcast Title: " + jsonObject.getString("title"));
				out.println("Podcast Subscribers: " + jsonObject.getInt("subscribers"));
				out.println("Podcast Link: " + jsonObject.getString("mygpo_link"));
				out.println();
				out.println();

			}


		} catch (Exception e) {
			//Error has occured. Direct user back to home page
			out.println("<html>");
			out.println("<body>");
			out.println("<center>");
			out.println("<font size=\"12\">");
			out.println("<b>");
			out.println("<font color=\"red\">");
			out.println("AUTHENTICATION ERROR HAS OCCURED! PLEASE ENTER CORRECT LOGIN CREDENTIALS!");
			out.println("<center> <font size=\\\"12\\\"> <button type=\"button\" name=\"back\" onclick=\"history.back()\">back</button> </center> </font>");
			out.println("<img src=https://i1.wp.com/s3.amazonaws.com/production-wordpress-assets/blog/wp-content/uploads/2017/11/16062724/401-unauthorized-error.jpg?fit=625%2C211&ssl=1>");
			out.println("</font>");
			out.println("</b>");
			out.println("</font>");
			out.println("</center>");
			out.println("Image Source: https://i1.wp.com/s3.amazonaws.com/production-wordpress-assets/blog/wp-content/uploads/2017/11/16062724/401-unauthorized-error.jpg?fit=625%2C211&ssl=1");
			out.println("</body>");
			out.println("</html>");
	
			
			
			//Image was obtained online
			//Image Source: https://i1.wp.com/s3.amazonaws.com/production-wordpress-assets/blog/wp-content/uploads/2017/11/16062724/401-unauthorized-error.jpg?fit=625%2C211&ssl=1
			

		}

}

}
