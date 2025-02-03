package com.tony.util;

import java.awt.*;
import java.util.*;

public class TColorConverter
{
	public static Hashtable m_htColors;
	
	public static Color stringToColor(String v_strColor)
	{
		if(m_htColors == null)
		{
			m_htColors = new Hashtable();
		}
		Object oColor = m_htColors.get(v_strColor);
		if(oColor == null)
		{
			oColor =  new Color(Integer.decode("0x" + v_strColor.substring(0, 2)).intValue(),
								Integer.decode("0x" + v_strColor.substring(2, 4)).intValue(),
								Integer.decode("0x" + v_strColor.substring(4, 6)).intValue());
			m_htColors.put(v_strColor, oColor);
		}
		return (Color)oColor;
	}
}
