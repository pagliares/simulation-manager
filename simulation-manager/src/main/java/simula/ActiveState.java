// Arquivo ActiveState.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

public abstract class ActiveState
{
	/**
	 * referência ao Scheduler para registro e chamadas
	 */
	protected Scheduler s;		
	/**
	 * observador (para estatísticas)
	 */
	protected ActiveObserver obs;
	/**
	 * nome (para identificar estado para o log)
	 */
	public String name = "";		

	/**
	 * registra com o Scheduler o estado ativo para ser servido na fase B 
	 * daqui a time instantes; retorna o instante absoluto em que ocorrerá o serviço.
	 */
	final protected float RegisterEvent(float time)
	{
		return s.ScheduleEvent(this, time);
	}

	/**
	 * referencia Scheduler para registrar-se e registrar eventos da fase B.
	 */
	public ActiveState(Scheduler s)
	{
		this.s = s;
		s.Register(this);
	}

	/**
	 * associa observador
	 */
	public void SetObserver(ActiveObserver o)
	{
		if(obs == null)
			obs = o;
		else
			obs.Link(o);
	}
	
	/**
	 * Coloca objeto em seu estado inicial para simulação
	 */
	public void Clear()
	{
		if(obs == null)
			return;
		obs.Clear();
	}
	
	/**
	 * executa fase B com as entidades que devem ser servidas no tempo se simulação time;
	 * retorna true se alguém foi servido.
	 */
	public abstract boolean BServed(float time);
	/**
	 * executa a fase C; retorna true se algum evento condicional ocorreu.
	 */
	public abstract boolean CServed();
}