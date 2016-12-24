package email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class groupGenerator {
	
	static HashMap<String, String> mapping = new HashMap<>();
	static ArrayList<String> users =  new ArrayList<>();
	
	public static void main(String[] args) {
		getMapping();
		int[][] graph = getAdjacenct();
		prim(graph);
	}
	
	
	static int[][] getAdjacenct(){
		int [][] graph = new int[users.size()][users.size()];
		String key1 = "to-addresses";
		String key2 = "from-address";
		String json = null;
		HashMap<String, HashMap<String, Integer>> result = new HashMap<>();
		int count = 1;
		try {
			File fileIn = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
			BufferedReader in = new BufferedReader(new FileReader(fileIn));
			while((json = in.readLine()) != null){
				JSONParser parser = new JSONParser();
				JSONObject obj = null;
				obj = (JSONObject) parser.parse(json); 
				String from = ((String)obj.get(key2)).trim();
				ArrayList<String> to = (ArrayList<String>)obj.get(key1);
				//System.out.println(count++);
				//System.out.println(from);
				//System.out.println(to);
				if(mapping.get(from) != null && to != null){
					for(String str : to){
						if(mapping.get(str.trim()) != null){
							int fromIdx = users.indexOf(mapping.get(from));
							int toIdx = users.indexOf(mapping.get(str.trim()));
							graph[fromIdx][toIdx]++;
						}
					}
				}
			}
			//for(int[] row : graph)
			//	System.out.println(Arrays.toString(row));
			//for(int i = 0; i < users.size(); i++){
			//	String sender = users.get(i);
			//	result.put(sender, new HashMap<String, Integer>());
			//	for(int j = i + 1; j < users.size(); j++){
			//		String receiver  = users.get(j);
			//		result.get(sender).put(receiver, graph[i][j] + graph[j][i]);
			//	}
			//}
		}catch (ParseException pe) {
			System.out.println("Error: could not parse JSON response:");
			System.out.println(json);
			System.exit(1);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return graph;
		//return result;
	}
	
	
	
	static void getMapping(){ 
		String key1 = "path";
		String key2 = "from-address";
		String json = null;
		HashMap<String, HashSet<String>> map = new HashMap<>();
		HashSet<String> names = new HashSet<>(); 
		int count = 1;
		try {
			File fileIn = new File("C:\\Users\\CAICAI\\Desktop\\filtered.json");
			File fileOut = new File("C:\\Users\\CAICAI\\Desktop\\mapping.txt");
			if(!fileOut.exists())
				fileOut.createNewFile();
			FileWriter writer= new FileWriter(fileOut);
			BufferedReader in = new BufferedReader(new FileReader(fileIn));
			//JSONArray emailAdds = new JSONArray();
			while((json = in.readLine()) != null){
				JSONParser parser = new JSONParser();
				JSONObject obj = null;
				obj = (JSONObject) parser.parse(json);
				System.out.println(count++);
				//System.out.println(response.toJSONString());
				String path = (String)obj.get(key1);
				String email = (String)obj.get(key2);
				if(path.contains("sent") || path.contains("sent_items") || path.contains("_sent_mail")){
					String[] folders = path.split("/");
					String name = folders[folders.length - 3];
					names.add(name.trim());
					if(map.get(name) == null)
						map.put(name, new HashSet<>());
						
					map.get(name).add(email);
				}
				
			}
			System.out.println(map);
			for(Map.Entry<String, HashSet<String>> entry : map.entrySet()){
				if(!entry.getKey().trim().equals("sent_items")){
					for(String emailAdd : entry.getValue()){
						mapping.put(emailAdd.trim(), (mapping.get(emailAdd.trim()) == null ? "" : mapping.get(emailAdd.trim()) + " , ") + entry.getKey().trim());
					}
				}
			}
			users = new ArrayList<>(new HashSet<>(mapping.values()));
			for(Map.Entry<String, String> entry : mapping.entrySet() ){
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
	
	static void prim(int[][] graph) {
        boolean[] visited = new boolean[users.size()];
        int[] weight = new int[users.size()];
        int maxGroupSize = 10;
        int groupSize = 1;
        int groupNum = 1;
        int count = 1;
        HashMap<String, Integer> result = new HashMap<>();
        visited[0] = true;
        result.put(users.get(0), 1);
        for(int idx = 0; idx < users.size(); idx++)
        	if(graph[0][idx] != 0)
        		weight[idx] = graph[0][idx];
        while(count < users.size()){
        	int maxPos = -1;
        	int max = 0;
        	for(int idx = 0; idx < users.size(); idx++){
        		if(!visited[idx] && weight[idx] > max){
        			max = weight[idx];
        			maxPos = idx;
        		}
        	}
        	
        	if(maxPos == -1){
        		for(int idx = 0; idx < visited.length; idx++)
        			if(!visited[idx]){
        				maxPos = idx;
        				break;
        			}
        	}
        		
    		if(groupSize == maxGroupSize){
    			groupSize = 1;
    			groupNum++;
    		}
    		else
    			groupSize++;
    		visited[maxPos] = true;
    		for(int idx = 0; idx < users.size(); idx++)
    			if(graph[maxPos][idx] != 0 && weight[idx] < graph[maxPos][idx])
    				weight[idx] = graph[maxPos][idx];
    		result.put(users.get(maxPos), groupNum);
    		System.out.println(count++);
        }
        
        //System.out.println(result.toString());
        try {
				File file = new File("C:\\Users\\CAICAI\\Desktop\\group.txt");
				if(!file.exists())
					file.createNewFile();
				FileWriter writer = new FileWriter(file);
				for(Map.Entry<String, Integer> entry : result.entrySet()){
					writer.write(entry.toString() + "\n");
					writer.flush();
				}
				System.out.println("Done");
        	}
		catch (Exception e) {
			// TODO: handle exception
		}
     }


}


//List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
//Iterator<Map.Entry<String, Integer>> iterator = list.iterator();
//while(iterator.hasNext()){
//	Map.Entry<String,Integer> entry = iterator.next();
//	if(entry.getValue() < 20)
//		iterator.remove();
//}

//Collections.sort(list, (Entry<String, Integer> o1, Entry<String, Integer> o2) ->  o1.getValue().compareTo(o2.getValue()) );
//System.out.println(list.size());
//for(Map.Entry<String, Integer> entry : list)	
//	writer.write(entry.getValue() + "\n");