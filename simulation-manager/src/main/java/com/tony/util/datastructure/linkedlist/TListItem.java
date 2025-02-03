package com.tony.util.datastructure.linkedlist;

public class TListItem
{
	protected TListItem	m_liPrevious;
	protected TListItem	m_liNext;
	protected Object	m_oObject;
	
	public	TListItem(TListItem v_liPrevious, Object v_oObject)
	{
		if(v_liPrevious != null)
		{
			v_liPrevious.setNext(this);
		}
		m_liPrevious = v_liPrevious;
		m_oObject = v_oObject;
	}
	
	public void setNext(TListItem v_liNext){	m_liNext = v_liNext;	}
	public TListItem getNext(){	return m_liNext;	}
	void setPrevious(TListItem v_liPrevious){	m_liPrevious = v_liPrevious;	}
	public TListItem getPrevious(){	return m_liPrevious;	}
	
	public void setObject(Object v_oObject){	m_oObject = v_oObject;	}
	public Object getObject(){	return m_oObject;	}
	
	public void removeFromList()
	{
		m_liPrevious.setNext(m_liNext);
		if(m_liNext !=  null)
		{
			m_liNext.setPrevious(m_liPrevious);
		}
		//clean();
	}
	
	public void clean()
	{
		m_liNext = null;
		m_liPrevious = null;
		m_oObject = null;
	}
}
