package email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class emailSpliter {
	

	public static void main(String[] args) {
		String json = null;
		String line = null;
		HashSet<String> users = new HashSet<>(); 
		int count = 1;
		try {
			File mappingFile = new File("C:\\Users\\CAICAI\\Desktop\\group.txt");
			File emailFile = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
			String prefix = "C:\\Users\\CAICAI\\Desktop\\splited\\";
			
			BufferedReader in = new BufferedReader(new FileReader(mappingFile));
			while((line = in.readLine()) != null){
				for(String user : line.split("=")[0].split(","))
					users.add(user.trim());
			}
			
			for(String user : users){
				in = new BufferedReader(new FileReader(emailFile));
				File fileOut = new File(prefix + user + ".txt");
				System.out.println(count++);
				System.out.println(prefix + user + ".txt");
				if(!fileOut.exists())
					fileOut.createNewFile();
				FileWriter writer = new FileWriter(fileOut);
				while((json = in.readLine()) != null){
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(json);
					String path = (String)obj.get("path");
					if(path.contains(user)){
						StringWriter out = new StringWriter();
						obj.writeJSONString(out);
				  		String jsonText = out.toString();
				  		json.replaceAll("\\/", "/");
				  		writer.write(jsonText + "\n");
						writer.flush();
					}
				}
				in.close();
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
