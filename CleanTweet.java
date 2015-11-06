import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CleanTweet {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// System.out.println("Working Directory = " +System.getProperty("user.dir"));
		//clean data
		LoadDataAndClean ld = new LoadDataAndClean();
		ld.readRawTweetLine("../tweet_input/tweets.txt","../tweet_output/ft1.txt");
		
		//draw graph
		DrawGraph dg = new DrawGraph();
		dg.readDataAndDrawGraph("../tweet_output/ft1.txt","../tweet_output/ft2.txt");
	}
}

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

class MinHeap{
	ArrayList<HeapObject> arr;
	
	void print(){
		for(int i =1;i<arr.size();i++)
			System.out.println(arr.get(i).getDate());
	}
	MinHeap(){
		arr = new ArrayList<HeapObject>();
		arr.add(0,null);
	}
	
	ArrayList<ArrayList<String>>  add(HeapObject newObject){
		
		ArrayList<ArrayList<String>> edgesToRemove = new ArrayList<ArrayList<String>>();
			while(getDateAtTop()!= null && timeDiffGreaterThanMinute(newObject.getDate(),getDateAtTop())){
				HeapObject obj = extract();
				if(obj != null){
					ArrayList<String> nodes = obj.getNodes();
					if(nodes != null)
						edgesToRemove.add(nodes);
				}	
		}
		
		
		arr.add(newObject);
		heapify(arr.size()-1,false);
		
		if(edgesToRemove.isEmpty())
			return null;
		
		return edgesToRemove;
	}
	
	boolean timeDiffGreaterThanMinute(Date newtime,Date oldtime){
		if (Math.abs(newtime.getTime()-oldtime.getTime())/1000 >60)
			return true;
		return false;
	}
	
	Date getDateAtTop(){
		if(arr.size() >1)
			return arr.get(1).getDate();
		return null;
	}
	
	HeapObject extract(){
		
		if(arr.size()>1){
			HeapObject last = arr.get(arr.size()-1);
			HeapObject first = arr.get(1);
			HeapObject removed = null;
			if(first == last){
				removed = first;
				arr.remove(first);
			}else{
				HeapObject temp = last;
				last = first;
				first = temp;
				removed = last;
				arr.remove(last);
				if(arr.size()>2)
					heapify(1,true);
			}
			return removed;
		}
		return null;
	}
	
	void heapify(int index,boolean TopToBottom){
		if(TopToBottom){
			while(index <= arr.size()/2){
				HeapObject root 	= arr.get(index);
				HeapObject left 	= arr.get(2*index);
				HeapObject right	= null;
				
				if(arr.size()-1 >= 2*index+1)
					right 	= arr.get(2*index+1);
				
				if(right == null){
					if(root.getDate().compareTo(left.getDate())<0)
						swap(root,left,index,index*2);
					return;
				}
				
				int nextIndex = swapRootWithMin(root,left,right,index,index*2,index*2+1);
				if(nextIndex == -1){
					return;
				}else{
					heapify(2*index+nextIndex,true);
				}
					
				
			}
		}else{
			if(index/2>0){
				HeapObject node 	= arr.get(index);
				HeapObject parent 	= arr.get(index/2);
				if(node.getDate().compareTo(parent.getDate())<0){
					HeapObject temp = node;
					swap(node,parent,index,index/2);
				}
				heapify(index/2,false);
			}
		}
	}
	
	void swap(HeapObject root,HeapObject left,int rootIndex,int swapIndex){
		HeapObject temp = left;
		arr.set(swapIndex,root);
		arr.set(rootIndex,temp);
	}
	
