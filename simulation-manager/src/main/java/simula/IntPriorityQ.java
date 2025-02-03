// Arquivo IntPriorityQ.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 9.Abr.1999	Wladimir

package simula;

import java.util.*;

class IntPriorityQ
{
	private Vector q;					// implementa fila como vetor

	/**
	 * constrói uma fila vazia com capacidade ilimitada. 
	 */
	public IntPriorityQ()
	{
		q = new Vector(5, 5);	
	}
	
	/**
	 * coloca no fim da fila de acordo com o instante de serviço
	 */
	public void Enqueue(IntQEntry e)
	{
		int min, max, cur;	// max pode ser negativo (qdo for inserir no começo)
		IntQEntry e2;
		// encontra posição de inserção baseado no tempo de serviço de IntQEntry e.
		// implementa busca binária, já que os elementos esão ordenados por tempo.
		min = 0;
		max = q.size() - 1;
		cur = 0;
		while(min <= max)
		{
			cur = (min + max) / 2;
			e2 = (IntQEntry)q.elementAt(cur);
			if(e.duetime < e2.duetime)
				max = cur - 1;
			else if(e.duetime >= e2.duetime)				// após os de mesma prioridade 
				min = ++cur;								// que já estão na fila
		}
		// cur contém a posição de inserção
		q.insertElementAt(e, cur);
	}
	
	/**
	 * recoloca na cabeça da fila;
	 * não é checado o tempo de serviço, portanto só deve ser usada se for para elemento que
	 * acabou de ser retirado através de Dequeue
	 */
	public void PutBack(IntQEntry e)
	{	
		q.insertElementAt(e, 0);
	}
	
	/**
	 * retira da cabeça da fila.
	 */
	public IntQEntry Dequeue()
	{
		try
		{
			IntQEntry e = (IntQEntry)q.firstElement();
			q.removeElementAt(0);
			return e;
		}
		catch(NoSuchElementException x){return null;}
	}

	/**
	 * retira do fim da fila
	 */
	public IntQEntry FromTail()
	{
		try
		{
			IntQEntry e = (IntQEntry)q.lastElement();
			q.removeElementAt(q.size() - 1);
			return e;
		}
		catch(NoSuchElementException x){return null;}
	}
	
	/**
	 * retorna true se fila está vazia
	 */
	public boolean IsEmpty(){return q.isEmpty();}
}