// Arquivo InterruptActivity.java
// Implementação das Classes do Grupo de Modelagem da Biblioteca de Simulação JAVA
// 22.Abr.1999	Wladimir

package simula;

import java.util.*;

public class InterruptActivity extends Activity
{
	private Vector IntVector;
	
	/**
	 * constrói uma atividade que interrompe e pode ser interrompida
	 */
	public InterruptActivity(Scheduler s)
	{
		super(s);
		IntVector = new Vector(2, 2);
	}
	
	/**
	 * adiciona a à lista das atividades que podem ser interrompidas
	 * quando se fizer necessário (obter recurso)
	 */
	public void AddInterruptable(InterruptActivity a){IntVector.add(a);}

	/**
	 * interrompe serviço dessa atividade em favor de a;
	 * se interrompeu, retorn true, senão false
	 */
	public boolean Interrupt(InterruptActivity a)
	{
		IntQEntry e = service_q.FromTail(); // interrompe o serviço mais demorado
		if(e == null)
			return false;	// não tinha serviço para interromper
			
		for(int i = 0; i < e.ve.length; i++)		// devolve as entidades às respectivas filas
			((DeadState)entities_from_v.elementAt(i)).PutBack(e.ve[i]);
		
		for(int i = 0; i < resources_from_v.size(); i++)	// e os recursos
			((ResourceQ)resources_from_v.elementAt(i)).
				Release(((Integer)resources_qt_v.elementAt(i)).intValue());

		Log.LogMessage(name + ":Interrupted by " + a.name);
		
		return true;
	}

	/**
	 * implementa protocolo
	 */
	public boolean CServed()
	{
		if(super.CServed())	// se o serviço normal foi possível...
			return true;
			
		if(blocked)					// não vai interromper ninguém se estiver bloquado
			return false;
			
		int esize = entities_from_v.size();
		boolean ok = true;
		for(int i = 0; i < esize && ok; i++)					
			ok &= ((DeadState)entities_from_v.elementAt(i)).HasEnough();

		if(!ok)
			return false;

		// se não foi, tenta interromper alguém, mas só se realmente houver entidades
		// suficientes para se iniciar o serviço.
		
			
		boolean interrupted = false;
		for(int i = 0; i < IntVector.size() && !interrupted; i++)
			if(((InterruptActivity)IntVector.elementAt(i)).Interrupt(this))
				interrupted = super.CServed(); 	// se conseguiu interromper e fazer o serviço
																				// senão tenta novamente
	
		return interrupted;			// avisa scheduler o que ocorreu
	}
			
}