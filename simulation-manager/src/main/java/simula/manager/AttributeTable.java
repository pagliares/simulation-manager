// Arquivo  AttributeTable.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 30.Out.1999 Wladimir

package simula.manager;

import java.util.Vector;
import java.util.Iterator;
import java.io.*;

/**
 * Entrada para os diversos tipos de entidades que podem exixtir no
 * no modelo. Usado também para as variáveis globais. Deve ser 
 * associada aos estados Generate para iniciar os atributos de cada
 * nova Entity criada. Deve existir um exclusivo para as variáveis
 * globais do modelo.
 */
public class AttributeTable extends Entry
{
	private static int lastid;		// identificador ÚNICO para as tabelas
	static boolean hasSerialized = true; // "lastid já foi serializado"
		
	/**
	 * atributos (Var's)
	 */
	private Vector vars;				
		
	public String toString()
	{
		StringBuffer stb = new StringBuffer();
		stb.append("<AttributeTable>\r\n");
		stb.append("<AT_super>\r\n");
		stb.append(super.toString());
		stb.append("</AT_super>\r\n");
		stb.append("<vars>\r\n");
		SimulationManager.appendVector(vars, stb);
		stb.append("</vars>\r\n");
		stb.append("</AttributeTable>\r\n");
		return stb.toString();
	}
	/**
	 * constrói um objeto com id gerado internamente;
	 * preenche com argumentos padrão os demais campos.
	 */
	public AttributeTable()
	{
		super("t_" + String.valueOf(lastid));
		lastid++;
		vars = new Vector(5, 5);
	}
	
	public void copyAttributes(Entry v_e)
	{
		super.copyAttributes(v_e);
		AttributeTable attTable = (AttributeTable)v_e;
		vars = attTable.vars;
	}
	
	public final Iterator getVarsIterator(){	return vars.iterator();	}
	public final void addVar(Object v_o){	vars.add(v_o);	}
	public final Object getVar(int v_i){	return vars.get(v_i);	}
	public void removeVar(int v_i){	vars.remove(v_i);	}
	
	boolean Generate(SimulationManager m){return true;}
	
	/**
	 * Retorna array de valores, na ordem em que estão armazenados
	 * no vetor vars. Retorna null se não há valores armazenados.
	 */
	public float[] GetValues()
	{
		//if(vars.isEmpty())
		//	return null;
		//<tony> deixa retornar vazio mesmo que tá dando NullPointerException
		float[] vals = new float[vars.size()];
		Iterator it = vars.iterator();
		int i = 0;
		while(it.hasNext())
		{
			vals[i++] = ((Var)it.next()).value;
		}
		
		return vals;
	}
	
	/**
	 * Retorna array de identificadores, na ordem em que estão 
	 * armazenados no vetor vars. Retorna null se vars estiver vazio. 
	 */
	public String[] GetIds()
	{
		//if(vars.isEmpty())
		//	return null;
		//<tony> deixa retornar vazio mesmo que tá dando NullPointerException
		String[] vals = new String[vars.size()];
		Iterator it = vars.iterator();
		int i = 0;
		while(it.hasNext())
		{
			vals[i++] = ((Var)it.next()).id;
		}
		
		return vals;
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