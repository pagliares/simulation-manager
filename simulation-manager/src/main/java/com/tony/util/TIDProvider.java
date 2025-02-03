package com.tony.util;

public class TIDProvider
{
	protected static long m_lCurrID = 0;
	
	public static synchronized long newID(){	return ++m_lCurrID;	}
}
