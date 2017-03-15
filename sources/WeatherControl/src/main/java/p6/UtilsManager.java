package p6;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Contiene utilidades de todo tipo, usadas por el programa
 */
public class UtilsManager 
{
	/**
	 * Obtiene el dia de la semana de una fecha con formato yyyy-mm-dd
	 */
	public static String getWeekDayFromDate(String date)
	{
		try {
			return (new SimpleDateFormat("EE").format(new SimpleDateFormat("yyyy-MM-dd").parse(date)));
		} catch (ParseException e) {
			e.printStackTrace();
			return "???";
		}
	}
	
	/**
	 * Obtiene el dia de la semana y su numero de una fecha con formato yyyy-mm-dd
	 */
	public static String getDayAndWeekDayFromDate(String date)
	{
		return getWeekDayFromDate(date)+" "+date.substring(date.length()-2);
	}
	
	/**
	 * Obtiene la ruta de una imagen para un determinado codigo. Se usa para mostrar las imagenes del tiempo. Por defecto estan guardadas en img/ y tienen extension .gif
	 */
	public static String getImageRouteForCode(String code)
	{
		if(code==null) return "img/null.gif";
		return "img/"+code+".gif";
	}
	
	/**
	 * Devuelve la importancia de una radiacion UV <uv> suministrada, pasando de baja, moderada a alta
	 */
	public static String getUVImportanceSpan(String uv)
	{
		int uvi=Integer.parseInt(uv);
		if(uvi<=2) return "<span class=\"raduv_pred_nivel1\" title=\"índice ultravioleta bajo\" style=\"background-image: url(img/uvi_c1_pred.gif);\">&nbsp;&nbsp;"+uvi+"</span>";
		else if(uvi<=5) return "<span class=\"raduv_pred_nivel2\" title=\"índice ultravioleta moderado\" style=\"background-image: url(img/uvi_c2_pred.gif);\">&nbsp;&nbsp;"+uvi+"</span>";
		else return "<span class=\"raduv_pred_nivel3\" title=\"índice ultravioleta alto\" style=\"background-image: url(img/uvi_c3_pred.gif);\">&nbsp;&nbsp;"+uvi+"</span>";
	}
	
	/**
	 * Devuelve true si y solo si <date1> es una fecha que ocurrio antes que <date2>, considerando un formato yyyy-mm-dd
	 */
	public static boolean isLessDateThan(String date1,String date2)
	{
		try {
			Date d1=new SimpleDateFormat("yyyy-MM-dd").parse(date1);
			Date d2=new SimpleDateFormat("yyyy-MM-dd").parse(date2);
			return(d1.before(d2));
		} catch (ParseException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	/**
	 * Devuelve un Hashmap con todos los municipios de AEMET y su ID (Ordenado por orden alfabetico en los nombres de las ciudades)
	 */
	public static TreeMap<String,Integer> getAllVillagesFromAEMET()
	{
		TreeMap<String,Integer> AEMETcities=new TreeMap<String,Integer>();
		//Para todas las provincias (Hay 50):
		for(int i=1;i<=50;i++)
		{
			URL url;
			try {
				url = new URL("http://www.aemet.es/es/eltiempo/prediccion/municipios?p="+i+"&w=t");
				//Obtenemos el codigo HTML de todos los municipios de una provincia y lo parseamos
				String html=downloadFile(url.openStream());
				//Parsearlo con JSON es REALMENTE LENTO. Usamos un Scanner. Es menos profesional pero es mucho mas rapido, y funciona.
				Scanner s=(new Scanner(html));
				s.useDelimiter("<tr class=\"localidades\">");
				//Iteramos todas las tablas de localidades
				s.next();
				while(s.hasNext()){
					String groupLocalidades=s.next();
					Scanner toHref=(new Scanner(groupLocalidades));
					//Hay hasta 4 municipios por tabla. Utilizamos como delimitador el link que siempre esta antes del municipio
					toHref.useDelimiter("<a href=\'/es/eltiempo/prediccion/municipios/");
					toHref.next();
					//Obtenemos los municipios, y su ID
					for(int l=0;l<=3;l++)
					{
						//Si no hay siguiente municipio es que habia menos de 4 en la tabla. Rompemos
						if(!toHref.hasNext()) break;
						String link=toHref.next();
						//El nombre de la ciudad se encontrará entre la ubicacion del primer > (O segundo, si la ciudad se encuentra entre <strong>) y el primer < encontrados.
						String city=link.substring(link.indexOf(">")+1);
						if(city.trim().startsWith("<")) city=city.substring(city.indexOf(">")+1);
						city=city.substring(0,city.indexOf("<")).trim();
						//La ID se encontrará embebida en el link, la obtenemos con un metodo
						AEMETcities.put(city,getAemetIdFor(link.substring(0, link.indexOf("\'"))));
					}
					toHref.close();
				}
				s.close();
			} catch (IOException e) {
				System.err.println("Error getting villages from AEMET for province "+i);
				e.printStackTrace();
			} 

		}
		return AEMETcities;
	}
	
	/**
	 * Devuelve el link de descarga de el XML de predicciones de tiempo de una ciudad, teniendo su ID
	 */
	public static int getAemetIdFor(String link)
	{
		return Integer.parseInt(link.substring(link.lastIndexOf("-id")+3,link.length()));
	}
	
	/**
	 * Descarga un fichero de <is> y lo introduce en un String
	 */
	public static String downloadFile(InputStream is) throws IOException
	{
		Scanner s = new Scanner(is).useDelimiter("\\A");
		 String toRet= s.hasNext() ? s.next() : "";
		 s.close();
		 return toRet;
	}
}
