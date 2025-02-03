// Arquivo ResourceObserver.java
// Implementação das Classes do Grupo de Resultados da Biblioteca de Simulação JAVA
// 16.Abr.1999	Wladimir

package simula;

public class ResourceObserver extends Observer
{
	private ResourceQ q;
	private float previousobservation = 0;

	public ResourceObserver(Scheduler s, ResourceQ q, Statistics st)
	// construtor
	{ super(s, st); this.q = q; q.SetObserver(this);}

	/**
	 * faz estatística da quantidade de recursos ociosos.
	 */
	public void StateChange(short to)
	{
		float clock = s.GetClock();

		if(previousobservation == clock)	// já observou neste instante
			return;
		
		stat.Add(clock - previousobservation, q.ObsLength());
		previousobservation = clock;
	}

	/**
	 * desnecessário para esse caso.
	 */
	public void Incoming(Entity e){}
	/**
	 * desnecessário para esse caso.
	 */
	public void Outgoing(Entity e){}
}