	int swapRootWithMin(HeapObject root,HeapObject left,HeapObject right,int rootIndex,int lIndex,int rIndex){
		Date rootDate = root.getDate();
		Date lDate = left.getDate();
		Date rDate = right.getDate();
		if(rootDate.compareTo(lDate)<=0 && rootDate.compareTo(rDate)<=0 )
			return -1;
		else{
			if(lDate.compareTo(rDate) <0){
				if(rootDate.compareTo(lDate) <0 ){
					//swap root and left child
					HeapObject temp = left;
					arr.set(lIndex,root);
					arr.set(rootIndex,temp);
					return 0;
				}
				
			}else{
				if(rootDate.compareTo(rDate) <0 ){
					//swap root and right child
					HeapObject temp = right;
					arr.set(rIndex,root);
					arr.set(rootIndex,temp);
					return 1;
				}
			}
		}
		return -1;
	}
	
}

class HeapObject{
	ArrayList<String> nodes;
	Date date;
	
	HeapObject(){
		nodes = new ArrayList<String>();
		
	}
	HeapObject(ArrayList<String>nodeslist,Date date){
		nodes = new ArrayList<String>();
		if(nodeslist != null)
			this.nodes.addAll(nodeslist);
		this.date = date;
	}
	void addValues(ArrayList<String>nodes,Date date){
		if(nodes != null)
			this.nodes.addAll(nodes);
		this.date = date;
	}
	
	Date getDate(){
		return date;
	}
	
	ArrayList<String> getNodes(){
		return nodes;
	}
	
}



class LoadDataAndClean{
	static long countForUnicode =0;
	
	void readRawTweetLine(String inputFilepath,String OutputFilePath){
		try{
			FileWriter writer = new FileWriter(OutputFilePath);
			Scanner sc = new Scanner(new FileInputStream(inputFilepath),"UTF-8");
			while(sc.hasNext()){
				String rawLine = sc.nextLine();
				String strToWrite = extractTweet(rawLine);
				if(strToWrite != null)
					writer.write(strToWrite);
			}
			writer.write(countForUnicode+" tweets contained unicode.");
			sc.close();
			writer.flush();
			writer.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	String extractTweet(String rawLine){
		String timestamp;
		String tweet;
		StringBuilder result = null;
		
		int len1 = "created_at\":\"".length();
		int len2 = rawLine.indexOf("\",\"id\":");
		int len3 = "text\":\"".length();
		int len4 = rawLine.indexOf("\",\"source\":\"");
		if(rawLine.contains(",\"created_at\":") && rawLine.contains("\"text\":")){
			timestamp = rawLine.substring(rawLine.indexOf("created_at")+len1,len2);
			tweet = rawLine.substring(rawLine.indexOf("text")+len3,len4).toLowerCase();
			result = new StringBuilder(cleanTweet(tweet));
			result.append(" (timestamp: ").append(timestamp).append(")\n");
			//System.out.println(tweet);
			//System.out.println(result.toString()+"\n----------------------");
			return result.toString();
		}
		
		return null;
		
	}
	
	String  cleanTweet(String tweet){
		tweet = tweet.replaceAll("\\\\n"," ").replaceAll("\\\\t"," ").replaceAll("[\\\\]", "");
		LinkedHashMap<String,String> dict = new LinkedHashMap<String,String>();
		dict.put("\\n"," ");
		dict.put("\n"," ");
		dict.put("\\\\n"," ");
		dict.put("\t"," ");
		dict.put("\\t"," ");
		dict.put("\\\\t"," ");
		dict.put(" +", " ");
		dict.put("\\\\","");
		dict.put("\\\"","\"");
		dict.put("\\\"","\"");
		dict.put("\\\\/","/");
		dict.put("\\\\","\\\\");
		dict.put("\\\\'","'");
	
		
		Iterator<String> it = dict.keySet().iterator();
		while(it.hasNext()){
			
			String val = it.next();
			//System.out.println(val);
			tweet = tweet.replaceAll(val, dict.get(val));
		}
		String result = tweet.replaceAll("[\\\\\\\\][\\\\\\\\u][0-9a-f][0-9a-f][0-9a-f][0-9a-f]","").replaceAll("[^\\x00-\\x7F]","").replaceAll(" +", " ");
		
		if(!result.equals(tweet)){
			countForUnicode++;
		}
		
		return result;
	}
	
	
}
