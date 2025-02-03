// Arquivo FifoQ.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.*;

/**
 * Implements a First-In First-Out Queue
 */
public class FifoQ extends DeadState
{
	private Vector q;					// implementa fila como vetor

	/**
	 * constrói uma fila vazia com capacidade para max entidades. 
	 */
	public FifoQ(Scheduler s, short max)
	{
		super(s, max);
		q = new Vector(max);	
	} 
	
	/**
	 * constrói uma fila vazia com capacidade ilimitada. 
	 */
	public FifoQ(Scheduler s)
	{
		super(s, 0);
		q = new Vector(10, 10);	
	}
	
	/**
	 * Coloca objeto em seu estado inicial para simulação
	 */
	public void Clear()
	{
		super.Clear();
		q.clear();
	}

	/**
	 * implementa a interface segundo a política FIFO; atualiza atributos de tamanho.
	 */
	public void Enqueue(Entity e)
	{
		if(obs != null)
			obs.Incoming(e);
		q.addElement(e);
		count++;
		e.EnteredQueue(s.GetClock());
	}
	/**
	 * implementa a interface segundo a política FIFO; atualiza atributos de tamanho.
	 */
	public void PutBack(Entity e)
	{	
		if(obs != null)
			obs.Incoming(e);
		q.insertElementAt(e, 0);
		count++;
		e.EnteredQueue(s.GetClock());
	}
	/**
	 * implementa a interface segundo a política FIFO; atualiza atributos de tamanho.
	 */
	public Entity Dequeue()
	{
		try
		{
			Entity e = (Entity)q.firstElement();
			e.LeftQueue(s.GetClock());
			if(obs != null)
				obs.Outgoing(e);
			q.removeElementAt(0);
			count--;
			return e;
		}
		catch(NoSuchElementException x){return null;}		
	}
}