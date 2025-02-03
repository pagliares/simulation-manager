// Arquivo  InternalActiveEntry.java 
// Implementa��o das Classes do Sistema de Gerenciamento da Simula��o
// 21.Mai.1999 Wladimir

package simula.manager;

import java.util.Vector;
//import com.tony.util.*;
import com.tony.util.TString;
import simula.*;

/**
 * Entrada para os estados ativos Activity e Router.
 */
public class InternalActiveEntry extends ActiveEntry
{
  boolean router;				// especifica se � um Router ou Activity

  /**
   * fq, toq, fr, tor:
   * ids dos estados mortos associados
   */
  private Vector fq;
  /**
   * fq, toq, fr, tor:
   * ids dos estados mortos associados
   */
  private Vector toq, fr, tor;
  /**
   * qtde de cada resource utilizado
   */
  private Vector rqty;	
  /**
   * condi��es (strings) associadas;
   * se router de sa�da, sen�o de entrada
   */
  private Vector conds;					
    
  public String toString()
  {
	StringBuffer stb = new StringBuffer();
	stb.append("<InternalActiveEntry router=\""+router+"\">\r\n");
	stb.append("<IA_super>\r\n");
	stb.append(super.toString());
	stb.append("</IA_super>\r\n");
	stb.append("<fq>\r\n");
	SimulationManager.appendVector(fq, stb);
	stb.append("</fq>\r\n");
	stb.append("<toq>\r\n");
	SimulationManager.appendVector(toq, stb);
	stb.append("</toq>\r\n");
	stb.append("<fr>\r\n");
	SimulationManager.appendVector(fr, stb);
	stb.append("</fr>\r\n");
	stb.append("<tor>\r\n");
	SimulationManager.appendVector(tor, stb);
	stb.append("</tor>\r\n");
	stb.append("<rqty>\r\n");
	SimulationManager.appendVector(rqty, stb);
	stb.append("</rqty>\r\n");
	stb.append("<conds>\r\n");
	int iNConds = conds.size();
	for(int i=0; i<iNConds; i++)
	{
		String strCond = (String)conds.elementAt(i);
		strCond = TString.replace(strCond, "<=", ".LE.");
		strCond = TString.replace(strCond, ">=", ".GE.");
		strCond = TString.replace(strCond, "<", ".LT.");
		strCond = TString.replace(strCond, ">", ".GT.");
		strCond = TString.replace(strCond, "=", ".EQ.");
		stb.append(strCond+"\r\n");
	}
	stb.append("</conds>\r\n");
	stb.append("</InternalActiveEntry>\r\n");
	return stb.toString();
  }
  /**
   * constr�i um objeto com id gerado internamente;
   * @param	isRouter se for do tipo Router deve passar true, sen�o false.
   */
  public InternalActiveEntry(boolean isRouter)
  {
    super();
    router = isRouter;
    fq = new Vector(2, 2); //from queue?
    toq = new Vector(2, 2);//to queue?
    fr = new Vector(2, 2);
    tor = new Vector(2, 2);
    conds = new Vector(2, 2); //conditions
    rqty = new Vector(2, 2);
    internal = true;
  }
  
  public void copyAttributes(Entry v_e)
  {
	super.copyAttributes(v_e);
	InternalActiveEntry intEntry = (InternalActiveEntry)v_e;
	router = intEntry.router;
	fq = intEntry.fq;
	toq = intEntry.toq;
	fr = intEntry.fr;
	tor = intEntry.tor;
	rqty = intEntry.rqty;
	conds = intEntry.conds;
  }
  
  
  boolean Generate(SimulationManager m)
	{
		if(router)
			SimObj = new Router(m.s);
		else
			SimObj = new Activity(m.s);
			
		return Setup(m);
	}
  	
