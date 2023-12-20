package edu.iastate.cs228.hw3;

import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;




// Prakarsha Poudel

/**
 * Implementation of the list interface based on linked nodes
 * that store multiple items per node.  Rules for adding and removing
 * elements ensure that each node (except possibly the last one)
 * is at least half full.
 */
public class StoutList<E extends Comparable<? super E>> extends AbstractSequentialList<E>
{
  /**
   * Default number of elements that may be stored in each node.
   */
  private static final int DEFAULT_NODESIZE = 4;
  
  /**
   * Number of elements that can be stored in each node.
   */
  private final int nodeSize;
  
  /**
   * Dummy node for head.  It should be private but set to public here only  
   * for grading purpose.  In practice, you should always make the head of a 
   * linked list a private instance variable.  
   */
  public Node head;
  
  /**
   * Dummy node for tail.
   */
  private Node tail;
  
  /**
   * Number of elements in the list.
   */
  private int size;
  
  /**
   * Constructs an empty list with the default node size.
   */
  public StoutList()
  {
    this(DEFAULT_NODESIZE);
  }

  /**
   * Constructs an empty list with the given node size.
   * @param nodeSize number of elements that may be stored in each node, must be 
   *   an even number
   */
  public StoutList(int nodeSize)
  {
    if (nodeSize <= 0 || nodeSize % 2 != 0) throw new IllegalArgumentException();
    
    // dummy nodes
    head = new Node();
    tail = new Node();
    head.next = tail;
    tail.previous = head;
    this.nodeSize = nodeSize;
  }
  
  /**
   * Constructor for grading only.  Fully implemented. 
   * @param head
   * @param tail
   * @param nodeSize
   * @param size
   */
  public StoutList(Node head, Node tail, int nodeSize, int size)
  {
	  this.head = head; 
	  this.tail = tail; 
	  this.nodeSize = nodeSize; 
	  this.size = size; 
  }

  @Override
  public int size()
  {
    // TODO Auto-generated method stub
    return size;
  }
  
	@Override
	public boolean add(E item) {
		if (item == null) {
			throw new NullPointerException("Item to be added to list is null");
		}
//Creates new node if list is empty
		if (head.next == tail) {
			Node newNode = new Node();
			head.next = newNode;
			newNode.previous = head;
			newNode.next = tail;
			tail.previous = newNode;
			newNode.addItem(item);
		}
//Adds item to first open spot in last data node if possible
		else if (tail.previous.count < nodeSize) {
			tail.previous.addItem(item);
		}
//Creates new node and places item in first slot if final data node is full
		else {
			Node newNode = new Node();
			newNode.previous = tail.previous;
			newNode.next = tail;
			newNode.previous.next = newNode;
			tail.previous = newNode;
			newNode.addItem(item);
		}
//Increments the number of items in the list
		size++;
		return true;
	}
  

//to check for duplicates
	private boolean contains(E item) {
		
		if(size < 1)
			return false;
		
		Node temp = head.next;
		
		while(temp != tail) {
			
			for(int i=0; i<temp.count; i++) {
				
				if(temp.data[i].equals(item))
					
					return true;
				
				temp = temp.next;
			}
		}
		return false;
	}

	@Override
	public void add(int pos, E item) {
		// TODO Auto-generated method stub
		
		if (pos < 0 || pos > size)
			throw new IndexOutOfBoundsException();


		if (head.next == tail)
			add(item);

		NodeInfo info = new NodeInfo(head, 0);
		 info = info.find(pos);
		Node currNode = info.node;
		
		
		int offset = info.offset;

	
		if (offset == 0) {

			if (currNode.previous.count < nodeSize && currNode.previous != head) {
				currNode.previous.addItem(item);
				size++;
				return;
			}

			else if (currNode == tail) {
				add(item);
				size++;
				return;
			}
		}

		if (currNode.count < nodeSize) {
			currNode.addItem(offset, item);
		}

		else {
			Node newNode = new Node();
			int halfPoint = nodeSize / 2;
			int count = 0;
			
			
			while (count < halfPoint) {
				
				newNode.addItem(currNode.data[halfPoint]);
				
				currNode.removeItem(halfPoint);
				
				count++;
			}

			Node oldNode = currNode.next;

			currNode.next = newNode;
			newNode.previous = currNode;
			
			newNode.next = oldNode;
			oldNode.previous = newNode;


			if (offset <= nodeSize / 2) {
				currNode.addItem(offset, item);
			}

			if (offset > nodeSize / 2) {
				newNode.addItem((offset - nodeSize / 2), item);
			}

		}
		
		size++;
	}

	
	
