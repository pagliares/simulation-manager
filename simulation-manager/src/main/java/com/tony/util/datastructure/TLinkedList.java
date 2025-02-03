package com.tony.util.datastructure;

import com.tony.util.*;
import com.tony.util.datastructure.linkedlist.*;

public class TLinkedList implements TCleanable
{
	protected long m_lSize;
	protected TListItem m_liRoot;
	protected TListItem m_liLast;
	protected boolean m_bDestroyContentsOnClean = false;
	
	public TLinkedList()
	{
		m_lSize = 0;
		m_liRoot = new TListItem(null, null);
		m_liLast = m_liRoot;
	}
	
	public void setDestroyContentsOnClean(boolean v_bDestroyContentsOnClean){	m_bDestroyContentsOnClean = v_bDestroyContentsOnClean;	}
	
	public synchronized TListItem getRoot(){	return m_liRoot;	}
	public synchronized long getSize(){	return m_lSize;	}
	
	public synchronized void addObject(Object v_o)
	{
		TListItem liLast = new TListItem(m_liLast, v_o);
		m_liLast = liLast;
		m_lSize++;
	}
	
	protected synchronized TListItem nextItem(TListItem v_li)
	{
		return v_li.getNext();
	}
	
	public Object runList_NoSync(TListRunner v_oRunner, Object v_oInfoProvider, boolean v_bRemove, int[] v_iCount, boolean v_bRunWholeList)
	{
		//TDebug.println("runlist");
		if(v_iCount == null)
		{
			v_iCount = new int[1];
		}
		v_iCount[0] = 0;
		Object oReturn = null;
		for(TListItem li = nextItem(m_liRoot); li != null; li = nextItem(li))
		{
			//TDebug.println("loop");
			Object o;
			if(v_oRunner.objectFound((o = li.getObject()), v_oInfoProvider))
			{
				if(v_bRemove)
				{
					if(li == m_liLast)
					{
						m_liLast = m_liLast.getPrevious();
					}
					li.removeFromList();
					m_lSize--;
				}
				if(v_bRunWholeList)
				{
					oReturn = o;
				}
				else
				{
					return o;
				}
			}
			v_iCount[0]++;
		}
		return oReturn;
	}

	public Object runList_NoSync(TListRunner v_oRunner, Object v_oInfoProvider, boolean v_bRemove, boolean v_bRunWholeList)
	{
		return runList_NoSync(v_oRunner, v_oInfoProvider, v_bRemove, null, v_bRunWholeList);
	}
	
	public Object runList_NoSync(TListRunner v_oRunner, Object v_oInfoProvider, boolean v_bRemove)
	{
		return runList_NoSync(v_oRunner, v_oInfoProvider, v_bRemove, null, false);
	}

	public Object runList_NoSync(TListRunner v_oRunner, Object v_oInfoProvider)
	{
		return runList(v_oRunner, v_oInfoProvider, false);
	}

	public synchronized Object runList(TListRunner v_oRunner, Object v_oInfoProvider, boolean v_bRemove, boolean v_bRunWholeList)
	{
		return runList_NoSync(v_oRunner, v_oInfoProvider, v_bRemove, v_bRunWholeList);
	}
	
	public synchronized Object runList(TListRunner v_oRunner, Object v_oInfoProvider, boolean v_bRemove)
	{
		return runList_NoSync(v_oRunner, v_oInfoProvider, v_bRemove, false);
	}
	
	public synchronized Object runList(TListRunner v_oRunner, Object v_oInfoProvider)
	{
		return runList_NoSync(v_oRunner, v_oInfoProvider);
	}
	
	public synchronized Object removeFirstObject() throws TNoMoreObjectsException
	{
		if(m_lSize > 0)
		{
			TListItem liRemoving = m_liRoot.getNext();
			Object oReturn = liRemoving.getObject();
			liRemoving.removeFromList();
			m_lSize--;
			return oReturn;
		}
		throw new TNoMoreObjectsException();
	}
	
	public synchronized Object removeLastObject() throws TNoMoreObjectsException
	{
		if(m_lSize > 0)
		{
			Object oReturn = m_liLast.getObject();
			TListItem liRemoving = m_liLast;
			m_liLast = m_liLast.getPrevious();
			liRemoving.removeFromList();
			m_lSize--;
			return oReturn;
		}
		throw new TNoMoreObjectsException();
	}
	
	/**
	 * TCleanable
	 */
	public synchronized void clean()
	{
		try
		{
			while(true)
			{
				Object oRemoved = removeLastObject();
				if(m_bDestroyContentsOnClean && oRemoved instanceof TCleanable)
				{
					((TCleanable)oRemoved).clean();
				}
			}
		}
		catch(TNoMoreObjectsException v_e){}
	}
}
