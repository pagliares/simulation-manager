// Arquivo IntQEntry.java
// Implementa��o das Classes do Grupo de Modelagem da Biblioteca de Simula��o JAVA
// 9.Abr.1999	Wladimir

package simula;

class IntQEntry
{
	/**
	 * vetor de entidades em servi�o
	 */
	public Entity ve[];				
	/**
	 * instante de fim de servi�o
	 */
	public float duetime;		

	public IntQEntry(int nentities, float duetime)
	{
		ve = new Entity[nentities];
		this.duetime = duetime;
	}
}