  @Override
  public E remove(int pos)
  {
	    if (pos < 0 || pos >= size) {
	        throw new IndexOutOfBoundsException("Invalid position: " + pos);
	    }

		NodeInfo info = new NodeInfo(head, 0);
		
	    info = info.find(pos);
	    
	    Node currNode = info.returnNode();  // Access the node using returnNode()
	    int offset = info.returnOffset();   // Access the offset using returnOffset()

	    
	    E removedItem = currNode.data[offset];

	    if (currNode.next == tail && currNode.count == 1) {
	        // If the node is the last node and has only one element, delete it
	        Node predecessor = currNode.previous;
	        predecessor.next = tail;
	        tail.previous = predecessor;
	        currNode = null;
	        
	        
	    } else if (currNode.next == tail || currNode.count > nodeSize / 2) {
	        // If the node is the last node or has more than M/2 elements, simply remove the item
	        currNode.removeItem(offset);
	    } 
	    
	    else {
	        // The node n must have at most M/2 elements
	        currNode.removeItem(offset);
	        Node newNode = currNode.next;
	        
	        
	        if (newNode.count > nodeSize / 2) {
	          
	            currNode.addItem(newNode.data[0]);
	            newNode.removeItem(0);
	        }
	        
	        
	        else {
	           
	            for (int i = 0; i < newNode.count; i++) {
	                currNode.addItem(newNode.data[i]);
	            }
	            
	            
	            currNode.next = newNode.next;
	            newNode.next.previous = currNode;
	        }
	    }
	    
	    
	    size--;

	    return removedItem;
  }
  
  

  /**
   * Sort all elements in the stout list in the NON-DECREASING order. You may do the following. 
   * Traverse the list and copy its elements into an array, deleting every visited node along 
   * the way.  Then, sort the array by calling the insertionSort() method.  (Note that sorting 
   * efficiency is not a concern for this project.)  Finally, copy all elements from the array 
   * back to the stout list, creating new nodes for storage. After sorting, all nodes but 
   * (possibly) the last one must be full of elements.  
   *  
   * Comparator<E> must have been implemented for calling insertionSort().    
   */
  public void sort()
  {
	    E[] elements = (E[]) new Comparable[size];
	    int index = 0;

	    Node current = head.next;
	    while (current != tail) {
	        for (int i = 0; i < current.count; i++) {
	            elements[index++] = current.data[i];
	        }
	        current = current.next;
	    }
	    
	    head.next = tail;
	    tail.previous = head;

	    
	    insertionSort(elements, new Comparator<E>() {
	        @Override
	        public int compare(E o1, E o2) {
	            return o1.compareTo(o2);
	        }
	    });
	    size = 0;
	    

	    rebuildList(elements);
  }
  
  /**
   * Sort all elements in the stout list in the NON-INCREASING order. Call the bubbleSort()
   * method.  After sorting, all but (possibly) the last nodes must be filled with elements.  
   *  
   * Comparable<? super E> must be implemented for calling bubbleSort(). 
   */
  public void sortReverse() 
  {
      // Implement sorting in non-increasing order using bubble sort.
      E[] elements = (E[]) new Comparable[size];
      int index = 0;

      Node current = head.next;
      
      while (current != tail) {
    	  
          for (int i = 0; i < current.count; i++) {
              elements[index++] = current.data[i];
          }
          current = current.next;
      }
      
	  head.next = tail;
	  tail.previous = head;

      bubbleSort(elements);
      size = 0;
      rebuildList(elements);
  }
  
  @Override
  public Iterator<E> iterator()
  {
    // TODO Auto-generated method stub
      return new StoutListIterator();
  }

  @Override
  public ListIterator<E> listIterator()
  {
    // TODO Auto-generated method stub
      return new StoutListIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index)
  {
    // TODO Auto-generated method stub
      return new StoutListIterator(index);
  }
  
  /**
   * Returns a string representation of this list showing
   * the internal structure of the nodes.
   */
  public String toStringInternal()
  {
    return toStringInternal(null);
  }

