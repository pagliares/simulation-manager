// Arquivo Destroy.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Implementa um Destroy no Systema
 * @author Wladimir
 */
public class Destroy extends ActiveState
{
	/**
	 * referência da fila conectada
	 */
	protected DeadState from_q;		
	
	/**
	 * número de entidades destruídas
	 */
	public int Destroyed = 0;		

	/**
	 * constrói um estado ativo sem conexões ou tempo de serviço definidos.
	 */
	public Destroy(Scheduler s){super(s);}

	/**
	 * determina origem das entidades destruídas.
	 * @param	from	the queue to connect from
	 */
	public void ConnectQueue(DeadState from)
	{
		if(from_q == null)
			from_q = from;
	}

	/**
	 * Coloca objeto em seu estado inicial para simulação
	 */
	public void Clear()
	{
		super.Clear();
		Destroyed = 0;
	}

	/**
	 * retorna false (nunca executa evento agendado (B)).
	 */
	public boolean BServed(float time){return false;}

	/**
	 * consome entidades disponíveis na fila e realiza estatísticas (quando aplicável).
	 */
	public boolean CServed()
	{
		boolean got = false;

		while(from_q.HasEnough())			// enquanto tiver entidades a serem destruídas
		{
			Entity e = from_q.Dequeue();	// retira entidade
			// faz estatísticas
			if(obs != null)
				obs.Incoming(e);
			got = true;
			Destroyed++;
			Log.LogMessage(name + ":Entity " + e.GetId() + 
				", from " + from_q.name + ", destroyed.");
		}
		
		return got;
	}
}