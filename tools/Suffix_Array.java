/* 
   O(n log^2(n) algorithm to compute the suffix array of a string based on
   Karp-Rabin fingerprinting.

   D. Sleator   Dec 4, 2012

*/
import java.io.*;
import java.util.*;

public class Suffix_Array {
    static final long P = 1000000007;
    static long[] p;  // p[i] = P^i modulo 2^64
    static long[] a;  // a[i] = s[i-1]*p[0] + s[i-2]*p[1] + ... + s[0]*p[i-1]
    static char[] s;
    static int n;

    static long hh(int x, int y) {
	/* Assumes x<=y.  Let k = y-x.  This function returns
	 * s[x]*p[k] + s[x+1]*p[k-1] + ... + s[y]*p[0].
	 * In other words, it's the hash function from x to y inclusive
	 */
        return a[y+1]-a[x]*p[y-x+1];
    }

    static int pre_len;  /* a side effect of comp, which is the common prefix
			    length of the two strings just compared */

    static int comp (int x, int y) {
	/* Compare the two strings which are the suffix of s beginning
	 * at x and beginning at y.  Return <0, 0, or >0 depending
	 * on the outcome. (Actually in this context they can't be equal.)
	 */
        int R = Math.min(n-x-1,n-y-1); 
        int L=0;
        while(L<R) { 
		    /* Loop invariant:  
		     * these two strings are equal: x[0..L-1], y[0..L-1]
		     * these two strings are not equal x[0..R], y[0..R]
		     */
		    int M=(L+R+1)/2;
		    if (hh(x,x+M-1) == hh(y,y+M-1)) L=M; else R=M-1;
		}
		pre_len = R;
        return s[x+R] - s[y+R];
    }

    public static void main(String[] args) {
	if (args.length <= 0) {
	    System.out.printf("Supply a string\n");
	    System.exit(1);
	}
	String input = args[0] + "\0";
	s = input.toCharArray();
	n = s.length;

	p = new long[n+1];
	a = new long[n+1];

	/* precompute p[] and a[] to make hh() work in O(1) time */
        p[0]=1;
	for(int i=1; i<=n; i++) p[i] = p[i-1] * P;
        for(int i=1; i<=n; i++) a[i]=a[i-1]*P+s[i-1];

	Integer[] perm = new Integer[n];
	for (int i=0; i<n; i++) perm[i] = i;

	Arrays.sort(perm, new Comparator<Integer>() {
		public int compare(Integer A, Integer B) {return comp(A,B);}
	    });

	int[] prefix = new int[n-1];
	for (int i=0; i<n-1; i++) {
	    comp(perm[i],perm[i+1]);
	    prefix[i] = pre_len;
	}

	System.out.printf("Suffix Array: ");
	for(int i=0; i<n; i++) {
	    System.out.printf("%d ", perm[i]);
	}
	System.out.println();

	System.out.printf("Common Prefix Lengths: ");
	for(int i=0; i<n-1; i++) {
	    System.out.printf("%d ", prefix[i]);
	}
	System.out.println();

    }
}