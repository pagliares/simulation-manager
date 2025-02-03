// Arquivo  SimulationManager.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 11.Jun.1999 Wladimir

package simula.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.io.*;
import simula.*;

/**
 * Classe principal do sistema de gerenciamento. Concentra todos os
 * pedidos de criação e remoção de Entry's no modelo. 
 * Verifica consistência e provê controle de concorrência.
 * Gerencia diferentes repositórios de entry's de diversos tipos
 * e gera programa de simulação. 
 * @author	Wladimir
 */
public class SimulationManager implements Serializable
{
	private HashMap queues, resources, activestates,
								 observers, histograms;

	private Vector types;
	private AttributeTable globals;
	
	private transient boolean running = false;
	transient Scheduler s;
	transient Sample sp;
	private transient float endtime = 0;	// instante programado 
											// de término da simulação
	private transient float resettime = 0;  // instante em que as 

	/**
	 * Creates a new, ready to use SimulationManager
	 */
	public SimulationManager()
	{
		queues = new HashMap();
		resources  = new HashMap();
		activestates = new HashMap();
		observers = new HashMap();
		histograms = new HashMap();
		types = new Vector(5, 2);
		globals = new AttributeTable();
		globals.name = "globals";	// nome padrão 
	}
	
	public String toString()
	{
		StringBuffer stb = new StringBuffer();
		stb.append("<SimulationManager>\r\n");
		stb.append("<SM_queues>\r\n");
		appendIterator(queues.values().iterator(), stb);
		stb.append("</SM_queues>\r\n");
		stb.append("<SM_resources>\r\n");
		appendIterator(resources.values().iterator(), stb);
		stb.append("</SM_resources>\r\n");
		stb.append("<SM_activestates>\r\n");
		appendIterator(activestates.values().iterator(), stb);
		stb.append("</SM_activestates>\r\n");
		stb.append("<SM_observers>\r\n");
		appendIterator(observers.values().iterator(), stb);
		stb.append("</SM_observers>\r\n");
		stb.append("<SM_histograms>\r\n");
		appendIterator(histograms.values().iterator(), stb);
		stb.append("</SM_histograms>\r\n");
		stb.append("<SM_types>\r\n");
		appendVector(types, stb);
		stb.append("</SM_types>\r\n");
		stb.append("<SM_globals>\r\n");
		stb.append(globals);
		stb.append("</SM_globals>\r\n");
		stb.append("</SimulationManager>\r\n");
		return stb.toString();
	}
	
	public static void appendIterator(Iterator v_it, StringBuffer v_stb)
	{
		while(v_it.hasNext())
		{
			v_stb.append(v_it.next()+"\r\n");
		}
	}
	
	public static void appendVector(Vector v_vec, StringBuffer v_stb)
	{
		int iN = v_vec.size();
		for(int i=0; i<iN; i++)
		{
			v_stb.append(v_vec.elementAt(i)+"\r\n");
		}
	}
	
	/**
	 * Adds a Queue to the system
	 */
	public boolean AddQueue(QueueEntry e)
	{
		synchronized(queues)
		{
			if(queues.containsKey(e.id))
				return false;
			
			queues.put(e.id, e);
		}
		
		return true;
	}
	
	/**
	 * Adds a Resource to the system
	 */
	public boolean AddResource(ResourceEntry e)
	{
		synchronized(resources)
		{
			if(resources.containsKey(e.id))
				return false;
			
			resources.put(e.id, e);
		}	
		return true;
	}
	
	/**
	 * Adds an ActiveState to the system
	 */
	public boolean AddActiveState(ActiveEntry e)
	{
		synchronized(activestates)
		{
			if(activestates.containsKey(e.id))
				return false;
			
			activestates.put(e.id, e);
		}
		return true;
	}
	
	/**
	 * Adds an Observer to the System
	 */
	public boolean AddObserver(ObserverEntry e)
	{
		synchronized(observers)
		{
			if(observers.containsKey(e.id))
				return false;
			
			observers.put(e.id, e);
		}
		return true;
	}

	
	/**
	 * Adds a Histogram to the system
	 */
	public boolean AddHistogram(HistogramEntry e)
	{
		synchronized(histograms)
		{
			if(histograms.containsKey(e.id))
				return false;
			
			histograms.put(e.id, e);
		}	
		return true;
	}
	
