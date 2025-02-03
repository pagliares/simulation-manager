// Arquivo QueueObserver.java
// Implementação das Classes do Grupo de Resultados da Biblioteca de Simulação JAVA
// 16.Abr.1999	Wladimir

package simula;

public class QueueObserver extends Observer
{
	private DeadState q;					// referência à fila observada
	private float previousobservation = 0;
	private boolean obsqtime;
	
	public QueueObserver(Scheduler s, DeadState q, Histogram h, boolean obsqtime)	
	{ 
		super(s, h); 
		this.q = q;
		this.obsqtime = obsqtime;
		q.SetObserver(this);
	}
	public QueueObserver(Scheduler s, DeadState q, Statistics st, boolean obsqtime)
	{ 
		super(s, st); 
		this.q = q;
		this.obsqtime = obsqtime;
		q.SetObserver(this);
	}
	// construtores

	/**
	 * para filas de entidades, não tem sentido.
	 */
	public void StateChange(short to){}
		
	/**
	 * para filas, realiza estatística do tamanho da fila.
	 */
	public void Incoming(Entity e)
	{
		if(obsqtime)
			return;
		float clock = s.GetClock();
		if(clock == previousobservation)		// já observou neste instante
			return;
		if(hist != null)
			hist.Add(q.ObsLength(), 1);
		else
			stat.Add(clock - previousobservation, q.ObsLength());

		previousobservation = clock;
		
		if(next != null)
			next.Incoming(e);
	}
	
	/**
	 * obseva o tempo de fila ou seu comprimento.
	 */
	public void Outgoing(Entity e)
	{
		if(!obsqtime)
		{
			Incoming(e);	// se não estiver observando o tempo, observo tamanho
			return;
		}
		
		if(hist != null)
			hist.Add(e.GetQTime(), 1);
		else
			stat.Add(e.GetQTime());
		
		if(next != null)
			next.Outgoing(e);
	}
		
}