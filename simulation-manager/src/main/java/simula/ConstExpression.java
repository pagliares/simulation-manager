// Arquivo ConstExpression.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 9.Abr.1999	Wladimir

package simula;

public class ConstExpression extends Expression
{
	public static final ConstExpression TRUE  = new ConstExpression(1);
	public static final ConstExpression FALSE = new ConstExpression(0);
	
	private float value;

	/**
	 * constrói uma expressão constante
	 */
	public ConstExpression(float constval)
	{
		super(constval);
		value = constval;
	}

	/**
	 * para manter o protocolo (estabelecido por Expression)
	 */
	public float Evaluate(Entity e)
	{return value;}
}