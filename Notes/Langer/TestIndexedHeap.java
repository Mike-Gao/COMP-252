package a1posted; //  change this to a1posted
import java.util.ArrayList;
import java.util.Random;

public class TestIndexedHeap {
	
	/*   
	 *  Add a list of names (v_i) and associated random priorities into the heap.
	 *  Then change the priority of each v_i so that the ith random element has priority i.
	 *  Each such change could rearrange the elements to preserve the heap property.
	 *  Then remove the elements one by one from the heap.  They should be removed in sorted order,
	 *  and, because of how we changed the priorities, we should have (v_i, i) for the names and 
	 *  priorities. 
	 *  
	 */  

	public static void main(String[] args) {
		
		Random generator = new Random();
		double d;

		//   Here the names of the elements will be of type String. 
		//   I am naming the heap "pq" just as a reminder what the heap is for. 
		
		IndexedHeap  pq = new IndexedHeap();

		/*    Now add some elements into the heap.
		 *    The name of element i is v_i.  For example, these might be the names of
		 *    vertices in a graph.   The priority is a random number.   Adding a sequence
		 *    of them should produce a heap. 
		 */

		int numElements = 200;
		for (int i=1; i <= numElements; i++){
			
			//  give this element a random priority
			
			d = generator.nextDouble();
			pq.add( "v_" + new Integer(i).toString() , d);
		}

		/*
		 *  Change the priority so that the ith random element has priority i.
		 */
		
		for (int i=1; i <= numElements; i++)
			pq.changePriority("v_" + new Integer(i).toString(), i*1.0);

		System.out.println("\nRemoving all the elements (in order of priority) ");

		/*   Remove all elements from the priority queue.  v_i should have priority i.
		 * 	 They should print out in order of priority,  (v_1, 1.0),  (v_2, 2.0), etc.
		 * 	 Notice we can use an array now since we know the number of elements in the heap.
		 */

		String    names[]      = new String[numElements]; 
		double    priorities[] = new double[numElements]; 

		for (int i=0; i < numElements; i++){
			priorities[i] = pq.getMinPriority();			
			names[i] = pq.removeMin();
		}

		for (int i=0; i < numElements; i++){
			System.out.println( String.valueOf(names[i]) + " " + String.valueOf(priorities[i])    );
		}
		
		
		/*
         *  Here is a little test to ensure that the namesToIndex map was updated correctly during removeMin().
         *  After the first three adds,  the heap should be [*,  a,  c, b] and namesToIndex is {a:1, b:3, c:2}
         *  We then removeMin which removes the a and the heap becomes [*, b, c] and namesToIndex is {b:1, c:2}
         *  If, however, namesToIndex was not updated by removeMin,  then namesToIndex will stay as {b:3, c:2}
         *  We then add "d" and the heap becomes [*, b, c, d] and namesToIndex is {b:3, c:2, d:3} and now you
         *  can see there is a problem, namely both b and d point to the same slot.   In particular, nameToIndex's
         *  mapping of b is wrong.   So when we change the priority of b, in fact we will change the priority of d
         *  which leads to an error.
         *
         */
		System.out.println("\nA second test:  please see comments in test code for explanation.\n");
		System.out.println("The correct output should be:\n1.0 a\n3.0 c\n4.0 d\n5.0 b\n\n Your output:");
        pq = new IndexedHeap();
        pq.add( "a" , 1.0);
        pq.add( "c" , 3.0);
        pq.add( "b" , 2.0);
        System.out.println( String.valueOf(pq.getMinPriority())  + " " +  String.valueOf(pq.removeMin())  );
        pq.add("d",4.0);
        pq.changePriority("b" , 5.0 );
        int i = 0;
        while ( !(pq.isEmpty()) && i < 100){
            System.out.println( String.valueOf(pq.getMinPriority())  + " " + String.valueOf(pq.removeMin())  );
            i++;
        }
	}

}
