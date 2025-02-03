// Arquivo Generate.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Classe que implementa um Generate
 */
public class Generate extends ActiveState
{
	/**
	 * referência da fila conectada
	 */
	protected DeadState to_q;		
	/**
	 * flag "gerando entidade"
	 */
	protected boolean inservice;
	/**
	 * gerador de números aleatórios
	 * de uma dada distribuição
	 */
	protected Distribution d;		

	/**
	 * valor inicial dos atributos
	 * das entidades geradas
	 */
	protected float[] attvals;		
	/**
	 * ids dos atributos das entidades
	 */
	protected String[] attids;		
	/**
	 * número de entidades geradas
	 */
	public int Generated = 0;	
	/**
	 * número de entidades perdidas
	 */
	public int Wasted = 0;			

	/**
	 * constrói um estado ativo sem conexões ou tempo de serviço definidos.
	 */
	public Generate(Scheduler s){super(s);}
	
	/**
	 * determina destino das entidades geradas.
	 */
	public void ConnectQueue(DeadState to)
	{
		if(to_q == null)
			to_q = to;
	}
	
	/**
	 * determina o tempo de serviço de acordo com a distribuição especificada;
	 * os parâmetros da distribuição são passados na criação do objeto 
	 * e registra primeiro evento de chegada.
	 */
	public void SetServiceTime(Distribution d)
	{
		this.d = d;
		RegisterEvent((float)d.Draw());
		inservice = true;
	}

	/**
	 * Coloca objeto em seu estado inicial para simulação
	 */
	public void Clear()
	{
		super.Clear();
		Generated = 0;
		Wasted = 0;
		RegisterEvent((float)d.Draw());
		inservice = true;
	}
	
	/**
	 * atribui vetores de atributos que contêm ids e valores
	 * iniciais dos atributos de cada entidade gerada por 
	 * uma instância deste estado ativo. 
	 */
	public void SetEntitiesAtts(String[] ids, float[] values)
	{
		if(ids.length != values.length)
			throw new IllegalArgumentException
				("Vetores de ids e valores devem ter o mesmo número de elementos");
		attids = ids;
		attvals = values;
	}

	/**
	 * implementa protocolo.
	 */
	public boolean BServed(float time)
	{
		Entity e = new Entity(time);	// cria entidade e atribui-lhe instante de criação
		
		// atribui atributos específicos a e

		if(attids != null)
		{
			for(int i = 0; i < attids.length; i++)
				e.SetAttribute(attids[i], attvals[i]);	
		}		
		
		if(to_q.HasSpace())				// se tem espaço para entidade na fila
		{
			to_q.Enqueue(e);
			Log.LogMessage(name + ":Entity " + e.GetId() + 
				" generated and sent to " + to_q.name);
			if(obs != null)
				obs.Outgoing(e);
		}
		else
		{
			Wasted++;					// mais uma entidade desperdiçada
			Log.LogMessage(name + ":Entity " + e.GetId() +
				" generated but wasted");
		}
		
		Generated++;					// mais uma entidade gerada

		inservice = false;				// libera a geração de novas entidades
		if(obs != null)
			obs.StateChange(Observer.BUSY);// marca fim do idle-time => determina inter-arrival

		return true;			
	}

	/**
	 * implementa protocolo; agenda evento B se não está "gerando" outra entidade.
	 * sempre retorna false pois não tem efeito no instante de simulação atual.
	 */
	public boolean CServed()
	{
		if(!inservice)					// se não está "gerando" outra entidade...
		{
			float t = (float)d.Draw();		// obtém instante de criação da próxima entidade
			RegisterEvent(t);				// agenda evento B
			inservice = true;				// está "gerando"
			if(obs != null)
				obs.StateChange(Observer.IDLE);// para o Generate, o idle-time é o inter-arrival
			Log.LogMessage(name + ":Scheduled entity generation to " + t);
		}

		return false;

	}
}