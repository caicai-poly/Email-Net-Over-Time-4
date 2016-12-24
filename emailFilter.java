package email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class emailFilter {
	
/*
 * 
 * Filter the original json file which contains all emails.
 * Just keep emails with less than 10 receivers(higher probility not junk mail)
 * and only keep emails from "inbox", "sent", "sent_items", "_sent_mails" folders, 
 * which may seems to be interesting mails.
 * 
 */
	public static void main(String[] args) {
		String[] keys = {"from-address", "to-addresses", "x-from", "x-to", "timestamp", "subject", "path"};
		String json = null;
		int count = 1;
		try {
			File fileIn = new File("C:\\Users\\CAICAI\\Desktop\\esbuildfile.json");
			File fileOut = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
			if(!fileOut.exists())
				fileOut.createNewFile();
			FileWriter writer= new FileWriter(fileOut);
			BufferedReader in = new BufferedReader(new FileReader(fileIn));
			while((json = in.readLine()) != null){
				JSONParser parser = new JSONParser();
				JSONObject obj = null;
				obj = (JSONObject) parser.parse(json);
		  		System.out.println(count++);
				String path = (String)obj.get("path");
				ArrayList<String> toAddress = (ArrayList<String>)obj.get("to-addresses"); 
				//System.out.println(toAddress);
				if(toAddress.size() < 10 && (path.contains("sent") || path.contains("sent_items") || path.contains("_sent_mail") || path.contains("inbox"))){
					JSONObject outObj = new JSONObject();
					for(String key : keys){
						Object value = obj.get(key);
						outObj.put(key,  (key.equals("timestamp")? new BigInteger(value.toString()) :  value));
						//System.out.println(value);
					}
					StringWriter out = new StringWriter();
					outObj.writeJSONString(out);
			  		String jsonText = out.toString();
			  		json.replaceAll("\\/", "/");
			  		writer.write(jsonText + "\n");
					writer.flush();
				}
			}
			System.out.println("Done");
		}
		catch (ParseException pe) {
			System.out.println("Error: could not parse JSON response:");
			System.out.println(json);
			System.exit(1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}


}
