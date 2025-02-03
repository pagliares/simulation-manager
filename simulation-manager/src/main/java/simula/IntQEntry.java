// Arquivo IntQEntry.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 9.Abr.1999	Wladimir

package simula;

class IntQEntry
{
	/**
	 * vetor de entidades em serviço
	 */
	public Entity ve[];				
	/**
	 * instante de fim de serviço
	 */
	public float duetime;		

	public IntQEntry(int nentities, float duetime)
	{
		ve = new Entity[nentities];
		this.duetime = duetime;
	}
}