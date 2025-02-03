// Arquivo Distribution.java
// Implementa��o das Classes do Grupo Utilit�rio da Biblioteca de Simula��o JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Superclasse comum � todas as distribuicoes
 */
public abstract class Distribution
{
	protected Sample stream;

	/**
	 * associa a stream � distribui��o.
	 */
	public Distribution(Sample s){stream = s;}
	
	/**
	 * obt�m uma amostra segundo a dada distribui��o.
	 */
	public abstract double Draw(); 
}
