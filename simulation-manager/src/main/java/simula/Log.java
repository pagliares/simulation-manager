// Arquivo Log.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 01.Nov.1999	Wladimir

package simula;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Classe que implementa log da simulação. Pode ser conectada a um
 * arquivo ou a um objeto servidor de mensagens para distribuir as
 * mensagens a diversos usuários distribuídos.
 * No caso de utilização de log em arquivo, este será gravado em 
 * arquivo texto de nome "sim"aaaa/mm/dd-hh:mm:ss.log
 */
public class Log
{
	private static PrintStream os;
	// aqui uma ref ao obj servidor de mensagens
	
	/**
	 * Abre arquivo de log para iniciar a sessão.
	 */
	public static boolean OpenFile()
	{
		SimpleDateFormat formatter
     = new SimpleDateFormat ("yyyy,MM,dd-HH'h'mm'm'ss's'");
 		Date currentTime_1 = new Date();
 		String dateString = formatter.format(currentTime_1);
 		try
		{
			FileOutputStream ofile = new FileOutputStream("sim" + dateString + ".log");	
			os = new PrintStream(ofile);
		}
		catch(FileNotFoundException x){return false;}
		
		return true;
	}

	/**
	 * Fecha log.
	 */
	public static synchronized void Close()
	{
		if(os != null)
		{
			os.close();
			os = null;
		}
	}
	
	protected Log()
		{throw new RuntimeException("Classe não pode ser instanciada");}
	
	/**
	 * Registra entrada no log
	 */
	public static synchronized void LogMessage(String entry)
	{
		if(os != null)
		{
			os.println(entry);
			os.flush();
		}
	}
}