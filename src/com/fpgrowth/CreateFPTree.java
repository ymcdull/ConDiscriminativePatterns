/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fpgrowth;

/**
 *
 * @author Donny
 */
import java.io.*;

/**
 * 创建FPTree的方法集合
 */
public class CreateFPTree {
	FPTree t;
	Item itTotal; // 总item链表，统计了每种项的个数
	FastVector f;

	public void SetFPTree(FPTree t) {
		this.t = t;
	}

	/**
	 * 主程序
	 * 
	 * @param f
	 *            存储事务记录的文件
	 * */
	public void start(FastVector f) {
		int num; // 项目个数
		this.f = f;
		final boolean isTb = false;
		final boolean isTree = true;
		itTotal = new Item("head"); // itTotal是统计所有种类的项及其个数的链表

		ReadFromTxt(isTb); // 读入文本事务中的项目到itTotal链表
		num = NumOfItem(itTotal); // 取得项的总共类数
		t.ConsItemTb(num); // 构造FPTree中的ItemTb表
		SortItem(itTotal); // 对itTotal链表排序
		Insert(itTotal, t.itemTb); // 将itTotal链表插入到itemTb表中
		ReadFromTxt(isTree);
		LinkItem(t);
	}

	/** 将Tree中各个Item和他的兄弟节点建立链接 */
	public void LinkItem(FPTree t) {
		FPTree.Node d;
		int num;
		num = t.itemTb.Length();

		String[] item = new String[num];
		FPTree.Node[] node = new FPTree.Node[num];
		t.itemTb.CopyItemArray(item);

		Queue<FPTree.Node> q = new Queue();
		d = t.tree.root;
		inQue(q, d);
		while (!q.IsEmpty()) {
			d = q.out();
			inQue(q, d);
			num = FindNum(item, d.item);
			if (num == -1)
				System.out.println("Exception:No found item");
			else {
				if (node[num] == null) {
					node[num] = d;
					t.itemTb.SetNode(d, num);
				} else {
					node[num].bnode = d;
					node[num] = d;
				}
			}
		}

	}

	/**
	 * 读入一个事务(一行)中的所有项,以链表形式存储
	 * 
	 * @param head
	 *            链表的头结点
	 * @param s
	 *            待读入的字符串
	 * */
	private Item ReadOneTrans(Item head, String s) {
		int start = 0, end = 0, n = 1;
		String name;
		Item it;

		it = head;
		s = s.trim();
		start = 0;
		end = s.indexOf(" ");
		while (start >= 0) {
			if (start == 0)
				name = s.substring(start, end);
			else if (end >= 0)
				name = s.substring(start + 1, end);
			else
				name = s.substring(start + 1);
			start = end;
			end = s.indexOf(" ", start + 1);
			// if (n++==1) continue;
			it.next = new Item(name);
			it = it.next;
		}
		return head;
	}

	/** 读入每个事务产生的Item链表插入到总Item链表中 */
	private void InsertItTotal(Item head) {
		String name;
		Item item = head.next;
		Item it = new Item("");

		while (item != null) {
			name = item.name;
			it = find(itTotal, name);
			if (it != null) {
				it.count = it.count + item.count;
			} else {
				it = goTail(itTotal);
				it.next = new Item(name);
				it.next.count = item.count;
			}
			item = item.next;
		}
	}

	private int NumOfItem(Item head) {
		Item it;
		int i;

		it = head.next;
		for (i = 0; it != null; i++, it = it.next)
			;
		return i;
	}

	/** 返回一个链表的尾部节点 */
	private Item goTail(Item head) {
		Item it;
		it = head;
		while (it.next != null) {
			it = it.next;
		}
		return it;
	}

