package com.Applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 * This class takes in a genre search and returns the user a list of 
 * matching genres from most popular to least popular
 * 
 */

public class Genre extends HttpServlet {


	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		PrintWriter out = res.getWriter();

		// Receive genre information from HTML file
		String genre = req.getParameter("genre").toLowerCase();

		// Receive checkbox value. If both or neither checkboxs are selected, default
		// value (MOST TO LEAST) will be chosen
		String order[] = null;
		boolean mostToLeast = true;

		if (req.getParameterValues("order") != null) {
			order = req.getParameterValues("order");
			if (order[0].toString().equalsIgnoreCase("Most Popular to Least Popular")) {
				// Boolean stays as true
			} else if (order[0].toString().equalsIgnoreCase("Least Popular to Most Popular")) {
				mostToLeast = false;
			}

		} else {
			// default value (MOST TO LEAST) will be chosen. Boolean == true
		}

		// Requesting the first 1000 Podcast that meet this genre
		String Direction = "https://gpodder.net/api/2/tag/" + URLEncoder.encode(genre, "UTF-8") + "/1000.json";

		try {

			// Establish a HTTP URL connection
			URL url = new URL(Direction);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.connect();

			// Get and read response from server
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Create JSON array object in order to access subscribers data
			JSONArray obj = new JSONArray(response.toString());

			// If JSON array object is null then no match is found.
			// Direct the user back to the homepage for a new search query

			if (obj.isNull(0)) {
				out.println("<html>");
				out.println("<body>");
				out.println("<center>");
				out.println("<font size=\"12\">");
				out.println("<b>");
				out.println("<font color=\"red\">");
				out.println("NO MATCH FOUND. PLEASE TRY AGAIN!");
				out.println(
						"<center> <font size=\\\"12\\\"> <button type=\"button\" name=\"back\" onclick=\"history.back()\">back</button> </center> </font>");
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

			out.println("THE FOLLOWING PODCASTS MATCHED YOUR GENRE SEARCH: " + genre);
			out.println();

			// Create new JSON array object. This object will be sorted
			// based on the users preference
			JSONArray sortedObj = new JSONArray();

			List<JSONObject> jsonValues = new ArrayList<JSONObject>();

			for (int i = 0; i < obj.length(); i++) {
				jsonValues.add(obj.getJSONObject(i));

			}

			// Sort JSON objects by tag - "Subscribers" from Greatest to Least
			Collections.sort(jsonValues, new Comparator<JSONObject>() {

				private static final String KEY = "subscribers";

				@Override
				public int compare(JSONObject a, JSONObject b) {

					int value1 = 0, value2 = 0;

					try {

						value1 = (a.getInt(KEY));
						value2 = (b.getInt(KEY));

					} catch (Exception e) {
						// Error has occurred. Direct user to homepage
						out.println("<html>");
						out.println("<body>");
						out.println("<center>");
						out.println("<font size=\"12\">");
						out.println("<b>");
						out.println("<font color=\"red\">");
						out.println("ERROR. PLEASE RETURN TO HOME PAGE");
						out.println(
								"<center> <font size=\\\"12\\\"> <button type=\"button\" name=\"back\" onclick=\"history.back()\">back</button> </center> </font>");
						out.println("</font>");
						out.println("</b>");
						out.println("</font>");
						out.println("</center>");
						out.println("</body>");
						out.println("</html>");
					}

					// Default sorting: Most Popular to Least Popular
					if (value1 == value2) {
						return 0;
					} else if (value1 < value2) {
						return 1;
					} else {
						return -1;
					}

				}
			});

			for (int i = 0; i < obj.length(); i++) {
				sortedObj.put(jsonValues.get(i));
			}

			// Loop through JSON array object and create a JSON object for each
			// Extract that data from the JSON object and display according to the users
			// Preference of order

			if (mostToLeast == true) {

				for (int index = 0; index < sortedObj.length(); index++) {
					JSONObject jsonObject = sortedObj.getJSONObject(index);

					out.println("Podcast Title: " + jsonObject.getString("title"));
					out.println("Description: " + jsonObject.getString("description"));
					out.println("Subscribers: " + jsonObject.getInt("subscribers"));
					out.println("Website Link: " + jsonObject.getString("website"));
					out.println("Podcast Link: " + jsonObject.getString("mygpo_link"));
					out.println();
					out.println();

				}

			} else {

				for (int index = sortedObj.length() - 1; 0 < index; index--) {
					JSONObject jsonObject = sortedObj.getJSONObject(index);
					out.println("Podcast Title: " + jsonObject.getString("title"));
					out.println("Description: " + jsonObject.getString("description"));
					out.println("Subscribers: " + jsonObject.getInt("subscribers"));
					out.println("Website Link: " + jsonObject.getString("website"));
					out.println("Podcast Link: " + jsonObject.getString("mygpo_link"));
					out.println();
					out.println();
				}
			}

		} catch (Exception e) {
			// Error has occured. Direct user back to homepage
			out.println("<html>");
			out.println("<body>");
			out.println("<center>");
			out.println("<font size=\"12\">");
			out.println("<b>");
			out.println("<font color=\"red\">");
			out.println("NO MATCH");
			out.println(
					"<center> <font size=\\\"12\\\"> <button type=\"button\" name=\"back\" onclick=\"history.back()\">back</button> </center> </font>");
			out.println("</font>");
			out.println("</b>");
			out.println("</font>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
		}
	}
}
