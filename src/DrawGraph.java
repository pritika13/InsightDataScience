import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

class DrawGraph{
	HashMap<String,HashSet<String>> hash;
	final String TWITTER="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	
	DrawGraph(){
		hash = new HashMap<String,HashSet<String>>() ;
	}
	
	void printHash(){
		Iterator<String> it = hash.keySet().iterator();
		while(it.hasNext()){
			String val = it.next();
			System.out.println(val+" -> "+hash.get(val).toString());
			
		}
	}
	
	void readDataAndDrawGraph(String inputFilepath,String OutputFilePath){
		try{
			
			MinHeap minheap = new MinHeap(); 
			FileWriter writer = new FileWriter(OutputFilePath);
			Scanner sc = new Scanner(new FileInputStream(inputFilepath),"UTF-8");
			
			SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
			sf.setLenient(true);
			
			while(sc.hasNext()){
				String line = sc.nextLine();
				if(line.indexOf("(timestamp") != -1){
					String arr[] = line.split("\\(timestamp:");
					if(arr.length == 2){
						line = arr[0].trim();
						Date date = sf.parse(arr[1].replace(")", "").trim()); 
						ArrayList<String> hashtags = getHashTags(line);
						if(hashtags!=null){
							HeapObject heapObj = new HeapObject(hashtags,date);
							ArrayList<ArrayList<String>> listOfEdgesToRemove = minheap.add(heapObj);
							if(listOfEdgesToRemove != null)
								removeEdgesFromHash(listOfEdgesToRemove);
							addInHashMap(hashtags);
							double avg = getAverage();
							double av = Math.round(avg*100.0)/100.0;
							writer.write(av+" \n");
						}
					}
				}
			}
			
			sc.close();
			writer.flush();
			writer.close();
		}catch(ParseException e){
			e.printStackTrace();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	double getAverage(){
		double N =0;
		double total = 0;
		Iterator<String> it = hash.keySet().iterator();
		while(it.hasNext()){
			String val = it.next();
			int size = hash.get(val).size();
			total += size;
			if(size>0)
				N++;
		}
		return total/N;
	}
	
	ArrayList<String> getHashTags(String line){
		if(line.contains("#")){
			ArrayList<String> hashTags = new ArrayList<String>();
			String arr[] = line.split(" ");
			for(String a:arr){
				if(a.contains("#")){
					hashTags.add(a.substring(a.indexOf("#")).replaceAll("[:,!]", "").trim());
				}
			}
			return hashTags;
		}
			
		return null;	
	}
	
	
	void removeEdgesFromHash(ArrayList<ArrayList<String>> list){
		for(ArrayList<String> tagsList:list){
			for(String hashtag:tagsList){
				HashSet<String> hs;
				if(hash.get(hashtag) != null){
					hs = hash.get(hashtag);
					hs.removeAll(tagsList);
					hash.put(hashtag,hs);
				}
			}
		
		}
	}
	
	void addInHashMap(ArrayList<String> tagsList){
		for(String hashtag:tagsList){
			HashSet<String> hs;
			if(hash.get(hashtag) != null){
				hs = hash.get(hashtag);
				hs.addAll(tagsList);
			}else{
				hs = new HashSet<String>();
				hs.addAll(tagsList);
			}
			hs.remove(hashtag);
			hash.put(hashtag,hs);
			
		}
	}


}
