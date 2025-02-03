// Arquivo HistogramEntry.java 
// Implementação das Classes do Sistema de Gerenciamento da Simulação
// 16.Jul.1999 Wladimir

package simula.manager;

import java.io.*;

/**
 * Entrada para os histogramas do modelo.
 * Entry.obsid é usado como link (opcional) para o Observador
 */
public class HistogramEntry extends Entry
{
	private static int lastid;	// identificador ÚNICO para os histogramas
	static boolean hasSerialized = true; // "lastid já foi serializado"
	
	/**
	 * largura de cada coluna 
	 */
	private float width;			
	/**
	 * número de colunas
	 */
	private int   num_columns;	
	/**
	 * início do histograma
	 */
	private float start;		
	/**
	 * tipo (Histogram.SERIES, WEIGHTED, e FREQUENCY(default))
	 */
	private short kind;			
												
	transient simula.Histogram SimObj;		// objeto de simulação
                              					// não é serializado
  		
	public String toString()
	{
		StringBuffer stb = new StringBuffer();
		stb.append("<HistogramEntry width=\""+width+"\" num_columns=\""+num_columns+"\" start=\""+start+"\" kind=\""+kind+"\">\r\n");
		stb.append("<H_super>\r\n");
		stb.append(super.toString());
		stb.append("</H_super>\r\n");
		stb.append("</HistogramEntry>\r\n");
		return stb.toString();
	}
	/**
	 * constrói um objeto com id gerado internamente. 
	 */
	public HistogramEntry()
	{
		super("h_" + String.valueOf(lastid));
		lastid++;
		kind = simula.Histogram.FREQUENCY;
	}
	
	public void copyAttributes(Entry v_e)
	{
		super.copyAttributes(v_e);
		HistogramEntry hstEntry = (HistogramEntry)v_e;
		width = hstEntry.width;
		num_columns = hstEntry.num_columns;
		start = hstEntry.start;
		kind = hstEntry.kind;
		SimObj = hstEntry.SimObj;
	}
	
	public final float getWidth(){	return width;	}
	public final int getNumColumns(){	return num_columns;	}
	public final float getStart(){	return start;	}
	public final short getKind(){	return kind;	}
	public final void setWidth(float v_fWidth){	width = v_fWidth;	}
	public final void setNumColumns(int v_iNumColumns){	num_columns = v_iNumColumns;	}
	public final void setStart(float v_fStart){	start = v_fStart;	}
	public final void setKind(short v_sKind){	kind = v_sKind;	}
	
	boolean Generate(SimulationManager m)
	{
		if(num_columns < 1 || width <= 0 || kind < simula.Histogram.SERIES ||
			kind > simula.Histogram.WEIGHTED)
			return false;
		
		SimObj = new simula.Histogram(m.s, start, width, num_columns, kind);
		
		return true;
	}
	
	void DoReport(PrintStream os)
	// Realiza relatório específico do histograma (tabela)
	{
		if(SimObj == null)	// erro
			return;
	
		os.println("----------------------------------------------------------------------");
		os.println("\r\nReport from histogram " + name + " (id = " + id + ")");
		os.println("Histogram type: ");
		
		switch(kind)
		{
			case simula.Histogram.FREQUENCY: os.println("frequency"); break;	
			case simula.Histogram.WEIGHTED: os.println("weighted"); break;
			case simula.Histogram.SERIES: os.println("time series"); break;
			default: os.println("Error"); return;
		}
	
		SimObj.PrintData(os);
		os.println("----------------------------------------------------------------------");
	}
	
	private void writeObject(ObjectOutputStream stream)
     throws IOException
	{
		stream.defaultWriteObject();
		
		if(hasSerialized)
			return;
			
		stream.writeInt(lastid);
		hasSerialized = true;
	}
 	private void readObject(ObjectInputStream stream)
     throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		
		if(hasSerialized)
			return;
			
		lastid = stream.readInt();
		hasSerialized = true;
	}
}