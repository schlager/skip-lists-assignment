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
  int maxLevel;
  int size;
  double p;

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
    private Node<T>[] next;

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
    this.maxLevel = 20;
    this.p = .5;
    this.size = 0;
    this.header = new Node<T>(null, maxLevel);
  }
  
  public SkipList(int pValue)
  {
    this.maxLevel = 20;
    this.p = pValue;
    this.size = 0;
    this.header = new Node<T>(null, maxLevel);
  }

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+

  private int randomLevel()
  {

    int level = 1;
    Random random = new Random();

    while (random.nextDouble() < this.p)
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
        Node<T> cursor = SkipList.this.header;

        int pos;

        public boolean hasNext()
        {
          return this.cursor.next[0] != null;
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
          SkipList.this.remove(this.cursor.val);
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
  public void add(T val)
  {
    if (val == null)
      throw new UnsupportedOperationException();

    Node<T>[] update = new Node[this.maxLevel];
    Node<T> newNode;
    Node<T> current = this.header;
    int newLevel;

    for (int level = this.header.level - 1; level >= 0; level--)
      {
        while (current.next[level] != null
               && current.next[level].val.compareTo(val) < 0)
          current = current.next[level];
        update[level] = current;
      }
      
    current = current.next[0];
    
    if (current != null && current.val.equals(val))
      return;
    
    newLevel = randomLevel();
    newNode = new Node<T>(val, newLevel);

    for (int level = 0; level < newLevel; level++)
      {
        newNode.next[level] = update[level].next[level];
        update[level].next[level] = newNode;
      }
    this.size++;
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    if (val == null)
      return false;

    Node<T> current = this.header;

    for (int level = this.header.level - 1; level >= 0; level--)
      {
        while (current.next[level] != null
               && val.compareTo(current.next[level].val) >= 0)
          current = current.next[level];
        if (val.equals(current.val))
          return true;
      }// for
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
    if (val == null)
      throw new UnsupportedOperationException();

    Node<T>[] update = new Node[this.maxLevel];
    Node<T> current = this.header;

    for (int level = this.header.level - 1; level >= 0; level--)
      {
        while (current.next[level] != null
               && current.next[level].val.compareTo(val) < 0)
          current = current.next[level];
        update[level] = current;
      }

    current = current.next[0];
    
    if (current != null && current.val.compareTo(val) == 0)
      {
        for (int level = 0; level < current.level; level++)
          {
            if (update[level].next[level] != current)
              break;
            update[level].next[level] = current.next[level];
          }
      }
    this.size--;
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
    if (i < 0 || i > this.size)
      throw new IndexOutOfBoundsException();

    Node<T> current = this.header;

    for (int pos = 0; pos < i; pos++)
      {
        if (current.next[0] != null)
          current = current.next[0];
      }
    
    if (current == null)
      return null;
    
    return current.val;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    return this.size;
  } // length()

} // class SkipList<T>
