/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fpgrowth;

/**
 *
 * @author Donny
 */
/**
 * 定义FPTree的数据结构,包括项目表和树
 * 
 */
public class FPTree {
	ItemTb itemTb;
	Tree tree;

	FPTree() {
		tree = new Tree();
	}

	public void ConsItemTb(int num) {// num为项目个数
		itemTb = new ItemTb(num);
	}

	/** 项目表 */
	class ItemTb {
		private int[] count;
		private String[] item;
		private Node[] link;
		private int len;

		ItemTb(int num) {
			count = new int[num];
			item = new String[num];
			link = new Node[num];
			len = num;
		}

		void Insert(String item, int num, int count, Node node) {
			if (len > num) {
				this.item[num] = item;
				this.count[num] = count;
				this.link[num] = node;
			}
		}

		int ReadCount(int num) {
			return count[num];
		}

		String ReadItem(int n) {
			String name = item[n];
			return name;
		}

		Node ReadNode(int num) {
			return link[num];
		}

		void SetNode(Node node, int num) {
			link[num] = node;
		}

		int Length() {
			return len;
		}

		void CopyItemArray(String[] dest) {
			System.arraycopy(item, 0, dest, 0, len);
		}

		void CopyCountArray(int[] dest) {
			System.arraycopy(count, 0, dest, 0, len);
		}

		/** 复制ItemTb表到一个Item链表 */
		Item CopyItemArray() {
			Item head = new Item("head");
			Item item = head;
			String name;
			int count;
			for (int i = 0; i < len; i++) {
				name = ReadItem(i);
				count = ReadCount(i);
				item.next = new Item(name);
				item.next.count = count;
				item = item.next;
			}
			return head.next;
		}

		/** 查找项item在树中的第一个节点 */
		public Node FindNode(String name) {
			int i = 0;
			while (i < len) {
				if (name.equals(item[i]))
					return link[i];
				i++;
			}
			return null;
		}
	}

	/** 树 */
	class Tree {
		Node root;

		Tree() {
			root = new Node();
		}
	}

	/** 树的节点 */
	class Node {
		String item;// 项目名称
		int count;// 支持度计数
		Node bnode;// 兄弟节点
		Node parent;// 父节点
		Cnode child;// 孩子节点

		Node() {
			count = 0;
			item = "root";
			bnode = null;
			parent = null;
			child = null;// new Cnode();
		}

		Node(String name) {
			count = 1;
			item = name;
			bnode = null;
			parent = null;
			child = null;// new Cnode();
		}

		void IncCount(int num) {
			count += num;
		}

		/** 在当前节点下插入孩子节点,返回新节点 */
		Node InsertNode(String item, int count) {
			Cnode c;
			Cnode temp = new Cnode();
			Node d = new Node(item);
			d.IncCount(count - 1);
			temp.link = d;
			d.parent = this;
			c = this.child;
			if (c == null) {
				this.child = temp;
				return d;
			}
			while (c.next != null) {
				c = c.next;
			}
			c.next = temp;
			return d;
		}

		/** 找当前节点的有item名的孩子节点 */
		public Node FindChild(Node d, String item) {
			String it;
			Cnode c;
			Node t;
			c = d.child;
			while (c != null) {
				t = c.link;
				it = c.link.item;
				if (it.equals(item))
					return c.link;
				c = c.next;

			}
			return null;
		}

		/** 孩子节点 */
		class Cnode {
			Cnode next;
			Node link;

			Cnode() {
				next = null;
				link = null;
			}
		}
	}
}
