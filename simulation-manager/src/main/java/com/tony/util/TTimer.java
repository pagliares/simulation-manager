package com.tony.util;

public class TTimer
{
	public static void pause(long v_l)
	{
		try
		{
			Thread.sleep(v_l);
		}
		catch(InterruptedException v_e){}
	}
}
