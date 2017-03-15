package sistemasweb.rest;

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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import p6.*;
import p6.xml.*;
import sistemasweb.soap.SoapCommManager;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.google.gson.JsonParser;

/**
 * SERVICIO REST
 */
@Path("/")
public class WeatherControl 
{
	
	private static TreeMap<String,Integer> availlableCities;
	
	static
	{
		availlableCities=UtilsManager.getAllVillagesFromAEMET();
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
	@GET
	@Path("WeatherXML/{param}")
	public Response WeatherXML(@PathParam("param") String msg)
	{
		try
		{
			int idMunicipio=parseInput(msg);
			if(idMunicipio==-1) return Response.status(404).entity(AEMETParser.XML_VERSION_TAG+AEMETParser.XML_WEATHER_DTD+"<root><error>City not found</error><ciudad id=\"0\"/><prediccion></prediccion></root>").header("content-type", "text/xml").build();
			String parsedId=Integer.toString(idMunicipio);
			//La ID se rellena con ceros hasta los 5 caracteres. Lo hacemos iterativamente
			while(parsedId.length()<5) parsedId="0"+parsedId;
			
			//Parseamos el String XML recibido a nuestro formato XML y lo devolvemos
			return Response.ok().entity(AEMETParser.transformStandardAEMETXMLIntoCustomXML(idMunicipio, UtilsManager.downloadFile((new URL("http://www.aemet.es/xml/municipios/localidad_"+parsedId+".xml")).openStream()))).header("Content-Type", "text/xml").build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity(AEMETParser.XML_VERSION_TAG+AEMETParser.XML_WEATHER_DTD+"<root><error>Server error</error><ciudad id=\"0\"/><prediccion></prediccion></root>").header("content-type", "text/xml").build();
		}
	}
	
	/**
	 * Genera un HTML partiendo de una ID o nombre de ciudad suministrado
	 */
	@GET
	@Path("WeatherHTMLusingJSON/{param}")
	public String WeatherHTMLusingJSON(@PathParam("param") String msg) 
	{
		try
		{
			SoapCommManager comm=new SoapCommManager();
			SAXBuilder constructor=new SAXBuilder(false);
			String jsonString=comm.getSOAPWeatherJson("{\"message\":\""+msg+"\"}");
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
	@GET
	@Path("WeatherHTMLusingXML/{param}")
	public String WeatherHTMLusingXML(@PathParam("param") String msg) 
	{
		try
		{
			SoapCommManager comm=new SoapCommManager();
			SAXBuilder constructor=new SAXBuilder(false);
			Document xmlDoc=constructor.build(new StringReader(comm.getSOAPWeatherXML(AEMETParser.XML_VERSION_TAG+AEMETParser.XML_SOAP_MESSAGE_ENVELOPE_DTD+"<message>"+msg+"</message>")));
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
	@GET
	@Path("WeatherJSON/{param}")
	public Response GenerarJSON(@PathParam("param") String msg) 
	{
		try
		{
			int idMunicipio=parseInput(msg);
			if(idMunicipio==-1) return Response.status(404).entity("{\"error\":\"City not found\"}").build();
			String parsedId=Integer.toString(idMunicipio);
			//La ID se rellena con ceros hasta los 5 caracteres. Lo hacemos iterativamente
			while(parsedId.length()<5) parsedId="0"+parsedId;
			SAXBuilder constructor=new SAXBuilder(false);
			Document xmlDoc=constructor.build(new StringReader(UtilsManager.downloadFile((new URL("http://www.aemet.es/xml/municipios/localidad_"+parsedId+".xml")).openStream())));
			AEMETParser parser=new AEMETParser(xmlDoc);
			StringWriter sw=new StringWriter();
			parser.createJSON(new PrintWriter(sw));
			return Response.ok().entity(sw.getBuffer().toString()).header("Content-Type", "application/json").build();
		}
		catch(Exception e)
		{
			return Response.serverError().entity("{\"error\":\"A error has ocurred handling the request\"}").build();
		}
	}
	
	@GET
	@Path("AvaillableCitiesAsXML")
	public Response AvaillableCitiesAsXML()
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
		return Response.ok().entity(sb.toString()).header("content-type", "text/xml").build();
	}
	@GET
	@Path("AvaillableCitiesAsJSON")
	public Response AvaillableCitiesAsJSON()
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
		return Response.ok().entity(sb.toString()).header("content-type", "application/json").build();
	}
}
