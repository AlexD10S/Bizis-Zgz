package sistemasweb.soap;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.gson.Gson;
 
public class StadisticControlService {
 
	private static final String XML_VERSION_TAG="<?xml version=\"1.0\"?>";
	private static final String XML_SUBMITSTATRESPONSE_DTD="<!DOCTYPE response [<!ELEMENT response (error?,message) >"+
															"<!ELEMENT error (#PCDATA) >"+
															"<!ELEMENT message (#PCDATA) >]>";
	
	private static final String XML_GETALLSTATRESPONSE_DTD="<!ELEMENT response (error?,message) >"+
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
	
	
	public String submitStadistic(String stadistic) throws JDOMException, IOException 
	{
		String errorMsg="";
		try {
			SAXBuilder constructor=new SAXBuilder(true);
			Document xmlDoc=constructor.build(new StringReader(stadistic));
			Stadistic stadisticObject=new Stadistic(xmlDoc.getRootElement());
			// create HTTP Client
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost("https://api.parse.com/1/classes/LogData");
			HttpResponse response;
			request.addHeader("Content-Type", "application/json");
			request.addHeader("X-Parse-Application-Id",
					"wRs86SgBLKJBaQM4Y6fgDgBUvXTO3H7mzZ7Acqq1");
			request.addHeader("X-Parse-Master-Key","SNlfyyIn2HAI3IPHJBIPXkxZ7wzrL9PbrqKmJXH7");
			request.setEntity(new StringEntity(Stadistic.parseAsJSON(stadisticObject)));
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String aux = null;
			while ((aux = rd.readLine()) != null) {
				sb.append(aux);
			}
			return XML_VERSION_TAG+XML_SUBMITSTATRESPONSE_DTD+"<response><message><![CDATA["+sb.toString()+"]]></message></response>";
		} 
		catch(org.jdom.input.JDOMParseException e){
			errorMsg="Invalid XML input: Can't validate using provided DTD";
		}
		catch (Exception e) {
			errorMsg="Server error";
		}
		return XML_VERSION_TAG+XML_SUBMITSTATRESPONSE_DTD+"<response><error info=\""+errorMsg+"\"/><message></message></response>";

	}
	
	public static void main(String args[]) throws JDOMException, IOException
	{
		String stad=XML_VERSION_TAG+"<!DOCTYPE stadistic [<!ELEMENT stadistic (IP?,StadisticDate?,AccessBrowser?,AccessOS?,City?,Action?) ><!ELEMENT IP (#PCDATA)><!ELEMENT StadisticDate (#PCDATA)><!ELEMENT AccessBrowser (#PCDATA)><!ELEMENT AccessOS (#PCDATA)><!ELEMENT City (#PCDATA)><!ELEMENT Action (#PCDATA)>]>";
		System.out.println((new StadisticControlService()).submitStadistic(stad+"<stadistic><IP>2.5.5.5</IP><Action>Hola</Action></stadistic>"));
		//System.out.println((new StadisticControlService()).getAllStadistics());	
	}
	public String getAllStadistics() 
	{
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

			  return XML_VERSION_TAG+XML_GETALLSTATRESPONSE_DTD+"<response>"+xml+"</response>";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return XML_VERSION_TAG+XML_GETALLSTATRESPONSE_DTD+"<response><error info=\"Server error\"/><message></message></response>";
 
	}
 
}