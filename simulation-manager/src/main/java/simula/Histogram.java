// Arquivo Histogram.java
// Implementação das Classes do Grupo de Resultados da Biblioteca de Simulação JAVA
// 16.Abr.1999 Wladimir

// Código original em C adaptado de 
// Watkins, Kevin. Discrete event simulation in C. 1993. McGraw-Hill International.

package simula;

import java.io.PrintStream;

public class Histogram
{
	private Scheduler s;
	private float[] data;
	private boolean first;
	private float   sum_f;
	private float   width;
	private int     num_columns;
	private float   start;
	private float   sum_xf;
	private float   sum_xxf;
	private float   min_val;
	private float   max_val;
	private float   time_last_recording;
	private short		kind;
	
	/**
	 * SERIES, FREQUENCY, WEIGHTED:
	 * possiveis tipos de histograma
	 */
	public static final short SERIES = 0, FREQUENCY = 1, WEIGHTED = 2;

	/**
	 * constrói um histograma vazio com início em start, cada coluna de largura width
	 * com num_columns colunas e do tipo SERIES, FREQUENCY ou WEIGHTED
	 */
	public Histogram(Scheduler s, float  start, float  width, int num_columns, short kind)
	{
		this.s = s;
		this.kind = kind;
		this.width = width;
		this.start = start;
		this.num_columns = num_columns ;
		data = new float[num_columns + 2]; 
		Clear();
	}

	/**
	 * limpa dados do histograma
	 */
	public void Clear()
	{
		for (int i = 0; i < num_columns + 2; i++) 
			data[i] = (float)0.0;
		first = true;
		sum_f = 0;
		sum_xf = 0;
		sum_xxf = 0;
		min_val = 0;
		max_val = 0;
		time_last_recording = s.GetClock();
	}

	/**
	 * adiciona dados ao histograma, de acordo com seu tipo
	 */
	public void Add(float x, float y)
	{
		int i, j;

		if (kind == SERIES)
			i = (int)(( s.GetClock() - start ) / width  + 1);
		else
			i = (int)(( x - start ) / width  + 1);
		if (i < 0) 
			i = 0;
		else if (i > num_columns+1) 
			i = num_columns + 1;

		/*
		** Check min and max values.
		*/
		if (first)
		{
			min_val = x;
			max_val = x;
			first = false;
		}
		else
		{
			if (x < min_val) min_val = x;
			if (x > max_val) max_val = x;
		}

		/*
		** Add sample to ith. column.
		*/
		if (kind == WEIGHTED)
		{
			y = (s.GetClock() - time_last_recording) * y;
			data[i] += y;
			sum_f += y;
		}
		else if (kind == FREQUENCY)
		{
			data[i] += y;
			sum_f += y;
		}
		else if (kind == SERIES)
		{
			/*
			** calc the lower limit
			*/
			j = (int)(( time_last_recording - start ) / width  + 1);
			if (j < 0) j = 0;
			else if (j > num_columns + 1) j = num_columns + 1;
			/*
			** If lower is less than upper then increase lower by 1
			** so that the previous column is not incremented twice.
			*/
			if (i > j) j++;
			/*
			** Update column values.
			*/
			while (j <= i) 
			{
				data[j++] += y;
				sum_f += y;
			}
		}

		time_last_recording = s.GetClock();
		sum_xf += x * y;
		sum_xxf += (x * x * y);
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
		return (sum_xxf - sum_xf * Mean()) / (sum_f - 1);
	}

	/**
	 * retorna máximo valor observado.
	 */
	public final float Max()
	{
		float max = 0;
		int i;
		
	  for (i = 0; i < num_columns + 2; i++)
    	if (max < data[i]) 
    		max = data[i];
    		
    return max;
	}

	/**
	 * retorna mínimo valor observado.
	 */
	public final float Min()
	{
		float min = Float.MAX_VALUE;
		int i;
		
	  for (i = 0; i < num_columns + 2; i++)
    	if (min > data[i]) 
    		min = data[i];
    		
    return min;
	}

	/**
	 * retorna o número de observações
	 */
	public final int NumObs(){return (int)sum_f;}

	/**
	 * Imprime uma tabela com os dados do histograma
	 */
	public void PrintData(PrintStream os)
	{
  	float  cum, sum;
	  int i;

	  /*
  	** Make sure there is data to print
	  */
  	if (sum_f < 1)
	  {
  	  os.print("no histogram data recorded\r\n");
    	return;
  	}

	  cum = 0;
  	sum = sum_f;
  	
  	/*
	  ** Print table
  	*/
	  os.print("\r\nRange        Observed  Per cent   Cumulative  Cumulative\r\n");
  	os.print("             value     of total   percentage  remainder\r\n\r\n");

	  /*
  	** Print first value
	  */
  	if (data[0] > 0)
  	{
    	os.print("    -> ");
    	os.print(start);
	    cum = data[0];
  	  os.print("     "); 
  	  os.print(data[0]);
    	os.print("   ");
    	os.print(100 * data[0] / sum);
	    os.print("   ");
	    os.print(100 * cum / sum);
  	  os.print("   ");
  	  os.println(100 * (sum - cum) / sum);
  	}

	  /*
  	** Print middle n-2 values
	  */
  	for (i = 1; i < num_columns + 1; i++)
	  {
  	  os.print(start + ((i - 1) * width));
  	  os.print(" -> ");
  	  os.print(start + (i * width));
    	cum += data[i];
	    os.print("     "); 
  	  os.print(data[i]);
    	os.print("   ");
    	os.print(100 * data[i] / sum);
	    os.print("   ");
	    os.print(100 * cum / sum);
  	  os.print("   ");
  	  os.println(100 * (sum - cum) / sum);
  	  if (cum >= sum)
    	{
      	os.print(" Remaining frequencies are all zero\r\n");
      	return;
	    }
  	}

	  /*
  	** Print last value
	  */
  	if (data[num_columns + 1] > 0)
  	{
  		os.print(start + ((num_columns + 1) * width));
    	os.print(" ->         ");
    	cum += data[num_columns + 1];
    	os.print("     "); 
  	  os.print(data[num_columns + 1]);
    	os.print("   ");
    	os.print(100 * data[num_columns + 1] / sum);
	    os.print("   ");
	    os.print(100 * cum / sum);
  	  os.print("   ");
  	  os.println(100 * (sum - cum) / sum);
  	}
	}
}