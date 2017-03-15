package com.catalogobizis.ws.rest;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import com.catalogobizis.ws.rest.cb.InfoEstaciones;
import com.catalogobizis.ws.rest.cb.ParserEstaciones;
import com.catalogobizis.ws.rest.cb.UtilsManager;



/**

 * SERVICIO REST

 */

@Path("/")

public class CatalogoBizis

{

	private static List<InfoEstaciones> estaciones;

	

	static{
		try{
		SAXBuilder constructor=new SAXBuilder(false);
		String standardXML=UtilsManager.downloadFile((new URL("http://zaragoza.es/api/recurso/urbanismo-infraestructuras/estacion-bicicleta.xml?&fl=id,title,estado,bicisDisponibles,anclajesDisponibles,geometry&srsname=wgs84")).openStream());
		Document xmlDoc=constructor.build(new StringReader(standardXML));
		ParserEstaciones p=new ParserEstaciones(xmlDoc);
		estaciones=ParserEstaciones.getLista();
		}
		catch(Exception e)

		{
		  System.out.println("A error has ocurred handling the request");
		  }

		}
	
	@GET
	@Path("EstacionesXML")

	public Response EstacionesXML(String msg)
	{

		StringBuilder sb=new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		sb.append("<!DOCTYPE estaciones [<!ELEMENT estaciones (estacion*)><!ELEMENT estacion EMPTY><!ATTLIST estacion \n\t titulo CDATA #REQUIRED \n\t id CDATA #REQUIRED >]>");
		sb.append("<estaciones>");

		for(int i=0;i<estaciones.size();i++)

		{
			sb.append("<estacion titulo=\""+estaciones.get(i).getTitle()+"\" id=\""+estaciones.get(i).getId()+"\"/>");

		}

		sb.append("</estaciones>");

		return Response.ok().entity(sb.toString()).header("content-type", "application/xml").build();

	}
	@GET

	@Path("EstacionesJSON")

	public Response EstacionesJSON(String msg)

	{

		StringBuilder sb=new StringBuilder();
		sb.append("{\"cities\":[");
		boolean first=true;
		

		for(int i=0;i<estaciones.size();i++)

		{

			if(!first) sb.append(",");

			sb.append("{\"titulo\":\""+estaciones.get(i).getTitle()+"\",\"id\":"+estaciones.get(i).getId()+"}");

			first=false;

		}

		sb.append("]}");

		return Response.ok().entity(sb.toString()).header("content-type", "application/json").build();

	}
	@GET
	@Path("InfoEstacionesXML")

	public Response InfoEstacionesXML(String msg)
	{

		StringBuilder sb=new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-15\" ?>");
		sb.append("<infoestaciones>");

		for(int i=0;i<estaciones.size();i++)

		{
			sb.append("<estacion>");
			sb.append("<titulo>"+estaciones.get(i).getTitle()+"</titulo>");
			sb.append("<estado>"+estaciones.get(i).getEstado()+"</estado>");
			sb.append("<bicisDisponibles>"+estaciones.get(i).getBicisDisponibles()+"</bicisDisponibles>");
			sb.append("<anclajesDisponibles>"+estaciones.get(i).getAnclajesDisponibles()+"</anclajesDisponibles>");
			sb.append("<coordenadaA>"+estaciones.get(i).getCoordenadaA()+"</coordenadaA>");
			sb.append("<coordenadaB>"+estaciones.get(i).getCoordenadaB()+"</coordenadaB>");
			sb.append("</estacion>");

		}

		sb.append("</infoestaciones>");

		Response response = Response.status(200).type(MediaType.TEXT_XML).entity(sb.toString()).build();
		return response;
		//return Response.ok().entity(sb.toString()).header("content-type", "application/xml").build();

	}
	@GET

	@Path("InfoEstacionesJSON")

	public Response InfoEstacionesJSON(String msg)

	{

		StringBuilder sb=new StringBuilder();
		sb.append("{\"estacion\":[");
		boolean first=true;
		

		for(int i=0;i<estaciones.size();i++)

		{

			if(!first) sb.append(",");

			sb.append("{\"titulo\":\""+estaciones.get(i).getTitle()
					+"\",\"estado\":\""+estaciones.get(i).getEstado()
					+"\",\"bicisDisponibles\":\""+estaciones.get(i).getBicisDisponibles()
					+"\",\"anclajesDisponibles\":\""+estaciones.get(i).getAnclajesDisponibles()
					+"\",\"coordenadaA\":\""+estaciones.get(i).getCoordenadaA()
					+"\",\"coordenadaB\":\""+estaciones.get(i).getCoordenadaB()
					+"\"}");

			first=false;

		}

		sb.append("]}");

		return Response.ok().entity(sb.toString()).header("content-type", "application/json").build();

	}
	
}