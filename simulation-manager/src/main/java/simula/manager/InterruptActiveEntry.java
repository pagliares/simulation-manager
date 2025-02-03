// Arquivo  InterruptActiveEntry.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 21.Mai.1999 Wladimir

package simula.manager;

import java.util.Vector;
import simula.InterruptActivity;

/**
 * Entrada para as Activity's Interruptable's.
 */
public class InterruptActiveEntry extends InternalActiveEntry
{
	
	/**
	 * vetor das Interruptable's 
	 * que pode interromper
	 */
  private Vector interrupts;			
  
  public String toString()
  {
	StringBuffer stb = new StringBuffer();
	stb.append("<InterruptActiveEntry>\r\n");
	stb.append("<IntA_super>\r\n");
	stb.append(super.toString());
	stb.append("<interrupts>\r\n");
	SimulationManager.appendVector(interrupts, stb);
	stb.append("</interrupts>\r\n");
	stb.append("</IntA_super>\r\n");
	stb.append("</InterruptActiveEntry>\r\n");
	return stb.toString();
  }
  /**
   * constrói um objeto com id gerado internamente.
   */
  public InterruptActiveEntry()
  {
    super(false); // é uma Activity
   	interrupts = new Vector(2, 2);
  }
  
  public void copyAttributes(Entry v_e)
  {
	super.copyAttributes(v_e);
	InterruptActiveEntry intpEntry = (InterruptActiveEntry)v_e;
	interrupts = intpEntry.interrupts;
  }
  
  boolean Generate(SimulationManager m)
	{
		SimObj = new InterruptActivity(m.s);

		return Setup(m);
	}

  /**
   * Ajusta os parâmetros referentes aos Router's e Activity's
   */
  protected boolean Setup(SimulationManager m)
  {
		if(!super.Setup(m))
			return false;
			
		InterruptActiveEntry e = null;
		
		for(int i = 0; i < interrupts.size(); i++)
		{
			e = (InterruptActiveEntry)m.GetActiveState((String)interrupts.get(i));
			if(e.SimObj == null)	// se ainda não foi gerado
				if(!e.Generate(m))	// gera
					return false;			
			((InterruptActivity)SimObj).
				AddInterruptable((InterruptActivity)e.SimObj);	
		}
		
		return true;
  }

  public void addInterrupt(Object v_o){	interrupts.add(v_o);	}
  public void removeInterrupt(Object v_o){	interrupts.remove(v_o);	}
  public Vector getInterrupts(){	return interrupts;	}
  
  /**
   * chama trimToSize() para cada Vector interno
   * para economizar memória alocada
   */
  public void TrimVectors()
	{
		super.TrimVectors();
		interrupts.trimToSize();
	}
}