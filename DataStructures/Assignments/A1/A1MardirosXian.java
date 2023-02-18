/**
 * SortTest
 *
 * COMP 2140 SECTION A01
 * INSTRUCTOR   Cameron
 * ASSIGNMENT   Assingment 1
 * @author      Xian Mardiros 7862786
 * @version     Oct 2, 2019
 *
 * PURPOSE:     to test demostrate knowledge of sorting algorithms and determine the relative
 *              speeds of each sort
 */
public class A1MardirosXian{

    private static int BREAKPOINT = 5;

    public static void main(String[] args){

        System.out.println("Begin processing.\n");
        compareSorts();
        System.out.println("\n\nEnd of Processing.");
    }

    // question 1
    //
    // Iterate array from start to end, at each new item compare it in
    // reverse order to the previously sorted items,
    // shifting each over by one, until the correct position is found
    private static void insertionSort(int[] array, int start, int end){

        for(int i = start+1; i < end; i++){

            int curr = array[i];                // store current item to be sorted
            int j = i-1;                        // start by comparing to prev index value

            // iterate backwards over array until the end is reached or correct position found
            while(j>=0 && array[j] > curr){
                array[j+1] = array[j];          // shift each element over by one
                j--;
            }

            array[j+1] = curr;
        }
    }

    // driver method for isertionSort; passes array and the start and end indices
    public static void insertionSort(int[] array){
        insertionSort(array, 0, array.length);
    }

    // question 2
    //
    // iterates over array until the minimum value is found
    private static int findMin(int[] array, int start, int end){
        int min = start;

        for(int i=start+1; i<end; i++){
            if(array[i] < array[min]){
                min = i;
            }
        }
        return min;
    }

    // iterates over the array, for each item, finds the next minimum, and switches that
    // minimum with the current element
    public static void selectionSort(int[] array){
        int end = array.length;

        for(int i=0; i<end-1; i++){

            int curr = array[i];
            int min = findMin(array, i, end);

            array[i] = array[min];
            array[min] = curr;
        }
    }

    // question 3
    //
    // driver method for merge sort; passes array range and a new temp array
    public static void mergeSort(int[] array){
        mergeSort(array, 0, array.length, new int[array.length]);
    }

    // sort the array by recursively sorting each half, each sorted half will be merged together
    // into the temp array, and then copied back
    private static void mergeSort(int[] array, int start, int end, int[] temp){

        int mid = start + (end - start)/2;

        if(end - start == 1){
            // check if the range includes only 2 elements, swap them if necessary
            conditionalSwapTwo(array, start, end-1);
        }else if(end - start > 1){
            mergeSort(array, start, mid, temp);
            mergeSort(array, mid, end, temp);
            merge(array, start, mid, end, temp);
        }
        // don't need to check for (end - start == 1) case since the singular element will
        // be in the correct position already
    }

    // from each of the two sorted halves, sequentially copy the lowest values between the
    // two halves into the temp array, and then copy them back once merged
    private static void merge(int[] array, int start, int mid, int end, int[] temp){

        int next1 = start;
        int next2 = mid;
        int index = start;

        for(int i=start; i<end; i++){
            // Not sure if this is the best way to control here
            // iterate over temp array, checking if the end of each sorted half has been reached
            if( (next1 != mid) && ( (next2 == end) || (array[next1] < array[next2]) ) ){
                temp[i] = array[next1];
                next1++;
            }else{
                temp[i] = array[next2];
                next2++;
            }
        }
        //while(next1 < mid || next2<end){
        //    if( (array[next1] < array[next2]) && next1 < mid){
        //        temp[index] = array[next1];
        //        next1++;
        //    }else{
        //        temp[index] = array[next2];
        //        next2++;
        //    }
        //    index++;
        //}

        // copy data from temp back to array
        for(int i = start; i<end; i++){
            array[i] = temp[i];
        }
    }

    // used in questions 3 and 4, swaps two array elements if necessary
    private static void conditionalSwapTwo(int[] array, int first, int second){
        if(array[second] < array[first]){
            swapTwo(array, first, second);
        }
    }

    // swaps two elements of an array
    private static void swapTwo(int[] array, int first, int second){
        int tempValue = array[second];

        array[second] = array[first];
        array[first] = tempValue;
    }

    // question 4
    //
    // driver method for quicksort
    public static void quickSort(int[] array){
        quickSort(array, 0, array.length);
    }

    // chooses pivot for the array range, partitions the array range according to the pivot,
    // then recursively quickSorts each partitions, until the base cases are reached
    private static void quickSort(int[] array, int start, int end){
        int mid = start + (end - start)/2;

        if(end - start == 2){
            conditionalSwapTwo(array, start, end-1);
        }else if(end - start > 1){
            choosePivot(array, start, mid, end);
            int pivotPosition = partition(array, start, end);

            quickSort(array, start, pivotPosition);
            quickSort(array, pivotPosition+1, end);
        }
    }

