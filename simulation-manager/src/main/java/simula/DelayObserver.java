// Arquivo DelayObserver.java
// Implementação das Classes do Grupo de Resultados da Biblioteca de Simulação JAVA
// 11.Jun.1999	Wladimir

package simula;

public class DelayObserver extends ActiveObserver
{
	private boolean stamp;
	private boolean entering;

	/**
	 * construtor que determina se esse é um observador que mede o delay 
	 * ou se é um que marca a entidade com o tempo (stamp = true) e se
	 * a ação deve ser tomada qdo a entidade entra (entering = true) 
	 * ou sai do ActiveState (entering = false).
	 */
	public DelayObserver(Scheduler s, ActiveState a, Statistics st, boolean stamp, boolean entering)
	{ super(s, a, st, null); this.stamp = stamp; this.entering = entering;}
	
	public DelayObserver(Scheduler s, ActiveState a, Histogram h, boolean stamp, boolean entering)
	{ super(s, a, h, null); this.stamp = stamp; this.entering = entering;}

	/**
	 * sem sentido
	 */
	public void StateChange(short to)
	{
		if(next != null)
			next.StateChange(to);	
	}

	/**
	 * atribui timestamp ou mede delay, de acordo com stamp
	 */
	private void Execute(Entity e)
	{ 
		if(stamp)
			e.Stamp(s.GetClock());
		else
		{
			if(hist != null)
				hist.Add(s.GetClock() - e.GetTimestamp(), 1);
			else
				stat.Add(s.GetClock() - e.GetTimestamp());
		}
	}

	/**
	 * processa entidade e manda para próximo observer da lista
	 */
	public void Incoming(Entity e)
	{
		if(entering)	// se deve ser processada na entrada...
			Execute(e);
		if(next != null)
			next.Incoming(e);
	}

	/**
	 * processa entidade e manda para próximo observer da lista
	 */
	public void Outgoing(Entity e)
	{
		if(!entering)	// se deve ser processada na saída...
			Execute(e);
		if(next != null)
			next.Outgoing(e);
	}

}