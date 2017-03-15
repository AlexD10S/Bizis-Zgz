package sistemasweb.soap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import p6.*;
import p6.xml.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.google.gson.Gson;

import com.google.gson.JsonParser;

/**
 * SERVICIO SOAP
 */

public class WeatherControl 
{
	
	private static TreeMap<String,Integer> availlableCities;

	static
	{
		availlableCities=UtilsManager.getAllVillagesFromAEMET();
	}
	
	private static String getInputFromXMLEnvelope(String xml) throws JDOMException, IOException
	{
		SAXBuilder constructor=new SAXBuilder(true);
		Document xmlDoc=constructor.build(new StringReader(xml));
		Element root=xmlDoc.getRootElement();
			
		String msg=root.getValue().trim();
		if(msg.startsWith("<![CDATA[")&&msg.endsWith("]>"))
		{
			msg=msg.substring(0, msg.length()-2);
		}
		return msg;
	}
	private static String getInputFromJSONEnvelope(String json)
	{
		 Gson gson = new Gson();
		 JSonEnvelope message;
		 message = gson.fromJson(json,
				 JSonEnvelope.class);
			
		return message.message;
	}
	private class JSonEnvelope
	{
		public String message;
	}
	private int parseInput(String msg)
	{
		int id=-1;
		try
		{
			id=Integer.parseInt(msg);
			boolean exists=false;
			for(String ks:availlableCities.keySet())
			{
				Integer i=availlableCities.get(ks);
				if(i!=null&&i==id){
					exists=true;
					break;
				}
			}
			if(!exists) id=-1;
		} 
		catch(NumberFormatException e)
		{
			Integer i=availlableCities.get(msg);
			if(i!=null) id=i;
		}
		
		return id;
	}
	
	/**
	 * Descarga la informacion del tiempo asociado al municipio <idMunicipio>. Si este no existe, lanzará una excepción (Al no existir tampoco la página a la que intentaremos acceder)
	 */
	public String WeatherXML(String xml)
	{
		try
		{
			String msg=getInputFromXMLEnvelope(xml);
			int idMunicipio=parseInput(msg);
			if(idMunicipio==-1) return AEMETParser.XML_VERSION_TAG+AEMETParser.XML_WEATHER_DTD+"<root><error>City not found</error><ciudad id=\"0\"/><prediccion></prediccion></root>";
			String parsedId=Integer.toString(idMunicipio);
			//La ID se rellena con ceros hasta los 5 caracteres. Lo hacemos iterativamente
			while(parsedId.length()<5) parsedId="0"+parsedId;
			
			//Parseamos el String XML recibido a nuestro formato XML y lo devolvemos
			return AEMETParser.transformStandardAEMETXMLIntoCustomXML(idMunicipio, UtilsManager.downloadFile((new URL("http://www.aemet.es/xml/municipios/localidad_"+parsedId+".xml")).openStream()));
		}
		catch(Exception e)
		{
			return AEMETParser.XML_VERSION_TAG+AEMETParser.XML_WEATHER_DTD+"<root><error>Server error</error><ciudad id=\"0\"/><prediccion></prediccion></root>";
		}
	}
	
	/**
	 * Genera un HTML partiendo de una ID o nombre de ciudad suministrado
	 */
	public String WeatherHTMLusingJSON(String json) 
	{
		try
		{
			SoapCommManager comm=new SoapCommManager();
			SAXBuilder constructor=new SAXBuilder(false);
			String jsonString=comm.getSOAPWeatherJson(json);
			AEMETParser parser=new AEMETParser(new JsonParser().parse(jsonString));
			StringWriter sw=new StringWriter();
			parser.createHTML(new PrintWriter(sw));
			return sw.getBuffer().toString();
		}
		catch(Exception e)
		{
			return "<head></head><body><h1>A error has ocurred handling the request</h1></body>";
		}
	}
	
	/**
	 * Genera un HTML partiendo de una ID o nombre de ciudad suministrado
	 */
	public String WeatherHTMLusingXML(String xml) 
	{
		try
		{
			SoapCommManager comm=new SoapCommManager();
			SAXBuilder constructor=new SAXBuilder(false);
			Document xmlDoc=constructor.build(new StringReader(comm.getSOAPWeatherXML(xml)));
			AEMETParser parser=new AEMETParser(xmlDoc);
			StringWriter sw=new StringWriter();
			parser.createHTML(new PrintWriter(sw));
			return sw.getBuffer().toString();
		}
		catch(Exception e)
		{
			return "<head></head><body><h1>A error has ocurred handling the request</h1></body>";
		}
	}
	
	/**
	 * Genera un JSON partiendo de una ID o nombre de ciudad suministrado
	 */
	public String WeatherJSON(String json) 
	{
		try
		{
			String msg=getInputFromJSONEnvelope(json);
			int idMunicipio=parseInput(msg);
			if(idMunicipio==-1) return "{\"error\":\"City not found\"}";
			String parsedId=Integer.toString(idMunicipio);
			//La ID se rellena con ceros hasta los 5 caracteres. Lo hacemos iterativamente
			while(parsedId.length()<5) parsedId="0"+parsedId;
			SAXBuilder constructor=new SAXBuilder(false);
			Document xmlDoc=constructor.build(new StringReader(UtilsManager.downloadFile((new URL("http://www.aemet.es/xml/municipios/localidad_"+parsedId+".xml")).openStream())));
			AEMETParser parser=new AEMETParser(xmlDoc);
			StringWriter sw=new StringWriter();
			parser.createJSON(new PrintWriter(sw));
			return sw.getBuffer().toString();
		}
		catch(Exception e)
		{
			return "{\"error\":\"A error has ocurred handling the request\"}";
		}
	}
	
	
	public String AvaillableCitiesAsXML()
	{
		StringBuilder sb=new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-15\" ?>");
		//Insert DTD
		sb.append("<!DOCTYPE cities [<!ELEMENT cities (city*) ><!ELEMENT city EMPTY ><!ATTLIST city \n\t cityname CDATA #REQUIRED \n\t id CDATA #REQUIRED >]>");
		sb.append("<cities>");
		for(String s:availlableCities.keySet())
		{
			sb.append("<city name=\""+s+"\" id=\""+availlableCities.get(s)+"\"/>");
		}
		sb.append("</cities>");
		return sb.toString();
	}
	
	public String AvaillableCitiesAsJSON()
	{
		StringBuilder sb=new StringBuilder();
		sb.append("{\"cities\":[");
		boolean first=true;
		for(String s:availlableCities.keySet())
		{
			if(!first) sb.append(",");
			sb.append("{\"name\":\""+s+"\",\"id\":"+availlableCities.get(s)+"}");
			first=false;
		}
		sb.append("]}");
		return sb.toString();
	}
}
