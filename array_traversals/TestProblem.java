package array_traversals;

public class TestProblem {

    public static void main(String[] args) {
//        System.out.println(smallLengthSubarraySum(new int[]{4,1,5,2,4,1}, 6, 7));
//        System.out.println(removeElement(new int[]{10, 20, 30, 10, 10}, 5, 10));
//        System.out.println(duplicateNumber(new int[]{3, 4, 2, 1, 2}, 5));
        System.out.println(missingNumber(new int[]{3,5,1,4,0}, 5));
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

    private static int duplicateNumber(int[] arr, int size)
    {
        int i =0;
        while(i < size) {
            if (arr[i] != i+1) {
                int index = arr[i] - 1;
                if (arr[i] != arr[index]) {
                    int temp = arr[i];
                    arr[i] = arr[index];
                    arr[index] = temp;
                }

                else
                    return arr[i];

            }
            else
                i++;
        }

        return -1;
    }

    private static int missingNumber(int[] arr, int size)
    {
        int i =0;

        while(i < size) {
            if(arr[i] < i-1) {
                if (arr[i] < size && i != arr[i]) {
                    int temp1 = arr[i];
                    int temp2 = arr[temp1];
                    arr[i] = arr[temp1];
                    arr[temp2] = temp1;
                }
            } else {
                i++;
            }
        }

        for (i = 0; i < size; i++) {
            if(arr[i] != i)
                return i;
        }

        return size;
    }
}


