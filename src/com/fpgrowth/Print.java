/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fpgrowth;

import com.fpgrowth.ITree.Node;

/**
 * 
 * @author Donny
 */

public class Print {

	/**
	 * 从底到顶打印堆栈
	 * */
	static void print(Stack s, String type) {
		int count = 0;
		Object obj;
		String str;
		Node node;
		Stack a = new Stack();
		Stack.Elem e;
		e = s.top;
		if (type.equalsIgnoreCase("node")) {
			node = (Node) e.obj;
			count = node.count;
		}
		while (e != null) {
			a.push(e.obj);
			e = e.next;
		}
		e = a.top;
		while (!a.IsEmpty()) {
			obj = a.pop();

			if (type.equalsIgnoreCase("item")) {
				// its=(ItemSet)obj;
				// System.out.print(its+"("+its.count+")-");
			} else if (type.equalsIgnoreCase("string")) {
				str = (String) obj;
				System.out.print(str + "-");
			} else if (type.equalsIgnoreCase("node")) {
				node = (Node) obj;
				System.out.print(node.name + "-");
			}

		}
		if (type.equalsIgnoreCase("node")) {
			System.out.println(count);
		} else
			System.out.println();
		a = null;
	}
}
