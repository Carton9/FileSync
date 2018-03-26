package com.carton.filesync.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.carton.filesync.common.util.Block;
import com.carton.filesync.common.util.ExtensionArrayList;


/**
 * Created by zfh on 16-4-19.
 */
public class MappedBiggerFileReader {
    private MappedByteBuffer[] mappedBufArray;
    private int count = 0;
    private final int number;
    private FileInputStream fileIn;
    private long fileLength;
    private int arraySize;
    private byte[] array;
    private  ExtensionArrayList<Block> saveList=new ExtensionArrayList<Block>();
    public MappedBiggerFileReader(String fileName, int arraySize) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        FileChannel fileChannel = fileIn.getChannel();
        this.fileLength = fileChannel.size();
        this.number = (int) Math.ceil((double) fileLength / (double) Integer.MAX_VALUE);
        this.mappedBufArray = new MappedByteBuffer[number];// �ڴ��ļ�ӳ������
        long preLength = 0;
        long regionSize = (long) Integer.MAX_VALUE;// ӳ������Ĵ�С
        for (int i = 0; i < number; i++) {// ���ļ�����������ӳ�䵽�ڴ��ļ�ӳ��������
            if (fileLength - preLength < (long) Integer.MAX_VALUE) {
                regionSize = fileLength - preLength;// ���һƬ����Ĵ�С
            }
            mappedBufArray[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
            preLength += regionSize;// ��һƬ����Ŀ�ʼ
        }
        this.arraySize = arraySize;
        saveList=new ExtensionArrayList<Block>();
    }
    public MappedBiggerFileReader(File file, int arraySize) throws IOException {
        this.fileIn = new FileInputStream(file);
        FileChannel fileChannel = fileIn.getChannel();
        this.fileLength = fileChannel.size();
        this.number = (int) Math.ceil((double) fileLength / (double) Integer.MAX_VALUE);
        this.mappedBufArray = new MappedByteBuffer[number];// �ڴ��ļ�ӳ������
        long preLength = 0;
        long regionSize = (long) Integer.MAX_VALUE;// ӳ������Ĵ�С
        for (int i = 0; i < number; i++) {// ���ļ�����������ӳ�䵽�ڴ��ļ�ӳ��������
            if (fileLength - preLength < (long) Integer.MAX_VALUE) {
                regionSize = fileLength - preLength;// ���һƬ����Ĵ�С
            }
            mappedBufArray[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
            preLength += regionSize;// ��һƬ����Ŀ�ʼ
        }
        this.arraySize = arraySize;
        saveList=new ExtensionArrayList<Block>();
    }
    public int read() throws IOException {
        if (count >= number) {
            return -1;
        }
        int limit = mappedBufArray[count].limit();
        int position = mappedBufArray[count].position();
        if (limit - position > arraySize) {
            array = new byte[arraySize];
            mappedBufArray[count].get(array);
            return arraySize;
        } else {// ���ڴ��ļ�ӳ�����һ�ζ�ȡ����
            array = new byte[limit - position];
            mappedBufArray[count].get(array);
            if (count < number) {
                count++;// ת������һ���ڴ��ļ�ӳ��
            }
            return limit - position;
        }
    }
    int counter=0;
    public synchronized Block syncRead(){
    	try {
			if(this.read()!=-1){
				Block out=new Block(getArray().clone(),counter);
				counter++; 
				saveList.add(out);
				return out;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    public void close() throws IOException {
        fileIn.close();
        array = null;
    }
    public ExtensionArrayList<Block> getResult(){
		return saveList;
    }
    public byte[] getArray() {
        return array;
    }

    public long getFileLength() {
        return fileLength;
    }
    public int blockCount() {
    	return this.number;
    }
    /*public static void main(String[] args) throws IOException {
        MappedBiggerFileReader reader = new MappedBiggerFileReader("/home/zfh/movie.mkv", 65536);
        long start = System.nanoTime();
        while (reader.read() != -1) ;
        long end = System.nanoTime();
        reader.close();
        System.out.println("MappedBiggerFileReader: " + (end - start));
    }*/
}