package com.rajaram.resumetailor.util;

import java.math.BigInteger;

class Solution {
//    public static int numSteps(String s) {
//        // BigInteger num = new BigInteger(s,2);
//        // int count=0;
//        // while(num.compareTo(BigInteger.ONE)>0){
//        //     if(!num.testBit(0)){
//        //         num = num.shiftRight(1);
//        //         count++;
//        //     }else{
//        //         num = num.add(BigInteger.ONE);
//        //     }
//        // }
//        int num = Integer.parseInt(s, 2);
//        int count = 0;
//
//        while(num>1){
//            if((num & 1) == 0){
//                num = num>>1;
//            }else{
//                num = num + 1;
//            }
//            count++;
//        }
//        return count;
//    }

//    20,100,10,12,5,13
//    public static boolean increasingTriplet(int[] nums) {
//        int left=0, right=0, count=0, temp=0;
//
//        while(right<nums.length){
//            if(nums[right]<nums[left]) {
//                left=right;
//                count=1;
//            }else{
//                if(temp<nums[right]) count++;
//                if(count==3) return true;
//            }
//            temp = nums[right];
//            right++;
//        }
//        return false;
//    }

    public static void sortColors(int[] nums) {
        int[] count = new int[3];

        for(int num : nums){
            count[num]++;
        }

        int index=0;
        int i=0;
        while(index < 2){
            while(i<nums.length){
                if(count[index]>0) {
                    nums[i]=index;
                    count[index]--;
                    i++;
                }else{
                    index++;
                }
            }
        }
    }

    public static void main(String[] args){
        int[] arr = new int[]{0};
        sortColors(arr);
        for(int num : arr){
            System.out.println(num + ", ");
        }
    }
}