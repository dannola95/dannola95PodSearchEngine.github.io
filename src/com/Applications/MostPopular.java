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

public class MostPopular extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		PrintWriter out = res.getWriter();

		// Receive genre information from HTML file
		String amount = req.getParameter("number");

		// Desired URL to retrieve JSON data
		String Direction = "https://gpodder.net/api/2/tags/" + URLEncoder.encode(amount, "UTF-8") + ".json";

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

			out.println("THE TOP " + amount + " PODCASTS GENRES ARE... ");
			out.println();

			JSONArray obj = new JSONArray(response.toString());

			// Create new JSON array object. This object will be sorted from greatest to
			// least
			JSONArray sortedObj = new JSONArray();

			List<JSONObject> jsonValues = new ArrayList<JSONObject>();

			for (int i = 0; i < obj.length(); i++) {
				jsonValues.add(obj.getJSONObject(i));

			}

			// Sort JSON objects by tag - "Subscribers" from Greatest to Least

			Collections.sort(jsonValues, new Comparator<JSONObject>() {

				private static final String KEY = "usage";

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

			// Loop through array obj and created a JSON object for each
			// Extract that data from the JSON object

			for (int index = 0; index < sortedObj.length(); index++) {
				JSONObject jsonObject = sortedObj.getJSONObject(index);
				out.println("Podcast Genre: " + jsonObject.getString("tag"));
				out.println("Usage: " + jsonObject.getInt("usage"));
				out.println();

			}

		} catch (Exception e) {
			// User has entered an invalid response Redirect user back to homepage
			out.println("<html>");
			out.println("<body>");
			out.println("<center>");
			out.println("<font size=\"12\">");
			out.println("<b>");
			out.println("<font color=\"red\">");
			out.println("PLEASE ENTER A POSITIVE NUMBER");
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
