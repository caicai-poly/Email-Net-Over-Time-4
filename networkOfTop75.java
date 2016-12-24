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
import java.util.Map;

import javax.naming.event.NamespaceChangeListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class networkOfTop75 {
	

	static HashMap<String, String> emailToUsers = new HashMap<>();
	static HashMap<String, HashSet<String>> userToEmails = new HashMap<>();
	static ArrayList<String> users =  new ArrayList<>();
	
	public static void main(String[] args) {
		getMapping();
		getHighest();
		generateNetwork();
	}
	
	static void generateNetwork(){
		JSONArray nodes = new JSONArray();
		JSONArray links= new JSONArray();
		JSONObject node = null;
		JSONObject link = null;
		String line = null;
		int count = 1;
		
		
		for(String user : users){
			node = new JSONObject();
			node.put("name", user);
			nodes.add(node);
		}
		
		try {
				File emailFile = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
				File fileOut = new File("C:\\Users\\CAICAI\\Desktop\\networks.json");
				if(!fileOut.exists())
					fileOut.createNewFile();
				BufferedReader in = new BufferedReader(new FileReader(emailFile));
				FileWriter writer = new FileWriter(fileOut);
				
				while((line = in.readLine()) != null){
					System.out.println(count++);
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(line);
					String path = (String)obj.get("path");
					String[] folders = path.split("/");
					String user = folders[folders.length - 3];
					if(users.contains(user)){
						String subject = (String)obj.get("subject");
						Object timestamp = obj.get("timestamp");
						String froms = null;
						if(path.contains("inbox")){
							String fromAdddress = (String)obj.get("from-address");
							if( (froms = emailToUsers.get(fromAdddress)) != null){
								for(String from : froms.split(",")){ 
									int fromIndex = users.indexOf(from);
									int toIndex = users.indexOf(user);
									if(users.contains(from) && fromIndex != toIndex){
										link = new JSONObject();
										link.put("source", fromIndex);
										link.put("target", toIndex);
										link.put("subject", subject);
										link.put("timestamp", new BigInteger(timestamp.toString()));
										links.add(link);
									}
								}
							}
						} else{
							String tos = null;
							ArrayList<String> toAdddress = (ArrayList<String>)obj.get("to-addresses");
							for(String toAdd : toAdddress){
								if( (tos = emailToUsers.get(toAdd)) != null){
									for(String to : tos.split(",")){
										int fromIndex = users.indexOf(user);
										int toIndex = users.indexOf(to);
										if(users.contains(to) && fromIndex != toIndex){
											link = new JSONObject();
											link.put("source", fromIndex);
											link.put("target", toIndex);
											link.put("subject", subject);
											link.put("timestamp", new BigInteger(timestamp.toString()));
											links.add(link);
										}
									}
								}
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
				System.out.println(nodes.size());
				System.out.println(links.size());
				System.out.println("Done");
			
		}
		catch (ParseException pe) {
			System.out.println("Error: could not parse JSON response:");
			System.out.println(line);
			System.exit(1);
		}
		catch (Exception e) {
		}
			
	}
	
	static void getHighest(){
		HashMap<String, Integer> map = new HashMap<>();
		String json = null;
		String line = null;
		int count = 1;
		try {
			File emailFile = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
			
			BufferedReader in = new BufferedReader(new FileReader(emailFile));
			while((line = in.readLine()) != null){
				System.out.println(count++);
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(line);
				String path = (String)obj.get("path");
				String[] folders = path.split("/");
				String user = folders[folders.length - 3];
				if(map.get(user) == null)
					map.put(user, 0);
				map.put(user, map.get(user) + 1);
			}
			ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
			entries.sort((Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) -> b.getValue() - a.getValue());
			for(int i = 0; i < 35; i++)
				users.add(entries.get(i).getKey());
			System.out.println("Done");
		}
		catch (ParseException pe) {
			System.out.println("Error: could not parse JSON response:");
			System.out.println(json);
			System.exit(1);
		}
		catch (Exception e) {
		}
	}


	static void getMapping(){ 
		String key1 = "path";
		String key2 = "from-address";
		String json = null;
		HashSet<String> names = new HashSet<>(); 
		int count = 1;
		try {
			File fileIn = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
			File fileOut = new File("C:\\Users\\CAICAI\\Desktop\\mapping.txt");
			if(!fileOut.exists())
				fileOut.createNewFile();
			FileWriter writer= new FileWriter(fileOut);
			BufferedReader in = new BufferedReader(new FileReader(fileIn));
			while((json = in.readLine()) != null){
				JSONParser parser = new JSONParser();
				JSONObject obj = null;
				obj = (JSONObject) parser.parse(json);
				System.out.println(count++);
				//System.out.println(response.toJSONString());
				String path = (String)obj.get(key1);
				String email = (String)obj.get(key2);
				if((path.contains("sent") || path.contains("_sent_mail")) && !email.trim().equals("no.address@enron.com")){
					String[] folders = path.split("/");
					String name = folders[folders.length - 3];
					names.add(name.trim());
					if(userToEmails.get(name) == null)
						userToEmails.put(name, new HashSet<>());
						
					userToEmails.get(name).add(email);
				}
				
			}
			System.out.println(userToEmails);
			for(Map.Entry<String, HashSet<String>> entry : userToEmails.entrySet()){
				if(!entry.getKey().trim().equals("sent_items")){
					for(String emailAdd : entry.getValue()){
						emailToUsers.put(emailAdd.trim(), (emailToUsers.get(emailAdd.trim()) == null ? "" : emailToUsers.get(emailAdd.trim()) + ",") + entry.getKey().trim());
					}
				}
			}
			for(Map.Entry<String, String> entry : emailToUsers.entrySet() ){
				writer.write(entry.toString() + "\n");
				writer.flush();
			}
			//System.out.println(mapping.toString());
			System.out.println("Done");
			in.close();
			writer.close();
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
