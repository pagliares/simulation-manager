package com.tony.util.array;

public class TArrayUtils
{
	public static void fillWith(int[] v_iArray, int v_iNumber)
	{
		for(int i=0; i<v_iArray.length; i++)
			v_iArray[i] = v_iNumber;
	}
	
	public static void fillWith(int[][] v_iTable, int v_iNumber)
	{
		for(int i=0; i<v_iTable.length; i++)
			fillWith(v_iTable[i], v_iNumber);
	}
}
