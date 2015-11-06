import java.util.ArrayList;
import java.util.Date;


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
