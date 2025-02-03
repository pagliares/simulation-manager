package com.tony.util.datastructure;

import com.tony.util.datastructure.linkedlist.*;

public class TListToArray implements TListRunner
{
	Object[] m_oArray;
	int m_iCurrIndex;
	
	public TListToArray()
	{}
	
	public void setList(TLinkedList v_oList)
	{
		m_oArray = new Object[(int)v_oList.getSize()];
		m_iCurrIndex = 0;
	}
	
	public boolean objectFound(Object v_o, Object v_oInfoProvider)
	{
		m_oArray[m_iCurrIndex] = v_o;
		m_iCurrIndex++;
		return false;
	}
	
	public synchronized Object[] toArray(TLinkedList v_oList)
	{
		setList(v_oList);
		v_oList.runList(this, null);
		return m_oArray;
	}
}
