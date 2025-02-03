package com.tony.util;

import java.io.*;

public class TDirectoryTreeMaker
{
	protected String m_strDestFile;
	protected String m_strDirPath;
	protected StringBuffer m_stbContent;
	protected int m_iRootPathLength;
	protected static char[] m_cSizeUnits = {'b', 'K', 'M', 'G'};
	
	public TDirectoryTreeMaker(String v_strDirPath, String v_strDestFile)
	{
		m_strDestFile = v_strDestFile;
		m_strDirPath = v_strDirPath;
		m_stbContent = new StringBuffer();
	}
	
	public void makeTree()
	{
		File flRoot = new File(m_strDirPath);
		m_iRootPathLength = flRoot.getPath().length();
		System.out.println(".");
		if(flRoot.isDirectory())
		{
			makeTree(flRoot);
		System.out.println("..");
			try
			{
				FileWriter fw = new FileWriter(m_strDestFile);
				String strToWrite = m_stbContent.toString();
				fw.write(strToWrite, 0, strToWrite.length());
				fw.flush();
				fw.close();
			}
			catch(IOException v_e)
			{
				v_e.printStackTrace();
			}
		}
	}
	
	public void makeTree(File v_flDirectory)
	{
		String[] v_strList = v_flDirectory.list();
		try
		{
			for(int i=0; i<v_strList.length; i++)
			{
				String strFilePath = v_flDirectory.getPath()+"\\"+v_strList[i];
				File flCurr = new File(strFilePath);
				if(flCurr.isDirectory())
				{
					m_stbContent.append(flCurr.getPath().replace('\\', '.').substring(2, flCurr.getPath().length())+"\r\n");
					makeTree(flCurr);
				}
				else
				{
					//System.out.println(strFilePath.substring(m_iRootPathLength+1, strFilePath.length()));
					FileInputStream fin = new FileInputStream(strFilePath);
					int iSize = (int)fin.available();
					if(iSize > 0)
					{
//						appendMinSize(m_stbContent, strFilePath.substring(m_iRootPathLength+1, strFilePath.length()), '.', 100, false);
						int iSizeUnit = 0;
						if(iSize > 1000)
						{
							iSize = (int)Math.ceil((double)iSize/1024);
							iSizeUnit++;
						}
						if(iSize > 1000000)
						{
							iSize = (int)Math.ceil((double)iSize/1024);
							iSizeUnit++;
						}
						if(iSize > 100)
						{
//							appendMinSize(m_stbContent, strFilePath.substring(m_iRootPathLength+1, strFilePath.length()), '.', 100, false);
//							m_stbContent.append("\t");
//							appendMinSize(m_stbContent, iSize+" "+m_cSizeUnits[iSizeUnit], ' ', 8, true);
//							m_stbContent.append("\r\n");
						}
					}
				}
			}
		}
		catch(IOException v_e)
		{
			v_e.printStackTrace();
		}
	}
	
	public void appendMinSize(StringBuffer v_stb, String v_str, char v_cFill, int v_iMinSize, boolean v_bFillOnLeft)
	{
		int iNSpaces = v_iMinSize - v_str.length();
		if(v_bFillOnLeft)
		{
			for(int i=0; i<iNSpaces; i++)
			{
				v_stb.append(v_cFill);
			}
			v_stb.append(v_str);
		}
		else
		{
			v_stb.append(v_str);
			for(int i=0; i<iNSpaces; i++)
			{
				v_stb.append(v_cFill);
			}
		}
	}
	
	public static void main(String[] v_strArgs)
	{
		if(v_strArgs.length >= 2)
		{
			TDirectoryTreeMaker oTreeMaker = new TDirectoryTreeMaker(v_strArgs[0], v_strArgs[1]);
			oTreeMaker.makeTree();
		}
		else
		{
			System.out.println("usage: TDirectoryTreeMaker <Directory> <output file>");
		}
	}
}