  /**
   * Returns a string representation of this list showing the internal
   * structure of the nodes and the position of the iterator.
   *
   * @param iter
   *            an iterator for this list
   */
  public String toStringInternal(ListIterator<E> iter) 
  {
      int count = 0;
      int position = -1;
      if (iter != null) {
          position = iter.nextIndex();
      }

      StringBuilder sb = new StringBuilder();
      sb.append('[');
      Node current = head.next;
      while (current != tail) {
          sb.append('(');
          E data = current.data[0];
          if (data == null) {
              sb.append("-");
          } else {
              if (position == count) {
                  sb.append("| ");
                  position = -1;
              }
              sb.append(data.toString());
              ++count;
          }

          for (int i = 1; i < nodeSize; ++i) {
             sb.append(", ");
              data = current.data[i];
              if (data == null) {
                  sb.append("-");
              } else {
                  if (position == count) {
                      sb.append("| ");
                      position = -1;
                  }
                  sb.append(data.toString());
                  ++count;

                  // iterator at end
                  if (position == size && count == size) {
                      sb.append(" |");
                      position = -1;
                  }
             }
          }
          sb.append(')');
          current = current.next;
          if (current != tail)
              sb.append(", ");
      }
      sb.append("]");
      return sb.toString();
  }


  /**
   * Node type for this list.  Each node holds a maximum
   * of nodeSize elements in an array.  Empty slots
   * are null.
   */
  private class Node
  {
    /**
     * Array of actual data elements.
     */
    // Unchecked warning unavoidable.
    public E[] data = (E[]) new Comparable[nodeSize];
    
    /**
     * Link to next node.
     */
    public Node next;
    
    /**
     * Link to previous node;
     */
    public Node previous;
    
    /**
     * Index of the next available offset in this node, also 
     * equal to the number of elements in this node.
     */
    public int count;

    /**
     * Adds an item to this node at the first available offset.
     * Precondition: count < nodeSize
     * @param item element to be added
     */
    void addItem(E item)
    {
      if (count >= nodeSize)
      {
        return;
      }
      data[count++] = item;
      //useful for debugging
      //      System.out.println("Added " + item.toString() + " at index " + count + " to node "  + Arrays.toString(data));
    }
  
    /**
     * Adds an item to this node at the indicated offset, shifting
     * elements to the right as necessary.
     * 
     * Precondition: count < nodeSize
     * @param offset array index at which to put the new element
     * @param item element to be added
     */
    
    void addItem(int offset, E item)
    {
      if (count >= nodeSize)
      {
    	  return;
      }
      for (int i = count - 1; i >= offset; --i)
      {
        data[i + 1] = data[i];
      }
      ++count;
      data[offset] = item;
      
      //useful for debugging 
//      System.out.println("Added " + item.toString() + " at index " + offset + " to node: "  + Arrays.toString(data));
    }

    /**
     * Deletes an element from this node at the indicated offset, 
     * shifting elements left as necessary.
     * Precondition: 0 <= offset < count
     * @param offset
     */

    void removeItem(int offset)
    {
      E item = data[offset];
      for (int i = offset + 1; i < nodeSize; ++i)
      {
        data[i - 1] = data[i];
      }
      data[count - 1] = null;
      --count;
    }    
  }
  
  

 
	private class StoutListIterator implements ListIterator<E> {// talked

		// from him
		// constants you possibly use ...
		// instance variables ...
		// current node
				private Node current;
				private Node removableCur;
		// the offset that is to be removed
				private int removableOff;
		// current index
				private int index;
		// Previous index
				private int preI;
		// current offset
				private int offset;
		// Previous offset
				private int preOff;

				/**
				 * Default constructor
				 */
				public StoutListIterator() {
					current = head.next;
					index = 0;
					offset = 0;
					preI = -1;
					preOff = -1;
				}

				/**
				 * Constructor finds node at a given position.
				 *
				 * @param pos
				 */
				public StoutListIterator(int pos) {
					this.index = pos;
					this.preI = index - 1;
		// call to the helper method to find node and offset
					NodeInfo temp = new NodeInfo(head, 0);
					temp = temp.find(pos);
					Node current = temp.returnNode();
					int curOffset = temp.returnOffset();
					this.current = current;
					this.offset = curOffset;
		// if the offset is zero then the previouseOffset is in the
					if (this.offset > 0) {
						preOff = offset - 1;
					}
					if (this.offset == 0) {
						if (current.previous != head) {
							Node tempPrevious = current.previous;
							this.preOff = tempPrevious.count - 1;
						}
					}
				}

				@Override
				public boolean hasNext() {
					if (offset < current.count && current.data[offset] != null) {
						return true;
					} else if (current.next != null || current != tail) {
						Node temp = current.next;
						if (temp.count > 0) {
							return true;
						}
					}
					return false;
				}

				@Override
				public E next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					if (index > size) {
		// if there is a bug in code
						throw new RuntimeException();
					}
					if (current.count <= offset) {
						current = current.next;
						offset = 0;
						preI = index;
						index++;
						preOff = offset;
						removableCur = current;
						removableOff = offset;
						return current.data[offset++];
					}
					if (current.count > offset) {
						preI = index;
						index++;
						preOff = offset;
						removableCur = current;
						removableOff = offset;
						return current.data[offset++];
					}
					return null;
				}

