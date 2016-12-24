package email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class spliteredNetworkGenerator {
	

	public static void main(String[] args) {
		String json = null;
		String line = null;
		HashMap<String, String>map = new HashMap<>(); 
		int count = 1;
		try {
			File groupFile = new File("C:\\Users\\CAICAI\\Desktop\\group.txt");
			File mappingFile = new File("C:\\Users\\CAICAI\\Desktop\\mapping.txt");
			String inputPrefix = "C:\\Users\\CAICAI\\Desktop\\splited\\";
			String outputPrefix = "C:\\Users\\CAICAI\\Desktop\\networks\\";
			
			BufferedReader mapping = new BufferedReader(new FileReader(mappingFile));
			while((line = mapping.readLine()) != null){
					map.put(line.split("=")[0], line.split("=")[1]);
			}
			
			mapping = new BufferedReader(new FileReader(groupFile));
			JSONObject node = null;
			JSONObject link = null;
			JSONArray nodes = null;
			JSONArray links = null;
			JSONObject network = null;
			while((line = mapping.readLine()) != null){
				ArrayList<String> order = new ArrayList<>(); 
				System.out.println(count++);
				nodes = new JSONArray();
				node = new JSONObject();
				links = new JSONArray();
				link = new JSONObject();
				String users = line.split("=")[0];
				node.put("name", users);
				nodes.add(node);
				order.add(users);
				for(String user : users.split(",")){
					user = user.trim();
					File fileIn = new File(inputPrefix + user + ".txt");
					BufferedReader in = new BufferedReader(new FileReader(fileIn));
					
					while((json = in.readLine()) != null){
							JSONParser parser = new JSONParser();
							JSONObject obj = (JSONObject) parser.parse(json);
							String path = (String)obj.get("path");
							Object timestamp = obj.get("timestamp");
							String from = (String)obj.get("from-address");
							ArrayList<String> tos = (ArrayList<String>)obj.get("to-addresses");
							if(path.contains("inbox")){
								String fromUser = map.get(from);
								if(fromUser != null){
								
									if(order.indexOf(fromUser) == -1){
										order.add(fromUser);
										node = new JSONObject();
										node.put("name", (fromUser == null? "1" : fromUser));
										nodes.add(node);
									}
									link = new JSONObject();
									link.put("source", order.indexOf(fromUser));
									link.put("target", order.indexOf(users));
									link.put("timestamp", new BigInteger(timestamp.toString()));
									links.add(link);
								}
							}
							else if(tos != null && tos.size() != 0){
								for(String to : tos){
									if(map.get(to.trim()) != null){
										String toUser = map.get(to.trim());
										if(order.indexOf(toUser) == -1){
											order.add(toUser);
											node = new JSONObject();
											node.put("name", (toUser == null? "2" : toUser));
											nodes.add(node);
										}
										link = new JSONObject();
										link.put("source", order.indexOf(toUser));
										link.put("target", order.indexOf(user));
										link.put("timestamp", timestamp);
										links.add(link);
									}
								}
							}
					}

					in.close();
				}
				
				File fileOut = new File(outputPrefix + users + ".txt");
				if(!fileOut.exists())
					fileOut.createNewFile();
				FileWriter writer = new FileWriter(fileOut);
				StringWriter out = new StringWriter();
				network = new JSONObject();
				network.put("nodes", nodes);
				network.put("links", links);
				network.writeJSONString(out);
		  		String jsonText = out.toString();
		  		writer.write(jsonText);
				writer.flush();
				writer.close();
				
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
