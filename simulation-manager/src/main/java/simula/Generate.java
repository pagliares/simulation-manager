// Arquivo Generate.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Classe que implementa um Generate
 */
public class Generate extends ActiveState
{
	/**
	 * refer�ncia da fila conectada
	 */
	protected DeadState to_q;		
	/**
	 * flag "gerando entidade"
	 */
	protected boolean inservice;
	/**
	 * gerador de n�meros aleat�rios
	 * de uma dada distribui��o
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
	 * n�mero de entidades geradas
	 */
	public int Generated = 0;	
	/**
	 * n�mero de entidades perdidas
	 */
	public int Wasted = 0;			

	/**
	 * constr�i um estado ativo sem conex�es ou tempo de servi�o definidos.
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
	 * determina o tempo de servi�o de acordo com a distribui��o especificada;
	 * os par�metros da distribui��o s�o passados na cria��o do objeto 
	 * e registra primeiro evento de chegada.
	 */
	public void SetServiceTime(Distribution d)
	{
		this.d = d;
		RegisterEvent((float)d.Draw());
		inservice = true;
	}

	/**
	 * Coloca objeto em seu estado inicial para simula��o
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
	 * atribui vetores de atributos que cont�m ids e valores
	 * iniciais dos atributos de cada entidade gerada por 
	 * uma inst�ncia deste estado ativo. 
	 */
	public void SetEntitiesAtts(String[] ids, float[] values)
	{
		if(ids.length != values.length)
			throw new IllegalArgumentException
				("Vetores de ids e valores devem ter o mesmo n�mero de elementos");
		attids = ids;
		attvals = values;
	}

	/**
	 * implementa protocolo.
	 */
	public boolean BServed(float time)
	{
		Entity e = new Entity(time);	// cria entidade e atribui-lhe instante de cria��o
		
		// atribui atributos espec�ficos a e

		if(attids != null)
		{
			for(int i = 0; i < attids.length; i++)
				e.SetAttribute(attids[i], attvals[i]);	
		}		
		
		if(to_q.HasSpace())				// se tem espa�o para entidade na fila
		{
			to_q.Enqueue(e);
			Log.LogMessage(name + ":Entity " + e.GetId() + 
				" generated and sent to " + to_q.name);
			if(obs != null)
				obs.Outgoing(e);
		}
		else
		{
			Wasted++;					// mais uma entidade desperdi�ada
			Log.LogMessage(name + ":Entity " + e.GetId() +
				" generated but wasted");
		}
		
		Generated++;					// mais uma entidade gerada

		inservice = false;				// libera a gera��o de novas entidades
		if(obs != null)
			obs.StateChange(Observer.BUSY);// marca fim do idle-time => determina inter-arrival

		return true;			
	}

	/**
	 * implementa protocolo; agenda evento B se n�o est� "gerando" outra entidade.
	 * sempre retorna false pois n�o tem efeito no instante de simula��o atual.
	 */
	public boolean CServed()
	{
		if(!inservice)					// se n�o est� "gerando" outra entidade...
		{
			float t = (float)d.Draw();		// obt�m instante de cria��o da pr�xima entidade
			RegisterEvent(t);				// agenda evento B
			inservice = true;				// est� "gerando"
			if(obs != null)
				obs.StateChange(Observer.IDLE);// para o Generate, o idle-time � o inter-arrival
			Log.LogMessage(name + ":Scheduled entity generation to " + t);
		}

		return false;

	}
}