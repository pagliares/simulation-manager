// Arquivo Router.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 16.Abr.1999	Wladimir

package simula;

import java.util.*;

/**
 * Classe que implementa um Router
 */
public class Router extends ActiveState
{
	/**
	 * entities_from_v, entities_to_v, conditions_to_v,
	 * resources_from_v, resources_to_v, resources_qt_v:
	 * vetores que mantêm as ligações e parâmetros
	 */
	protected Vector entities_from_v, entities_to_v, conditions_to_v,
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
	public Router(Scheduler s)
	{
		super(s);
		// constrói vetores de ligações
		entities_from_v = new Vector(1, 1);
		entities_to_v = new Vector(1, 1);
		conditions_to_v = new Vector(1, 1);
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

	/**
	 * Receive a connection FROM a queue
	 * @param from the Origin of the connection
	 */
	public void ConnectQueues(DeadState from) {entities_from_v.add(from);}

	/**
	 * conecta estados mortos à atividade,
	 * mas a entidade é liberada somente se cond é satisfeita;
	 * o usuário deve garantir que a entidade siga um caminho apenas (com mais condições).
	 */
	public void ConnectQueues(DeadState to, Expression cond)
	{
		entities_to_v.add(to);
		conditions_to_v.add(cond);
	}

	/**
	 * conecta recursos à atividade de forma que não se misturem.
	 */
	public void ConnectResources(ResourceQ from, ResourceQ to, int qty_needed)
	{
		resources_from_v.add(from);
		resources_to_v.add(to);
		resources_qt_v.add(new Integer(qty_needed));
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

		if(e == null)						// não há mais nada a servir
			return false;

		if(time < e.duetime)				// serviço foi interrompido e scheduler 
		{									// não foi notificado
			service_q.PutBack(e);				// devolve à fila para ser servido mais tarde
			return false;
		}

		// fim de serviço!

		for(int j = 0; j < e.ve.length; j++)					//para cada entidade em serviço...
		{
			if(e.ve[j] == null)	// este pode ser um serviço que estava bloqueado
				continue;
			for(int i = 0; i < entities_to_v.size(); i++)
				if(((Expression)conditions_to_v.elementAt(i)).Evaluate(e.ve[j]) != 0)
				{
					DeadState q = (DeadState)entities_to_v.elementAt(i);// obtém fila associada
					if(q.HasSpace())									// se tem espaço
					{
						q.Enqueue(e.ve[j]);								// envia ao estado morto
						Log.LogMessage(name + ":Entity " + e.ve[j].GetId() +
							" sent to " + q.name);
		
						if(obs != null)
							obs.Outgoing(e.ve[j]);
		
						e.ve[j] = null;
					}
					else
						blocked = true;	// sinaliza e não faz nada com a entidade
							
					break;												// p/ próx. ent em serviço
				}
		}
		
		if(blocked)
		{
			service_q.PutBack(e);	// devolve as que restaram	
			Log.LogMessage(name + ":Blocked");
			return false;					// considera como não processado
		}

		for(int i = 0; i < resources_to_v.size(); i++)		// e os recursos.
		{
			int qt;
			ResourceQ q = (ResourceQ)resources_to_v.elementAt(i);	// obtém fila associada
			q.Release(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());// envia ao estado morto
			Log.LogMessage(name + ":Released " + qt + " resources to " + q.name);
		}
		
		if(obs != null && service_q.IsEmpty())
			obs.StateChange(Observer.IDLE);
		
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

		for(int i = 0; i < resources_from_v.size() && ok; i++)	// os recursos...
			ok &= ((ResourceQ)resources_from_v.elementAt(i)).
				HasEnough(((Integer)resources_qt_v.elementAt(i)).intValue());

		for(int i = 0; i < esize && ok; i++)					// as entidades...
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		if(!ok)					// se alguma entidade ou recurso não estiver disponível
			return false;		// não realiza nada e informa scheduler
		
		IntQEntry entry = new IntQEntry(esize, (float)d.Draw());

		// retira entidades...

		for(int i = 0; i < esize && ok; i++)					
			entry.ve[i] = ((DeadState)entities_from_v.elementAt(i)).Dequeue();
																
		// obtém os recursos

		for(int i = 0; i < resources_from_v.size(); i++)	
		{
			int qt;
			((ResourceQ)resources_from_v.elementAt(i)).
				Acquire(qt = ((Integer)resources_qt_v.elementAt(i)).intValue());
			Log.LogMessage(name + ":Acquired " + qt + " resources from " +
				((ResourceQ)resources_from_v.elementAt(i)).name);
		}

		entry.duetime = RegisterEvent(entry.duetime);			// notifica scheduler
		service_q.Enqueue(entry);								// coloca na fila de serviço

		if(obs != null)
		{
			obs.StateChange(Observer.BUSY);
			for(int i = 0; i < entry.ve.length; i++)
				obs.Incoming(entry.ve[i]);
		}

		for(int i = 0; i < entry.ve.length; i++)
			Log.LogMessage(name + ":Entity " + entry.ve[i].GetId() +
				" got from " + ((DeadState)entities_from_v.elementAt(i)).name);

		return true;
	}
}