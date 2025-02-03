package com.tony.util.datastructure;

import java.util.*;

public class THashtableToArray
{
	public static Object[] toArray(Hashtable v_ht)
	{
		Object[] oReturn = new Object[v_ht.size()];
		int i=0;
		for(Enumeration oKeys = v_ht.keys();
			oKeys.hasMoreElements();
			i++)
		{
			Object oKey = oKeys.nextElement();
			oReturn[i] = v_ht.get(oKey);
		}
		return oReturn;
	}
}
