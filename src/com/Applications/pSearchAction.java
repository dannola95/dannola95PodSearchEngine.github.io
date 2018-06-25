package com.Applications;

import java.io.BufferedReader;
import org.json.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * This class takes a query from the user and uses the 
 * GPODDER.net search API to conduct a search. If no results are found then a
 * "NO MATCH FOUND" page is displayed and allows the user to redirect to
 * the home page
 */

public class pSearchAction extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		PrintWriter out = res.getWriter();
		
		//Receive query provided by the user through a html form 
		String query =  req.getParameter("search1"); 
		
		
		//Desired URL to retrieve subscriber JSON data
		String Direction = "https://gpodder.net/search.json?q=" + URLEncoder.encode(query, "UTF-8");

		try {
			
			//Establish a HTTP URL connection 
		
			URL url = new URL(Direction);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.connect();

			//Read response from server
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			
			
			
			//Create JSON Array Object in order to access subscribers data 
			JSONArray obj = new JSONArray(response.toString());
			
			//If JSON array object is null then no match is found.
			//Direct the user back to the homepage for a new search query
			
			if(obj.isNull(0)) {
				out.println("<html>");
				out.println("<body>");
				out.println("<center>");
				out.println("<font size=\"12\">");
				out.println("<b>");
				out.println("<font color=\"red\">");
				out.println("NO MATCH FOUND. PLEASE TRY AGAIN!");
				out.println("<center> <font size=\\\"12\\\"> <button type=\"button\" name=\"back\" onclick=\"history.back()\">back</button> </center> </font>");
				out.println("<img src=https://cdn.dribbble.com/users/308895/screenshots/2598725/no-results.gif>");
				out.println("</font>");
				out.println("</b>");
				out.println("</font>");
				out.println("</center>");
				out.println("Image Source: https://cdn.dribbble.com/users/308895/screenshots/2598725/no-results.gif");
				out.println("</body>");
				out.println("</html>");
				return;
			}
			
			
			out.println("THE FOLLOWING PODCASTS MATCHED YOUR SEARCH...");
			out.println();

			// Loop through array obj and create individual JSON objects
			// Extract desired data from the JSON objects

			for (int index = 0; index < obj.length(); index++) {
				JSONObject jsonObject = obj.getJSONObject(index);
				out.println("Podcast Title: " + jsonObject.getString("title"));
				out.println("Description: " + jsonObject.getString("description"));
				out.println("Subscribers: " + jsonObject.getInt("subscribers"));
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
			out.println("NO MATCH FOUND. PLEASE TRY AGAIN!");
			out.println("<center> <font size=\\\"12\\\"> <button type=\"button\" name=\"back\" onclick=\"history.back()\">back</button> </center> </font>");
			out.println("</font>");
			out.println("</b>");
			out.println("</font>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
			
			
		}

	}

}
