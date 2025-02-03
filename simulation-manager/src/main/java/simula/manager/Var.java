// Arquivo  Var.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 30.Out.1999 Wladimir

package simula.manager;

public class Var implements java.io.Serializable
{
	protected String id;
	protected float value;
	
	public String toString()
	{
		return "<Var id=\""+id+"\" value=\""+value+"\"/>\r\n";
	}
	public Var(String Id, float Value){id = Id; value = Value;}
	public Var(String Id){id = Id;}
	
	public String getID(){	return id;	}
	public float getValue(){	return value;	}
	public void setID(String v_strID){	id = v_strID;	}
	public void setValue(float v_fValue){	value = v_fValue;	}
}