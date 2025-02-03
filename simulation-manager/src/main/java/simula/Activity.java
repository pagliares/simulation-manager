// Arquivo Activity.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 9.Abr.1999	Wladimir

package simula;

import java.util.*;

public class Activity extends ActiveState
{

	/**
	 * entities_from_v, entities_to_v, conditions_from_v,
	 * resources_from_v, resources_to_v, resources_qt_v:
	 * vetores que mantêm as ligações e parâmetros
	 */
	protected Vector entities_from_v, entities_to_v, conditions_from_v,
					 	resources_from_v, resources_to_v, resources_qt_v;
													
	private Distribution d;		// gerador de números aleatórios de uma dada distribuição

	/**
	 * fila de entidades/recursos em serviço
	 */
	protected IntPriorityQ service_q;
		
	/**
	 * se está bloqueado
	 */
	protected boolean blocked;	

	/**
	 * constrói um estado ativo sem conexões ou tempo de serviço definidos.
	 */
	public Activity(Scheduler s)
	{
		super(s);
		// constrói vetores de ligações
		entities_from_v = new Vector(1, 1);
		entities_to_v = new Vector(1, 1);
		conditions_from_v = new Vector(1, 1);
		resources_from_v = new Vector(1, 1);
		resources_to_v = new Vector(1, 1);
		resources_qt_v = new Vector(1, 1);
		service_q = new IntPriorityQ();
	}
	
	/**
	 * determina o tempo de serviço de acordo com a distribuição especificada;
	 * os parâmetros da distribuição são passados na criação do objeto.
	 */
	public void SetServiceTime(Distribution d){this.d = d;}
	
	public void ConnectQueues(DeadState from, DeadState to)
	{ConnectQueues(from, ConstExpression.TRUE, to);}
	
	/**
	 * conecta estados mortos à atividade de forma que 
	 * a(s) entidade(s) (recurso(s)) não se misturem.
	 */
	public void ConnectResources(ResourceQ from, ResourceQ to, int qty_needed)
	{
		resources_from_v.add(from);
		resources_to_v.add(to);
		resources_qt_v.add(new Integer(qty_needed));
	}
	
	/**
	 * conecta estados mortos à atividade de forma que 
	 * a(s) entidade(s) (recurso(s)) não se misturem.
	 * mas a entidade é obtida de from somente se cond é satisfeita
	 */
	public void ConnectQueues(DeadState from, Expression cond, DeadState to)
	{
		conditions_from_v.add(cond);
		entities_from_v.add(from);
		entities_to_v.add(to);
	}
	
	/**
	 * Coloca objeto em seu estado inicial para simulação
	 */
	public void Clear()
	{
		super.Clear();
		service_q = new IntPriorityQ();
	}

	/**
	 * implementa protocolo.
	 */
	public boolean BServed(float time)
	{
		if(blocked)									// não faz nada enquanto estiver bloqueado
			return false;
			
		IntQEntry e = service_q.Dequeue();

		if(e == null)								// não há mais nada a servir
			return false;

		if(time < e.duetime)				// serviço foi interrompido e scheduler 
		{														// não foi notificado
			service_q.PutBack(e);				// devolve à fila para ser servido mais tarde
			return false;
		}

		// fim de serviço!
		
		boolean shouldnotblock = true;

		for(int i = 0; i < entities_to_v.size(); i++)		// as entidades...
		{
			DeadState q = (DeadState)entities_to_v.elementAt(i);	// obtém fila associada
			shouldnotblock &= q.HasSpace();												// condição para não bloquear
		}
		
		if(!shouldnotblock)
		{
			service_q.PutBack(e);
			blocked = true;											// bloqueia
			Log.LogMessage(name + ":Blocked");
			return false;
		}

		for(int i = 0; i < entities_to_v.size(); i++)		// as entidades...
		{
			DeadState q = (DeadState)entities_to_v.elementAt(i);	// obtém fila associada
			if(q.HasSpace())										// se tem espaço
				q.Enqueue(e.ve[i]);									// envia ao estado morto
		}
		
		for(int i = 0; i < resources_to_v.size(); i++)		// e os recursos.
		{
			int qt;
			ResourceQ q = (ResourceQ)resources_to_v.elementAt(i);	// obtém fila associada
			q.Release(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());// envia ao estado morto
			Log.LogMessage(name + ":Released " + qt + " resources to " +
				((ResourceQ)resources_to_v.elementAt(i)).name);
		}

		if(obs != null)
		{
			if(service_q.IsEmpty())
				obs.StateChange(Observer.IDLE);
			for(int i = 0; i < e.ve.length; i++)
				obs.Outgoing(e.ve[i]);
		}
		
		for(int i = 0; i < e.ve.length; i++)
			Log.LogMessage(name + ":Entity " + e.ve[i].GetId() +
				" sent to " + ((DeadState)entities_to_v.elementAt(i)).name);
				
		return true;
	}
	
	/**
	 * implementa protocolo.
	 */
	public boolean CServed()
	{
		// primeiro tenta resolve o estado bloqueado, se for o caso
		if(blocked)
		{
			blocked = false;
			while(BServed(s.GetClock()));	// extrai todos os bloqueados
							
			if(blocked)		// se ainda estiver bloqueado, não faz nada
				return false;
			Log.LogMessage(name + ":Unblocked");
		}
			
		
		// primeiro verifica se todos os recursos e entidades estão disponíveis
		boolean ok = true;
		int esize = entities_from_v.size();
		int i;

		for(i = 0; i < resources_from_v.size() && ok; i++)	// os recursos...
			ok &= ((ResourceQ)resources_from_v.elementAt(i)).
				HasEnough(((Integer)resources_qt_v.elementAt(i)).intValue());

		for(i = 0; i < esize && ok; i++)					// as entidades...
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		IntQEntry possible = new IntQEntry(esize, (float)d.Draw());
		Entity e;
		for(i = 0; i < esize && ok; i++)					// as condições.
		{
			possible.ve[i] = e = ((DeadState)entities_from_v.elementAt(i)).Dequeue();
																// retira entidades...
			ok &= ((Expression)conditions_from_v.elementAt(i)).Evaluate(e) != 0;
																// e testa condição
		}

		if(!ok)
		{
			if(i > 0)		// alguma condição não foi satisfeita
			{
				for(i--; i >= 0; i--)		// devolve as entidades às respectivas filas
					((DeadState)entities_from_v.elementAt(i)).PutBack(possible.ve[i]);
			}

			return false;
		}

		// obtém os recursos

		for(i = 0; i < resources_from_v.size(); i++)
		{
			int qt;	
			((ResourceQ)resources_from_v.elementAt(i)).
				Acquire(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());
				
			Log.LogMessage(name + ":Acquired " + qt + " resources from " +
				((ResourceQ)resources_from_v.elementAt(i)).name);
		}

		possible.duetime = RegisterEvent(possible.duetime);		// notifica scheduler
		service_q.Enqueue(possible);							// coloca na fila de serviço
		
		if(obs != null)
		{
			obs.StateChange(Observer.BUSY);
			for(i = 0; i < possible.ve.length; i++)
				obs.Incoming(possible.ve[i]);
		}

		for(i = 0; i < possible.ve.length; i++)
			Log.LogMessage(name + ":Entity " + possible.ve[i].GetId() +
				" got from " + ((DeadState)entities_from_v.elementAt(i)).name);

		return true;
	}
}