    // creates an array with elements start, mid, and end, and shifts each element over
    // until the middle element is the median, then switches that element to the start
    // of the parameter array
    private static void choosePivot(int[] array, int start, int mid, int end){
        int[] findPivot = {start, mid, end-1};

        //System.out.println( "\n" );
        while( !( (array[findPivot[0]] < array[findPivot[1]] &&
                   array[findPivot[1]] < array[findPivot[2]]) ||
                  (array[findPivot[0]] > array[findPivot[1]] &&
                   array[findPivot[1]] > array[findPivot[2]]) ) ){
            // System.out.println( "\nindex 1: " + findPivot[0] + ", index 2: " + findPivot[1] + ", index 3: " + findPivot[2] );
            // System.out.println( "element 1: " + array[findPivot[0]] + ", element 2: " + array[findPivot[1]] + ", element 3: " + array[findPivot[2]] );
            int temp = findPivot[0];
            findPivot[0] = findPivot[1];
            findPivot[1] = findPivot[2];
            findPivot[2] = temp;
        }
        swapTwo(array, start, findPivot[1]);
    }

    // splits array into above-pivot and below-pivot by iterating across array, for each element,
    // if the element is smaller than the pivot, then the element is switched with the first
    // element in the array which is larger than the array; finally the pivot is switched with
    // the last occuring below-pivot element
    private static int partition(int[] array, int start, int end){
        //System.out.println( "partition" );
        int firstUnsorted = start + 1;
        int lastUnsorted = end - 1;
        int pivot = array[start];
        int bigStart = start+1;

        for(int i=start+1; i<end; i++){
            if(array[i] < pivot){
                swapTwo(array, bigStart, i);
                bigStart++;
            }
        }

        int pivotPosition = bigStart-1; // to make more clear what partition returns
        swapTwo(array, pivotPosition, start);

        return pivotPosition;
    }

    // question 5
    //
    // perform insertion sort if range is less than breakpoint,
    // otherwise do recursive hybrid quick sort
    private static void hybridQuickSort(int[] array, int start, int end){

        if( (end - start) < BREAKPOINT){
            insertionSort(array, start, end);
        }else if( (end - start) == 2 ){
            conditionalSwapTwo(array, start, end-1);
        }else if(end - start > 1){
            int mid = start + (end - start)/2;
            choosePivot(array, start, mid, end);
            int pivotPosition = partition(array, start, end);

            hybridQuickSort(array, start, pivotPosition);
            hybridQuickSort(array, pivotPosition+1, end);
        }
    }

    // driver method for hybrid quick sort
    public static void hybridQuickSort(int[] array){
        hybridQuickSort(array, 0, array.length);
    }

    // question 6
    //
    // checks that each item in the array is not larger than the next item
    public static boolean sortVerify(int[] array){
        boolean sorted = true;

        for(int i=1; i<array.length; i++){
            if(array[i] < array[i-1]){
                sorted = false;
            }
        }
        return sorted;
    }

    // question 7
    //
    // fills an array with numbers in order
    public static void arrayFill(int[] array){
        for(int i=0; i<array.length; i++){
            array[i] = i;
        }
    }

    // question 8
    //
    // selects two random positions in the array and swaps them, n times
    public static void arrayRandomize(int[] array, int n){
        int positionA;
        int positionB;

        for(int i=0; i<n; i++){
            positionA = (int) (Math.random()*array.length);
            positionB = (int) (Math.random()*array.length);

            swapTwo(array, positionA, positionB);
        }
    }

