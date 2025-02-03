// Arquivo  Entry.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 21.Mai.1999 Wladimir

package simula.manager;

/**
 * Classe base para todas as entradas do Sistema de Gerenciamento. 
 * Fornece métodos para uso em Hashtables e mantém identificador
 * único do objeto em todo modelo. Unicidade dever ser garantida
 * pelos descendentes e por quem mais atribuir o seu valor.
 */
public abstract class Entry implements java.io.Serializable
{
	protected String id;							// identificador ÚNICO EM TODO O MODELO do objeto
	/**
	 * identificador do observador
	 */
	protected String obsid;	
	/**
	 * nome (para o usuário referenciar)
	 */
	protected String name;		
	
	public String toString()
	{
		return "<Entry id=\""+id+"\" obsid=\""+obsid+"\" name=\""+name+"\"/>\r\n";
	}
	
	/**
	 * constrói um objeto com dado id; id deve ser único e não nulo.
	 */
	public Entry(String id){this.id = id; name = id;}
	
	public void copyAttributes(Entry v_e)
	{
		id = v_e.id;
		obsid = v_e.obsid;
		name = v_e.name;
	}
	
	/**
	 * Cria objeto de simulação baseado nos dados fornecidos 
	 * é chamado pelo SimulationManager após a criação do 
	 *  objetos Scheduler interno ao manager.
	 */
	abstract boolean Generate(SimulationManager m);
	
	/**
	 * Returns a hascode for this object
	 */
	public final int hashCode(){return id.hashCode();}
	/**
	 * para uso nas hashtables
	 */
	public final boolean equals(Entry e){return id == e.id;}

	/**
	 * Returns the id of the Entry
	 */
	public final String GetId(){return id;}
	/**
	 * Sets the id of the entry (unsafe)
	 */
	public final void SetId(String v_strId){	id = v_strId;	}
	/**
	 * retorna o identificador do observador 
	 */
	public final String getObsid(){	return obsid;	}
	/**
	 * seta o identificador do observado
	 */
	public final void setObsid(String v_strObsid){	obsid = v_strObsid;	}
	/**
	 * retorna o name do Entry
	 */
	public final String getName(){	return name;	}
	/**
	 * seta o name desse Entry
	 */
	public final void setName(String v_strName){	name = v_strName;	}
}
		
	
	
	
	