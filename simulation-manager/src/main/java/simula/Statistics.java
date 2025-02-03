// Arquivo Statistics.java
// Implementação das Classes do Grupo de Resultados da Biblioteca de Simulação JAVA
// 16.Abr.1999	Wladimir

// Código original em C adaptado de 
// Watkins, Kevin. Discrete event simulation in C. 1993. McGraw-Hill International.

package simula;

/**
 * Código original em C adaptado de 
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
	 * limpa estatísticas
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
	 * adiciona mais um dado às estatísticas com peso unitário
	 */
	public void Add(float  v){Add(1, v);}

	/**
	 * adiciona mais um dado às estatísticas com peso weight
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
	 * calcula a média.
	 */
	public final float Mean(){return sum_xf / sum_f;}

	/**
	 * calcula o desvio padrão; retorna 0 se não houverem dados suficientes.
	 */
	public final float StdDev(){return (float)Math.sqrt(Variance());}

	/**
	 * calcula a variância; retorna 0 se não houverem dados suficientes.
	 */
	public final float Variance()
	{	
		if(sum_f <= 1) 
			return 0;
		return (sum_xxf - sum_xf * Mean())/(sum_f - 1);
	}
	
	/**
	 * retorna máximo valor observado.
	 */
	public final float Max(){return max_val;}
	
	/**
	 * retorna mínimo valor observado.
	 */
	public final float Min(){return min_val;}
	
	/**
	 * retorna o número de observações
	 */
	public final int NumObs(){return sum_f;}
}