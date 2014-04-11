/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fpgrowth;

/**
 *
 * @author Yunfeng Zhu
 */
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 测试
 * <p>
 * 用FP-Growth算法产生的项集全部存储在项集树itr中,
 * */

public class Test {
	protected static String m_phosAA = "S";
	protected static int m_length = 21;
	public static LinkedList<LinkedList<String>> readTransData(String filename) {
		LinkedList<LinkedList<String>> records = new LinkedList<LinkedList<String>>();
		LinkedList<String> record;
		try {
			FileReader fr = new FileReader(new File(filename));
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim() != "") {
					record = new LinkedList<String>();
					String[] items = line.split(" ");
					for (int i =0; i<items.length;i++) {
						record.add(String.valueOf(i)+"."+items[i]);
					}
					records.add(record);
				}
			}
		} catch (IOException e) {
			System.exit(-2);
		}
		return records;
	}
	private static String transferData(String s) {
		String result="";
		String [] temp = s.split(" ");		
		for(int i =0; i < temp.length; i++){
			if(i == (temp.length-1)/2)
				continue;
			result+=String.valueOf(i)+"."+temp[i]+" ";
		}
		return result;
	}
	public static void main(String args[]) {
		ITree itr; // 存储所有项集的树
		File f;
		FPTree pt = new FPTree();
		CreateFPTree c = new CreateFPTree();
		int support = 200;
		double gSigThreshold = 2;
		double lSigThreshold = 2;
		String fileName = "/home/ymcdull/desktop/1.pep";
//		 String fileName = "/home/ymcdull/workspace/DataMiningFPGrowth/mushroom.data";
		
		// 对background文件处理：将数据存入到Link<Link<String>> transRecordBackground中
		String fileName2 = "/home/ymcdull/desktop/3.pep";
//		 String fileName2 = "/home/ymcdull/workspace/DataMiningFPGrowth/datafile.dat";
//		LinkedList<LinkedList<String>> transRecordsBg = readTransData(transFile2);

		FastVector fv = new FastVector();
		FastVector fvBg = new FastVector();
		try {
			if (support < 0) {
				System.out.println("支持度阈值不能是负值");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("参数输入错误");
			System.exit(1);
		}
		c.SetFPTree(pt);
		f = new File(fileName);
		

		try {
			// Change the form of data, and save them to phosData and
			// nonPhosData
			BufferedReader fg = new BufferedReader(new FileReader(f));
			String s = fg.readLine();
			// transferData(s);

			while (s != null) {
				// System.out.println(qop++);
				// slabel=new String(s);
				s = transferData(s);
				fv.addElement(s);
				s = fg.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		f = new File(fileName2);
		try {
			// Change the form of data, and save them to phosData and
			// nonPhosData
			BufferedReader bg = new BufferedReader(new FileReader(f));
			String s = bg.readLine();
			// transferData(s);
			m_length = s.length();
            m_phosAA = s.substring((m_length - 1) / 2, (m_length - 1) / 2 + 1);
 
			while (s != null) {
				// System.out.println(qop++);
				// slabel=new String(s);
				// s = transferData(s);

				fvBg.addElement(s);
				s = bg.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		double programStart = System.currentTimeMillis();
		FPGrowth fpg = new FPGrowth(m_length, m_phosAA);
		
		c.start(fv); // 从事务文件f中产生FP_Tree pt
		itr = new ITree();
		double startEstablish = System.currentTimeMillis();
		fpg.Start(pt, itr, support, gSigThreshold, lSigThreshold, fv,
				fvBg); // 用FP-Growth算法产生项集存储在项集树（ITree）itr
		double endEstablish = System.currentTimeMillis();
		System.out.println("the time to establish is "+(endEstablish-startEstablish));
		
		System.out.println("----频繁项集----");

		// itr.print(itr.head, new Stack());

		// Test for print all patterns meet the requirement of support threshold
		// for(int i = 0;i<fpg.totalPatterns.size();i++){
		// System.out.print((i+1)+":");
		// LinkedList<String> LItem = fpg.totalPatterns.get(i);
		// int value = fpg.countInFgList.get(i);
		// for(String item : LItem){
		// System.out.print(item+"-");
		// }
		// System.out.println(value);
		// }
		//

		// Test for print all patterns met the requirements of support and
		// local+global significances







//		 for (int i = 0; i < fpg.resultPatterns.size(); i++) {
//		 	System.out.print((i + 1) + ":");
//		 	LinkedList<String> LItem = fpg.resultPatterns.get(i);
//		 	int value = fpg.resultCount.get(i);
//		 	for (String item : LItem) {
//		 		System.out.print(item + "-");
//		 	}
//		 	System.out.println(value);
//		 }

		
		//print all the motifs
		for(int i=0;i<fpg.resultMotifs.size();i++){
			System.out.print((i+1)+":");
			Motif motif = (Motif) fpg.resultMotifs.elementAt(i);
			
			System.out.println(motif.getMotifAsAString()+"   "+motif.global_significanceValue);
		}
		
		// for(LinkedList<String> LItem : fpg.totalPatterns){
		// for(String item : LItem){
		// System.out.print(item+"-");
		// }
		// System.out.println("*******");
		// }
		//
		// for(Integer item : fpg.totalSupport){
		// System.out.println("+++++++"+item);
		// }
		// System.out.println(fpg.totalSupport.size());

		// Iterator<Map.Entry<LinkedList<String>, Integer>> it =
		// fpg.map.entrySet().iterator();
		// int number = 0;
		// while(it.hasNext()){
		// Map.Entry<LinkedList<String>, Integer> entry=it.next();
		// number++;
		// System.out.print(number+":");
		// int value=entry.getValue();
		// LinkedList<String> temp = entry.getKey();
		// for(String item : temp){
		// System.out.print(temp.size()+"-");
		// }
		// System.out.println(value);
		// }
		double programEnd = System.currentTimeMillis();
		System.out.println("The time is:" + (programEnd - programStart));
		 itr.printMotifs();
		// int m=300000;
		// int[] num=new int[m];
		// for(int i=0;i<num.length;i++)
		// num[i]=i;
		// for(int i=m-1;i>0;i--)
		// {
		// int temp=(int)(Math.random()*(i+1));
		// int temp2=num[temp];
		// num[temp]=num[i];
		// num[i]=temp2;
		// }

	}
}
