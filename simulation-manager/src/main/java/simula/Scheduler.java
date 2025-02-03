// Arquivo Scheduler.java
// Implementação das Classes do Grupo Executivo da Biblioteca de Simulação JAVA
// 19.Mar.1999	Wladimir

package simula;

import java.util.*;

public class Scheduler implements Runnable
{
	private Calendar calendar;		// estrutura de armazenamento dos estados ativos a servir
	private float clock = 0;		// relógio da simulação
	private float endclock;			// fim da simulação
	private float timeprecision;	// diferença mínima que deve haver entre dois instantes
									// para que sejam considerados diferentes 
	private Vector activestates;	// Vetor dos estados ativos da simulação
	private boolean crescan = true;	// flag de habilitação de re-varrudura dos eventos C
	private volatile boolean running = false;
									// controla se a simulação deve continuar rodando
	private boolean stopped = false;// indica se a simulação parou conforme ordenado
	private Thread simulation;		// thread em que a simulação irá executar
	private byte termreason;		// porque encerrou a simulação
	
	private static Scheduler s;		// uma referência estática ao Scheduler
									// para permitir ações de emergência (parada)

	/**
	 * retorna referência ao objeto ativo
	 */
	public static Scheduler Get(){return s;}

	public Scheduler()
	{
		activestates = new Vector(20, 10);
		calendar = new Calendar();
		timeprecision = (float)0.001;
		s = this;
	}
	
	/**
	 * Coloca objeto em seu estado inicial para simulação
	 * Apaga todos os eventos agendados. Deve ser chamado ANTES
	 * de todos os Clear() dos Active/DeadState
	 */
	public void Clear()
	{
		if(running)
			return;
		simulation = null;	// impede continuação
		clock = 0;					// reinicia relógio
		stopped = false;
		termreason = 0;
		calendar = new Calendar();
	}

	
	float ScheduleEvent(ActiveState a, double duetime)
	{
		double time = clock + duetime;
		time = Math.floor(time / timeprecision);
		time *= timeprecision;					// trunca parte menor que timeprecision
		calendar.Add(a, time);
		return (float)time;							// retorna instante realmente utilizado
	}
	
	void Register(ActiveState a)
	{
		if(!activestates.contains(a))
			activestates.addElement(a);
	}
	
	/**
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	public void CRescan(boolean on)
	{	
		if(!running)
			crescan = on;
	}

	/**
	 * retorna true se a simulação terminou
	 */
	public boolean Finished(){return stopped;}
	
	/**
	 * Inicia execuçao da simulacao numa thread separada
	 */
	public synchronized boolean Run(double endtime)
	{
		if(endtime < 0.0)				// relógio não pode ser negativo
			return false;				// se for 0.0 executa até acabarem as entidades

		if(!running)
		{
			if(activestates.isEmpty())	// se não há nenhum estado ativo registrado, 
				return false;			// como executar?
			activestates.trimToSize();
			running = true;
			stopped = false;
			endclock = (float)endtime;
			clock = 0;
			termreason = 0;
			simulation = new Thread(this);
			simulation.setPriority(Thread.MAX_PRIORITY);
			simulation.start();			// inicia execução
			Log.LogMessage("Scheduler: simulation started");

			return true;
		}

		return false;
	}
	
	/**
	 * Pára a simulacao
	 */
	public synchronized void Stop()
	{
		if(stopped)						// se já parou
			return;
		
		stopped = false;
		running = false;				// encerramento suave
		try
		{
			simulation.join(5000);		// espera até 5 segundos
		}
		catch(InterruptedException e)
		{
			stopped = true;
			simulation = null;	// não pode continuar
			termreason = 4;			// parada drástica
			Log.LogMessage("Scheduler: simulation stopped drastically");
			Log.Close();
			return;							// já parou mesmo...
		}
		
		termreason = 3;			// parada suave bem sucedida
			
		if(!stopped)					// se ainda não parou...
		{
			try
			{
				simulation.interrupt();		// pára de forma drástica
			}
			catch(SecurityException e){}
			
			simulation = null;	// não pode continuar
			termreason = 4;
			Log.LogMessage("Scheduler: simulation stopped drastically");
			Log.Close();
		}
		
		Log.LogMessage("Scheduler: simulation paused");
		
	}
	
	/**
	 * Continua a execução de uma simulação parada por Stop()
	 */
	public synchronized boolean Resume()
	{
		if(running || simulation == null || termreason != 3)
			return false;
		
		running = true;
		stopped = false;	
		simulation.start();
		termreason = 0;
		Log.LogMessage("Scheduler: simulation resumed");
		
		return true;
	}
	
	/**
	 * Seta o mínimo intervalo entre dois instantes para que sejam considerados o mesmo.
	 */
	public void SetPrecision(double timeprec)
	{ 
		if(!running)
			timeprecision = (float)timeprec;
	}

	/**
	 * retorna relógio da simulação.
	 */
	public float GetClock(){return clock;}

	/**
	 * Código que roda a simulacao. (rodado numa Thread separada)
	 */
	public void run()
	{
		while(running)
		{
			// atualiza relógio da simulação

			clock = calendar.GetNextClock();
			
			Log.LogMessage("Scheduler: clock advanced to " + clock);

			// verifica se simulação chegou ao fim

			if(clock == 0.0)			// fim das entidades
			{
				running = false;
				termreason = 1;			
				Log.LogMessage("Scheduler: simulation finished due to end of entities");
				Log.Close();
				break;
			}
			if(clock >= endclock && endclock != 0.0)	// fim do intervalo
			{
				running = false;
				termreason = 2;
				Log.LogMessage("Scheduler: simulation finished due to end of simulation time");
				Log.Close();
				break;
			}

			boolean executed;			// se algum evento B ou C foi executado
			
			// Fase B
			
			executed = false;
			ActiveState a;

			do
			{
				a = calendar.GetNext();
				executed |= a.BServed(clock);	// se ao menos um executou, fica registrado
			}while(calendar.RemoveNext());

			if(!executed)				// se não havia nada a ser executado nesse instante
				continue;				// pula para o próximo sem executar a fase C.
										// (as atividades podem ter alterado o tempo localmente)
						
			// Fase C

			do
			{
				executed = false;

				for(short i = 0; i < activestates.size(); i++)
					executed |= ((ActiveState)activestates.elementAt(i)).CServed();
				
			}while(crescan && executed);	
		}

		stopped = true;			// sinaliza o encerramento
		running = false;
	}
}