				@Override
				public void remove() {
					if (preI >= size) {
						throw new IndexOutOfBoundsException();
					}
					if (removableCur == null) {
						throw new IllegalStateException();
					}
					if (removableCur.count == 1) {
						Node temp = removableCur.previous;
						removableCur = removableCur.next;
						temp.next = removableCur;
						removableCur.previous = temp;
						if (removableOff <= offset) {
							offset--;
							index--;
							preOff--;
							preI--;
						}
						size--;
						removableCur = null;
						return;
					}
					if ((removableCur.next == tail && removableCur.previous == head) || (removableCur.count > (nodeSize / 2))) {
						removableCur.removeItem(removableOff);
						size--;
						if (removableOff < offset && offset > 0) {
							offset--;
							index--;
							preOff--;
							preI--;
						}
						removableCur = null;
						return;
					}
					if (removableCur.count <= (nodeSize / 2)) {
						Node node = removableCur.next;
						if (node == null || node == tail) {
							removableCur.removeItem(removableOff);
							size--;
							if (removableOff < offset && offset > 0) {
								offset--;
								index--;
								preOff--;
								preI--;
							}
							removableCur = null;
							return;
						}
						if (node.count > (nodeSize / 2)) {
							removableCur.removeItem(removableOff);
							removableCur.addItem(node.data[0]);
							node.removeItem(0);
							size--;
							if (removableOff < offset && offset > 0) {
								offset--;
								index--;
								preOff--;
								preI--;
							}
							removableCur = null;
							return;
						}
						if (node.count <= (nodeSize / 2)) {
							removableCur.removeItem(removableOff);
							size--;
							while (node.count > 0) {
								removableCur.addItem(node.data[0]);
								node.removeItem(0);
							}
							node = node.next;
							removableCur.next = node;
							node.previous = removableCur;
							if (removableOff < offset && offset > 0) {
								offset--;
								index--;
								preOff--;
								preI--;
							}
							removableCur = null;
							return;
						}
					}
				}

				@Override
				public boolean hasPrevious() {
					if (this.preOff >= 0 && (this.preOff < nodeSize && current.data[this.preOff] != null)) {
						return true;
					} else if (current.previous != null || current.previous != head) {
						Node temp = current.previous;
						if (temp.count > 0) {
							return true;
						}
					}
					return false;
				}

				@Override
				public int nextIndex() {
					return index;
				}

		// Other methods you may want to add or override that could possiblyf//acilitate
		// other operations, for instance, addition, access to the previous
		//element,

		// etc.
		//
		// ...
		//
				@Override
				/**
				 * adds item
				 */
				public void add(E item) {
					if (item == null) {
						throw new NullPointerException();
					}
					if (index > size) {
						throw new IllegalArgumentException();
					}
					if (size == 0) {
						Node cur = head;
						Node temp = new Node();
						cur.next = temp;
						temp.previous = cur;
						temp.next = tail;
						tail.previous = temp;
						temp.addItem(index, item);
						size++;
						this.preI = this.index;
						this.index++;
						this.preOff = this.offset;
						this.offset++;
						this.current = temp;
						removableCur = null;
						return;
					}
					NodeInfo n = new NodeInfo(head, 0);
					n = n.find(index);
					Node cur = n.returnNode();
					int offset = n.returnOffset();
					if (offset == 0) {
						if (cur.previous.count != nodeSize && cur.previous != head) {
							cur = cur.previous;
							cur.addItem(item);
							size++;
							this.preI = index;
							this.index++;
							this.offset = cur.count;
							this.preOff = this.offset - 1;
							this.current = cur;
							removableCur = null;
							return;
						}
						if (cur == tail && cur.previous.count == nodeSize) {
							Node tempNode = new Node();
							Node lastNode = cur.previous;
							tempNode.previous = lastNode;
							tempNode.next = cur;
							cur.previous = tempNode;
							lastNode.next = tempNode;
							tempNode.addItem(item);
							size++;
							this.preI = index;
							this.index++;
							this.offset = cur.count;
							this.preOff = this.offset - 1;
							this.current = tempNode;
							removableCur = null;
							return;
						}
					}
					if (cur.count < nodeSize) {
						cur.addItem(offset, item);
						size++;
						this.preI = index;
						this.index++;
						this.preOff = this.offset;
						this.offset++;
						removableCur = null;
						return;
					}
					if (cur.count >= nodeSize) {
						Node tempNode = new Node();
						Node tempNext = cur.next;
						tempNode.next = tempNext;
						cur.next = tempNode;
						tempNode.previous = cur;
						tempNext.previous = tempNode;
						while (tempNode.count != (nodeSize / 2)) {
							tempNode.addItem(cur.data[nodeSize / 2]);
							cur.removeItem(nodeSize / 2);
						}
						if (offset <= (nodeSize / 2)) {
							cur.addItem(offset, item);
							size++;
							this.preI = index;
							this.index++;
							this.preOff = offset;
							this.offset = offset + 1;
							this.current = cur;
							removableCur = null;
							return;
						}
						if (offset > (nodeSize / 2)) {
							tempNode.addItem(offset - (nodeSize / 2), item);
							size++;
							this.preI = index;
							this.index++;
							this.preOff = offset - (nodeSize / 2);
							this.offset = offset - (nodeSize / 2) + 1;
							this.current = tempNode;
							removableCur = null;
							return;
						}
					}
				}

