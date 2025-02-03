package com.tony.util;

import com.tony.util.datastructure.*;

public class TTokenizer
{
	/*************************
	 * Objects
	 */
	protected static Object[] loadStringArray(String v_str, String v_str0, boolean v_bTrim, TListToArray v_lta)
	{
		TLinkedList ll = new TLinkedList();
		int i0 = 0;
		int i1 = v_str.indexOf(v_str0);
		boolean bFirst = true;
		while(i1 != -1)
		{
			int iF = bFirst ? i0 : (i0+v_str0.length());
			//System.out.println(iF+" "+i1);
			if(bFirst)
				bFirst = false;
			String str = v_str.substring(iF, i1);
			if(v_bTrim)
			{
				str = str.trim();
			}
			if(str.length() > 0)
			{
				ll.addObject(str);
			}
			i0 = i1;
			i1 = v_str.indexOf(v_str0, i0+1);
		}
		//System.out.println()
		int iF = bFirst ? i0 : (i0+v_str0.length());
		String str = v_str.substring(iF, v_str.length());
		if(v_bTrim)
			str = str.trim();
		if(str.length() > 0)
		{
			ll.addObject(str);
		}
		if(v_lta == null)
			v_lta = new TListToArray();
		Object[] oReturn = v_lta.toArray(ll);
		ll.clean();
		return oReturn;
	}
	
	public static Object[][] loadStringTable(String v_str, String v_str1, String v_str0, boolean v_bTrim)
	{
		TLinkedList ll = new TLinkedList();
		int i0 = 0;
		int i1 = v_str.indexOf(v_str1);
		TListToArray lta = new TListToArray();
		boolean bFirst = true;
		while(i1 != -1)
		{
			int iF = bFirst ? i0 : (i0+v_str1.length());
			if(bFirst)
				bFirst = false;
			Object[] oStringArray = loadStringArray(v_str.substring(iF, i1), v_str0, v_bTrim, lta);
			if(oStringArray.length > 0)
			{
				ll.addObject(oStringArray);
			}
			i0 = i1;
			i1 = v_str.indexOf(v_str1, i0+1);
		}
		//System.out.println(i0+" "+v_str1.length()+" "+v_str.length());	
		int iF = bFirst ? i0 : (i0+v_str0.length());
		Object[] oStringArray = loadStringArray(v_str.substring(iF, v_str.length()), v_str0, v_bTrim, lta);
		if(oStringArray.length > 0)
		{
			ll.addObject(oStringArray);
		}
		Object[] oTable = lta.toArray(ll);
		Object[][] oReturn = new Object[oTable.length][];
		for(int i=0; i<oReturn.length; i++)
		{
			oReturn[i] = (Object[])oTable[i];
		}
		ll.clean();
		return oReturn;
	}
	
	/***********************************
	 * Booleans
	 */
	public static boolean[] stringArrayToBoolean(Object[] v_o)
	{
		boolean[] bResult = new boolean[v_o.length];
		for(int i=0; i<bResult.length; i++)
		{
			bResult[i] = !v_o[i].equals("0");
		}
		return bResult;
	}
	
	/***********************************
	 * Ints
	 */
	
	public static int[] stringArrayToInt(Object[] v_o)
	{
		int[] iResult = new int[v_o.length];
		for(int i=0; i<iResult.length; i++)
		{
			iResult[i] = Integer.parseInt((String)v_o[i]);
		}
		return iResult;
	}
	
	public static int[][] stringTableToInt(Object[][] v_o)
	{
		int[][] iResult = new int[v_o.length][];
		for(int i=0; i<iResult.length; i++)
		{
			iResult[i] = stringArrayToInt(v_o[i]);
		}
		return iResult;
	}
	
	/************************************
	 * Doubles
	 */
	public static double[] stringArrayToDouble(Object[] v_o)
	{
		double[] dResult = new double[v_o.length];
		for(int i=0; i<dResult.length; i++)
		{
			dResult[i] = new Double((String)v_o[i]).doubleValue();
		}
		return dResult;
	}
	
	public static double[][] stringTableToDouble(Object[][] v_o)
	{
		double[][] dResult = new double[v_o.length][];
		for(int i=0; i<dResult.length; i++)
		{
			dResult[i] = stringArrayToDouble(v_o[i]);
		}
		return dResult;
	}
	
	/************************************
	 * Redirecting
	 */
	public static boolean[] loadBooleanArray(String v_str, String v_str0)
	{
		return stringArrayToBoolean(loadStringArray(v_str, v_str0, true, null));
	}
	
	public static int[] loadIntArray(String v_str, String v_str0)
	{
		return stringArrayToInt(loadStringArray(v_str, v_str0, true, null));
	}

	public static int[][] loadIntTable(String v_str, String v_str1, String v_str0)
	{
		return stringTableToInt(loadStringTable(v_str, v_str1, v_str0, true));
	}

	public static double[] loadDoubleArray(String v_str, String v_str0)
	{
		return stringArrayToDouble(loadStringArray(v_str, v_str0, true, null));
	}

	public static double[][] loadDoubleTable(String v_str, String v_str1, String v_str0)
	{
		return stringTableToDouble(loadStringTable(v_str, v_str1, v_str0, true));
	}
	
	
	
	public static Object[] loadStringArray(String v_str, String v_str0, boolean v_bTrim)
	{
		return loadStringArray(v_str, v_str0, v_bTrim, null);
	}
	
	public static String replace(String v_str, String v_strSearch, String v_strReplace, int[] v_iNReplaces)
	{
		StringBuffer stb = new StringBuffer();
		int iNReplaces = 0;
		int i0 = v_str.indexOf(v_strSearch);
		if(i0 == -1)
		{
			v_iNReplaces[0] = 0;
			return new String(v_str);
		}
		else
		{
			//System.out.println("i0 "+i0);
			stb.append(v_str.substring(0,i0));
			boolean bFinish = false;
			while(!bFinish)
			{
				//System.out.println("i0 "+i0);
				stb.append(v_strReplace);
				iNReplaces++;
				int i1 = v_str.indexOf(v_strSearch,i0+1);
				if(i1 == -1)
				{
					i1 = v_str.length();
					bFinish = true;
				}
				stb.append(v_str.substring(i0+v_strSearch.length(),i1));
				i0 = i1;
			}
			v_iNReplaces[0] = iNReplaces;
			return stb.toString();
		}
	}
	
	public static String replace(String v_str, String v_strSearch, String v_strReplace)
	{
		return replace(v_str, v_strSearch, v_strReplace, new int[1]);
	}

	public static String replace(String v_str, String v_strSearch, String v_strReplace, boolean v_bStopOnFirst)
	{
		int iI = v_str.indexOf(v_strSearch);
		if(iI == -1)
		{
			return new String(v_str);
		}
		else return v_str.substring(0,iI)+v_strReplace+v_str.substring(iI+v_strSearch.length(), v_str.length());
	}
	
	
	
	
}
