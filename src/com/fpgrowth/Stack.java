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
 * 堆栈
 * 
 * @author qjt 当将某类型数据放入堆栈又 取出后，应该将取出的Object类型Downcast回原来类型
 * 
 */
class Stack {
	Elem top;
	int count;

	void push(Object obj) {
		Elem temp;
		temp = top;
		top = new Elem();
		top.obj = obj;
		top.next = temp;
		count++;
	}

	Object pop() {
		Elem temp;
		if (top != null) {
			temp = top;
			top = top.next;
			count--;
			return temp.obj;

		} else
			return null;
	}

	/**
	 * 生成一个转置的新栈
	 * */
	Stack Invert() {
		Stack s = new Stack();
		Elem e;
		e = top;
		while (e != null) {
			s.push(e.obj);
			e = e.next;
		}
		return s;
	}

	/** 产生一个克隆的堆栈 */
	public Stack clone() {
		Stack s = new Stack();
		Elem e, ee;
		e = top;
		ee = new Stack.Elem();
		s.top = ee;
		while (e != null) {
			ee.obj = e.obj;
			if (e.next != null) {
				ee.next = new Stack.Elem();
				ee = ee.next;
			}
			e = e.next;
		}
		s.count = this.count;
		return s;
	}

	/**
	 * 清空堆栈
	 * */
	void Empty() {
		top = null;
	}

	boolean IsEmpty() {
		if (top == null) {
			return true;
		} else
			return false;
	}

	int Len() {
		int i;
		Elem t;
		for (i = 0, t = top; t != null; i++, t = t.next)
			;
		return i;

	}

	Stack() {
		top = null;
		count = 0;

	}

	int size() {
		return count;

	}

	class Elem {
		Object obj;
		Elem next;

		Elem() {
			obj = null;
			next = null;
		}
	}
}
