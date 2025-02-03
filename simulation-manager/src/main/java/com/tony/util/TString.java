package com.tony.util;

public class TString
{
	public static String replace(String v_strSource, String v_strSearch, String v_strReplace)
	{
		int iIndexOf;
		int iSourceLen = v_strSource.length();
		int iSearchLen = v_strSearch.length();
		int iReplaceLen = v_strReplace.length();
		while((iIndexOf = v_strSource.indexOf(v_strSearch)) != -1)
		{
			v_strSource = v_strSource.substring(0, iIndexOf) + v_strReplace + v_strSource.substring(iIndexOf+iSearchLen, iSourceLen);
			iSourceLen+= iReplaceLen - iSearchLen;
		}
		return v_strSource;
	}
	
	public static void main(String[] args)
	{
		System.out.println(replace("o Tony é muito feio, feio que dói! feio", "feio", "bonito"));
	}
}
