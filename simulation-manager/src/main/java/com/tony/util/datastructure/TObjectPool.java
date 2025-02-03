package com.tony.util.datastructure;

public class TObjectPool
{
	protected TLinkedList m_ll;
	protected TPoolObjectProvider m_oProvider;
	protected int m_iInitialObjects;
	
	public TObjectPool(int v_iInitialObjects, TPoolObjectProvider v_oProvider)
	{
		m_oProvider = v_oProvider;
		m_iInitialObjects = v_iInitialObjects;
		m_ll = new TLinkedList();
		fillPool();
	}
	
	protected void fillPool()
	{
		for(int i=0; i<m_iInitialObjects; i++)
		{
			m_ll.addObject(m_oProvider.newObject());
			System.out.print("\r"+i);
		}
		System.out.println("fillPool. "+m_ll.getSize());

	}
	
	public Object takeObject()
	{
		Object oReturn = null;
		try
		{
			oReturn = m_ll.removeLastObject();
			System.out.println("takeObject. "+m_ll.getSize());
			return oReturn;
		}
		catch(TNoMoreObjectsException v_e)
		{
			fillPool();
			try
			{
				oReturn = m_ll.removeLastObject();
			}
			catch(TNoMoreObjectsException v_eImpossible)
			{
				System.out.println("looooouco!");
			}
			System.out.println("takeObject. "+m_ll.getSize());
			return oReturn;
		}
	}
	
	public void takeObjectBack(Object v_o)
	{
		m_ll.addObject(v_o);
		System.out.println("takeObjectBack. "+m_ll.getSize()+" objects in pool");
	}
}
