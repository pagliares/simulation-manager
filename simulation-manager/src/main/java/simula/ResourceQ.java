// Arquivo ResourceQ.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

public class ResourceQ extends DeadState
{
	private short init_qty;

	/**
	 * constrói uma fila de recursos com init_qty recursos iniciais.
	 */
	public ResourceQ(Scheduler s, int init_qty)
	{
		super(s, 0);	// sinaliza que este estado tem capacidade ilimitada
		this.init_qty = count = (short)init_qty;
	}
	
	/**
	 * constrói uma fila de recursos com zero recursos iniciais.
	 */
	public ResourceQ(Scheduler s){super(s, 0);}
	
	/**
	 * Coloca objeto em seu estado inicial para simulação
	 */
	public void Clear()
	{
		super.Clear();
		count = init_qty;;
	}
	
	/**
	 * não utilizada; produz erro em tempo de execução.
	 */
	public void Enqueue(Entity e)
	{
		System.err.println("\nChamou Enqueue() de um objeto ResourceQ!\nEncerrando simulação!");
		Scheduler.Get().Stop();
	}
	
	/**
	 * não utilizada; produz erro em tempo de execução.
	 */
	public Entity Dequeue()
	{
		System.err.println("\nChamou Dequeue() de um objeto ResourceQ!\nEncerrando simulação!");
		Scheduler.Get().Stop();
		return null;
	}
	/**
	 * não utilizada; produz erro em tempo de execução.
	 */
	public void PutBack(Entity e) 
	{
		System.err.println("\nChamou PutBack() de um objeto ResourceQ!\nEncerrando simulação!");
		Scheduler.Get().Stop();
	}
	
	/**
	 * retira n recursos do armazém; se não tem suficientes o resultado é imprevisto.
	 */
	public void Acquire(int n)
	{
		if(obs != null)
			obs.StateChange(Observer.IDLE);		// o argumento não tem sentido nesse caso
		count -= (short)n;
	}

	/**
	 * libera n recursos ao armazém; a integridade da operação é responsabilidade do usuário.
	 */
	public void Release(int n)
	{
		if(obs != null)
			obs.StateChange(Observer.IDLE);		// o argumento não tem sentido nesse caso
		count += (short)n;
	}

}