// Arquivo Uniform.java
// Implementa��o das Classes do Grupo Utilit�rio da Biblioteca de Simula��o JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Uniform Distribution Generator
 */
public class Uniform extends Distribution
{
	private double upper, lower;

	/**
	 * associa a stream � distribui��o e recebe par�metros.
	 */
	public Uniform(Sample s, double Lower, double Upper)
	{super(s); lower = Lower; upper = Upper;}

	/**
	 * obt�m uma amostra segundo a dada distribui��o.
	 */
	public double Draw(){return ( stream.Uniform() * ( upper - lower ) + lower );}
}
