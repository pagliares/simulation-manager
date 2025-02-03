// Arquivo  ExternalActiveEntry.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 21.Mai.1999 Wladimir

package simula.manager;

import simula.*;

/**
 * Entrada para os estados ativos Generate e Destroy.
 */
public class ExternalActiveEntry extends ActiveEntry
{
  private boolean gen;      					// true -> generate / false -> destroy
	/**
	 * id do queue associado
	 */
  private String qid;      
  /**
   * tipo da Entity gerada (se Generate)
   * se null, nenhum tipo (s/ attribs).
   */
  private String enttype;		
    
  public String toString()
  {
	StringBuffer stb = new StringBuffer();
	stb.append("<ExternalActiveEntry gen=\""+gen+"\" qid=\""+qid+"\" enttype=\""+enttype+"\">\r\n");
	stb.append("<EA_super>\r\n");
	stb.append(super.toString());
	stb.append("</EA_super>\r\n");
	stb.append("</ExternalActiveEntry>\r\n");
	return stb.toString();
  }
  /**
   * constrói um objeto com id gerado internamente;
   * @param	generate	deve especificar se é um estado Generate (true) ou Destroy(false).
   */
  public ExternalActiveEntry(boolean generate)
  {
    super();
    gen = generate;
    internal = false;
  }
  
  public void copyAttributes(Entry v_e)
  {
	super.copyAttributes(v_e);
	ExternalActiveEntry extEntry = (ExternalActiveEntry)v_e;
	gen = extEntry.gen;
	qid = extEntry.qid;
	enttype = extEntry.enttype;
  }
  
  /**
   * Seta o ID da Queue associada
   */
  public void setQID(String v_strQID)
  {
	  System.out.println("EAE: id "+id+".setQID "+v_strQID);
	  qid = v_strQID;
  }
  public String getQID(){	return qid;	}
  
  boolean Generate(SimulationManager m)
	{
		if(gen)
			SimObj = new Generate(m.s);
		else
			SimObj = new Destroy(m.s);
			
		return Setup(m);
	}

  /**
   * Ajusta os parâmetros comuns aos ActiveState's
   */
  protected boolean Setup(SimulationManager m)
	{
		if(!super.Setup(m))
			return false;
		
		if(gen)
		{
			switch(servicedist)
			{
				case NONE: break;
				case CONST: 	((Generate)SimObj).SetServiceTime(new ConstDistribution(m.sp, distp1)); break;
				case UNIFORM: ((Generate)SimObj).SetServiceTime(new Uniform(m.sp, distp1, distp2)); break;
				case NORMAL: 	((Generate)SimObj).SetServiceTime(new Normal(m.sp, distp1, distp2)); break;
				case NEGEXP: 	((Generate)SimObj).SetServiceTime(new NegExp(m.sp, distp1)); break;
				case POISSON: ((Generate)SimObj).SetServiceTime(new Poisson(m.sp, distp1)); break;
				default: return false;
			}
			
			((Generate)SimObj).ConnectQueue(m.GetQueue(qid).SimObj);
		}
		else
		{
			System.out.println("ExternalActiveEntry.Setup id= "+id+" qid"+qid);
			System.out.println("ExternalActiveEntry.Setup "+SimObj+" "+m.GetQueue(qid));
			((Destroy)SimObj).ConnectQueue(m.GetQueue(qid).SimObj);
		}

		if(enttype != null)
		{
			AttributeTable type = m.GetType(enttype);
			if(type == null)
				return false;
			
			((Generate)SimObj).SetEntitiesAtts(type.GetIds(), type.GetValues());
		}

		return true;	
	}

  /**
   * Returns true if it is a Generate
   */
	public final boolean IsGenerate(){return gen;}
	public String getEnttype(){	return enttype;	}
	public void setEnttype(String v_strEnttype){	enttype = v_strEnttype;	}
}