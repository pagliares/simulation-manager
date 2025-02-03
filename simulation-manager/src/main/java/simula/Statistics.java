// Arquivo Statistics.java
// Implementa��o das Classes do Grupo de Resultados da Biblioteca de Simula��o JAVA
// 16.Abr.1999	Wladimir

// C�digo original em C adaptado de 
// Watkins, Kevin. Discrete event simulation in C. 1993. McGraw-Hill International.

package simula;

/**
 * C�digo original em C adaptado de 
 * Watkins, Kevin. Discrete event simulation in C. 1993. McGraw-Hill International.
 */
public class Statistics
{
	private Scheduler s;
	private int zero;
	private float  sum_xf;
	private float  sum_xxf;
	private int sum_f;
	private float min_val, max_val;

	/**
	 * cria um observador vazio
	 */
	public Statistics(Scheduler s)
	{
		this.s = s;
		Clear();
	}

	/**
	 * limpa estat�sticas
	 */
	public void Clear()
	{
		zero = 0;
		sum_xf = 0;
		sum_xxf = 0;
		sum_f = 0;
		min_val = Float.MAX_VALUE;
		max_val = 0;
	}

	/**
	 * adiciona mais um dado �s estat�sticas com peso unit�rio
	 */
	public void Add(float  v){Add(1, v);}

	/**
	 * adiciona mais um dado �s estat�sticas com peso weight
	 */
	public void Add(float weight, float  v)
	{
		sum_f++;
		if (v == 0.0) 
			zero++;
		sum_xf += v * weight;
		sum_xxf += v * v * weight * weight;
		if (v > max_val)
			max_val = v;
		else if (v < min_val)
			min_val = v;
	}

	/**
	 * calcula a m�dia.
	 */
	public final float Mean(){return sum_xf / sum_f;}

	/**
	 * calcula o desvio padr�o; retorna 0 se n�o houverem dados suficientes.
	 */
	public final float StdDev(){return (float)Math.sqrt(Variance());}

	/**
	 * calcula a vari�ncia; retorna 0 se n�o houverem dados suficientes.
	 */
	public final float Variance()
	{	
		if(sum_f <= 1) 
			return 0;
		return (sum_xxf - sum_xf * Mean())/(sum_f - 1);
	}
	
	/**
	 * retorna m�ximo valor observado.
	 */
	public final float Max(){return max_val;}
	
	/**
	 * retorna m�nimo valor observado.
	 */
	public final float Min(){return min_val;}
	
	/**
	 * retorna o n�mero de observa��es
	 */
	public final int NumObs(){return sum_f;}
}