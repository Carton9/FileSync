package com.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;  

public class ExtensionArrayList<T> {
	private ArrayList<ArrayList> totalList;
	private ArrayList<T> currentList;
	private static int MAX_ARRAY_SIZE;
	private static int MAX_ARRAY_LEVEL;
	private long size;
	private int deep;
	public ExtensionArrayList(){	
		currentList=new ArrayList<T>();
		totalList=new ArrayList<ArrayList>();
		totalList.add(currentList);
		MAX_ARRAY_SIZE= Integer.MAX_VALUE/2;
		MAX_ARRAY_LEVEL=-1;
	}
	public ExtensionArrayList(int arraySize){	
		currentList=new ArrayList<T>();
		totalList=new ArrayList<ArrayList>();
		totalList.add(currentList);
		MAX_ARRAY_SIZE= arraySize;
		MAX_ARRAY_LEVEL=-1;
	}
	private long toLong(Integer[] pointer) {
		ArrayList<Integer> indexs=new ArrayList<Integer>();
		indexs.addAll(Arrays.asList(pointer));
		Collections.reverse(indexs);
		long indexInLong=0;
		for(int i=0;i<indexs.size();i++) {
			if(indexs.get(i)!=0) {
				int index=indexs.get(i);
				indexInLong+=Math.pow(MAX_ARRAY_SIZE,i)*index;
			}
		}
		return indexInLong;
	}
	private Integer[] toArray(long pointer) {
		ArrayList<Integer> indexs=new ArrayList<Integer>();
		long buff=pointer;
		while(true) {
			if(buff>=MAX_ARRAY_SIZE) {
				int unit=(int) (pointer%MAX_ARRAY_SIZE);
				indexs.add(unit);
				buff=pointer/MAX_ARRAY_SIZE;
			}else {
				indexs.add((int)buff);
				int size=indexs.size();
				for(int i=0;i<deep-size;i++) {
					indexs.add(0);
				}
				Collections.reverse(indexs);
				return indexs.toArray(new Integer[indexs.size()]);
			}
		}
		
	}
	@SuppressWarnings("unchecked")
	public void add(T item) {
		if(currentList.size()<MAX_ARRAY_SIZE) {
			currentList.add(item);
		}else {
			@SuppressWarnings("rawtypes")
			ArrayList upperList=getAddList();
			currentList=new ArrayList<T>();
			currentList.add(item);
			upperList.add(currentList);
			deep=getDeep(totalList);
			
		}
		size++;
	}
	@SuppressWarnings("unchecked")
	public T get(Integer[] pointer) {
		if(pointer.length!=deep)
			return null;
		ArrayList bufferList=totalList;
		for(int i=0;i<pointer.length-1;i++) {
			bufferList=(ArrayList)bufferList.get(pointer[i]);
		}
		return (T)bufferList.get(pointer[pointer.length-1]);
	}
	public T get(long pointer) {
		return get(toArray(pointer));
	}
	public Integer[] indexOf(T item) {
		ArrayList<Integer> index=new ArrayList<Integer>();
		if(search(item,totalList,index)) {
			//System.out.println(index);
			Collections.reverse(index);
			return index.toArray(new Integer[index.size()]);
		}
		return null;
	}
	public long indexOfInLong(T item) {
		return toLong(indexOf(item));
	}
	public long getSize() {
		return size;
	}
	private boolean search(T item,ArrayList node,ArrayList<Integer> index) {
		if(node.isEmpty()) {
			return false;
		}
		else if(node.contains(item)) {
			index.add(node.indexOf(item));
			return true;
		}else if(containArrayList(node)) {
			node=(ArrayList<ArrayList>)node;
			for(Object i:node) {
				boolean result=search(item,(ArrayList)i,index);
				if(result) {
					index.add(node.indexOf(i));
					return true;
				}
			}
				
		}
		return false;
	}
	public int getDeep() {
		return deep;
	}
	private boolean containArrayList(ArrayList l) {
		if(l.isEmpty())
			return false;
		if(l.get(0).getClass().equals(ArrayList.class))
			return true;
		return false;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList getAddList() {
		ArrayList result=getUpperList(totalList,currentList);
		if(result.size()<MAX_ARRAY_SIZE)
			return result;
		else {
			ArrayList highList=getAvaliableList(totalList);
			if(highList==null) {
				
				ArrayList newRoot=new ArrayList();
				newRoot.add(totalList);
				totalList=newRoot;
				highList=newRoot;
			}
			int deep=getDeep(highList);
			for(int i=1;i<deep-1;i++) {
				ArrayList newList=new ArrayList();
				highList.add(newList);
				highList=newList;
			}
			
			
			return highList;
		}
	}
	private static ArrayList getAvaliableList(ArrayList upperList) {
		if(upperList.size()<MAX_ARRAY_SIZE&&!upperList.isEmpty()&&upperList.get(0).getClass().equals(ArrayList.class)) {
			for(int i=0;i<upperList.size();i++) {
				ArrayList list=getAvaliableList((ArrayList)upperList.get(i));
				if(list!=null)
					return list;
			}
			return upperList;
		}else if(upperList.isEmpty()){
			return upperList;
		}else {
			return null;
		}
	}
	private int getDeep(ArrayList upperList) {
		int result=1;
		if(containArrayList(upperList))
			return result+getDeep((ArrayList)upperList.get(0));
		else
			return result;
	}
	private static ArrayList getUpperCurrentList(ArrayList upperList,Object ContainItem) {
		ArrayList buffList=null;
		if(upperList.contains(ContainItem)) {
			return upperList;
		}else if(upperList.get(0).getClass().equals(ArrayList.class)) {
			ArrayList result=null;
			for(Object i:upperList) {
				result=getUpperCurrentList((ArrayList)i,ContainItem);
				if(result!=null)
					break;
			}
			return result;
		}else
			return null;
	}
	private static ArrayList getUpperList(ArrayList upperList,Object ContainItem) {
		ArrayList buffList=null;
		if(upperList.contains(ContainItem)) {
			return upperList;
		}else if(upperList.get(0).getClass().equals(ArrayList.class)) {
			ArrayList result=null;
			result=getUpperCurrentList((ArrayList)upperList.get(upperList.size()-1),ContainItem);
			return result;
		}else
			return null;
	}
}
