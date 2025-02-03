// Arquivo ConstDistribution.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

public class ConstDistribution extends Distribution
{
	private double value;
	
	/**
	 * constrói uma "distribuição" que retorna um valor constante
	 */
	public ConstDistribution(Sample s, double value)
	{super(s); this.value = value;}
	
	/**
	 * obtém uma amostra segundo a dada distribuição.
	 */
	public double Draw(){return value;}
}
