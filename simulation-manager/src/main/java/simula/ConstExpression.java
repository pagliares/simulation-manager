// Arquivo ConstExpression.java
// Implementa��o das Classes do Grupo Utilit�rio da Biblioteca de Simula��o JAVA
// 9.Abr.1999	Wladimir

package simula;

public class ConstExpression extends Expression
{
	public static final ConstExpression TRUE  = new ConstExpression(1);
	public static final ConstExpression FALSE = new ConstExpression(0);
	
	private float value;

	/**
	 * constr�i uma express�o constante
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