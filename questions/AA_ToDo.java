package sprax.questions;


/** NIECE: No Input Error Checking/Exceptions */


/**
 * TODO:
 * re-factor Poppers and add ML at least for guiding heuristic search
 * Family Trees
 * Map problem: where to hold a reunion to minimize collective cost.
 * LanguageId from ML  supervised learning
 * Remember Bhattacharyya distance from pattern classification?
 * 
 * random stream<T> from array<T>
 * linked-hash-set for max-N counts?    backed by balanced tree?
 * 
 * find duplicate lines or sentences (with fuzzy strings/fuzzy sets)
 * 
 * Given an array of length N and an integer K, sort the array as much as possible
 * such that no element travels more than k positions to its left - an element 
 * however can travel as much as it likes to its right.
 * 
 * DONE:
 * TrafficLight
 * TrafficLights
 * Elevator
 * Elevators
 * PoppersSolver
 * TimeSinceLastQuery[C, Java, Python] => function that returns true if it not returned n trues in last k seconds in O(1) time
 * 
 * @author Sprax Lines
 */
public class AA_ToDo {
    
    
    /**
     * You are given the toplogical information of a terrain in the following format - There are n points ( x_i , y_i ) and for each point (x_i , y_i ) the altitude h_i is given. 

For any rectangle (axis parallel) defined by the x-y coordinates of 
the corner points, we must answer the query about which is the highest altitude point lying within the rectangle. 

Implement this using a range-query data-structure that answers such a 
query in O( log^2 n) time


     */

/*
A white cell has been given. Every second, the white cell gets divided into 
9 parts in which middle one is black and all other remains as white. Black
 cell (if any) also gets divided into 9 cells which are all black. Given 
 time passed, x(horizontal) and y(vertical) indices of the cell, find the 
 color of the cell. Ex: time passed as 1 and x,y(index starts from 0) as 2,2, 
 color is white. For time passed as 2, x,y as 4,1, color is black.


You are given a tree (an undirected acyclic connected graph) with N nodes, and edges numbered 1, 2,
3...N-1. Each edge has an integer value assigned to it, representing its length.
We will ask you to perform some instructions of the following form:
DIST a b : ask for the distance between node a and node b
or KTH a b k : ask for the k-th node on the path from node a to node b.


Q: How will you make your own Hashmap class?
A: Use open addressing


Q: Given a number, come up with all of the possible ways to insert '+' and '-'
 in that number. for example given 123, possible answer would be

1+23
1+2+3
1-23
1-2+3
1-2-3
1+2-3
12+3
12-3
A: Sprax:  N digits  X combos  namely
            1         1         1
            2         3         12  1+2 1-2
            3         9         123 12+3 12-3  1+23 1+2+3 1+2-3  1-23 1-2+3 1-2-3
            which is just: {12  1+2 1-2}3 and {12  1+2 1-2}+3 and {12  1+2 1-2}-3
That is, for each combo that existed before adding another digit, there
are 3 new combos:  to each existing combo, append the new digit after a +, a -, or neither.
So the number of combos is X = 3^(N-1), or 3 raised to the (N - 1)th power.  
If you don't recognize 0 as a valid count of +'s and -'s, then X = 3^(N-1) - 1, but that's dumb.
            

Given an array containing elements 1..N but containing repeated elements.we have to find the missing element..
No extra spaces allowed and time complexity should be O(n).

Given a binary tree we have to update another pointer sibling in the node such that every 
node sibling is the left node of current node.  If there is no left node then its sibling 
should point to the right most node at that level...!!


Given an array of elements and an integer x, rearrange the array 
such that all the elements greater than or equal to x should be
 in descending order and all the elements less than x should be
 in ascending order in most efficient manner.

Sort the array using quick sort in descending order
Find the index of key element in the array
Reverse the array from key elements' index + 1 element to the last element to get the ascending order
Anonymous on September 12, 2011 |Edit | Edit

The second step has to take possible multi appearance of the key into account.
Reply to Comment


O(NlogN) algo.

-> Write your partition function with pivot as x( x may not be present in the array)

-> Partition function returns index k such that all elements in [0,k] are less than 'x' and all elements in range [k+1, N-1] are greater than 'x' .

->Now sort [0,k] range in ascending and [k+1, N-1] in descending order using Quick Sort or some other O(NlogN) algo.
Reply to Comment
0
of 0 vote
Anonymous on September 15, 2011 |Edit | Edit

Question doesn't asks you to have array as "all less elements followed by greater elements". 
It may be mix as well but order for less and greater elements has to be maintained among themselves.
Reply to Comment
0
of 0 vote
Anonymous on September 21, 2011 |Edit | Edit

Quick sort and then take i+1 to n elements and reverse them. Here i is the index for the given element.
 */
    
