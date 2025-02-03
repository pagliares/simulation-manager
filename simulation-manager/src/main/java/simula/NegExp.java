// Arquivo NegExp.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 30.Abr.1999	Wladimir

package simula;

/**
 * Distribuicao exponencial
 */
public class NegExp extends Distribution
{
	private double mean;

	/**
	 * associa a stream à distribuição e recebe parâmetros.
	 */
	public NegExp(Sample s, double Mean)
	{super(s); mean = Mean;}

	/**
	 * obtém uma amostra segundo a dada distribuição.
	 */
	public double Draw(){return mean * stream.NegExp();}
}