/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fpgrowth;

/**
 *
 * @author Donny
 */
import java.util.Comparator;

/**
 * 排序程序类
 * */
class Sort {
	static void sort(Stack s) {
		sort(s, null);
	}

	static void sort(Stack s, String order[]) {
		Stack.Elem it, temp, prep, end = null, top;
		Stack stack = new Stack();
		int result;
		top = s.top;
		it = top;
		prep = stack.new Elem();
		prep.next = top;
		top = prep;

		while (it.next != end) {
			while (it.next != end) {
				if ((result = Comp.compare(it.obj, it.next.obj, order)) == -1) {
					temp = it.next;
					it.next = it.next.next;
					prep.next = temp;
					temp.next = it;
				}
				prep = prep.next;
				it = prep.next;
			}
			end = it;
			it = top.next;
			prep = top;
		}
		s.top = top.next;
		top = null;
		stack = null;
	}

	/**
	 * 对itInTran按照itemTb表排序
	 * */
	static void sort(Item h, FPTree tree) {
		String order[] = new String[tree.itemTb.Length()];
		tree.itemTb.CopyItemArray(order);
		sort(h, order);
	}

	/**
	 * 对Item链表按照order数组秩序排序
	 * */
	static void sort(Item h, String order[]) {
		Item it, temp, prep, end = null;
		int result;

		it = h.next;
		prep = h;
		while (it.next != end) {
			while (it.next != end) {
				if ((result = Comp.compare(it.name, it.next.name, order)) == -1) {
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

	/**
	 * 对Item链表按照结点的count值排序（链表h应带头结点） 选择排序,返回链表长度
	 * */
	static int sort(Item h) {
		Item it, maxIt = null, temp, prep, r;// it记录当前插入位置
		int max, len = 0;

		it = h;
		prep = h;
		if (it == null || it.next == null)
			return 0;
		for (; it.next != null; len++) {
			max = 0;
			r = it;
			while (r.next != null) {
				if (r.next.count > max) {
					maxIt = r.next;
					max = r.next.count;
					prep = r;
				}
				r = r.next;
			}

			prep.next = maxIt.next;
			temp = it.next;
			it.next = maxIt;
			maxIt.next = temp;
			it = maxIt;
		}
		return len;
	}
}

/**
 * 比较器类
 * */
class Comp implements Comparator {
	public int compare(Object a, Object b) {
		int r = 0;
		r = (a.toString()).compareTo(b.toString());
		return r;
	}

	public int compare(int a, int b) {
		int r;
		if (a == b)
			return 0;
		return r = a > b ? 1 : -1;
	}

	static int compare(Object a, Object b, String order[]) {
		return compare(a.toString(), b.toString(), order);
	}

	/**
	 * 根据order数组的顺序比较a和b的大小
	 * */
	static int compare(String a, String b, String order[]) {
		int num, i = 0, result = 0;
		String name;
		num = order.length;
		while (i < num) {
			name = order[i];
			if (name.equals(a)) {
				result = 1;
				return result;
			} else if (name.equals(b)) {
				result = -1;
				return result;
			} else
				i++;
		}
		return result;
	}
}
