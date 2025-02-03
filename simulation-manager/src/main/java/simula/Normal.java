// Arquivo Normal.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Distribuicao Gaussiana
 */
public class Normal extends Distribution
{
	private double mean, std_dev;

	/**
	 * associa a stream à distribuição e recebe parâmetros.
	 */
	public Normal(Sample s, double Mean, double Std_dev)
	{super(s); mean = Mean; std_dev = Std_dev;}

	/**
	 * obtém uma amostra segundo a dada distribuição.
	 */
	public double Draw()
	{
		double  u, v1, v2, s, x1;

		do
		{
			u  = stream.Uniform();
			v1 = 2.0 * u  - 1.0;
			u  = stream.Uniform();
			v2 = 2.0 * stream.Uniform() - 1.0;
			s = v1 * v1 + v2 * v2;
		} while (s >= 1.0 || s == 0.0);

		x1 = v1 * Math.sqrt((-2.0 * Math.log(s)) / s);
		return (mean + x1 * std_dev);
	}
}
