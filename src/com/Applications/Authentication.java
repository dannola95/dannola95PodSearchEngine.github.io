package com.Applications;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/*
 * This class passes login credentials to gpodder.net in order to gain authentication.
 * Login is successful once the client receives a 200 status code from the server. The client then
 * sends a sessionid cookie through a HTTP request header in order to gain subscription information. 
 * Subscription information is then displayed to the user.  
 */

public class Authentication extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		
		
		
		//Receive login credentials provided by the user through a html form 
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String info = username + ":" + password;
		
		//Desired URL endpoint 
		String endDirection = "/login.json";
		
		//Desired HTTP URL
		String Direction = "https://gpodder.net/api/2/auth/" + username + endDirection;

		
		try {

			//Establish a HTTP URL Basic Authentication connection 
			
			URL url = new URL(Direction);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			String authStr = Base64.getEncoder().encodeToString(info.getBytes());

			connection.setRequestProperty("Authorization", "Basic " + authStr);
			connection.setRequestMethod("POST");
			connection.connect();
			
			/*
			 * Get sessionid from the server response. sessionid will be sent
			 * back to the server when the next request is made.
			 * 
			 * sessionid helps establish a stafeul protocol between 
			 * the client and server
			 */
			
			String sessionid = connection.getHeaderField(11);
			sessionid = sessionid.split(";")[0];

			//Read status code to check whether a good connection is established
			//int statusCode = connection.getResponseCode();

			//Create a session between an HTTP client and an HTTP server using sessionid
			HttpSession session = req.getSession();
			session.setAttribute("sessionid", sessionid);

		} catch (Exception e) {
			//System.out.println("Error has occured in class pSearch");
		}

		

		// Pass data to subData servlet identified with url pattern "subrequest" in xml file
		req.setAttribute("userinfo", username);
		RequestDispatcher rd = req.getRequestDispatcher("subrequest");
		rd.forward(req, res);



	}
}
