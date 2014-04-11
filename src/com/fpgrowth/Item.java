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
 * 存储项集的链表
 * */
class Item {
	int count;
	String name;
	Item next;

	Item(String name) {
		count = 1;
		next = null;
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
