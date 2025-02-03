// Arquivo Poisson.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

/**
 * Distribuição de Poisson
 */
public class Poisson extends Distribution
{
	private double mean, sq, alxm, g, oldm = -1.0;

	/**
	 * associa a stream à distribuição e recebe parâmetros.
	 */
	public Poisson(Sample s, double Mean)
	{super(s); mean = Mean;}

	/**
	 * obtém uma amostra segundo a dada distribuição.
	 */
	public double Draw()
	{
		double  em, t, y;

		/*
		** Can we use direct method
		*/
		if (mean < 12.0) 
		{
			if (mean != oldm) 
			{
				oldm = mean;
				g = Math.exp(-mean);
			}
			em = -1.0;
			t  = 1.0;
			do 
			{
				em += 1.0;
				t *= stream.Uniform();
			} while (t > g);
		}
		/*
		** If not, use rejection method
		*/
		else 
		{
			if (mean != oldm) 
			{
				oldm = mean;
				sq   = Math.sqrt(2.0 * mean);
				alxm = Math.log(mean);
				g    = mean * alxm - Sample.lngamma(mean + 1.0);
			}
			do 
			{
				do 
				{
					/*
					** y is a deviate from a Lorentzian comparison function
					*/
					y    = Math.tan (Math.PI * stream.Uniform());
					em   = sq * y + mean;
				} while (em < 0.0);
				/*
				** poisson is integer valued
				*/
				em = Math.floor(em);
				/*
				** t is the ration of the desired distribution to the comparison
				** function - accept or reject by comparing it to the the uniform
				** deviate. Factor 0.9 is chosen so that it never exceeds 1.0
				*/
				t = 0.9 * (1.0 + y * y) * Math.exp(em * alxm - Sample.lngamma(em + 1.0) - g);
			} while (stream.Uniform() > t);
		}
		return em;
	}
}