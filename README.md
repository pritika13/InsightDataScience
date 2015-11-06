The programs consists of two main classes: CleanTweet and DrawGraph

CleanTweet:
This class has a function "ReadRawTweetLine" which takes reads from the tweet_input/tweets.txt file line by line so that no memory overflow takes place.After reading a line,it uses function extractTweet() to extract tweet and timestamp from the entire line.After extracting timestamp and tweet, tweet is cleaned by removing unicode characters and other escape characters  cleantweet().
After obtaining clean tweet and timestamp,it is written in file ft1.txt


DrawGraph:
Draw graph reads its input from the file ft1.txt which consists of tweet and timestamp.It first extracts hashtags from the tweets and then update hashmap and minheap.

It uses two data structure to make a graph dynamically and to update it

Hashmap
Hashmap - key(String) and value(Arraylist of Strings)
hashmap keeps tracks of all the nodes/hashtags which it encountered and adds an item in its key arraylist if there is an edge.

MinHeap
It keeps track of the timestamp of the hashtags encountered.It stores a HeapObject which consists of Timestamp + ArrayList of tags.When a new tweet comes in,we reed the minheap and remove the min until the tweet get in the 60seconds range of the new tweet.When a new object is added,the add function returns list of all the edges which should be removed from the existing graph.The edges are removed by modifying the hashmap so the each key consists a list of its neighbor edges which are within 60seconds range.

The average edges are counted by :
	total edges/Number of nodes
	
	where total edges = sum of size of all arraylist for each key in hashmap
	
			N = Number of nodes which have atleast one edge 
