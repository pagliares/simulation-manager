// Arquivo StackQ.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.*;

public class StackQ extends DeadState
{
	private Stack q;					// implementa fila como vetor

	/**
	 * constrói uma fila vazia com capacidade para max entidades.
	 */
	public StackQ(Scheduler s, short max)
	{
		super(s, max);
		q = new Stack();
		q.ensureCapacity(max);	
	} 
	
	/**
	 * constrói uma fila vazia com capacidade ilimitada. 
	 */
	public StackQ(Scheduler s)
	{
		super(s, 0);
		q = new Stack();	
		q.ensureCapacity(10);
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
		q.push(e);
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
		q.push(e);
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
			Entity e = (Entity)q.peek();
			e.LeftQueue(s.GetClock());
			if(obs != null)
				obs.Outgoing(e);
			q.pop();
			count--;
			return e;
		}
		catch(EmptyStackException x){return null;}	
	}
}