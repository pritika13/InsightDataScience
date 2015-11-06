public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
		//clean data
		CleanTweet ld = new CleanTweet();
		ld.readRawTweetLine("../tweet_input/tweets.txt","../tweet_output/ft1.txt");
		
		//draw graph
		DrawGraph dg = new DrawGraph();
		dg.readDataAndDrawGraph("../tweet_output/ft1.txt","../tweet_output/ft2.txt");
	}
}