	/**
	 * Adds an AttributeTable to the system
	 */
	public boolean AddType(AttributeTable type)
	{
		synchronized(types)
		{
			if(types.contains(type))
				return false;
			
			types.add(type);
		}	
		return true;
	}
		
	/**
	 * Atualiza variáveis globais.
	 */
	public boolean UpdateGlobals(AttributeTable globalVars)
	{
		if(globalVars == null)
			return false;
			
		if(!globals.id.equals(globalVars.id))
			return false;
			
		globals = globalVars;
		
		return true;
	}
	
	/**
	 * Removes a Queue from the system,
	 * updating the related entities properly
	 */
	public void RemoveQueue(String id){	RemoveQueue(id, true, true);	}
	public void RemoveQueue(String id, boolean v_bRemoveObservers, boolean v_bRemovePointingActiveStates)
	{
		QueueEntry e;
		
		synchronized(queues)
		{
			e = (QueueEntry)queues.remove(id);
		}
		
		if(e != null)
		{
			// remove todos os observadores
			if(e.obsid != null && v_bRemoveObservers)
			{
				synchronized(observers)
				{
					String oid = e.obsid; //existe uma lista encadeada de observadores dentro da tabela de observadores
					ObserverEntry oe = (ObserverEntry)observers.remove(oid);

					while(oe != null)
					{
						oid = oe.obsid;
						oe = (ObserverEntry)observers.remove(oid);
					}
				}
			}
			if(v_bRemovePointingActiveStates)
			{
				synchronized(activestates)
				{
					// remove todas as referências a esse queue
					Iterator it; // para percorrer todos os active states
						
					it = activestates.values().iterator();
					ActiveEntry ae;
						
					while(it.hasNext())
					{
						ae = (ActiveEntry)it.next();
						if(ae.internal)
						{
							InternalActiveEntry ia = (InternalActiveEntry)ae;
							if(ia.router)
							{
								// remove se for fonte ou destino
								ia.removeFromQueue(e.id);
								int i = ia.toQueueIndexOf(e.id);
								if(i != -1) // se é destino, remove tb a condição
								{
									ia.removeToQueue(i);
									ia.removeCond(i);
								}
							}
							else
							{
							int i;
								if(ia.fromQueueContains(e.id))
								{
									i = ia.fromQueueIndexOf(e.id);
									ia.removeFromQueue(i);
									ia.removeToQueue(i);
									ia.removeCond(i);
								}
							if(ia.toQueueContains(e.id))
								{
									i = ia.toQueueIndexOf(e.id);
									ia.removeFromQueue(i);
									ia.removeToQueue(i);
									ia.removeCond(i);
								}
							}
						}
						else
						{
	//		System.out.println("removequeue8.1 "+((ExternalActiveEntry)ae).qid+" "+e.id);
							//boolean bTest = ((ExternalActiveEntry)ae).qid.equals(e.id);
							if(e.id.equals(((ExternalActiveEntry)ae).getQID()))
							{
								((ExternalActiveEntry)ae).setQID(null); //GOTCHA! aqui que tu tah anulando essa porra!!!!
							}
						}
					}
				}
			}
		}
	}
		
	/**
	 * Removes a Resource from the system
	 * updating the related entities properly
	 */
	public void RemoveResource(String id)
	{
		ResourceEntry e;
		
		synchronized(resources)
		{
			e = (ResourceEntry)resources.remove(id);
		}
		if(e != null)
		{
			// remove o observador
			if(e.obsid != null)
			{
				synchronized(observers)
				{
					observers.remove(e.obsid);
				}
			}
			
			synchronized(activestates)
			{
				// remove todas as referências a esse queue
				Iterator it; // para percorrer todos os active states
				
				it = activestates.values().iterator();
				ActiveEntry ae;
				
				while(it.hasNext())
				{
					ae = (ActiveEntry)it.next();
					if(ae.internal)
					{
						InternalActiveEntry ia = (InternalActiveEntry)ae;
						int i;
						
						i = ia.fromResourceIndexOf(e.id);
						if(i != -1)
						{
							ia.removeFromResource(i);
							ia.removeToResource(i);
							ia.removeResourceQty(i);
						}

						i = ia.toResourceIndexOf(e.id);
						if(i != -1)
						{
							ia.removeFromResource(i);
							ia.removeToResource(i);
							ia.removeResourceQty(i);
						}
					}
				}
			}	
		}
	}
		
