// Arquivo  ActiveEntry.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 21.Mai.1999 Wladimir

package simula.manager;

import java.io.*;

/**
 * Classe base para todos os estados ativos do modelo. 
 */
public abstract class ActiveEntry extends Entry
{
  private static int lastid;  // identificador ÚNICO para os 
                              // estados ativos 
	static boolean hasSerialized = true; // "lastid já foi serializado"
		
  transient simula.ActiveState SimObj;	// objeto de simulação
                              					// não é serializado
  boolean internal;						// se é um estado ativo interno ou externo
                              					
	/**
	 * FIFO, STACK, PRIORITY:
	 * constantes que identificam
	 * as distribuições de serviço
	 */
  public static final short NONE    = 0;
  public static final short CONST   = 1;
  public static final short UNIFORM = 2;
  public static final short NORMAL  = 3;
  public static final short NEGEXP  = 4; 
  public static final short POISSON = 5;
  
  /**
   * tipo de distribuição de serviço
   */
  protected short servicedist;
  /**
   * distp1, distp2:
   * parâmetros da distribuição;
   * têm significados diferentes 
   * de acordo com a distribuição
   */
  protected float distp1, distp2;

  public String toString()
  {
	StringBuffer stb = new StringBuffer();
	stb.append("<ActiveEntry internal=\""+internal+"\" servicedist=\""+serviceDistString()+"\" distp1=\""+distp1+"\" distp2=\""+distp2+"\">\r\n");
	stb.append("<A_super>\r\n");
	stb.append(super.toString());
	stb.append("</A_super>\r\n");
	stb.append("</ActiveEntry>\r\n");
	return stb.toString();
  }
  String serviceDistString()
  {
	if(servicedist == NONE)
	{
		return "NONE";
	}
	else if(servicedist == CONST)
	{
		return "CONST";
	}
	else if(servicedist == UNIFORM)
	{
		return "UNIFORM";
	}
	else if(servicedist == NORMAL)
	{
		return "NORMAL";
	}
	else if(servicedist == NEGEXP)
	{
		return "NEGEXP";
	}
	else if(servicedist == POISSON)
	{
		return "POISSON";
	}
	return "SERVICEDIST???";
  }
  /**
   * constrói um objeto com id gerado internamente;
   * preenche com argumentos padrão os demais campos.
   */
  public ActiveEntry()
  {
    super("a_" + String.valueOf(lastid));
    lastid++;
    servicedist = UNIFORM;
	distp1 = 0;
	distp2 = 10;
  }
  
  public void copyAttributes(Entry v_e)
  {
	super.copyAttributes(v_e);
	ActiveEntry actEntry = (ActiveEntry)v_e;
	SimObj = actEntry.SimObj;
	internal = actEntry.internal;
	servicedist = actEntry.servicedist;
	distp1 = actEntry.distp1;
	distp2 = actEntry.distp2;
  }
  
  /**
   * Cria os observadores e histogramas associados
   */
  protected boolean Setup(SimulationManager m)
  {
System.out.println("ActiveEntry.Setup. name = "+name);	  
  	SimObj.name = name;
		if(obsid == null)			// nada para criar
			return true;
			
		return m.GetObserver(obsid).Generate(m);
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
	
	public short getServiceDist(){	return servicedist;	}
	public void setServiceDist(short v_sServiceDist){	servicedist = v_sServiceDist;	}
	public float getDistP1(){	return distp1;	}
	public float getDistP2(){	return distp2;	}
	public void setDistP1(float v_fDistP1){	distp1 = v_fDistP1;	}
	public void setDistP2(float v_fDistP2){	distp2 = v_fDistP2;	}
}