
package heap;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Scanner;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class A3Test {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10); // 10sec timeout

    /** Use assertEquals to check that mh correctly represents a heap
     *  with values b and priorities p.
     *  This means that:
     *    - for each i in 0..size-1, (b[i], p[i]) is in mh.c
     *    - mh.size() = b.length = p.length
     *    - mh satisfies invariants 1 and 2.
     *  Precondition: b.length == p.length.  */
    public <V,P extends Comparable<P>> void check(String m, V[] b, P[] p, Heap<V,P> mh) {
        assert b.length == p.length;

        String msg; 
        // Invariant 1
        for (int i = 0; i < mh.size(); i++) {
          msg = m + "\nHeap invariant 1 not satisfied: All levels except the last should be full, nodes in last level should be as far left as possible";
        	assertTrue(msg, mh.c.get(i) != null);
        }

        // invariant 2
        for (int i = 1; i < mh.size(); i++) {
          msg = m + "\nHeap invariant not satisfied: Each element's priority should be greater than or equal to its parent's";
        	assertTrue(msg, mh.c.get(i).priority.compareTo(mh.c.get((i-1)/2).priority) >= 0);
        }

        // check equality with (b,p)
        msg = m  + "\nHeap does not match the correct tree: it is the wrong size.";
        assertEquals(msg, b.length, mh.size());
        //assertEquals(b.length, mh.map.size());

        // check the entries of c match b and p
        boolean seen[] = new boolean[b.length];
        for (int i = 0; i < mh.size(); i++) {
            V val = mh.c.get(i).value;
            P pri = mh.c.get(i).priority;
            for (int j = 0; j < b.length; j++) {
                if (val.equals(b[j]) && pri.equals(p[j])) {
                    // make sure we don't 't see an element twice
                    msg = m + "\nAn element in the heap erroneously appeared twice.";
                    assertFalse(msg, seen[j]);
                    seen[j] = true;
                }
            }
        }

        // make sure we saw every element
        for (int i= 0; i < seen.length; i++) {
            msg = m  + "\nThe heap was missing an entry";
            assertTrue(msg, seen[i]);
        }
    }


    /** Use assertEquals to check that mh correctly represents a heap
     *  with values b and priorities p, including the 
     *  This means that:
     *    - for each i in 0..size-1, (b[i], p[i]) is in mh.c
     *    - mh.size() = b.length = p.length
     *    - mh satisfies all 5 invariants (including phase 3 invariants).
     *  Precondition: b.length == p.length.  */
    public <V,P extends Comparable<P>> void checkPhase3(String m, V[] b, P[] p, Heap<V,P> mh) {
        // check phase 1 invariants and equality with p and b:
        check(m, b, p, mh);

     //  In Phase 3, the following class invariant also must be maintained:
     //    3. The tree cannot contain duplicate *values*; note that dupliate
     //       *priorities* are still allowed.
     //    4. map contains one entry for each element of the heap, so
     //       map.size() == c.size()
     //    5. For each value v in the heap, its map entry contains in the
     //       the index of v in c. Thus: map.get(b[i]) = i.
     //

      
        String msg;
        // invariant 4:
        msg = m + "\nHeap invariant not satisfied: The number of values in the heap should be equal to the size of the map";
        assertEquals(msg, b.length, mh.map.getSize());

        // invariants 3 and 5:
        for (int i= 0; i < mh.c.size(); i++) {
        	// check that (b,p) is in the map
        	msg = m + "\nMap did not contain a key that should have existed";
        	assertTrue(msg, mh.map.containsKey(b[i]));

        	int n = mh.map.get(b[i]);
        	msg = m + "\nThe index stored in the map for a given value did not represent the correct index in the heaps arraylist";
        	assertEquals(msg, mh.c.get(n).value, b[i]);

        	msg = m + "\nThis test found the index of a value in the arraylist using the map, but the priority of this value was not correct";
        	assertTrue(msg, mh.c.get(n).priority.compareTo(p[i]) == 0);
        }
    }

    /**Return a heap with the values of b added to it, in that order. The
     * priorities are the values. */
    public Heap<Integer,Integer> makeHeap(Integer[] b) {
        Heap<Integer,Integer> m= new Heap<>();
        for (Integer e : b) m.add(e, e);
        return m;
    }

    /**Return a heap with the values of b and corresponding priorities p
     * added to it, in that order.  */
    public Heap<Integer,Double> makeHeap(Integer[] b, double[] p) {
        Heap<Integer,Double> m= new Heap<>();
        for (int h= 0; h < b.length; h= h+1) {
            m.add(b[h], p[h]);
        }
        return m;
    }

    /**Return a heap with the values of b and corresponding priorities p
     * added to it, in that order.  */
    public Heap<String,Double> makeHeap(String[] b, double[] p) {
        Heap<String,Double> m= new Heap<String,Double>();
        for (int h= 0; h < b.length; h= h+1) {
            m.add(b[h], p[h]);
        }
        return m;
    }

    @Test
    /** Test whether add works when the priority of the value being added is
     * not smaller than priorities of other values in the heap. */
    public void test100Add() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[] {5});
        check("Attempting to add a single element to the heap.", new Integer[]{5}, new Integer[]{5}, mh);

        String message = "Attempting to add entries whose priorities are in descending order (swapping is not yet necesary).";
        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {5, 7});
        check(message, new Integer[]{5, 7}, new Integer[]{5,7}, mh1);
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {5, 7, 8});
        check(message, new Integer[]{5, 7, 8}, new Integer[]{5, 7, 8}, mh2);
    }


    @Test
    /**  Test whether swap works in isolation */
    public void test110Swap() {
        Heap<Integer,Integer> mh = new Heap<Integer,Integer>();
        mh.add(10, 5);
        mh.add(11, 5);
        mh.swap(0, 1);
        String message = "Swapping two elements (with equal priorities) failed.";
        assertEquals(message, 11, (int)mh.c.get(0).value);
        assertEquals(message, 10, (int)mh.c.get(1).value);
        check(message, new Integer[]{10, 11}, new Integer[]{5, 5}, mh);
    }


    @Test
    /** Test add and bubble up. */
    public void test115Add_BubbleUp() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[]{3});

        String message = "Attempting to add an entry with a greater priority: bubbleUp shouldn't need to swap anything";
        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{3, 6});
        check(message, new Integer[]{3, 6}, new Integer[]{3, 6}, mh1);
        mh1.add(8, 8);
        check(message, new Integer[]{3, 6, 8}, new Integer[]{3, 6, 8}, mh1);
        mh1.add(5, 5);
        message = "Attempting to add a node with smaller priority than its parent, which requires a swap in bubbleUp";
        check(message, new Integer[]{3, 5, 8, 6}, new Integer[]{3, 5, 8, 6}, mh1);
        mh1.add(4, 4);
        check(message, new Integer[]{3, 4, 8, 6, 5}, new Integer[]{3, 4, 8, 6, 5}, mh1);
        mh1.add(1, 1);
        message = "Attempting to add a node with smallest priority, which should be swapped up to the root position.";
        check(message, new Integer[]{1, 4, 3, 6, 5, 8}, new Integer[]{1, 4, 3, 6, 5, 8}, mh1);
    }

    @Test
    /** Test add and bubble up with duplicate priorities */
    public void test117Add_BubbleUpDuplicatePriorities() {
        String message = "Adding elements with duplicate priorities; they should not be swapped with their parent if their priorities are equal.";
        Heap<Integer,Double> mh= new Heap<Integer,Double>();
        mh.add(4, 4.0);
        check(message, new Integer[]{4}, new Double[]{4.0}, mh);
        mh.add(2, 4.0);
        check(message, new Integer[]{4, 2}, new Double[]{4.0, 4.0}, mh);
        mh.add(1, 4.0);
        check(message, new Integer[]{4, 2, 1}, new Double[]{4.0, 4.0, 4.0}, mh);
        mh.add(0, 4.0);
        check(message, new Integer[]{4, 2, 1, 0}, new Double[]{4.0, 4.0, 4.0, 4.0}, mh);
    }

    @Test
    /** Test peek. */
    public void test120Peek() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[]{1, 3});
        String message = "The peek function did not return the root element (the one with smallest priority value)";
        assertEquals(message, 1, (int)mh.peek());
        message = "Peek returned the correct value but the heap was incorrect afterwards; remember that peek should not modify the heap in any way";
        check(message, new Integer[]{1, 3}, new Integer[]{1, 3}, mh);

        Heap<Integer,Double> mh1= new Heap<Integer,Double>();
        try {
            mh1.peek();  
            fail("Peek didn't throw an exception when the heap is empty.");
        } catch (NoSuchElementException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Peek threw something other than NoSuchElementException when the heap is empty");
        }
    }

    @Test
    /** Test poll and bubbledown with no duplicate priorities. */
    public void test130Poll_BubbleDown_NoDups() {
        String pollIncorrectReturn = "Poll did not return the correct value.";
        String badHeap = "Heap was incorrect after poll.";
        String badHeapCompare = "Heap was incorrect after poll when bubbleDown has to choose the correct child to swap with.";
        String badHeapOneChild = "Heap was incorrect after poll when the node has only one child.";

        Heap<Integer,Integer> mh= makeHeap(new Integer[]{5});
        Integer res= mh.poll();
        assertEquals(pollIncorrectReturn, 5, (int)res);
        check(badHeap, new Integer[]{}, new Integer[]{}, mh);

        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{5, 6});
        Integer res1= mh1.poll();
        assertEquals(pollIncorrectReturn, 5, (int)res1);
        check(badHeap, new Integer[]{6}, new Integer[]{6}, mh1);

        // this requires comparing lchild and rchild and using lchild
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {4, 5, 6, 7, 8, 9});
        Integer res2= mh2.poll();
        assertEquals(pollIncorrectReturn, 4, (int)res2);
        check(badHeapCompare, new Integer[]{5, 7, 6, 9, 8}, new Integer[]{5, 7, 6, 9, 8}, mh2);

        // this requires comparing lchild and rchild and using rchild
        Heap<Integer,Integer> mh3= makeHeap(new Integer[] {4, 6, 5, 7, 8, 9});
        Integer res3= mh3.poll();
        assertEquals(pollIncorrectReturn, 4, (int)res3);
        check(badHeapCompare, new Integer[]{5, 6, 9, 7, 8}, new Integer[]{5, 6, 9, 7, 8}, mh3);

        // this requires bubbling down when only one child
        Heap<Integer,Integer> mh4= makeHeap(new Integer[] {4, 5, 6, 7, 8});
        Integer res4= mh4.poll();
        assertEquals(pollIncorrectReturn, 4, (int)res4);
        check(badHeapOneChild, new Integer[]{5, 7, 6, 8}, new Integer[]{5, 7, 6, 8}, mh4);

        String message = "Heap was incorrect after poll; check bubbleDown.";
        Heap<Integer,Integer> mh5= makeHeap(new Integer[] {2, 4, 3, 6, 7, 8, 9});
        Integer res5= mh5.poll();
        assertEquals(pollIncorrectReturn, 2, (int)res5);
        check(message, new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh5);

        Heap<Integer,Integer> mh6= makeHeap(new Integer[] {2, 4, 3, 6, 7, 9, 8});
        Integer res6= mh6.poll();
        assertEquals(pollIncorrectReturn, 2, (int)res6);
        check(message, new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh6);

        Heap<Integer,Integer> mh7= new Heap<Integer,Integer>();
        try {
            mh7.poll();  
            fail("Polling an empty heap didn't throw an exception");
        } catch (NoSuchElementException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Polling an empty heap threw something other than NoSuchElementExceptionException");
        }
    }

    @Test
    /** Test bubble-up and bubble-down with duplicate priorities. */
    public void test140testDuplicatePriorities() {
        // values should not bubble up or down past ones with duplicate priorities.
        // First two check bubble up
        String message = "Attempting to add elements with duplicate priorities; make sure bubbleUp and bubbleDown don't swap elements if their priorities are equal.";
        Heap<Integer,Double> mh1= makeHeap(new Integer[] {6}, new double[] {4});
        mh1.add(5, 4.0);
        check(message, new Integer[]{6, 5}, new Double[]{4.0, 4.0}, mh1);

        Heap<Integer,Double> mh2= makeHeap(new Integer[] {7, 6}, new double[] {4, 4});
        mh2.add(3, 4.0);
        check(message, new Integer[]{7, 6, 3}, new Double[]{4.0, 4.0, 4.0}, mh2);

        message = "Attempting to poll elements from a heap with all equal priorities.";
        // Check bubble down
        Heap<Integer,Double> mh3= makeHeap(new Integer[] {5, 6, 7}, new double[] {4, 4, 4});
        mh3.poll();
        check(message, new Integer[]{7, 6}, new Double[]{4.0, 4.0}, mh3);

        // Check bubble down
        Heap<Integer,Double> mh4= makeHeap(new Integer[] {5, 7, 6, 8}, new double[] {4, 4, 4, 4});
        mh4.poll();
        check(message, new Integer[]{8, 7, 6}, new Double[]{4.0, 4.0, 4.0}, mh4);
    }


    @Test
    /** Test a few calls with Strings */
    public void test170Strings() {
        Heap<String,Integer> mh= new Heap<String,Integer>();
        String message = "Testing heap operations on a heap with String values and Integer priorities (Heap<String,Integer>)";
        check(message, new String[]{}, new Integer[]{}, mh);
        mh.add("abc", 5);
        check(message, new String[]{"abc"}, new Integer[]{5}, mh);
        mh.add("beep", 3);
        check(message, new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);
        mh.add("", 2);
        check(message, new String[]{"", "abc", "beep"}, new Integer[]{2, 5, 3}, mh);
        String p= mh.poll();
        assertEquals("Poll did not return the correct value in a Heap<String,Integer>", "", p);
        check(message, new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);
    }

    @Test
    /** Test using values in 0..999 and random values for the priorities.
     *  There will be duplicate priorities. */
    public void test190BigTests() {
        // The values to put in Heap
        int[] b= new int[1000];
        for (int k= 0; k < b.length; k= k+1) {
            b[k]= k;
        }

        Random rand= new Random(52);

        // bp: priorities of the values
        double[] bp= new double[b.length];
        for (int k= 0; k < bp.length; k= k+1) {
            bp[k]= (int)(rand.nextDouble()*bp.length);
        }

        // Build the Heap and map to be able to get priorities easily
        Heap<Integer,Double> mh= new Heap<Integer,Double>();
        HashMap<Integer, Double> hashMap= new HashMap<Integer, Double>();
        for (int k= 0; k < b.length; k= k+1) {
            mh.add(b[k], bp[k]);
            hashMap.put(b[k], bp[k]);
        }

        // poll values one by one, check that priorities are in order, store
        // in dups the number of duplicate priorities, and save polled value
        // in array bpoll.
        double prevPriority= -1;
        int dups= 0; //Number of duplicate keys,
        int[] bpoll= new int[b.length];
        for (int k= 0; k < b.length; k= k+1) {
            bpoll[k]= mh.poll();
            Double p= hashMap.get(bpoll[k]);
            if (p == prevPriority) {
                dups= dups + 1;
            }
            assertEquals("In a larger-scale test, the heap order property was violated after calls to both add and poll.", true, prevPriority <= p);
            prevPriority= p;
        }

        // Sort bpoll and check that it contains 0..bpoll.length-1
        Arrays.sort(bpoll);
        for (int k= 0; k < b.length; k= k+1) {
            assertEquals("In a larger-scale test, the heap did not have the correct elements.", k, bpoll[k]);
        }
        // System.out.println("duplicate priorities: " + dups);
    }


    ///////////////////
    // Phase 2 tests //
    ///////////////////

    /* make a hash table containing the giving key-value mappings */
    private static <K,V> HashTable<K,V> make( K[] keySet, V[] valSet, int initCap) {
        assert keySet.length == valSet.length;
        HashTable<K,V> hm = new HashTable<K,V>(initCap);
        for (int i = 0; i < keySet.length; i++) {
            hm.put(keySet[i], valSet[i]);
        }

        return hm;
    }

    private static <K,V> HashTable<K,V> make(K[] keySet, V[] valSet) {
        return make(keySet, valSet, 17);
    }


    /* check that the hash table contains the appropriate mappings */
    private static <K,V> void check(String m, K[] keySet, V[] valSet, HashTable<K,V> hm) {
        assert keySet.length == valSet.length;
        String msg = m + "\nHashTable invariant not satisfied: Size value of map and number of keys were not equal";
        assertEquals(msg, keySet.length, hm.getSize());
        msg = m + "\nget did not return the correct value";
        for (int i = 0; i < keySet.length; i++) {
            assertEquals(msg, valSet[i], hm.get(keySet[i]));
        }
    }

    @Test
    /** Test put and get with no collisions and no mod*/
    public void test210PutGet() {
        HashTable<Integer,Integer> hm = new HashTable<Integer,Integer>();
        for (int i = 0; i < 8; i++) {;
            assertEquals("The put method did not return null when there were no duplicate keys in map", null, hm.put(i,i));
            assertEquals("The size was incorrect after a put.", i+1, hm.getSize());
            assertEquals("The capacity of the buckets array increased when it should not have.", 17, hm.getCapacity());
        }
        for (int i = 0; i < 8; i++) {
            assertEquals("The get method didn't return the correct value on a simple table with no mod and no collisions.", i, (int)hm.get(i));
        }
        for (int i = 8; i < 17; i++) {
            assertEquals("The get method did not return null given a nonexistent key.", null, hm.get(i));
        }
    }

    @Test
    /** Test put and get with no collisions but mod needed */
    public void test211PutGet() {
        Integer[] keys = new Integer[]{0,1,2,3,22,23,24,25};
        Integer[] vals = new Integer[]{0,1,2,3,22,23,24,25};
        HashTable<Integer,Integer> hm = make(keys, vals);
        check("Attempting to build a table with no collisions but modding the hash code required.", keys, vals, hm);
    }

    @Test
    /** Test that put overwrites and returns a pre-existing value */
    public void test212Put() {
        Integer[] keys = new Integer[]{0,1,2,3,4,5,6,7,8,9};
        Integer[] vals = new Integer[]{0,1,2,3,4,5,6,7,8,9};
        HashTable<Integer,Integer> hm = make(keys, vals);

        for (int i = 0; i < 10; i++) {
            assertEquals("Put did not correctly overwrite and return an existing value when the key already existed.", i, (int)hm.put(i,i+1));
            assertEquals("The size of the map wasn't correct after an existing key's value was overwritten (the size should not have changed).", 10, hm.getSize());
        }

    }

    @Test
    /** Test put and get with no collisions but mod needed */
    public void test213Put() {
        Integer[] keys = new Integer[]{0,-1,-2,-3,-22,-23,-24,-25};
        Integer[] vals = new Integer[]{0,1,2,3,22,23,24,25};
        HashTable<Integer,Integer> hm = make(keys, vals);
        check(".", keys, vals, hm);
    }

    @Test
    /** Test put/get with collisions */
    public void test230PutGet() {
        Integer[] keys = new Integer[]{0,1,2,3,17,18,19};
        Integer[] vals = new Integer[]{0,1,2,3,17,18,19};
        HashTable<Integer,Integer> hm = make(keys, vals);
        check("Attempting to build a table with collisions. Elements that hash to a non-empty bucket should be added to the linked list (made up of Pairs) in the bucket.", keys, vals, hm);
    }

    @Test
    /** Test that put overwrites and returns a pre-existing value, with
     * collisions */
    public void test231Put() {
        Integer[] keys = new Integer[]{0,1,2,3,17,18,19};
        Integer[] vals = new Integer[]{0,1,2,3,17,18,19};
        HashTable<Integer,Integer> hm = make(keys, vals);

        for (int i = 0; i < keys.length; i++) {
          assertEquals("Attempting to overwite an existing key's value, with collisions; the old value was not correctly returned.", vals[i], hm.put(keys[i],keys[i]+1));
          assertEquals("The size of the map wasn't correct after an existing key's value was overwritten (the size should not have changed).", 7, hm.getSize());
        }


        keys = new Integer[]{0,1,2,3,17,18,19};
        vals = new Integer[]{1,2,3,4,18,19,20};
        check("Hash table does not match correct mapping after overwriting an existing key's value with collisions involved.", keys, vals, hm);
    }

    @Test
    /** Test containsKey (no collisions) */
    public void test240ContainsKey() {
        Integer[] keys = new Integer[]{0,1,2,3,4,5,26,27,28,29};
        Integer[] vals = new Integer[]{0,1,2,3,4,5,26,27,28,29};
        HashTable<Integer,Integer> hm = make(keys, vals);

        int i = 0;
        int ki = 0;
        while (i < 35) {
            if (ki < keys.length && keys[ki] == i) {
                assertEquals("The containsKey method didn't return true for a key that exists in the map.", true, hm.containsKey(i));
                ki++;
            } else {
                assertEquals("The containsKey method didn't return false for a key that doesn't exist in the map.", false, hm.containsKey(i));
            }
            i++;
        }
    }

    @Test
    /** Test containsKey (with collisions) */
    public void test241ContainsKey() {
        Integer[] keys = new Integer[]{0,1,2,17,18,19};
        Integer[] vals = new Integer[]{0,1,2,17,18,19};
        HashTable<Integer,Integer> hm = make(keys, vals);

        int i = 0;
        int ki = 0;
        while (i < 34) {
            if (ki < keys.length && keys[ki] == i) {
                assertEquals("The containsKey function returned false when called with a valid key in a map with collisions.", true, hm.containsKey(i));
                ki++;
            } else {
                assertEquals("The containsKey function returned true when called with a nonexistant key in a map with collisions.", false, hm.containsKey(i));
            }
            i++;
        }
    }

    @Test
    /** Test that remove works (no collisions) */
    public void test250Remove() {
        Integer[] keys = new Integer[]{0,1,2,3,4,5,6,7,8,9};
        Integer[] vals = new Integer[]{0,1,2,3,4,5,6,7,8,9};
        HashTable<Integer,Integer> hm = make(keys, vals);

        for (int i = 0; i < 10; i++) {
            assertEquals("The remove method did not return the value of the removed key in a table with no collisions.", vals[i], hm.remove(keys[i]));
            assertEquals("The size of the map wasn't correct after a removal in a table with no collisions.", 10-i-1, hm.getSize());
        }
    }


    @Test
    /** Test that remove works (with collisions) */
    public void test251Remove() {
        Integer[] keys = new Integer[]{0,1,2,17,18,19};
        Integer[] vals = new Integer[]{0,1,2,17,18,19};
        HashTable<Integer,Integer> hm = make(keys, vals);
        String removeSizeError = "The size was not correct after a remove from a table with collsiions.";
        String removeReturnError = "A call to remove did not return the correct value in a table with collisions.";

        // remove 0 (first-inserted element in the bucket)
        assertEquals(removeReturnError, vals[0], hm.remove(keys[0]));
        assertEquals(removeSizeError, 5, hm.getSize());

        // remove 19 (most-recently-inserted element in the bucket)
        assertEquals(removeReturnError, vals[5], hm.remove(keys[5]));
        assertEquals(removeSizeError, 4, hm.getSize());

        assertEquals(removeReturnError, vals[4], hm.remove(keys[4]));
        assertEquals(removeSizeError, 3, hm.getSize());

        assertEquals(removeReturnError, vals[2], hm.remove(keys[2]));
        assertEquals(removeSizeError, 2, hm.getSize());

        assertEquals(removeReturnError, vals[1], hm.remove(keys[1]));
        assertEquals(removeSizeError, 1, hm.getSize());

        assertEquals(removeReturnError, vals[3], hm.remove(keys[3]));
        assertEquals(removeSizeError, 0, hm.getSize());
    }



    @Test
    /** Test that table grows when load factor exceeds 0.8 */
    public void test280Grow() {

        Integer[] keys = new Integer[]{0,1,2,17,18,19,20,21,5,6,7,8,9,11};
        Integer[] vals = new Integer[]{0,1,2,17,18,19,20,21,5,6,7,8,9,11};
        HashTable<Integer,Integer> hm = new HashTable<Integer,Integer>();

        for (int i = 0; i < 13; i++) {
            hm.put(keys[i], vals[i]);
            assertEquals("Capacity changed from 17 without exceeding the load factor threshold of 0.8.", 17, hm.getCapacity());
        }

        hm.put(keys[13], vals[13]);
        assertEquals("The hashtable's load factor exceeded 0.8, but did not double its capacity from 17 to 34.", 34, hm.getCapacity());
    }

    @Test
    /** Test that rehashing works after growth */
    public void test281Rehash() {
        Integer[] keys = new Integer[]{0,1,2,17,18,19,20,21,5,6,7,8,9,11,12,63,64};
        Integer[] vals = new Integer[]{0,1,2,17,18,19,20,21,5,6,7,8,9,11,12,63,64};
        HashTable<Integer,Integer> hm = new HashTable<Integer,Integer>();

        for (int i = 0; i < keys.length; i++) {
            hm.put(keys[i], vals[i]);
        }
        assertEquals("The hashtable's load factor exceeded 0.8, but did not double its capacity from 17 to 34.", 34, hm.getCapacity());
        check("The larger hash table after rehashing does not contain the same mapping as the original table.", keys, vals, hm);
    }


    @Test
    /** Test a small example with strings */
    public void test290Strings() {
        String[] keys = new String[]{"iztf", "uiu", "eqm", "rzh", "vjw", "ris", "tut", "wbb", "sjb", "lii", "urv", "fhm"};
        Integer[] vals = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};

        HashTable<String,Integer> hm = make(keys, vals);
        check("The hash table does not match the given mapping in a small test using String keys and Integer values.", keys, vals, hm);

    }

    @Test
    /** Test a big example with strings, including put (new and replace), get,
     * and remove */
    public void test291Strings() {
        HashMap<String,String> truth = new HashMap<String,String>();
        HashTable<String,String> hm = new HashTable<String,String>();

        try {
            Scanner sc = new Scanner(new File("P2TestInput.txt"));
            while (sc.hasNext()) {
                String k = sc.next();
                String v = sc.next();
                hm.put(k, v);
                truth.put(k, v);
            }
        } catch (FileNotFoundException e) {
            assertTrue("File P2TestInput.txt from skeleton repo not found.", false);
        }

        Set<String> trueKeySet = truth.keySet();
        Random rand = new Random(0);
        Iterator<Map.Entry<String,String>> iter = truth.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,String> entry = iter.next();
            String k = entry.getKey();
            double roll = rand.nextDouble();
            if (roll > 0.9) {
                // replace k's mapped value with a new random one
                String newVal = "";
                for (int i = 0; i < 4; i++) {
                    newVal = newVal + (char) (97 + rand.nextInt(26));
                }
                hm.put(k, newVal);
                truth.put(k, newVal);
            } else if (roll < 0.1) {
                // remove k from the mapping
                hm.remove(k);
                iter.remove();
            }
        }
        // check that the mappings are equivalent:
        assertEquals("The size of the hashmap was incorrect in a larger test with Strings as keys", truth.size(), hm.getSize());
        for (String k : truth.keySet()) {
            assertTrue("The hashmap did not contain a key that it should have in a larger test with Strings as keys", hm.containsKey(k));
            assertEquals("A key did not have the correct value in a larger test with Strings as keys", truth.get(k), hm.get(k));
        }
        assertEquals("The capacity of the hashmap was incorrect in a larger test with Strings as keys", hm.getCapacity(), 2176);
    }

    ///////////////////
    // Phase 3 Tests //
    ///////////////////

    @Test
    /** Test whether add works when the priority of the value being added is
     * not smaller than priorities of other values in the heap. */
    public void test300Add() {
        Heap<Integer,Integer> mh= makeHeap(new Integer[] {5});
        checkPhase3("Attempting to add a single element to the heap.", new Integer[]{5}, new Integer[]{5}, mh);

        String message = "Attempting to add entries whose priorities are in descending order (swapping is not yet necesary).";
        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {5, 7});
        checkPhase3(message, new Integer[]{5, 7}, new Integer[]{5,7}, mh1);
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {5, 7, 8});
        checkPhase3(message, new Integer[]{5, 7, 8}, new Integer[]{5, 7, 8}, mh2);
    }

    @Test
    /**  Test whether swap works in isolation */
    public void test310Swap() {
        Heap<Integer,Integer> mh = new Heap<Integer,Integer>();
        mh.add(10, 5);
        mh.add(11, 5);
        mh.swap(0, 1);
        String message = "Swapping two elements (with equal priorities) failed.";
        assertEquals(message, 11, (int)mh.c.get(0).value);
        assertEquals(message, 10, (int)mh.c.get(1).value);
        checkPhase3(message, new Integer[]{10, 11}, new Integer[]{5, 5}, mh);
    }


    @Test
    /** Test add and bubble up. */
    public void test315Add_BubbleUp() {

        Heap<Integer,Integer> mh= makeHeap(new Integer[]{3});

        String message = "Attempting to add an entry with a greater priority: bubbleUp shouldn't need to swap anything";
        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{3, 6});
        checkPhase3(message, new Integer[]{3, 6}, new Integer[]{3, 6}, mh1);
        mh1.add(8, 8);
        checkPhase3(message, new Integer[]{3, 6, 8}, new Integer[]{3, 6, 8}, mh1);
        mh1.add(5, 5);
        message = "Attempting to add a node with smaller priority than its parent, which requires a swap in bubbleUp";
        checkPhase3(message, new Integer[]{3, 5, 8, 6}, new Integer[]{3, 5, 8, 6}, mh1);
        mh1.add(4, 4);
        checkPhase3(message, new Integer[]{3, 4, 8, 6, 5}, new Integer[]{3, 4, 8, 6, 5}, mh1);
        mh1.add(1, 1);
        message = "Attempting to add a node with smallest priority, which should be swapped up to the root position.";
        checkPhase3(message, new Integer[]{1, 4, 3, 6, 5, 8}, new Integer[]{1, 4, 3, 6, 5, 8}, mh1);
    }

    @Test
    /** Test add and bubble up with duplicate priorities */
    public void test317Add_BubbleUpDuplicatePriorities() {
        String message = "Adding elements with duplicate priorities; they should not be swapped with their parent if their priorities are equal.";
        Heap<Integer,Double> mh= new Heap<Integer,Double>();
        mh.add(4, 4.0);
        checkPhase3(message, new Integer[]{4}, new Double[]{4.0}, mh);
        mh.add(2, 4.0);
        checkPhase3(message, new Integer[]{4, 2}, new Double[]{4.0, 4.0}, mh);
        mh.add(1, 4.0);
        checkPhase3(message, new Integer[]{4, 2, 1}, new Double[]{4.0, 4.0, 4.0}, mh);
        mh.add(0, 4.0);
        checkPhase3(message, new Integer[]{4, 2, 1, 0}, new Double[]{4.0, 4.0, 4.0, 4.0}, mh);
    }

    @Test
    /** Test poll and bubbledown with no duplicate priorities. */
    public void test330Poll_BubbleDown_NoDups() {
        String pollIncorrectReturn = "Poll did not return the correct value.";
        String badHeap = "Heap was incorrect after poll.";
        String badHeapCompare = "Heap was incorrect after poll when bubbleDown has to choose the correct child to swap with.";
        String badHeapOneChild = "Heap was incorrect after poll when the node has only one child.";

        Heap<Integer,Integer> mh= makeHeap(new Integer[]{5});
        Integer res= mh.poll();
        assertEquals(pollIncorrectReturn, 5, (int)res);
        checkPhase3(badHeap, new Integer[]{}, new Integer[]{}, mh);

        Heap<Integer,Integer> mh1= makeHeap(new Integer[]{5, 6});
        Integer res1= mh1.poll();
        assertEquals(pollIncorrectReturn, 5, (int)res1);
        checkPhase3(badHeap, new Integer[]{6}, new Integer[]{6}, mh1);

        // this requires comparing lchild and rchild and using lchild
        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {4, 5, 6, 7, 8, 9});
        Integer res2= mh2.poll();
        assertEquals(pollIncorrectReturn, 4, (int)res2);
        checkPhase3(badHeapCompare, new Integer[]{5, 7, 6, 9, 8}, new Integer[]{5, 7, 6, 9, 8}, mh2);

        // this requires comparing lchild and rchild and using rchild
        Heap<Integer,Integer> mh3= makeHeap(new Integer[] {4, 6, 5, 7, 8, 9});
        Integer res3= mh3.poll();
        assertEquals(pollIncorrectReturn, 4, (int)res3);
        checkPhase3(badHeapCompare, new Integer[]{5, 6, 9, 7, 8}, new Integer[]{5, 6, 9, 7, 8}, mh3);

        // this requires bubbling down when only one child
        Heap<Integer,Integer> mh4= makeHeap(new Integer[] {4, 5, 6, 7, 8});
        Integer res4= mh4.poll();
        assertEquals(pollIncorrectReturn, 4, (int)res4);
        checkPhase3(badHeapOneChild, new Integer[]{5, 7, 6, 8}, new Integer[]{5, 7, 6, 8}, mh4);

        String message = "Heap was incorrect after poll; check bubbleDown.";
        Heap<Integer,Integer> mh5= makeHeap(new Integer[] {2, 4, 3, 6, 7, 8, 9});
        Integer res5= mh5.poll();
        assertEquals(pollIncorrectReturn, 2, (int)res5);
        checkPhase3(message, new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh5);

        Heap<Integer,Integer> mh6= makeHeap(new Integer[] {2, 4, 3, 6, 7, 9, 8});
        Integer res6= mh6.poll();
        assertEquals(pollIncorrectReturn, 2, (int)res6);
        checkPhase3(message, new Integer[]{3, 4, 8, 6, 7, 9}, new Integer[]{3, 4, 8, 6, 7, 9}, mh6);

        Heap<Integer,Integer> mh7= new Heap<Integer,Integer>();
        try {
            mh7.poll();  fail("Polling an empty heap didn't throw an exception");
        } catch (NoSuchElementException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Polling an empty heap threw something other than NoSuchElementExceptionException");
        }
    }

    @Test
    /** Test bubble-up and bubble-down with duplicate priorities. */
    public void test340testDuplicatePriorities() {
        // values should not bubble up or down past ones with duplicate priorities.
        // First two check bubble up
        String message = "Attempting to add elements with duplicate priorities; make sure bubbleUp and bubbleDown don't swap elements if their priorities are equal.";
        Heap<Integer,Double> mh1= makeHeap(new Integer[] {6}, new double[] {4});
        mh1.add(5, 4.0);
        checkPhase3(message, new Integer[]{6, 5}, new Double[]{4.0, 4.0}, mh1);

        Heap<Integer,Double> mh2= makeHeap(new Integer[] {7, 6}, new double[] {4, 4});
        mh2.add(3, 4.0);
        checkPhase3(message, new Integer[]{7, 6, 3}, new Double[]{4.0, 4.0, 4.0}, mh2);

        message = "Attempting to poll elements from a heap with all equal priorities.";
        // Check bubble down
        Heap<Integer,Double> mh3= makeHeap(new Integer[] {5, 6, 7}, new double[] {4, 4, 4});
        mh3.poll();
        checkPhase3(message, new Integer[]{7, 6}, new Double[]{4.0, 4.0}, mh3);

        // Check bubble down
        Heap<Integer,Double> mh4= makeHeap(new Integer[] {5, 7, 6, 8}, new double[] {4, 4, 4, 4});
        mh4.poll();
        checkPhase3(message, new Integer[]{8, 7, 6}, new Double[]{4.0, 4.0, 4.0}, mh4);
    }

    @Test
    /** Test the contains method */
    public void test350contains() {
        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
        String message = "contains returned false for a value that is present. Contains should use the map to check if the heap contains an entry with a certain value";
        for (int i = 1; i < 10; i++) {
            assertTrue(message, mh1.contains(i));
        }
        message = "contains did not return false for a nonexistent value";
        assertFalse(message, mh1.contains(0));
        assertFalse(message, mh1.contains(11));
        assertFalse(message, mh1.contains(974));
    }


    @Test
    /** Test updatePriority. */
    public void test360ChangePriority() {
        // First three: bubble up tests
        String message = "Attempting to change priority with no swaps required";
        Heap<Integer,Integer> mh1= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh1.changePriority(5, 4);
        checkPhase3(message, new Integer[]{1, 2, 3, 5, 6, 7, 9}, new Integer[]{1, 2, 3, 4, 6, 7, 9}, mh1);

        Heap<Integer,Integer> mh2= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh2.changePriority(2, 1);
        checkPhase3(message, new Integer[]{1, 2, 3, 5, 6, 7, 9}, new Integer[]{1, 1, 3, 5, 6, 7, 9}, mh2);

        message = "Attempting to change the priority of an entry that requires bubbling up to maintain the invariant";
        Heap<Integer,Integer> mh3= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh3.changePriority(5, 1);
        checkPhase3(message, new Integer[]{1, 5, 3, 2, 6, 7, 9}, new Integer[]{1, 1, 3, 2, 6, 7, 9}, mh3);

        message = "Attempting to change the priority of an entry that requires bubbling down to maintain the invariant";
       // second three: bubble down tests
        Heap<Integer,Integer> mh4= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh4.changePriority(2, 5);
        checkPhase3(message, new Integer[]{1, 2, 3, 5, 6, 7, 9}, new Integer[]{1, 5, 3, 5, 6, 7, 9}, mh4);

        Heap<Integer,Integer> mh5= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh5.changePriority(2, 6);
        checkPhase3(message, new Integer[]{1, 5, 3, 2, 6, 7, 9}, new Integer[]{1, 5, 3, 6, 6, 7, 9}, mh5);

        Heap<Integer,Integer> mh6= makeHeap(new Integer[] {1, 2, 3, 5, 6, 7, 9});
        mh6.changePriority(1, 7);
        checkPhase3(message, new Integer[]{2, 5, 3, 1, 6, 7, 9}, new Integer[]{2, 5, 3, 7, 6, 7, 9}, mh6);

        Heap<Integer,Integer> mh7= new Heap<Integer,Integer>();
        mh7.add(5, 5);
        try {
            mh7.changePriority(6, 5);  fail("Changing priority of a nonexistent element didn't throw an exception");
        } catch (IllegalArgumentException e) {
            // This is supposed to happen
        } catch (Throwable e){
            fail("Changing priority of a nonexistent element threw something other than IllegalArgumentException");
        }
    }


    @Test
    /** Test a few calls with Strings */
    public void test370Strings() {
        Heap<String,Integer> mh= new Heap<String,Integer>();
        String message = "Testing heap operations on a heap with String values and Integer priorities (Heap<String,Integer>)";
        checkPhase3(message, new String[]{}, new Integer[]{}, mh);
        mh.add("abc", 5);
        checkPhase3(message, new String[]{"abc"}, new Integer[]{5}, mh);
        mh.add("beep", 3);
        checkPhase3(message, new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);
        mh.add("", 2);
        checkPhase3(message, new String[]{"", "abc", "beep"}, new Integer[]{2, 5, 3}, mh);
        String p= mh.poll();
        assertEquals("Poll did not return the correct value in a Heap<String,Integer>", "", p);
        checkPhase3(message, new String[]{"beep", "abc"}, new Integer[]{3, 5}, mh);

        assertTrue("contains did not return true when called with a string value that exists in the heap", mh.contains("beep"));
        assertFalse("contains did not return false when called with a string value that doesn't exist in the heap", mh.contains("boop"));
    }

    @Test
    /** Test using values in 0..999 and random values for the priorities.
     *  There will be duplicate priorities. */
    public void test390BigTests() {
        // The values to put in Heap
        int[] b= new int[1000];
        for (int k= 0; k < b.length; k= k+1) {
            b[k]= k;
        }

        Random rand= new Random(52);

        // bp: priorities of the values
        double[] bp= new double[b.length];
        for (int k= 0; k < bp.length; k= k+1) {
            bp[k]= (int)(rand.nextDouble()*bp.length);
        }

        // Build the Heap and map to be able to get priorities easily
        Heap<Integer,Double> mh= new Heap<Integer,Double>();
        HashMap<Integer, Double> hashMap= new HashMap<Integer, Double>();
        for (int k= 0; k < b.length; k= k+1) {
            mh.add(b[k], bp[k]);
            hashMap.put(b[k], bp[k]);
        }

        // poll values one by one, check that priorities are in order, store
        // in dups the number of duplicate priorities, and save polled value
        // in array bpoll.
        double prevPriority= -1;
        int dups= 0; //Number of duplicate keys,
        int[] bpoll= new int[b.length];
        for (int k= 0; k < b.length; k= k+1) {
            bpoll[k]= mh.poll();
            Double p= hashMap.get(bpoll[k]);
            if (p == prevPriority) {
                dups= dups + 1;
            }
            assertEquals("In a larger-scale test, the heap order property was violated after calls to both add and poll.", true, prevPriority <= p);
            prevPriority= p;
        }

        // Sort bpoll and check that it contains 0..bpoll.length-1
        Arrays.sort(bpoll);
        for (int k= 0; k < b.length; k= k+1) {
            assertEquals("In a larger-scale test, the heap did not have the correct elements.", k, bpoll[k]);
        }
        // System.out.println("duplicate priorities: " + dups);
    }

}