	/**
	 * Removes an ActiveState from the system
	 * updating the related entities properly
	 */
	public void RemoveActiveState(String id)
	{
		ActiveEntry ae;
		
		synchronized(activestates)
		{
			ae = (ActiveEntry)activestates.remove(id);
		}
		
		if(ae != null)
		{
			// remove todos os observadores
			if(ae.obsid != null)
			{
				synchronized(observers)
				{
					String oid = ae.obsid;				
					ObserverEntry oe = (ObserverEntry)observers.remove(oid);

					while(oe != null)
					{
						oid = oe.obsid;
						oe = (ObserverEntry)observers.remove(oid);
					}
				}
			}
			if(ae instanceof InterruptActiveEntry)
			{
				synchronized(activestates)
				{
					Iterator it = activestates.values().iterator();
					while(it.hasNext())
					{
						ae = (ActiveEntry)it.next();
						if(ae instanceof InterruptActiveEntry)
							((InterruptActiveEntry)ae).removeInterrupt(id);
					}
				}
			}
		}
	}
		
	/**
	 * Removes an Observer from the system
	 * updating the related entities properly
	 */
	public void RemoveObserver(String id)
	{
		ObserverEntry oe;
		
		synchronized(observers)
		{
			oe = (ObserverEntry)observers.remove(id);
		}
		
		if(oe != null)
		{
			Entry e;
			switch(oe.getType())
			{
				case ObserverEntry.RESOURCE: 
					e = (Entry)resources.get(oe.getObserved());
					break;
				case ObserverEntry.QUEUE: 
					e = (Entry)queues.get(oe.getObserved());
					break;
				case ObserverEntry.ACTIVE: 
				case ObserverEntry.PROCESSOR:
				case ObserverEntry.DELAY:
					e = (Entry)activestates.get(oe.getObserved());
					break;
				default: return;
			}
			while(e.obsid != null && !e.obsid.equals(id))	// encontra o anterior na lista 
			{
				e = (Entry)observers.get(e.obsid);
			}
			e.obsid = oe.obsid;		// liga com o próximo
			if(oe.getHistid() != null)
				RemoveHistogram(oe.getHistid());
		}
	}
			
	/**
	 * Removes a Histogram from the system
	 * updating the related entities properly
	 */
	public void RemoveHistogram(String id)
	{
		HistogramEntry he;
		
		synchronized(histograms)
		{
			he = (HistogramEntry)histograms.remove(id);
		}
		
		if(he == null)
			return;
			
		if(he.obsid != null)
		{
			ObserverEntry oe = GetObserver(he.obsid);
			if(oe != null)
				oe.setHistid(null);
		}
	}
	
	/**
	 * Removes an AttributeTable from the system
	 */
	public void RemoveType(String id)
	{
		synchronized(types)
		{
			Iterator it = types.iterator();
			while(it.hasNext())
			{
				if(((AttributeTable)it.next()).id == id)
				{
					it.remove();
					break;
				}	
			}
		}	
	}
	// Remove entrada do repositório respectivo e suas associadas
	// de forma a manter a consistência
	
	/**
	 * Returns a Queue given its ID
	 */
	public QueueEntry GetQueue(String id)
	{return (QueueEntry)queues.get(id);}
		
	/**
	 * Returns a Resource given its ID
	 */
	public ResourceEntry GetResource(String id)
	{return (ResourceEntry)resources.get(id);}
		
	/**
	 * Returns an ActiveState given its ID
	 */
	public ActiveEntry GetActiveState(String id)
	{return (ActiveEntry)activestates.get(id);}
	
	/**
	 * Returns an iterator to the ActiveStates HashMap
	 */
	public Iterator GetActiveStatesIterator(){	return activestates.values().iterator();	}
		
	/**
	 * Returns an Observer given its ID
	 */
	public ObserverEntry GetObserver(String id)
	{return (ObserverEntry)observers.get(id);}
		