    /*
 // It's also possible the number of removals may exceed the maximum
 // expression count allowed by oracle.
 // The number of property values governs the number of expressions emitted.
 while (endNdx < propArray.length){
   // the deletion is done from startNdx up to but not including endNdx
   endNdx = (endNdx+mMaximumExpressions < propArray.length)
     ? (endNdx+mMaximumExpressions) : propArray.length;
   result += deleteRange(pItemId, pProperty, propArray, startNdx, endNdx, pConnection);
   startNdx = endNdx;
 }


 There are three arrays of numbers A,B and C.
 You have to find out all tuples <a,b,c> such that
  a-b = c where a is from A,b is from B and c is from C.
 16
 Country: India
 Interview Type: In-Person
 Tags: Microsoft � Algorithm  � Software Engineer / Developer
 Question #11446728 (Report Dup) | Edit | History


 1
 of 1 vote
 sonali.kapor007 on November 14, 2011 |Edit | Edit

 yes I said that. But asked to solve it without hashing.
 eugene.yarovoi on November 15, 2011 |Edit | Edit

 You could have gotten O(N^2) with hashing. You can get O(N^2 log N) by producing
  a sorted version of the result array and doing binary searches 
  on it (O(N^2) searches, O(log N) each).
 Reply to Comment
 0
 of 0 vote
 Jeff on November 15, 2011 |Edit | Edit

 The question didn't say a,b,c has to be have same index, which mean it can be any combination. 
 So your merging from B and C to D will take n^2 time, and D array will be size of B*C. I don't think this is appropriate.
 Reply to Comment
 0
 of 0 vote
 Anonymous on November 15, 2011 |Edit | Edit

 @Puru:
 There is no need to merge it. Just sort B & C: (Both A & B are in ascending order now).

 Now for every a belongs to A, search element say b in B and say c in C whose some is equal to a.

 keep in mind that start the search from End of B and C and see if the sum of b & c is greater than a then fall back to element that just smaller element from just previous elements in B & C.

 O(n) = nlogn+n*n
 Reply to Comment
 0
 of 0 vote
 kamal on November 15, 2011 |Edit | Edit

 i think we cant merge nay of the arrays becoz a b and c has to be from different array , and if we merge the we 2 number could be from same array.

 n^3 and n^2logn solution is obvious.
 Reply to Comment
 0


 i think instead of adding b+c to create d, we can create array d=a-b.. thus saving some ammount of memory.
 Reply to Comment
 0
 of 0 vote
 encap on November 15, 2011 |Edit | Edit

 i think instead of adding b+c to create d, we can create array d=a-b.. thus saving some ammount of memory.


 what if we do not need to merge the sorted arrays ?
 that is, sort B and C, then for each element of A check if A[i] == B[j] + C[k]
 using standard approach. Complexity: O(n^2)


 void find_sum(int *a, int *b, int *c, int n) {
     // check if a == b + c
     for(int i = 0; i < n; i++) {
         int x = a[i];
         int l = 0, r = n - 1;
         for(; ;) {
             int s = b[l] + c[r];
             if(s == x) {
                 printf("%d = %d + %d\n", x, b[l], c[r]);
                 l++;
             } else if(s < x) {
                 l++;
             } else
                 r--;
             if(l == n || r == -1)
                 break;
         }
     }
     printf("brute force check:\n");
     for(int i = 0; i < n; i++) {
         int x = a[i];
         for(int j = 0; j < n; j++) {
             for(int k = 0; k < n; k++) {
                 if(x == b[j] + c[k]) {
                     printf("all: %d = %d + %d\n", x, b[j], c[k]);
                 }
             }
         }
     }
 }



 int main() {
 int a[] = {8, 9, 10, 11, 20, 22};
 int b[] = {1, 5, 6, 6, 9, 11};
 int c[] = {3, 4, 7, 11, 12, 15};
 int n = sizeof(a) / sizeof(int);


 find_sum(a, b, c, n);
 return 1;
 }

 output:
 8 = 1 + 7
 8 = 5 + 3
 9 = 5 + 4
 9 = 6 + 3
 9 = 6 + 3
 10 = 6 + 4
 10 = 6 + 4
 20 = 5 + 15
 20 = 9 + 11
 22 = 11 + 11
 brute force check:
 all: 8 = 1 + 7
 all: 8 = 5 + 3
 all: 9 = 5 + 4
 all: 9 = 6 + 3
 all: 9 = 6 + 3
 all: 10 = 6 + 4
 all: 10 = 6 + 4
 all: 20 = 5 + 15
 all: 20 = 9 + 11
 all: 22 = 11 + 11
 Anonymous on November 17, 2011 |Edit | Edit

 small correction. the inner loop should be:


   for(; ;) {
             int s = b[l] + c[r];
             if(s == x) {
                 printf("%d = %d + %d\n", x, b[l], c[r]);
                 if(l < n - 1 && b[l] == b[l + 1])
                     l++;
                 else 
                     r--;
             } else if(s < x) {
                 l++;
             } else
                 r--;
             if(l == n || r == -1)
                 break;
         }

 to handle duplicates properly
 */
    
}
