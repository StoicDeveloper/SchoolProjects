public class Cammat{

    public static void main(String[] args){
        int N = Integer.parseInt(args[0]);
        long start = System.nanoTime();
        long result = recursiveCammat(N);
        long stop = System.nanoTime();
        long elapsedTime = stop - start;
        printReport( "Report on the nonrecursiveCammat method:",
                N, result, elapsedTime );

        // Test the recursive cammat method
        start = System.nanoTime();
        result = nonrecursiveCammat(N);
        stop = System.nanoTime();
        elapsedTime = stop - start;
        printReport( "Report on the recursiveCammat method:",
                 N, result, elapsedTime );

    } // end method testCammatMethods

    // Print out a header specifying the method used to compute C(n),
    // then on the next line, print out n, C(n), and the time taken to
    // compute n using the method.
    private static void printReport( String methodUsed,
				     int n,
				     long result,
				     long elapsedTime ) {
	System.out.println( methodUsed + "\n" );
	System.out.println( "Time needed to compute C( " + n
			    + " ): " + elapsedTime + " nanoseconds");
	System.out.println( "C( " + n + " ) = " + result + "\n" );

    } // end method printReport


    // Recursively compute C(n),
    // where C(0) = 1 and C(1) = 2 and C(2) = 3 (these are the base cases)
    // and, when n > 2, C(n) is computed using the following formula
    // C(n) = C(n-1) + sum of ((-1)^k * C((n-2)-2*k)) for k = 0, 1, ..., (n-2)/2.
    //
    // Note that when n > 2,https://stackoverflow.com/questions/26620388/c-substrings-c-string-slicing
    // 1) C(n-1) and each C((n-2)-2*k) term on the right side of the equals sign
    //     is computed with a recursive call; and
    // 2) (-1)^k is 1 when k=0 and alternates between -1 and 1 as k increases.
    public static long recursiveCammat( int n ) {

        long cammat = 0;
        if(n > 2){
            long sum = 0;
            for(int k = 0; k <= (n-2)/2; k++){
                sum += Math.pow(-1, k) * recursiveCammat(n-2-2*k);
            }
            cammat += recursiveCammat(n-1) + sum;
        }else{
            cammat = n+1;
        }
        return cammat;
	// ********** REPLACE THIS COMMENT WITH YOUR CODE **************

    } // end method recursiveCammat

    // Compute C(n) without recursion,
    // where C(0) = 1 and C(1) = 2 and C(2) = 3
    // and, when n > 2, C(n) is computed using the following formula
    // C(n) = C(n-1) + sum of ((-1)^k * C((n-1)-2*k)) for k = 0, 1, ..., (n-1)/2.
    //
    // Idea:
    // Store C(0), C(1), C(2) in an array.
    // Then compute C(3), C(4), ..., C(n) in turn, storing each in the array.
    // When we're computing C(i), we look up in the array the values of
    // C(i-1) and C((i-2)-2*k) on the right side of the formula --- they
    // will already have been stored in the array by the time we are
    // computing C(i).
    public static long nonrecursiveCammat( int n ) {
        long[] array = new long[n+1];
        int i=0;
        while(i<3 && i<array.length){
            array[i] = (long) ++i;
        }
        for(i=i; i<array.length; i++){
            long sum = 0;
            for(int k=0; k<=(i-2)/2; k++){
                sum += Math.pow(-1, k)*array[i-2-2*k];
            }
            array[i] = array[i-1] + sum;
        }
        return array[n];

    } // end method nonrecursiveCammat

} // end class Cammat
