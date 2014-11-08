package taojava.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Your Name Here
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  
  
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  Node<T> header;
  double p;
  int maxLevel;

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */
  @SuppressWarnings("hiding")
  public class Node<T>
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The value stored in the node.
     */
    T val;
    int level;
    Node<T>[] next;

    public Node(T i, int n)
    {
      val = i;
      level = n;
      next = new Node[n];

      for (int j = 0; j < n; j++)
        next[j] = null;
    }
  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+  

  public SkipList()
  {
    this.header = new Node<T>(null, maxLevel);
    this.p = .5;
    maxLevel = 20;
  }

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+
  
  private int randomLevel(){
    
    int level = 0;
    Random random = new Random();
    
    while (random.nextInt() % 2 != 0)
      level++;
    
    return Math.min(level, this.maxLevel);
  }
  
  // +-----------------------+-------------------------------------------
  // | Methods from Iterable |
  // +-----------------------+

  /**
   * Return a read-only iterator (one that does not implement the remove
   * method) that iterates the values of the list from smallest to
   * largest.
   */

  public Iterator<T> iterator()
  {
    // We use a wrapper/adapter class, even though we currently
    // don't do any adaptations, because we might eventually 
    // find it useful to adapt.
    return new Iterator<T>()
      {
        Node cursor = SkipList.this.header;
        
        int pos;

        public boolean hasNext()
        {
          return this.cursor.next != null;
        } // next()

        public T next()
        {
          if (!this.hasNext())
            throw new NoSuchElementException();
          
          this.pos++;
          this.cursor = this.cursor.next[0];
          return (T) this.cursor.val;
        } // hasNext()

        public void remove()
        {
          throw new UnsupportedOperationException();
        } // remove()
      }; // new Iterator<T>
  } // iterator()

  // +------------------------+------------------------------------------
  // | Methods from SimpleSet |
  // +------------------------+

  /**
   * Add a value to the set.
   *
   * @post contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to add, contains(lav) continues to hold.
   */
  @SuppressWarnings("unchecked")
  public void add(T val)
  {
    if (val == null)
      throw new UnsupportedOperationException();
    
    Node<T> newNode;
    Node<T> current = this.header;
    int newLevel;
    
    for (int i = this.header.level; i >= 0; i--){
      while (current.next[i] != null &&
          current.next[i].val.compareTo(val) < 0){
        current = current.next[i];
      }
    }
    
    current = current.next[0];
    newLevel = randomLevel();
    newNode = new Node(val, newLevel);
    
    for (int i = 0; i < newLevel; i++){
      newNode.next[i] = current.next[i]; 
      current.next[i] = newNode;
    }
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    // STUB
    return false;
  } // contains(T)

  /**
   * Remove an element from the set.
   *
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    // STUB
  } // remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   *
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    // STUB
    return null;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    // STUB
    return 0;
  } // length()

} // class SkipList<T>
