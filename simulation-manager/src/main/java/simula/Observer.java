// Arquivo Observer.java
// Implementa��o das Classes do Grupo de Resultados da Biblioteca de Simula��o JAVA
// 16.Abr.1999	Wladimir

package simula;

/**
 * Implements an observer of a System's Entry
 */
public abstract class Observer
{
	Histogram hist;
	Statistics stat;
	protected Scheduler s;
	protected Observer next;

	/**
	 * IDLE, BUSY:
	 * constantes que definem estado de recursos
	 * e estados ativos 
	 */
	public static final short IDLE = 0, BUSY = 1;	

	public Observer(Scheduler s, Histogram h){ this.s = s; hist = h;}
	public Observer(Scheduler s, Statistics st){ this.s = s; stat = st;}
	
	/**
	 * liga em uma lista os observadores
	 */
	public final void Link(Observer obs)
	{
		if(next == null)
			next = obs;
		else
			next.Link(obs);
	}
	
	/**
	 * Coloca objeto em seu estado inicial para simula��o
	 */
	public void Clear()
	{
		if(hist != null)	// limpa estat�sticas
			hist.Clear();
		if(stat != null)
			stat.Clear();
		if(next != null)	// e todos observers ligados
			next.Clear();
	}

	/**
	 * informa a observer que houve mudan�a no estado do ResourceQ ou ActiveState associado.
	 */
	abstract public void StateChange(short to);
	
	/**
	 * realiza processamento na entidade e que acaba de chegar ao Active/DeadState;
	 * tamb�m realiza estat�sticas quando aplic�vel.
	 */
	abstract public void Incoming(Entity e);
	
	/**
	 * realiza processamento na entidade e prestes a sair ao Active/DeadState;
	 * tamb�m realiza estat�sticas quando aplic�vel.
	 */
	abstract public void Outgoing(Entity e);

	/**
	 * calcula a m�dia.
	 */
	public final float Mean()
	{
		if(hist == null)
			return stat.Mean();
		return hist.Mean();
	}

	/**
	 * calcula o desvio padr�o; retorna 0 se n�o houverem dados suficientes.
	 */
	public final float StdDev()
	{
		if(hist == null)
			return stat.StdDev();
		return hist.StdDev();
	}

	/**
	 * calcula a vari�ncia; retorna 0 se n�o houverem dados suficientes.
	 */
	public final float Variance()
	{
		if(hist == null)
			return stat.Variance();
		return hist.Variance();
	}

	/**
	 * retorna m�ximo valor observado.
	 */
	public final float Max()
	{
		if(hist == null)
			return stat.Max();
		return hist.Max();
	}
	
	/**
	 * retorna m�nimo valor observado.
	 */
	public final float Min()
	{
		if(hist == null)
			return stat.Min();
		return hist.Min();
	}
	
	/**
	 * retorna o n�mero de observa��es
	 */
	public final int NumObs()
	{
		if(hist == null)
			return stat.NumObs();
		return hist.NumObs();
	}	

}