	/**
	 * Returns a Histogram given its ID
	 */
	public HistogramEntry GetHistogram(String id)
	{return (HistogramEntry)histograms.get(id);}
	
	/**
	 * Returns an AttributeTable given its TypeID
	 * TypeID is the user-visible, and editable field.
	 */
	public AttributeTable GetType(String id)
	// Este é o único caso em que o Id único não é usado para indexação,
	// e sim o name, que é o campo visto e alterado pelo usuário.
	{
		AttributeTable e = null;
		synchronized(types)
		{
			Iterator it = types.iterator();
			while(it.hasNext())
			{
				if((e = (AttributeTable)it.next()).name.equals(id))
					return e;
			}
		}	
		
		return null;
	}
	
	/**
	 * Obtém entrada do repositório respectivo através de seu ID único
	 */
	public AttributeTable GetGlobals(){return globals;}
		
	/**
	 * Gera modelo de simulação e prepara para execução
	 */
	public synchronized boolean GenerateModel()
	{
//System.out.println("simulationmanager.GenerateModel");
		Iterator it;
		
		// 1.o cria o Scheduler
		
		s = new Scheduler();
		
		// logo depois a stream de números aleatórios
		
		sp = new Sample();
		
		synchronized(queues){
			synchronized(activestates){
				synchronized(resources){
					synchronized(observers){
						synchronized(histograms)
		// ninguém pode estar sendo alterado
		{
			QueueEntry qe;
			ActiveEntry ae;
			ResourceEntry re;
			ObserverEntry oe;
			HistogramEntry he;
			
			// agora os estados mortos

			it = queues.values().iterator();
			while(it.hasNext())
			{
				qe = (QueueEntry)it.next();
				if(!qe.Generate(this))
				{
					System.err.println("Impossível criar fila " + qe.id);
					return false;
				}
			}
			
			it = resources.values().iterator();
			while(it.hasNext())
			{
				re = (ResourceEntry)it.next();
				if(!re.Generate(this))
				{
					System.err.println("Impossível criar recurso " + re.id);
					return false;
				}
			}

			// daí os ativos
			
			// 1.o limpa os objs de simulação (devido às InterruptActivity's)
			it = activestates.values().iterator();
			while(it.hasNext())
			{
				ae = (ActiveEntry)it.next();
				ae.SimObj = null;
			}

			it = activestates.values().iterator();
			while(it.hasNext())
			{
				ae = (ActiveEntry)it.next();
				if(!ae.Generate(this))
				{
					System.err.println("Impossível criar estado ativo " + ae.id);
					return false;
				}
			}
		}
					}
				}
			}
		}
		
		// cria, por último, as variáveis globais
		
		Expression.globals = new Variables();
		
		QueueEntry qe = null;
		ResourceEntry re = null;
		HashMap deads = new HashMap(queues.size() + resources.size());
		
		it = queues.values().iterator();
		while(it.hasNext())
		{
			qe = (QueueEntry)it.next();
			deads.put(qe.name, qe.SimObj);		
		}
		it = resources.values().iterator();
		while(it.hasNext())
		{
			re = (ResourceEntry)it.next();
			deads.put(re.name, re.SimObj);		
		}
		
		Expression.globals.AssignQueuesTable(deads);
		
		Var var = null;
		it = globals.getVarsIterator();
		while(it.hasNext())
		{
			var = (Var)it.next();
			Expression.globals.CreateVar(var.id, var.value);
		}

		return true;
	}		
	
	/**
	 * Executa simulação até instante endTime
	 */
	public synchronized boolean ExecuteSimulation(float endTime)
	{
		boolean ok = false;
		
		if(endTime >= 0 && s != null)	// o modelo já deve ter sido gerado
		{
			Log.Close();
			Log.OpenFile();
			ok = s.Run(endTime);
			if(ok)
			{
				endtime = endTime;
				running = true;
			}
		}
		
		return ok;
	}
		
