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
 * 树广度遍历的辅助队列,尾进头出；
 * */
class Queue<T>{
	Elem head,tail,elem;
	//void in(FPTree.Node node){
	void in(T node){
		elem=new Elem(node);
		if ((head==null)&&(tail==null)){
		    tail=elem;
		    head=elem;
		}
		else{
			tail.next=elem;
			tail=elem;
		}
	}
	//FPTree.Node out(){
	T out(){
		Elem e;
		if ((head==null)&&(tail==null)){
			return null;
		}
		else if(head==tail){
			e=head;
			head=tail=null;
		    return e.node;
		}
		else{
			e=head;
			head=head.next;
		    return e.node;
		}
	}
	boolean IsEmpty(){
		if ((head==null)&&(tail==null)){
			return true;
		}
		return false;
	}
	/**
	 * 清空队列
	 * */
	void Empty(){
		head=null;
		tail=null;
	}
	Queue(){
		head=null;
		tail=null;


	}
	class Elem{
		//FPTree.Node node;
		T node;
		Elem next;
		//Elem(FPTree.Node node){
		Elem(T node){
			this.node=node;
			next=null;
		}
	}
}

