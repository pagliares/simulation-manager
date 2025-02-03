// Arquivo  QueueEntry.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 21.Mai.1999 Wladimir

package simula.manager;

import java.io.*;

/**
 * Entrada para as filas de entidades do modelo.
 */
public class QueueEntry extends Entry
{
	private static int lastid;	// identificador ÚNICO para as filas
	static boolean hasSerialized = true; // "lastid já foi serializado"
	
	/**
	 * FIFO, STACK, PRIORITY:
	 * constantes que identificam
	 * a política da fila
	 */
	public final static short FIFO = 0;			// constantes que identificam
	public final static short STACK = 1;		// a política da fila
	public final static short PRIORITY = 2;
		
	/**
	 * qtde máx de entidades na fila
	 */
	private short max;
	/**
	 * política da fila
	 */ 
	private short policy;							
		
  transient simula.DeadState SimObj;	// objeto de simulação
                              			// não é serializado
	
	public String toString()
	{
		StringBuffer stb = new StringBuffer();
		stb.append("<QueueEntry max=\""+max+"\" policy=\""+policyString()+"\">\r\n");
		stb.append("<Q_super>\r\n");
		stb.append(super.toString());
		stb.append("</Q_super>\r\n");
		stb.append("</QueueEntry>\r\n");
		return stb.toString();
	}
	String policyString()
	{
		if(policy == FIFO)
		{
			return "FIFO";
		}
		else if(policy == STACK)
		{
			return "STACK";
		}
		else if(policy == PRIORITY)
		{
			return "PRIORITY";
		}
		return "POLICY??";
	}
  /**
   * constrói um objeto com id gerado internamente;
   * preenche com argumentos padrão os demais campos.
   */
	public QueueEntry()
	{
		super("q_" + String.valueOf(lastid));
		lastid++;
    max = (short)10;
    policy = FIFO;
	}
	
	public void copyAttributes(Entry v_e)
	{
		super.copyAttributes(v_e);
		QueueEntry qEntry = (QueueEntry)v_e;
		max = qEntry.max;
		policy = qEntry.policy;
		SimObj = qEntry.SimObj;
	}
	
	public final short getMax(){	return max;	}
	public final short getPolicy(){	return policy;	}
	public final void setMax(short v_sMax){	max = v_sMax;	}
	public final void setPolicy(short v_sPolicy){	policy = v_sPolicy;	}
	
	
	boolean Generate(SimulationManager m)
	{
		switch(policy)
		{
			case FIFO: SimObj = new simula.FifoQ(m.s, max); break;
			case STACK: SimObj = new simula.StackQ(m.s, max); break;
			case PRIORITY: SimObj = new simula.PriorityQ(m.s, max); break;
			default: return false;
		}

		SimObj.name = name;
				
		if(obsid != null)
			return m.GetObserver(obsid).Generate(m);
			
		return true;
	}
	
	private void writeObject(ObjectOutputStream stream)
     throws IOException
	{
		stream.defaultWriteObject();
		
		if(hasSerialized)
			return;
			
		stream.writeInt(lastid);
		hasSerialized = true;
	}
 	private void readObject(ObjectInputStream stream)
     throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		
		if(hasSerialized)
			return;
			
		lastid = stream.readInt();
		hasSerialized = true;
	}
}