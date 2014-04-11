/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fpgrowth;

/**
 *
 * @author Donny
 */
import com.fpgrowth.ITree.Node;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * FP_growth算法
 * 
 * @author qjt
 * 
 */
public class FPGrowth {
	int m_length = 21;
	String m_phosAA = "S";
	int minSupport = 1;
	double gSigThreshold = 1;
	double lSigThreshold = 1;
	CreateFPTree ct;
	ITree iTree; // 存储挖掘出的所有项集的树
//	LinkedList<LinkedList<String>> totalPatterns;
	FastVector totalMotifs;
	FastVector resultMotifs;
//	LinkedList<Integer> totalSupport;
//	LinkedList<Integer> countInFgList;
//	LinkedList<Integer> countInBgList;
	
	LinkedList<Double> gSigList;
	LinkedList<LinkedList<String>> resultPatterns;
	LinkedList<Integer>  resultCount;
	FastVector fv;
	FastVector fvBg;
//	LinkedList<LinkedList<String>> transRecordBg;
	FPGrowth(int m_length, String m_phosAA) {
		this.m_length = m_length;
		this.m_phosAA = m_phosAA;
		ct = new CreateFPTree();
		totalMotifs = new FastVector();
		resultMotifs = new FastVector();
//		totalPatterns = new LinkedList<LinkedList<String>>();
//		totalSupport = new LinkedList<Integer>();
//		countInFgList = new LinkedList<Integer>();
//		countInBgList = new LinkedList<Integer>();
		
		gSigList = new LinkedList<Double>();
		resultPatterns = new LinkedList<LinkedList<String>>();
		resultCount = new LinkedList<Integer>();
	}

	/* 主程序 */
	public void Start(FPTree tree, ITree itr, int supp, double gSigThreshold, double lSigThreshold, FastVector fv,FastVector fvBg) {
		this.fv = fv;
		this.fvBg = fvBg;
		this.gSigThreshold = gSigThreshold;
		this.lSigThreshold = lSigThreshold;
		iTree = itr;
		minSupport = supp;
		Motif preMotif = new Motif(m_length, m_phosAA);
		// LinkedList<String> prePattern = new LinkedList<String>();
		FP_growth(tree, itr.head, preMotif);
		
		
//		for(int i=0;i<resultMotifs.size();i++){
//			System.out.print((i+1)+":");
////			Motif motif = (Motif) fpg.resultMotifs.elementAt(i);
//			
////			for(int j=0;j<)
//			System.out.println(((Motif) resultMotifs.elementAt(i)).toString());
//		}
	}

//	public int calCount(LinkedList<String> list,
//			FastVector fvBg) {
//		int count = 0;
//		for (int i=0;i<fvBg.size();i++) {
//			String temp = (String)fvBg.elementAt(i);
//			String [] tt = temp.split(" ");
//			boolean isContained = true;
//			for (String item : list) {
//				for(int j=0;j<tt.length;j++){
//					if(item.equals(tt[j]))
//						continue;
//				}
//				isContained = false;
//				break;
////				if (!record.contains(item)) {
////					isContained = false;
////					break;
////				}
//			}
//			if (isContained) {
//				count++;
//			}
//
//		}
//		return count;
//	}
	

	
	