	/**
	 * Coloca todos os objetos da simulação em seus estados iniciais
	 */
	public synchronized boolean ResetSimulation()
	{
		if(s == null || running)
			return false;	// modelo precisa ser gerado antes
		
		s.Clear();
		resettime = 0;
		
		Iterator it;
		
		it = queues.values().iterator();
		while(it.hasNext())
		{
			((QueueEntry)it.next()).SimObj.Clear();
		}
		it = activestates.values().iterator();
		while(it.hasNext())
		{
			((ActiveEntry)it.next()).SimObj.Clear();
		}
		it = resources.values().iterator();
		while(it.hasNext())
		{
			((ResourceEntry)it.next()).SimObj.Clear();
		}
		
		return true;
	}
		
	/**
	 * Limpa todos os objetos estatísticos, mesmo durante a simulação
	 * a simulação deve estar pausada (Stop() suave do scheduler)
	 */
	public synchronized boolean ResetStatistics()
	{
		if(s == null || running)
			return false;	// modelo precisa ser gerado antes e  
										// não pode estar executando
		Iterator it;
		resettime = s.GetClock();
		
		it = observers.values().iterator();
		while(it.hasNext())
		{
			((ObserverEntry)it.next()).SimObj.Clear();
		}
		
		return true;
	}
		
	/**
	 * Interrompe simulação
	 */
	public synchronized void StopSimulation()
	{
		if(running)
			s.Stop();		// pára
				
		running = false;
	}
	
	/**
	 * Continua a simulação, se possível
	 */
	public synchronized boolean ResumeSimulation()
	{
		if(s == null)
			return false;
			
		return s.Resume();
	}
	
	/**
	 * Verifica se a simulação ainda está executando
	 */
	public boolean Finished()
	{
		if(s == null) 
			return true;
			
		return s.Finished();
	}
	
	/**
	 * Depois da simulação terminada, escreve os resultados
	 * das estatísticas nos arquivos de saída
	 */
	public synchronized boolean OutputSimulationResults(String filename)
	{
		System.out.println("OutputSimulationResults "+filename);
		PrintStream os;
		FileOutputStream ofile;
		
		try
		{
			ofile = new FileOutputStream(filename);
			os = new PrintStream(ofile);
		}
		catch(FileNotFoundException x)
		{
			return false;
		}
		
		os.println("\r\n                    Simulation Report");
		os.print("\r\nSimulation ended at time ");
		os.println(s.GetClock());
		os.print("Statistics collected from instant " + resettime);
		os.println(" during " + (s.GetClock() - resettime) + " time units.");
		
		os.println("\r\n          Observers' report");
		
		Iterator it;
		
		it = observers.values().iterator();
		while(it.hasNext())
		{
			((ObserverEntry)it.next()).DoReport(os, s.GetClock() - resettime);
		}
		
		os.println("\r\n          Histograms' report");

		it = histograms.values().iterator();
		while(it.hasNext())
		{
			((HistogramEntry)it.next()).DoReport(os);
		}
		
		os.println("\r\nSimulation Report End");
		
		os.close();
		
		return true;
	}
		
	private void writeObject(ObjectOutputStream stream)
     throws IOException
	{
		ActiveEntry.hasSerialized = false;
		ResourceEntry.hasSerialized = false;
		QueueEntry.hasSerialized = false;
		ObserverEntry.hasSerialized = false;
		HistogramEntry.hasSerialized = false;
		AttributeTable.hasSerialized = false;
		
		stream.defaultWriteObject();
		
		ActiveEntry.hasSerialized = true;
		ResourceEntry.hasSerialized = true;
		QueueEntry.hasSerialized = true;
		ObserverEntry.hasSerialized = true;
		HistogramEntry.hasSerialized = true;
		AttributeTable.hasSerialized = true;
	}
 	private void readObject(ObjectInputStream stream)
     throws IOException, ClassNotFoundException
	{
		ActiveEntry.hasSerialized = false;
		ResourceEntry.hasSerialized = false;
		QueueEntry.hasSerialized = false;
		ObserverEntry.hasSerialized = false;
		HistogramEntry.hasSerialized = false;
		AttributeTable.hasSerialized = false;
		
		stream.defaultReadObject();
		
		ActiveEntry.hasSerialized = true;
		ResourceEntry.hasSerialized = true;
		QueueEntry.hasSerialized = true;
		ObserverEntry.hasSerialized = true;
		HistogramEntry.hasSerialized = true;
		AttributeTable.hasSerialized = true;
	}
}	