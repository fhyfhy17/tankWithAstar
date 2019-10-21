package com.ourpalm.tank.app.map.astar;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class BinaryHeap<T> {

	private final Stack<T> heap;
	private final Comparator<T> comparator;

	/**
	 * 构造方法
	 *
	 * @param comparator
	 *            元素比较器
	 */
	public BinaryHeap(Comparator<T> comparator) {
		heap = new Stack<>();
		this.comparator = comparator;
	}

	/**
	 * 获取二叉堆元素数量
	 *
	 * @return 元素数量
	 */
	public int getSize() {
		return heap.size();
	}

	/**
	 * 交换元素，将最大值或最小值置于顶端
	 *
	 * @param objIndex
	 *            当前元素位置
	 * @param parentIndex
	 *            父元素位置
	 */
	private void swap(int objIndex, int parentIndex) {
		T temp = heap.get(objIndex);
		while (objIndex > 0) {
			// 只有objIndex>0才有可能有parent
			// （大堆）如果新插入的数据大于parent的数据，则应不断上移与parent交换位置
			// （小堆）如果新插入的数据小于parent的数据，则应不断上移与parent交换位置
			T parentObj = heap.get(parentIndex);
			if (comparator.compare(temp, parentObj) > 0) {
				heap.set(objIndex, parentObj);
				objIndex = parentIndex;
				// parent索引的算法
				parentIndex = (parentIndex - 1) >> 1;
			} else {
				break;
			}
		}
		heap.set(objIndex, temp);
	}

	/**
	 * 修改堆，将元素obj修改为newObj，并计算排序
	 *
	 * @param obj
	 *            旧的元素
	 * @param newObj
	 *            新的元素
	 * @return 是否修改成功
	 */
	public boolean modify(T obj, T newObj) {
		int objIndex = heap.indexOf(obj);
		if (objIndex < 0) {
			return false;
		}
		heap.set(objIndex, newObj);
		int parentIndex = (objIndex - 1) >> 1;
		swap(objIndex, parentIndex);
		return true;
	}

	/**
	 * 增加元素
	 *
	 * @param obj
	 *            待增加的元素
	 */
	public void push(T obj) {
		heap.add(obj);
		int size = heap.size();
		int parentIndex = (size - 2) >> 1;
		int objIndex = size - 1;
		swap(objIndex, parentIndex);
	}

	/**
	 * 弹出堆顶的数据
	 *
	 * @return 堆顶元素
	 */
	public T shift() {
		if (heap.size() > 1) {
			T r = heap.get(0);
			T last = heap.pop();
			heap.set(0, last);
			int parentIndex = 0;
			int childIndex = 1;
			T parent = heap.get(parentIndex);
			while (childIndex < heap.size()) {
				T child = heap.get(childIndex);
				T nextChild;
				int nextIndex = childIndex + 1;
				if (nextIndex >= heap.size()) {
					nextChild = null;
				} else {
					nextChild = heap.get(nextIndex);
				}
				if (nextChild != null && comparator.compare(child, nextChild) < 0) {
					childIndex = nextIndex;
				}
				child = heap.get(childIndex);
				if (comparator.compare(parent, child) < 0) {
					heap.set(parentIndex, child);
					parentIndex = childIndex;
					childIndex = (childIndex << 1) + 1;
				} else {
					break;
				}
			}
			heap.set(parentIndex, parent);
			return r;
		}
		return heap.pop();
	}

	@Override
	public String toString() {
		return heap.toString();
	}

	/**
	 * 转换为栈列表
	 * 
	 * @return 元素栈
	 */
	public List<T> toStack() {
		return heap;
	}

	/**
	 * 转换为数组
	 * 
	 * @return 元素数组
	 */
	public T[] toArray() {
		return (T[]) (heap.toArray());
	}
}
