package com.catalogobizis.ws.rest.cb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.TreeMap;

public class UtilsManager {
	
	
	/**
	 * Descarga un fichero de <is> y lo introduce en un String
	 */
	public static String downloadFile(InputStream is) throws IOException
	{
		byte[] buf=new byte[4096];
		int cont=0;
		String s="";
		while((cont=is.read(buf))!=-1)
		{
			String aux=new String(buf,0,cont,Charset.forName("UTF-8" ));
			s=s+aux;
		}
		System.out.println(s);
		 return s;
	}

}
