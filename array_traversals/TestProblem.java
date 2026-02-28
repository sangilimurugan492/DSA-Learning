package array_traversals;

public class TestProblem {

    public static void main(String[] args) {
//        System.out.println(smallLengthSubarraySum(new int[]{4,1,5,2,4,1}, 6, 7));
        System.out.println(removeElement(new int[]{10, 20, 30, 10, 10}, 5, 10));
    }

    /**
     * To find the Smallest length sub array
     * @param arr - Input Array
     * @param n - Length of Array
     * @param S - Target Value
     * @return smallest Length
     */
    private static int smallLengthSubarraySum(int[] arr, int n, int S)
    {
        int len = Integer.MAX_VALUE;
        int wStart = 0;
        int subSum = 0;

        for(int i =0; i < n; i++) {
            subSum += arr[i];
            while (subSum >= S) {
                int winSize = i - wStart + 1;
                if (winSize < len) {
                    len = winSize;
                }
                subSum -= arr[wStart];
                wStart++;
            }
        }


        return len == Integer.MAX_VALUE ? 0 : len;

    }

    static int removeElement(int arr[], int n, int val)
    {
        int start = 0;
        int end = n - 1;
        int count = 0;

        while(start < end) {
            if (arr[end] == val) {
                end --;
            } else if (arr[start] == val) {
               int temp = arr[start];
               arr[start] = arr[end];
               arr[end] = temp;
            } else {
                count++;
                start++;
            }
        }
        return count;
    }
}
