// Arquivo Entity.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.*;

class Entity
{
	/**
	 * A mínima prioridade (255)
	 */
	public static final short MinPriority = 255;	
	/**
	 * A máxima prioridade (ZERO)
	 */
	public static final short MaxPriority = 0;
	private static long counter = 1;

	private long 	id;							// id da entidade
	private float creationtime;
	private float timestamp;
	private short priority = 128;	// valor padrão
	private float qentertime;			// instante em que entrou na fila atual
	private float totalqtime = 0;	// tempo total que passou em filas
	private float qtime = 0;			// tempo que passou na última fila

	private HashMap attributes;

	/**
	 * constrói uma entidade e atribui o instante da sua criação.
	 */
	public Entity(float creationtime)
	{
		timestamp = this.creationtime = creationtime;
		id = counter++;	
	}
	
	/**
	 * obtém instante de criação.
	 */
	public float GetCreationTime(){return creationtime;}
	
	/**
	 * obtém valor de um atributo personalisado.
	 */
	public float GetAttribute(String name)
	{
		if(attributes == null)
			return Float.NaN;
		Float data = (Float)attributes.get(name);
		if(data == null)							// atributo não existe
			return Float.NaN;
		
		return data.floatValue();
	}

	/**
	 * atribui valor a um atributo personalisado; cria, se necessário. 
	 */
	public void SetAttribute(String name, float value)
	{
		if(attributes == null)
			attributes = new HashMap(5);			// cria a tabela na primeira atribuição
		attributes.put(name, new Float(value));		// armazena
	}

	/**
	 * determina prioridade de uma entidade em relação às outras.
	 */
	public void SetPriority(short p)
	{
		if(p > MinPriority)
			priority = MinPriority;
		else if(p < MaxPriority)
			priority = MaxPriority;
		else
			priority = p;
	}

	/**
	 * obtém prioridade da entidade.
	 */
	public short GetPriority(){return priority;} 

	/**
	 * faz uma marcação de tempo na entidade.
	 */
	public void Stamp(float time){timestamp = time;}

	/**
	 * obtém valor da última marcação.
	 */
	public float GetTimestamp(){return timestamp;}

	/**
	 * obtém o tempo em que a entidade ficou em filas
	 */
	public float GetTotalQueueTime(){return totalqtime;}

	/**
	 * obtém o tempo em que a entidade ficou em filas
	 */
	public long GetId(){return id;}

	/**
	 * notifica o instante em que entrou em uma fila
	 */
	public void EnteredQueue(float time){qentertime = time;}

	/**
	 * notifica o instante em que saiu de uma fila; 
	 */
	public void LeftQueue(float time)
	{
		qtime = time - qentertime;
		totalqtime += qtime;
		qentertime = 0;
	}

	/**
	 * retorna o tempo em que passou na fila
	 */
	public float GetQTime(){return qtime;}
		
}