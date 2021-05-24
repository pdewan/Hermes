package dayton.ellwanger.helpbutton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.ui.console.MessageConsoleStream;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPRequest {
	private static final int timeout = 5000;
	
	public static JSONObject post(JSONObject request, String urlString) {
		BufferedReader reader;
		String line;
		StringBuffer sb = new StringBuffer();
		int status = 500;
		JSONObject body = new JSONObject();
		try {
			body.put("body", request);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Length", (body.toString().length()+2)+"");
			OutputStream os = conn.getOutputStream();
			byte[] input = body.toString().getBytes();
//			System.out.println(body.toString(4));
			os.write(input, 0, input.length);
			os.write("\r\n".getBytes());
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			
//			MessageConsoleStream out = HelpView.findConsole("debugRequestHelp").newMessageStream();
//			try {
//				out.println("sending request = " + body.toString(4));
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			status = conn.getResponseCode();
			
//			out.println("status code = " + status);
			
			if (status > 299) {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
			} else {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
			}
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} 
//		catch (JSONException e) {
//			e.printStackTrace();
//		} 
//		if (status < 299) {
			try {
//				JSONObject response = new JSONObject(sb.toString().substring(sb.toString().indexOf("{")));
//				System.out.println(response.toString(4));
//				return response;
				return new JSONObject(sb.toString().substring(sb.toString().indexOf("{")));
			} catch (JSONException e) {
			}
//		} 
		return null;
	}
}
