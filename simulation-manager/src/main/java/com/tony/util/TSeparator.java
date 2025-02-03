package com.tony.util;

public class TSeparator
{
	public static String separator(int[] v_iNumbers)
	{
		char[] cSeparator = new char[v_iNumbers.length];
		for(int i=0; i<v_iNumbers.length; i++)
		{
			cSeparator[i] = (char)v_iNumbers[i];
		}
		return new String(cSeparator);
	}
	
	public static String separator(int v_iN1, int v_iN2, int v_iN3)
	{
		return separator(new int[]{v_iN1, v_iN2, v_iN3});
	}
}
