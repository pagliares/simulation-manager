// Arquivo RandomExpression.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 3.Dez.1999	Wladimir

package simula;

public class RandomExpression extends Expression
{
	private static Sample sp = new Sample();
	private Distribution dist;

	/**
	 * constrói uma expressão aleatória no formato:
	 * #<nome da distribuição> <par1> [<par2>]
	 * onde: <nome da distribuição> := normal | poisson | uniform | negexp
	 * par1 e par2 dependem do tipo da distribuição
	 */
	public RandomExpression(String exp)
	{
		super(0);
		exp.toLowerCase();
		if(exp.charAt(0) != '#')
			throw new IllegalArgumentException("Expressões aleatórias devem iniciar por #!");
		String temp = exp.substring(1);
		int spindex = temp.indexOf(' ');
		String type = temp.substring(0, spindex);
		temp = temp.substring(spindex + 1);
		spindex = temp.indexOf(' ');
		String par1 = temp.substring(0, spindex);
		try
		{
			if(type.equals("uniform"))
			{
				String par2 = temp.substring(spindex + 1);
				
				dist = new Uniform(sp, Float.parseFloat(par1), Float.parseFloat(par2));
			}
			else if(type.equals("normal"))
			{
				String par2 = temp.substring(spindex + 1);	
				
				dist = new Normal(sp, Float.parseFloat(par1), Float.parseFloat(par2));
			}
			else if(type.equals("poisson"))
			{
				dist = new Poisson(sp, Float.parseFloat(par1));
			}
			else if(type.equals("negexp"))
			{
				dist = new NegExp(sp, Float.parseFloat(par1));
			}
			else
				throw new IllegalArgumentException("Tipo de distribuição não suportada!");
		}
		catch(NumberFormatException x)
		{
			throw new IllegalArgumentException("Números não reconhecidos como Float!");
		}
	}

	/**
	 * para manter o protocolo
	 */
	public float Evaluate(Entity e)
	{return (float)dist.Draw();}
}