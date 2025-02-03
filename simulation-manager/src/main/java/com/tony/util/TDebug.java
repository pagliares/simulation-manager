package com.tony.util;

public class TDebug
{
	public static boolean DEBUG;

	public static void println(String v_str)
	{
		if(DEBUG)
			System.out.println(v_str);
	}
	public static void print(String v_str)
	{
		if(DEBUG)
			System.out.print(v_str);
	}
	
	public static void print(Object[] v_str)
	{
		if(DEBUG)
		{
			for(int i=0; i<v_str.length; i++)
			{
				System.out.print(i+": "+v_str[i]+"\t");
			}
			System.out.println();
		}
	}
	
	public static void print(Object[][] v_str)
	{
		if(DEBUG)
		{
			for(int i=0; i<v_str.length; i++)
			{
				System.out.println(i+"__");
				print(v_str[i]);
			}
			System.out.println();
		}
	}
}