    // question 9
    //
    // find the means of the sort times of 100 sorts of each type
    public static void compareSorts(){
        int SORT_NUMBER = 100;
        int ARRAY_ITEMS = 10000;
        int SWAP_NUMBER = 5000;
        int SORTS = 5;
        int INSERTION_INDEX = 0;
        int SELECTION_INDEX = 1;
        int MERGE_INDEX = 2;
        int QUICK_INDEX = 3;
        int HYBRID_INDEX = 4;
        int[] array = new int[ARRAY_ITEMS];
        long[][] times = new long[SORTS][SORT_NUMBER];

        arrayFill(array);
        chooseBreakpoint(array);

        System.out.println("Test insertion sort");
        for(int i=0; i<SORT_NUMBER; i++){
            arrayRandomize(array, SWAP_NUMBER);

            long start = System.nanoTime();
            insertionSort(array);
            long stop = System.nanoTime();
            long result = start - stop;
            times[INSERTION_INDEX][i] = result;

            if(!sortVerify(array)){
                System.out.println("Insertion sort FAILED, array not sorted");
            }
        }

        // Is it possible to pass a funcion as a parameter?
        // this would save a lot of wasted space
        System.out.println("Test selection sort");
        for(int i=0; i<SORT_NUMBER; i++){
            arrayRandomize(array, SWAP_NUMBER);

            long start = System.nanoTime();
            selectionSort(array);
            long stop = System.nanoTime();
            long result = start - stop;
            times[SELECTION_INDEX][i] = result;

            if(!sortVerify(array)){
                System.out.println("Selection sort FAILED, array not sorted");
            }
        }

        System.out.println("Test merge sort");
        for(int i=0; i<SORT_NUMBER; i++){
            arrayRandomize(array, SWAP_NUMBER);

            long start = System.nanoTime();
            mergeSort(array);
            long stop = System.nanoTime();
            long result = start - stop;
            times[MERGE_INDEX][i] = result;

            if(!sortVerify(array)){
                System.out.println("Merge sort FAILED, array not sorted");
            }
        }

        System.out.println("Test quick sort");
        for(int i=0; i<SORT_NUMBER; i++){
            arrayRandomize(array, SWAP_NUMBER);

            long start = System.nanoTime();
            quickSort(array);
            long stop = System.nanoTime();
            long result = start - stop;
            times[QUICK_INDEX][i] = result;

            if(!sortVerify(array)){
                System.out.println("Quick sort FAILED, array not sorted");
            }
        }

        System.out.println("Test hybrid quick sort");
        for(int i=0; i<SORT_NUMBER; i++){
            arrayRandomize(array, SWAP_NUMBER);

            long start = System.nanoTime();
            hybridQuickSort(array);
            long stop = System.nanoTime();
            long result = start - stop;
            times[HYBRID_INDEX][i] = result;

            if(!sortVerify(array)){
                System.out.println("Hybrid quick sort FAILED, array not sorted");
            }
        }

        double insertionSortMean = arithmeticMean(times[INSERTION_INDEX]);
        double selectionSortMean = arithmeticMean(times[SELECTION_INDEX]);
        double mergeSortMean = arithmeticMean(times[MERGE_INDEX]);
        double quickSortMean = arithmeticMean(times[QUICK_INDEX]);
        double hybridQuickSortMean = arithmeticMean(times[HYBRID_INDEX]);

        System.out.println("Average sorting times:\nInsertion Sort: " + insertionSortMean +
                           "\nSelection Sort: " + selectionSortMean + "\nMerge Sort: " +
                           mergeSortMean + "\nQuick Sort: " + quickSortMean +
                           "\nHybrid Quick Sort: " + hybridQuickSortMean);
    }


    // question 10
    //
    // Find the breakpoint between 25 and 100 which will result in the fastest hybrid quick sort
    // iterate from 25 to 100 in increments of 5, perform 100 quick sorts for each breakpoint
    // then find the mean of each 100 sorts. Set BREAKPOINT to the value which gave the best mean
    public static void chooseBreakpoint(int[] array){
        int fastest = BREAKPOINT;
        int BREAKPOINT_INIT = 25;
        int BREAKPOINT_MAX = 100;
        int BREAKPOINT_INCREMENT = 5;
        int SORTS = 100;
        int RANDOMIZE = 5000;
        double fastestTime = 0.0;
        long[] times = new long[SORTS];

        for(int i=BREAKPOINT_INIT; i<=BREAKPOINT_MAX; i+=BREAKPOINT_INCREMENT){
            System.out.println( "testing breakpoint " + i );
            BREAKPOINT = i;

            for(int j=0; j<SORTS; j++){
                arrayRandomize(array, RANDOMIZE);

                long start = System.nanoTime();
                hybridQuickSort(array);
                sortVerify(array);
                long stop = System.nanoTime();
                long result = stop - start;
                times[j] = result;
            }

            double mean = arithmeticMean(times);
            if(mean < fastestTime || fastestTime == 0.0){
                fastestTime = mean;
                fastest = BREAKPOINT;
            }
        }
        BREAKPOINT = fastest;
        System.out.println("The breakpoint which results in the fastest hybrid quick sort is " +
                            BREAKPOINT);
    }

    // question 11
    //
    // Find arithmetic mean of elements in an array
    private static double arithmeticMean(long[] array){
        double sum = 0;
        int len = array.length;
        for(int i=0; i<len; i++){
            sum += (double)array[i];
        }
        return sum/(double)len;
    }
}

// Report
//
//  1)  Insertion sort was several times faster than selection sort. This is because the time complexity
//      of selection sort will always be n*(n-1)/2, whereas this amount is only the worst-case complexity
//      of insertion sort. On average, insertion sort will be better than its worst-case, and so better
//      than selection sort.
//
//  2)  Quick sort was much faster than insertion sort, because the time complexity of quick sort is
//      n*log(n), which is much faster than that of insertion sort
//
//  3)  The hybrid quick sort was faster than quick sort. This was probably due to insertion sort being
//      faster when n is lower. At low n, the "-1" and "/2" in n*(n-1)/2 will have a greater effect. The
//      other non-principle operations that are part of quick sort may also have a larger effect at low n
//
//  4)  Which sort to recommend depends entirely on what the sort will be used for. Quick sort isn't
//      faster when dealing with sorted or nearly sorted lists. If the user will be attempting to keep
//      already sorted lists sorted, then insertion sort would be better. If the lists to be sorted will
//      be mostly randomized, then the hybrid quick sort would be better.
//
//  5)  I would warn others away from using selection sort, as it is the slowest algorithm tested here.
