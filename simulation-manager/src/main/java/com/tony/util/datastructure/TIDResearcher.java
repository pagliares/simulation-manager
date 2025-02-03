package com.tony.util.datastructure;

import com.tony.util.*;
import com.tony.util.datastructure.linkedlist.*;

public class TIDResearcher implements TListRunner
{
	public TIDResearcher()
	{}
	
	public Object findID(TLinkedList v_oList, long v_lID, boolean v_bRemove)
	{
		return v_oList.runList_NoSync(this, new Long(v_lID), v_bRemove);
	}
	public Object findID(TLinkedList v_oList, long v_lID)
	{
		return findID(v_oList, v_lID, false);
	}
	
	public boolean objectFound(Object v_o, Object v_oInfoProvider)
	{
		try
		{
			return (((TIDUser)v_o).getID() == ((Long)v_oInfoProvider).longValue());
		}
		catch(ClassCastException v_e)
		{
			v_e.printStackTrace();
			return false;
		}
	}
}