				@Override
				/**
				 * returns the previous element
				 */
				public E previous() {
					if (!hasPrevious()) {
						throw new NoSuchElementException();
					}
					if (preI < 0 || preI > size) {
						throw new RuntimeException("index 1");
					}
					if (this.preOff < 0 || this.preOff > offset) {
						current = current.previous;
						preOff = current.count - 1;
						offset = current.count - 1;
						preI--;
						index--;
						removableCur = current;
						removableOff = preOff;
						return current.data[this.preOff--];
					}
					if (preOff <= offset) {
						preI--;
						index--;
						offset--;
						removableCur = current;
						removableOff = preOff;
						return current.data[this.preOff--];
					}
					return null;
				}

				@Override
				/**
				 * returns the previous index
				 */
				public int previousIndex() {
					return preI;
				}

				@Override
				/**
				 * replaces the previous or next item uses helper properties to make sure it
				 * removes the correct one and if previous or next havent been called throws an
				 * exception
				 */
				public void set(E item) {
					if (item == null) {
						throw new NullPointerException();
					}
					if (removableCur == null) {
						throw new IllegalStateException();
					}
					removableCur.removeItem(removableOff);
					removableCur.addItem(removableOff, item);
				}
			}
  

  /**
   * Sort an array arr[] using the insertion sort algorithm in the NON-DECREASING order. 
   * @param arr   array storing elements from the list 
   * @param comp  comparator used in sorting 
   */
  private void insertionSort(E[] arr, Comparator<? super E> comp)
  {
	    for (int i = 1; i < arr.length; i++) {
	        E currentElement = arr[i];
	        int j = i - 1;
	        
	        while (j >= 0 && comp.compare(currentElement, arr[j]) < 0) {
	            arr[j + 1] = arr[j];
	            j--;
	        }
	        
	        arr[j + 1] = currentElement;
	    }
  }
  
  /**
   * Sort arr[] using the bubble sort algorithm in the NON-INCREASING order. For a 
   * description of bubble sort please refer to Section 6.1 in the project description. 
   * You must use the compareTo() method from an implementation of the Comparable 
   * interface by the class E or ? super E. 
   * @param arr  array holding elements from the list
   */
  private void bubbleSort(E[] arr)
  {
      for (int i = 0; i < arr.length - 1; i++) {
    	  
          for (int j = 0; j < arr.length - i - 1; j++) {
        	  
              if (arr[j].compareTo(arr[j + 1]) < 0) {
            	  
                  // Swap arr[j] and arr[j+1]
            	  
                  E temp = arr[j];
                  arr[j] = arr[j + 1];
                  arr[j + 1] = temp;
              }
          }
      }
  }
  private void rebuildList(E[] elements) {
	    Iterator<E> iter = iterator();
	    int index = 0;
	    while (iter.hasNext()) {
	        iter.next();
	        iter.remove();
	    }

	    for (E element : elements) {
	        if (element != null) {
	            add(element);
	        }
	    }
	}
  
	private class NodeInfo {
		public Node node;
		public int offset;

		public NodeInfo(Node node, int offset) {
			this.node = node;
			this.offset = offset;
		}

		NodeInfo find(int pos) {
			if (pos == size()) {
				return new NodeInfo(tail, 0);
			}
			Node cur = head.next;
			int nextCount = cur.count;
			int prev = 0;
			while ((pos >= nextCount && cur.next != null)) {
				prev += cur.count;
				cur = cur.next;
				nextCount += cur.count;
			}
			return new NodeInfo(cur, pos - prev);
		}

		private Node returnNode() {
			return node;
		}

		private int returnOffset() {
			return offset;
		}
	}
  
  

 

}