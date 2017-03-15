package sistemasweb.rest;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.annotations.Form;

import com.google.gson.Gson;

import sistemasweb.soap.Stadistic;
import sistemasweb.soap.StadisticList;
 
@Path("/")
public class StadisticControlService {
 
	private static final String XML_VERSION_TAG="<?xml version=\"1.0\"?>";
	private static final String XML_SUBMITSTATRESPONSE_DTD="<!DOCTYPE response [<!ELEMENT response (error?,message) >"+
															"<!ELEMENT error (#PCDATA) >"+
															"<!ELEMENT message (#PCDATA) >]>";
	
	private static final String XML_GETALLSTATRESPONSE_DTD="<!DOCTYPE response [<!ELEMENT response (error?,message) >"+
															"<!ELEMENT error (#PCDATA) >"+
															"<!ELEMENT message (stadistics?) >"+
															"<!ELEMENT stadistics (stadistic*) >"+
															"<!ELEMENT stadistic (IP?,StadisticDate?,AccessBrowser?,AccessOS?,City?,Action?) >"+
															"<!ELEMENT IP (#PCDATA)>"+
															"<!ELEMENT StadisticDate (#PCDATA)>"+
															"<!ELEMENT AccessBrowser (#PCDATA)>"+
															"<!ELEMENT AccessOS (#PCDATA)>"+
															"<!ELEMENT City (#PCDATA)>"+
															"<!ELEMENT Action (#PCDATA)>]>";
	@POST
	@Consumes("text/xml")
	@Produces("text/xml")
	@Path("/submitStadistic")
	public Response submitStadistic(XMLStadistic stadistic) 
	{
		String errorMsg="";
		
		// create HTTP Client
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost("https://api.parse.com/1/classes/LogData");
		HttpResponse response;
		try {
			request.addHeader("Content-Type", "application/json");
			request.addHeader("X-Parse-Application-Id",
					"wRs86SgBLKJBaQM4Y6fgDgBUvXTO3H7mzZ7Acqq1");
			request.addHeader("X-Parse-Master-Key","SNlfyyIn2HAI3IPHJBIPXkxZ7wzrL9PbrqKmJXH7");
			request.setEntity(new StringEntity(XMLStadistic.parseAsJSON(stadistic)));
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String aux = null;
			while ((aux = rd.readLine()) != null) {
				sb.append(aux);
			}
			
			//We must return a XML file. We parse the Parse response into a valid XML using GSON
			if(response.getStatusLine().getStatusCode()/100==2){
				//Good response
				String outMsg="<![CDATA["+sb+"]]>";
				return Response.status(response.getStatusLine().getStatusCode()).entity(XML_VERSION_TAG+XML_SUBMITSTATRESPONSE_DTD+"<response><message>"+outMsg+"</message></response>").header("Content-Type", "text/xml").build();
			}
			else{
				//Error!
				errorMsg="Parse returned status "+response.getStatusLine().getStatusCode();
			}
		} catch (Exception e) {
			errorMsg="Server error";
		}
		return Response.serverError().entity(XML_VERSION_TAG+XML_SUBMITSTATRESPONSE_DTD+"<response><error info=\""+errorMsg+"\"/><message></message></response>").header("Content-Type", "text/xml").build();
	}
	
	@GET
	@Produces("text/xml")
	@Path("/getAllStadistics")
	public Response getAllStadistics() 
	{
		String errorMsg="";
		// create HTTP Client
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("https://api.parse.com/1/classes/LogData");
		  HttpResponse response;
		try {
			request.addHeader("Content-Type", "application/json");
			request.addHeader("X-Parse-Application-Id", "wRs86SgBLKJBaQM4Y6fgDgBUvXTO3H7mzZ7Acqq1");
			request.addHeader("X-Parse-REST-API-Key","2RoSRvC9UAL6Ddb4OWdth94nWfnhPUnaaKy8MIw2");
			response = client.execute(request);
			  BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			  StringBuilder sb=new StringBuilder();
			  String aux=null;
			  while ((aux = rd.readLine()) != null) {
			    sb.append(aux);
			  }
			//We must return a XML file. We parse the Parse response into a valid XML using GSON
			  if(response.getStatusLine().getStatusCode()/100==2){
				  Gson gson = new Gson();
				  StadisticList statList=new StadisticList();
				  statList = gson.fromJson(sb.toString(),
						  StadisticList.class);
				  String xml="<stadistics>";
				  for(Stadistic s:statList.getResults())
				  {
					  xml=xml+Stadistic.parseAsXML(s);
			  	}
				  xml=xml+"</stadistics>";
				  return Response.status(response.getStatusLine().getStatusCode()).entity(XML_VERSION_TAG+XML_GETALLSTATRESPONSE_DTD+"<response>"+xml+"</response>").header("Content-Type", "text/xml").build();
			  }
				else{
					//Error!
					errorMsg="Parse returned status "+response.getStatusLine().getStatusCode();
				}
		} catch (Exception e) {
			errorMsg="Server error";
		}
		return Response.serverError().entity(XML_VERSION_TAG+XML_SUBMITSTATRESPONSE_DTD+"<response><error info=\""+errorMsg+"\"/><message></message></response>").header("Content-Type", "text/xml").build();
	}
 
}