	public int calCount(Motif preMotif, FastVector fvBg){
		int count =0;
		for(int i=0;i<fvBg.size();i++){
			String instance = (String) fvBg.elementAt(i);
			if(preMotif.containedBy(instance)){
				count++;
			}
		}
//		System.out.println("*****************************"+count);
		return count;
	}
	
	
	/**
	 * fp-growth算法
	 * 
	 * @param tree
	 *            FPtree
	 * @param head
	 *            频繁项集树的头结点
	 * */
	public void FP_growth(FPTree tree, LinkedList<Node> head, Motif preMotif) {
		FPTree branch; // 条件FPTree
		String itemName;
		int len, i = 0, count;
		LinkedList<Node> l, cl;
		ITree itr = new ITree();
		ITree.Node node;
		
		l = head;
		len = tree.itemTb.Length();
		
		while (i < len) {
			count = tree.itemTb.ReadCount(i);
			if (count < minSupport) {
				i++;
				continue;
			}
			itemName = tree.itemTb.ReadItem(i);
			node = itr.new Node(itemName, count);
			l.add(node);
			String name = node.name;
			int index = Integer.valueOf(name.substring(0, name.indexOf('.')));
			String motifItem = name.substring(name.indexOf('.')+1);
			
			preMotif.m_index.addElement(index);
			preMotif.m_items[index] = motifItem;
			
			
//			prePattern.add(node.name);
			
			int countInFg = node.count;
//			System.out.println("*****************************"+countInFg);
			int countInBg = calCount(preMotif, fvBg);
//			System.out.println("*************"+countInBg);
			
			//在ITree结构的基础上，从左到右，从上到下，产生ITree的同时，将conditional pattern存到totalMotifs中，将该pattern在Fg中支持度存到countInFg中，将在Bg中的支持度存到countInBg
			
//			totalPatterns.add(new LinkedList<String>(prePattern));
//			countInFgList.add(countInFg);
//			countInBgList.add(countInBg);	
			
			
			
			
			
//			double gSig;
			if(countInBg == 0)
				preMotif.global_significanceValue = Math.abs(((countInFg+0.00001)*fvBg.size())/((countInBg+0.00001)*fv.size()));
			else
				preMotif.global_significanceValue = Math.abs(((double)countInFg*fvBg.size())/((double)countInBg*fv.size()));
			totalMotifs.addElement(new Motif(preMotif, m_length, m_phosAA));
//			System.out.println("******************"+preMotif.global_significanceValue);
			
			
			//			Test for the gSig
//			System.out.println("#$%^&*(^%$#$%^&*&^%$%^&*"+gSig);
//			gSigList.add(gSig);
		
			if (preMotif.global_significanceValue >= gSigThreshold){
				if (preMotif.m_index.size() == 1){
//					resultPatterns.add(new LinkedList<String>(prePattern));
//					resultCount.add(countInFg);
					resultMotifs.addElement(new Motif(preMotif, m_length, m_phosAA));
				}
				else{

//					for(LinkedList<String> LItem : totalPatterns){
//						for(String item : LItem){
//							System.out.print(item+"---");
//						}
//						System.out.println();
//					} 
					
					double minSig = 100;
					
                    //Check the other sub-motifs to calculate the corresponding local significance values  
//                    for (int ff = 0; ff < preMotif.m_index.size() ; ff++) {
//                        int position = ((Integer) result.m_index.elementAt(ff)).intValue();
//                        Motif temp = new Motif(m_length, m_phosAA);
//                        for (int tt = 0; tt < m_length; tt++) {
//                            if (tt == position || result.m_items[tt].equals("_") || tt == (m_length - 1) / 2) {
//                                continue;
//                            } else {
//                                temp.m_items[tt] = result.m_items[tt];
//                                temp.m_index.addElement(new Integer(tt));
//                            }
//                        }
//                        FastVector temp_foreground = iteratorMap(temp, motif_Foreground_dataset);
//                        FastVector temp_background = iteratorMap(temp, motif_Background_dataset);
//                        significanceValue = result.upDateSignificanceValue(result_Foreground.size(), result_Background.size(), temp_foreground.size(), temp_background.size());
//
//                        if (significanceValue < result.local_significanceValue) {
//                            result.local_significanceValue = significanceValue;
//
//
//                        }
//
//
//                    }
					

					for (int j=0;j<preMotif.m_index.size();j++){
//						int subIndex = (Integer) preMotif.m_index.elementAt(j);
//						String subMotifItem = preMotif.getMotifAsAString();
//						subMotifItem[subIndex] = '_';
						Motif subMotif = preMotif.getSubMotif(j, m_length, m_phosAA);
						
//						LinkedList<String> subPattern = new LinkedList<String>(prePattern);
//						subPattern.remove(j);
						for(int k=0; k< totalMotifs.size();k++){
//							System.out.println("****************************");
//							LinkedList<String> currentList = totalPatterns.get(k);
							Motif currentMotif = (Motif) totalMotifs.elementAt(k);
//							for(String item : currentList){
//								System.out.print(item+"---");
//							}
							
//							System.out.println("size is   "+subPattern.size());
							if(currentMotif.isEqual(subMotif)){
								double SigForPj = preMotif.global_significanceValue/ currentMotif.global_significanceValue;
//								System.out.println("**********"+SigForPj);
								
								
								//Math.abs((double)countInFg/countInFgList.get(k) - (double)countInBg/countInBgList.get(k));
								if (SigForPj < minSig){
									minSig = SigForPj;
									
								}
								break;

							}
							
						}
						
						
					}
					if (minSig >= lSigThreshold){
//						resultPatterns.add(new LinkedList<String>(prePattern));
//						resultCount.add(countInFg);
//						System.out.println("**********"+preMotif.toString());
						System.out.println("%%%%%%%%%%%%%%%%%%%"+preMotif.getMotifAsAString()+"   "+minSig);
						 resultMotifs.addElement(new Motif(preMotif, m_length, m_phosAA));
					}
				}
			}
			
			branch = ct.ConditionFPT(tree, itemName); // 产生条件FP树
			if (branch == null) { /*
								 * 如果没有产生条件FPTree，
								 * 当一个项只是root结点下的子结点是就不会产生条件FPTree
								 */
				i++;
				preMotif.removeLastElement();
//				prePattern.removeLast();

				continue;
			}
			cl = new LinkedList<Node>();
			node.cNode = cl;
			FP_growth(branch, cl,preMotif);
			branch = null;
			i++;
			preMotif.removeLastElement();
//			prePattern.removeLast();

		}
	}
}
