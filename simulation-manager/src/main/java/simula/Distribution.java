// Arquivo Distribution.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Superclasse comum à todas as distribuicoes
 */
public abstract class Distribution
{
	protected Sample stream;

	/**
	 * associa a stream à distribuição.
	 */
	public Distribution(Sample s){stream = s;}
	
	/**
	 * obtém uma amostra segundo a dada distribuição.
	 */
	public abstract double Draw(); 
}
