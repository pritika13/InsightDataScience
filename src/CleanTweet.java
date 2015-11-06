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

class CleanTweet{
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
