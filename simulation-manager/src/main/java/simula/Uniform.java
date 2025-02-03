// Arquivo Uniform.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Uniform Distribution Generator
 */
public class Uniform extends Distribution
{
	private double upper, lower;

	/**
	 * associa a stream à distribuição e recebe parâmetros.
	 */
	public Uniform(Sample s, double Lower, double Upper)
	{super(s); lower = Lower; upper = Upper;}

	/**
	 * obtém uma amostra segundo a dada distribuição.
	 */
	public double Draw(){return ( stream.Uniform() * ( upper - lower ) + lower );}
}