	/**
	 * 从文本文件中读入项目到itTotal链表中
	 * <p>
	 * 或者读入项目插入到FPTree的树中
	 * */
	private void ReadFromTxt(boolean isTree) {
		Item itInTran;
		String s;
		itInTran = new Item("head"); // itInTran是一个事务中的所有项的链表，
										// 顺序按照所有种类项的支持度计数的高到低的顺序
		try {
			for (int i = 0; i < f.size(); i++) {
				s = (String) f.elementAt(i);
				itInTran = ReadOneTrans(itInTran, s);
				if (isTree) {
					Sort.sort(itInTran, t);
					InsertTree(itInTran, t.tree);
				} else
					InsertItTotal(itInTran);

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private Item find(Item item, String name) {
		while ((item != null) && (!item.name.equals(name))) {
			item = item.next;
		}
		if (item == null)
			return null;
		else
			return item;
	}

	/** 对Item链表由大到小冒泡排序 */
	private void SortItem(Item h) {
		Item it, temp, prep, end = null;
		it = h.next;
		prep = h;
		while (it.next != end) {
			while (it.next != end) {
				if (it.count < it.next.count) {
					temp = it.next;
					it.next = it.next.next;
					prep.next = temp;
					temp.next = it;
				}
				prep = prep.next;
				it = prep.next;
			}
			end = it;
			it = h.next;
			prep = h;
		}
	}

	/** 将Item链表的内容插入到FPTree的ItemTb表里 */
	private void Insert(Item head, FPTree.ItemTb t) {
		int n = 0;
		Item item;
		item = head.next;
		while (item != null) {
			t.Insert(item.name, n++, item.count, null);
			item = item.next;
		}
	}

	/** 将一个事务中的项目排序后的Item链插入到FPTree的树中 */
	private void InsertTree(Item head, FPTree.Tree t) {
		FPTree.Node node, cNode;
		Item it;
		String name;

		it = head.next;
		node = t.root;
		while (it != null) {
			name = it.name;
			cNode = node.FindChild(node, name);
			if (cNode != null) {
				cNode.IncCount(it.count);
				node = cNode;
			} else
				node = node.InsertNode(name, it.count);
			it = it.next;
		}
	}

	private void buildLink(FPTree t) {
		FPTree.Node d;
		int num;
		num = t.itemTb.Length();

		String[] item = new String[num];
		FPTree.Node[] node = new FPTree.Node[num];
		;
		t.itemTb.CopyItemArray(item);

		Queue q = new Queue();
		d = t.tree.root;
		inQue(q, d);
		while (!q.IsEmpty()) {
			d = (FPTree.Node) q.out();
			inQue(q, d);
			num = FindNum(item, d.item);
			if (num == -1)
				System.out.println("Exception:No found item");
			else {
				if (node[num] == null) {
					node[num] = d;
					t.itemTb.SetNode(d, num);
				} else {
					node[num].bnode = d;
					node[num] = d;
				}
			}
			// System.out.println("name:"+d.item+"/parent:"+d.parent.item+"/count:"+d.count);
		}

	}

	/** 查找一个name在字符数组中位置 */
	public int FindNum(String[] item, String name) {
		int num = 0, len;
		len = item.length;
		while (num < len) {
			if (name.equals(item[num]))
				return num;
			num++;
		}
		return -1;
	}

	/** 从FP树t和一个项item产生该项的条件FPTree */
	public FPTree ConditionFPT(FPTree tree, String item) {
		FPTree tjFPT = new FPTree();
		FPTree.Node node, nd;
		Item its, cpb; // cpb(conditional pattern base)是单个条件模式基
		int num, supp; // supp是支持度计数

		/* 第一遍扫描,对所有条件模式基中的项排序 */
		itTotal = new Item("head");
		its = new Item("head");
		node = tree.itemTb.FindNode(item);
		nd = node;
		cpb = its;
		while (nd != null) {
			supp = nd.count;
			node = nd.parent;
			its = cpb;
			/*
			 * 产生条件模式基，条件模式基是一个项的前缀路径， 条件模式基的支持度计数就是该路径上该项的支持度计数
			 */
			while (!node.item.equals("root")) {
				its.next = new Item(node.item);
				its.next.count = supp;
				its = its.next;
				node = node.parent;
			}
			if (cpb.next != null) {
				InsertItTotal(cpb);
			}
			nd = nd.bnode;

		}
		num = NumOfItem(itTotal); // 取得项的总共类数
		if (num == 0)
			return null; // 如果构造不成一棵条件FP树
		tjFPT.ConsItemTb(num); // 构造FPTree中的ItemTb表
		SortItem(itTotal); // 对itTotal链表排序
		Insert(itTotal, tjFPT.itemTb); // 将itTotal链表插入到itemTb表中

		/* 第二遍扫描,将条件模式基插入到条件FPTree中 */
		its = new Item("");
		node = tree.itemTb.FindNode(item);
		nd = node;
		cpb = its;
		while (nd != null) {
			supp = nd.count;
			node = nd.parent;
			its = cpb;

			/* 产生条件模式基 */
			while (!node.item.equals("root")) {
				its.next = new Item(node.item);
				its.next.count = supp;
				its = its.next;
				node = node.parent;
			}
			if (cpb.next != null) {
				Sort.sort(cpb, tjFPT);
				InsertTree(cpb, tjFPT.tree); // 将条件模式基插入到条件模式树中
			}
			nd = nd.bnode;
		}
		buildLink(tjFPT); // 打印FPTree的Tree并将各个Item和他的兄弟节点建立链接
		return tjFPT;
	}

	/** 将一个节点的所有孩子节点入队列 */
	private void inQue(Queue q, FPTree.Node n) {
		FPTree.Node.Cnode c;
		c = n.child;
		while (c != null) {
			q.in(c.link);
			c = c.next;
		}
	}
}
