package TestTextTransformation;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.Headers;

import org.apache.commons.lang3.StringUtils;
import TextTransformation.Constants;
import TextTransformation.SimpleHttpServer;

public class UnitTestNetworkHandler {
	SimpleHttpServer server = new SimpleHttpServer();
	
	@Before
	public void setUp() throws Exception {
		server.setupServer();
		
	}

	@After
	public void tearDown() throws Exception {
		server.stopServer();
	}
	
	private HttpURLConnection getResponse(String request) throws Exception {
		
		URL url;
		HttpURLConnection con;
		url = new URL("http://127.0.0.1:" + Constants.Networking.socketAddress.getPort() + request);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		return con;
	}
	
	private String getStringResponse(HttpURLConnection con) {
		ArrayList<String> content = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.add(inputLine);
			
			}
			in.close();
		} catch (Exception e) {
			//e.printStackTrace();
			return "No string response";
		}
		String response = StringUtils.join(content, "\n");
		return response;
		
	}
	
	@Test
	public void testInfo()  {
		HttpURLConnection con;
		String response;
		try {
			 con = getResponse("/info");
			 con.setRequestMethod("GET");
			 assertEquals(HttpURLConnection.HTTP_OK, con.getResponseCode());
			 response = getStringResponse(con);
			 assertEquals(response, Constants.StaticText.NetworkWelcomeMessage);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception");
			return;
		}		
		con = null;
		try {
			 con = getResponse("/info");
			 con.setRequestMethod("POST");
			 assertEquals(HttpURLConnection.HTTP_OK, con.getResponseCode());
			 assertEquals(Constants.StaticText.NetworkWelcomeMessage, getStringResponse(con));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception");
			return;
		}		
	}
	
	
	@Test
	public void testBadEmptyRequest() {
		HttpURLConnection con;
		int httpResponse = HttpURLConnection.HTTP_NOT_FOUND;
		try {
			con = getResponse(Constants.Networking.transformAndForward);
			con.setRequestMethod("POST");
			httpResponse = con.getResponseCode();
		} catch (Exception err) {
			fail("Bad BadEmpty response");
			return;
		}
		assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, httpResponse);
	}
	
	@Test
	public void testGoodRequest() {
		HttpURLConnection con;
		HashMap<String,String> arguments = new HashMap<String, String>();
		arguments.put("html", UnitTestHtmlParser.simpleHtmlString);
		StringJoiner sj = new StringJoiner("&");
		String response;
		int httpResponse = -1;
		try {
		
//			for(Entry<String, String> entry : arguments.entrySet())
//			    sj.add(URLEncoder.encode(entry.getKey() + "=" 
//			         + URLEncoder.encode(entry.getValue(), "UTF-8"));
//				sj.add(entry.getKey()+ "=" 
//				         + entry.getValue());
			
//			System.out.println(sj.toString());
			
		
			String request = Constants.Networking.transformAndForward;
			con = getResponse(request);
			con.setRequestMethod("GET");
			con.setRequestProperty("meta", "{}");
			con.setRequestProperty("html", URLEncoder.encode(UnitTestHtmlParser.simpleHtmlString, "UTF-8"));
			System.out.println(con.toString());
			response = getStringResponse(con);
			
			//System.out.println("Response: " + response);
			httpResponse = con.getResponseCode();
			System.out.println(con.getRequestProperty("meta"));
		} catch (Exception err) {
			err.printStackTrace();
			fail("Failed Good request");
			return;
		}
		
		assertEquals(HttpURLConnection.HTTP_OK, httpResponse);
	}
	
	
	
	@Test
	public void testRequest() {
		// TODO: Complete test...
		return;
		/*
		URL url;
		URLConnection c;
		HttpURLConnection httpConnection;
		try {
			url = new URL("http://127.0.0.1:" + Constants.Networking.socketAddress.getPort());
			c = url.openConnection();
			httpConnection = (HttpURLConnection)c;
			httpConnection.setRequestMethod("POST"); // PUT is another valid option
			httpConnection.setDoOutput(true);
		} catch (Exception e) {
			fail("Failed to create simple request");
			return;
		}
		return;
		HashMap<String,String> arguments = new HashMap<String, String>();
		// TODO: Set arguments
		
		/*
		 * 
		 * { 
			 metadata: {
			      url: "www.rpi.edu"
			      access_time: "2018-10-9T10:00:00Z" (ISO 8061)
			      forward_address: "some.aws.address.com/...." or NULL
			            }
			  content: { "<doctype HTML!>....</HTML>" }
			}
		 * 
		 
		
		StringJoiner joinedParams = new StringJoiner("&");
		for(HashMap.Entry<String,String> entry : arguments.entrySet()) {
			try {
				joinedParams.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" 
						+ URLEncoder.encode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		byte[] out = joinedParams.toString().getBytes(StandardCharsets.UTF_8);
		int length = out.length;

		httpConnection.setFixedLengthStreamingMode(length);
		httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		try {
			httpConnection.connect();
			try(OutputStream os = httpConnection.getOutputStream()) {
				os.write(out);
			}

		} catch (Exception e){
			fail("Could not send request");
		}
		*/
	}

}
