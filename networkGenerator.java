package email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class networkGenerator {
	

	public static void main(String[] args) {
		String key1 = "from-address";
		String key2 = "to-addresses";
		String line = null;
		String json = null;
		HashMap<String, String> mapping = new HashMap<>();
		ArrayList<String> users = new ArrayList<>(); 
		int count = 1;
		try {
			File fileIn = new File("C:\\Users\\CAICAI\\Desktop\\group.txt");
			File fileOut = new File("C:\\Users\\CAICAI\\Desktop\\network.json");
			if(!fileOut.exists())
				fileOut.createNewFile();
			FileWriter writer= new FileWriter(fileOut);
			
			JSONArray nodes = new JSONArray();
			BufferedReader in = new BufferedReader(new FileReader(fileIn));
			while((line = in.readLine()) != null){
				JSONObject node = new JSONObject();
				node.put("user", line.split("=")[0]);
				node.put("group", Integer.parseInt(line.split("=")[1]));
				users.add(line.split("=")[0]);
				nodes.add(node);
			}
			
			
			fileIn = new File("C:\\Users\\CAICAI\\Desktop\\mapping.txt");
			in = new BufferedReader(new FileReader(fileIn));
			while((line = in.readLine()) != null)
				mapping.put(line.split("=")[0], line.split("=")[1]);
			
			
			fileIn = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
			in = new BufferedReader(new FileReader(fileIn));
			JSONArray links = new JSONArray();
			while((json = in.readLine()) != null){
				JSONParser parser = new JSONParser();
				JSONObject obj = null;
				obj = (JSONObject) parser.parse(json);
				System.out.println(count++);
				String from = ((String)obj.get(key1)).trim();
				ArrayList<String> tos = (ArrayList<String>)obj.get(key2);
				if(mapping.get(from) != null){
					for(String  to : tos){
						if(mapping.get(to.trim()) != null){
							JSONObject link = new JSONObject();
							link.put("source", users.indexOf(mapping.get(from)));
							link.put("target", users.indexOf(mapping.get(to.trim())));
							links.add(link);
						}
					}
				}
			}
				
			JSONObject network = new JSONObject();
			network.put("nodes", nodes);
			network.put("links", links);
			StringWriter out = new StringWriter();
			network.writeJSONString(out);
	  		String jsonText = out.toString();
	  		System.out.println(count++);
	  		writer.write(jsonText);
			writer.flush();
			in.close();
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
