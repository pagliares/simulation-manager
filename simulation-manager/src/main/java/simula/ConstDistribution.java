// Arquivo ConstDistribution.java
// Implementa��o das Classes do Grupo Utilit�rio da Biblioteca de Simula��o JAVA
// 26.Mar.1999	Wladimir

package simula;

public class ConstDistribution extends Distribution
{
	private double value;
	
	/**
	 * constr�i uma "distribui��o" que retorna um valor constante
	 */
	public ConstDistribution(Sample s, double value)
	{super(s); this.value = value;}
	
	/**
	 * obt�m uma amostra segundo a dada distribui��o.
	 */
	public double Draw(){return value;}
}
