// Arquivo Variables.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 22.Abr.1999	Wladimir

package simula;

import java.util.*;

/**
 * Classe que guarda variáveis - pares (nome, valor)
 */
public class Variables
{
	private HashMap table;			// armazém de variáveis
	private HashMap queues;			// armazém de referências às filas
										// para obter seus comprimentos
	
	public Variables()
	// construtor
	{
		table = new HashMap(20);		// capacidade inicial para 20 variáveis	
	}
	
	/**
	 * cria variável name e inicializa com inival;
	 * se name já existir, retorna false, senão true
	 */
	public boolean CreateVar(String name, float inival)
	{
		if(table.containsKey(name))
			return false;
			
		table.put(name, new Float(inival));
		return true;
	}
	
	/**
	 * cria variável name e inicializa com zero;
	 * se name já existir, retorna false, senão true
	 */
	public boolean CreateVar(String name){return CreateVar(name, 0);}
	
	/**
	 * exclui variável name
	 */
	public void DeleteVar(String name){table.remove(name);}
	
	/**
	 * atribui à variável name valor value;
	 * se name não existir retorna false, senão true
	 */
	public boolean SetVar(String name, float value)
	{
		if(!table.containsKey(name))
			return false;
		
		table.put(name, new Float(value));
		return true;
	}
	
	/**
	 * recupera valor da variável name; se name não existir retorna NAN
	 */
	public float Value(String name)
	{
		if(!table.containsKey(name))
		{
			if(!queues.containsKey(name))
				return Float.NaN;
			
			DeadState q = (DeadState)queues.get(name);
			return q.ObsLength();
		}
		
		return ((Float)table.get(name)).floatValue();
	}
	
	/**
	 * atribui tabela contendo (nome da fila, referência)
	 * de todas as filas do modelo, fazendo com que seus nomes
	 * se tornem nomes globais; o nome de uma fila retorn seu comprimento
	 */
	public void AssignQueuesTable(HashMap qtable){queues = qtable;}
	
}