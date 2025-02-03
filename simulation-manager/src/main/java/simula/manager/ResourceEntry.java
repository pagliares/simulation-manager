// Arquivo  ResourceEntry.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 21.Mai.1999 Wladimir

package simula.manager;

import java.io.*;

/**
 * Entrada para as "filas" de recursos do modelo
 */
public class ResourceEntry extends Entry
{
  private static int lastid;  // identificador ÚNICO para as filas
	static boolean hasSerialized = true; // "lastid já foi serializado"
  
	/**
	 * qtde inicial de recuros
	 */
  private short init;                 // 
    
	transient simula.ResourceQ SimObj;	// objeto de simulação
																			// não é serializado
	public String toString()
	{
		StringBuffer stb = new StringBuffer();
		stb.append("<ResourceEntry init=\""+init+"\">\r\n");
		stb.append("<R_super>\r\n");
		stb.append(super.toString());
		stb.append("</R_super>\r\n");
		stb.append("</ResourceEntry>\r\n");
		return stb.toString();
	}
	/**
	 * constrói um objeto com id gerado internamente;
	 * preenche com argumentos padrão os demais campos.
	 */
  public ResourceEntry()
  {
    super("r_" + String.valueOf(lastid));
    lastid++;
    init = (short)1;
  }
  
  public void copyAttributes(Entry v_e)
  {
	super.copyAttributes(v_e);
	ResourceEntry rscEntry = (ResourceEntry)v_e;
	init = rscEntry.init;
	SimObj = rscEntry.SimObj;
  }
  
  public final short getInit(){	return init;	}
  public final void setInit(short v_sInit){	init = v_sInit;	}
  
  boolean Generate(SimulationManager m)
	{
		SimObj = new simula.ResourceQ(m.s, init);
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