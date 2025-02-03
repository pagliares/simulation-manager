// Arquivo PriorityQ.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.*;

/**
 * Classe que implementa uma fila de prioridades
 */
public class PriorityQ extends DeadState
{
	private Vector q;					// implementa fila como vetor

	/**
	 * constrói uma fila vazia com capacidade para max entidades. 
	 */
	public PriorityQ(Scheduler s, int max)
	{
		super(s, max);
		q = new Vector(max);	
	} 
	
	/**
	 * constrói uma fila vazia com capacidade ilimitada. 
	 */
	public PriorityQ(Scheduler s)
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
	 * implementa a interface segundo a política priority FIFO; atualiza atributos de tamanho.
	 */
	public void Enqueue(Entity e)
	{
		if(obs != null)
			obs.Incoming(e);
		e.EnteredQueue(s.GetClock());
		int min, max, cur;	// max pode ser negativo (qdo for inserir no começo)
		Entity e2;
		// encontra posição de inserção baseado na prioridade da Entity e.
		// implementa busca binária, já que os elementos esão ordenados por prioridade.
		min = 0;
		max = q.size() - 1;
		cur = 0;
		while(min <= max)
		{
			cur = (min + max) / 2;
			e2 = (Entity)q.elementAt(cur);
			if(e.GetPriority() < e2.GetPriority())
				max = cur - 1;
			else if(e.GetPriority() >= e2.GetPriority())	// após os de mesma prioridade 
				min = ++cur;								// que já estão na fila
		}
		// cur contém a posição de inserção
		q.insertElementAt(e, cur);
		count++;
	}
	/**
	 * implementa a interface segundo a política priority FIFO; atualiza atributos de tamanho.
	 */
	public void PutBack(Entity e)
	{	
		e.EnteredQueue(s.GetClock());
		if(obs != null)
			obs.Incoming(e);
		q.insertElementAt(e, 0);
		count++;
	}
	/**
	 * implementa a interface segundo a política priority FIFO; atualiza atributos de tamanho.
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