  /**
   * Ajusta os par�metros referentes aos Router's e Activity's
   */
  protected boolean Setup(SimulationManager m)
  {
		if(!super.Setup(m))
			return false;
		
		TrimVectors();
		
		if(router)
		{
			switch(servicedist)
			{
				case NONE: break;
				case CONST: 	((Router)SimObj).SetServiceTime(new ConstDistribution(m.sp, distp1)); break;
				case UNIFORM: ((Router)SimObj).SetServiceTime(new Uniform(m.sp, distp1, distp2)); break;
				case NORMAL: 	((Router)SimObj).SetServiceTime(new Normal(m.sp, distp1, distp2)); break;
				case NEGEXP: 	((Router)SimObj).SetServiceTime(new NegExp(m.sp, distp1)); break;
				case POISSON: ((Router)SimObj).SetServiceTime(new Poisson(m.sp, distp1)); break;
				default: return false;
			}
			
			for(int i = 0; i < fq.size(); i++)
			{
				((Router)SimObj).ConnectQueues(m.GetQueue((String)fq.get(i)).SimObj);	
			}
			
			String sexp;
			Expression exp;
			
			for(int i = 0; i < toq.size(); i++)
			{
				sexp = (String)conds.get(i);
				if(sexp.equalsIgnoreCase("true"))
					exp = ConstExpression.TRUE;
				else if(sexp.equalsIgnoreCase("false"))
					exp = ConstExpression.FALSE;
				else
					exp = new Expression(sexp);
					
				((Router)SimObj).ConnectQueues(m.GetQueue((String)toq.get(i)).SimObj, exp);	
			}
			
			for(int i = 0; i < fr.size(); i++)
			{
				((Router)SimObj).ConnectResources(m.GetResource((String)fr.get(i)).SimObj,
					 m.GetResource((String)tor.get(i)).SimObj, ((Integer)rqty.get(i)).intValue());	
			}

		}
		else
		{
			switch(servicedist)
			{
				case NONE: break;
				case CONST: 	((Activity)SimObj).SetServiceTime(new ConstDistribution(m.sp, distp1)); break;
				case UNIFORM: ((Activity)SimObj).SetServiceTime(new Uniform(m.sp, distp1, distp2)); break;
				case NORMAL: 	((Activity)SimObj).SetServiceTime(new Normal(m.sp, distp1, distp2)); break;
				case NEGEXP: 	((Activity)SimObj).SetServiceTime(new NegExp(m.sp, distp1)); break;
				case POISSON: ((Activity)SimObj).SetServiceTime(new Poisson(m.sp, distp1)); break;
				default: return false;
			}
			
			String sexp;
			Expression exp;
			
			for(int i = 0; i < toq.size(); i++)
			{
				sexp = (String)conds.get(i);
				if(sexp.equalsIgnoreCase("true"))
					exp = ConstExpression.TRUE;
				else if(sexp.equalsIgnoreCase("false"))
					exp = ConstExpression.FALSE;
				else
					exp = new Expression(sexp);
					
				((Activity)SimObj).ConnectQueues(m.GetQueue((String)fq.get(i)).SimObj,
					 exp, m.GetQueue((String)toq.get(i)).SimObj);	
			}
			
			for(int i = 0; i < fr.size(); i++)
			{
				((Activity)SimObj).ConnectResources(m.GetResource((String)fr.get(i)).SimObj,
					 m.GetResource((String)tor.get(i)).SimObj, ((Integer)rqty.get(i)).intValue());	
			}
			
		}
		
		return true;	
	}
  	
  public final boolean isRouter(){	return router;	}
  public final void addToQueue(Object v_o){	toq.add(v_o);	}
  public final void addFromQueue(Object v_o){	fq.add(v_o);	}
  public final void addToResource(Object v_o){	tor.add(v_o);	}
  public final void addFromResource(Object v_o){	fr.add(v_o);	}
  public final void addCond(Object v_o){	conds.add(v_o);	}
  public final void addResourceQty(Object v_o){	rqty.add(v_o);	}
  public final Vector getToQueue(){	return toq;	}
  public final Vector getFromQueue(){	return fq;	}
  public final Vector getToResource(){	return tor;	}
  public final Vector getFromResource(){	return fr;	}
  public final Vector getConds(){	return conds;	}
  public final int toQueueIndexOf(Object v_o){	return toq.indexOf(v_o);	}
  public final int fromQueueIndexOf(Object v_o){	return fq.indexOf(v_o);	}
  public final int toResourceIndexOf(Object v_o){	return tor.indexOf(v_o);	}
  public final int fromResourceIndexOf(Object v_o){	return fr.indexOf(v_o);	}
  public final boolean fromQueueContains(Object v_o){	return fq.contains(v_o);	}
  public final boolean toQueueContains(Object v_o){	return toq.contains(v_o);	}
  public final void removeFromQueue(int v_i){	fq.remove(v_i);	}
  public final void removeFromQueue(Object v_o){	fq.remove(v_o);	}
  public final void removeToQueue(int v_i){	toq.remove(v_i);	}
  public final void removeFromResource(int v_i){	fr.remove(v_i);	}
  public final void removeToResource(int v_i){	tor.remove(v_i);	}
  public final void removeCond(int v_i){	conds.remove(v_i);	}
  public final void removeResourceQty(int v_i){	rqty.remove(v_i);	}
  public final void setCondAt(Object v_o, int v_i){	conds.setElementAt(v_o, v_i);	}
  
  /**
   * chama trimToSize() para cada Vector interno
   * para economizar mem�ria alocada
   */
  public void TrimVectors()
	{
		fq.trimToSize();
		toq.trimToSize();
		fr.trimToSize();
		tor.trimToSize();
		conds.trimToSize();
		rqty.trimToSize();
	}
}