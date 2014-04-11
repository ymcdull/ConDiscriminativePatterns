/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fpgrowth;

/**
 *
 * @author Donny
 */
import java.io.Serializable;
import java.util.*;

/**
 * ITree树 05.12.10－第二版 ITree树中同一层次的兄弟结点之间按照项目编号升序排序 用LinkedList存储同层的兄弟结点
 * 
 * _______________________ head --->|__|____|___|___|___|___| | | | V | |
 * 孩子LinkedList | | V | | | _______________________ 孩子LinkedList
 * ---->|__|____|___|___|___|___| | | | V V V
 * */

public class ITree implements Serializable {
	private int total; // 记录事务总数
	private double sCount; // 在当前记录数下的支持度计数
	public LinkedList<Node> head;
	public FastVector fv;
	int n = 0;
	String m_phosAA = "S";

	public ITree(String m_phosAA) {
		this.total = 0;
		this.sCount = 0;
		head = new LinkedList<ITree.Node>();
		fv = new FastVector();
		this.m_phosAA = m_phosAA;
	}
	

	public ITree() {
		this.total = 0;
		this.sCount = 0;
		head = new LinkedList<ITree.Node>();
		fv = new FastVector();
	}

	public ITree(int total, int sCount) {
		this.total = total;
		this.sCount = sCount;
		head = new LinkedList<ITree.Node>();
		fv = new FastVector();
	}

	/**
	 * 增加事务记录总数，重新计算1/1000支持度计数
	 * 
	 * @param inc
	 *            新增的事务记录数
	 * */
	void addTotal(int inc) {
		total += inc;
	}

	/**
	 * 取得当前最小支持度计数
	 * */
	double readSCount() {
		return sCount;
	}

	/**
	 * 设置ITree的最小支持度计数
	 * 
	 * @param n
	 *            最小支持度计数
	 * */
	void setSCount(double n) {
		sCount = n;
	}

	/**
	 * 将堆栈中的项集插入到ITree1
	 * */
	void Insert2(Stack s) {
		Stack ss;
		double count;
		Node top;

		top = (Node) s.top.obj;
		count = top.count;
		ss = s.clone();
		Sort.sort(ss); // 按照项目编号排序
		InsertToITree(ss, count);
		ss = null;
	}

	private void InsertToITree(Stack s, double count) {
		int n;
		Node node = null, cn = null;
		LinkedList<Node> l;
		String itemName;
		int comp;

		if (s == null || s.IsEmpty())
			return;
		if (head == null)
			head = new LinkedList<Node>();
		l = head;
		while (!s.IsEmpty()) {
			node = (Node) s.pop();
			node.count = 0;
			itemName = node.name;
			if (l.size() == 0) {
				l.add(node);
				l = new LinkedList<Node>();
				node.cNode = l;
				continue;
			}
			for (int i = 0; i < l.size(); i++) {
				cn = (Node) l.get(i);
				comp = compare(itemName, cn.toString());
				if (comp == 0) { // 找到则继续
					l = cn.cNode;
					node = cn;
					if (l == null) {
						l = new LinkedList<Node>();
						cn.cNode = l;
					}
					break;
				} else if (comp < 0) { // 未找到则插入
					l.add(i, node);
					l = new LinkedList<Node>();
					node.cNode = l;
					break;
				} else if (i == l.size() - 1) { // 到链尾了还未找到
					l.add(node);
					l = new LinkedList<Node>();
					node.cNode = l;
					break;
				}
			}

		}
		node.count += count; // 更新支持度计数
	}

	private int compare(String it1, String it2) {
		int i1, i2;
		it1 = it1.trim();
		it2 = it2.trim();
		i1 = Integer.parseInt(it1.substring(1));
		i2 = Integer.parseInt(it2.substring(1));
		if (i1 > i2)
			return 1;
		else if (i1 == i2)
			return 0;
		return -1;
	}

	void empty() {
		head = null;
	}

	/** 树是否为空 */
	boolean IsEmpty() {
		if (head == null || head.size() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 测试用打印
	 * */
	void print(LinkedList<Node> h, Stack s) {
		Node node = null;
		FastVector temp = null;
		if (h == null) {
			// System.out.print(n+++":");
			// Print.print(s,"node");
			return;
		}
		for (int i = 0; i < h.size(); i++) {
			node = h.get(i);
			s.push(node);
			int size = s.size();
			if (!s.IsEmpty()) {
				if (fv.size() < size) {
					for (int k = fv.size(); k < size; k++) {
						temp = new FastVector();
						fv.addElement(temp);
					}
				}
				Motif m = makeMotif(s);
				temp = (FastVector) fv.elementAt(size - 1);
				temp.addElement(m);
			}
			 System.out.print(n+++":");
			 Print.print(s,"node");
			print(node.cNode, s);
			s.pop();
		}
	}
	Motif makeMotif(Stack s) {

		int num;
		String c;
		Stack.Elem e = s.top;
		int count = ((Node) e.obj).count;
		Motif m = new Motif(21, this.m_phosAA);
		while (e != null) {
			Node node = (Node) e.obj;
			if (node.name.length() == 2) {
				num = new Integer(node.name.substring(0, 1)).intValue();
				c = node.name.substring(1);
			} else {
				num = new Integer(node.name.substring(0, 2)).intValue();
				c = node.name.substring(2);
			}
			m.m_items[num] = c;
			m.m_index.addElement(new Integer(num));
			e = e.next;
		}
		m.m_counter_foreground = count;
		return m;

	}

	/**
	 * ITree结点 内部类
	 * */
	public class Node implements Serializable {
		public int count;
		public String name;
		public LinkedList<Node> cNode;

		Node(String name) {
			count = 0;
			this.name = name;
		}

		public Node(String name, int count) {
			this.count = count;
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	void printMotifs() {
		for (int i = 0; i < fv.size(); i++) {
			FastVector temp = (FastVector) fv.elementAt(i);
			for (int k = 0; k < temp.size(); k++) {
				Motif m = (Motif) temp.elementAt(k);
				System.out.println(m.toString());
			}
		}
	}
}
