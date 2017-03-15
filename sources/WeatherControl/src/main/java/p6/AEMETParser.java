package p6;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import p6.xml.Prediccion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Parsea los datos de tiempo de AEMET proporcionados en XML, guardados en una BD o en JSON, y los guarda en un tipo de dato local.
 * (Una lista de InfoDias)
 */
public class AEMETParser 
{
	public static final String XML_VERSION_TAG="<?xml version=\"1.0\" encoding=\"ISO-8859-15\" ?>";
	
	public static final String XML_WEATHER_DTD="<!DOCTYPE root [<!ELEMENT root (error?,ciudad,prediccion)>"+
												"<!ELEMENT error (#PCDATA)>"+
												"<!ELEMENT ciudad EMPTY>"+
												"<!ATTLIST ciudad \n\t"+
												"id CDATA #REQUIRED>"+
												"<!ELEMENT prediccion (dia*)>"+
												"<!ELEMENT dia (prob_precipitacion+,estado_cielo+,viento+,temperatura,uv_max?)>"+
												"<!ATTLIST dia \n\t"+
													"fecha CDATA #REQUIRED>"+
												"<!ELEMENT prob_precipitacion (#PCDATA)>"+
												"<!ATTLIST prob_precipitacion \n\t"+
													"periodo CDATA #IMPLIED>"+
												"<!ELEMENT estado_cielo (#PCDATA)>"+
												"<!ATTLIST estado_cielo \n\t"+
													"descripcion CDATA #REQUIRED \n\t"+
													"periodo CDATA #IMPLIED>"+
												"<!ELEMENT viento (direccion,velocidad)>"+
												"<!ATTLIST viento \n\t"+
													"periodo CDATA #IMPLIED>"+
												"<!ELEMENT direccion (#PCDATA)>"+
												"<!ELEMENT velocidad (#PCDATA)>"+
												"<!ELEMENT temperatura (maxima,minima)>"+
												"<!ELEMENT maxima (#PCDATA)>"+
												"<!ELEMENT minima (#PCDATA)>"+
												"<!ELEMENT uv_max (#PCDATA)>]>";
	
	public static final String XML_SOAP_MESSAGE_ENVELOPE_DTD="<!DOCTYPE message [<!ELEMENT message (#PCDATA) >]>";
	
	//Dias de tiempo
	private List<InfoDia> infoDias=new ArrayList<InfoDia>();
	
	/**
	 * Parsea los datos de tiempo contenidos en el XML <xmlDoc> usando JSON.
	 */
	public AEMETParser(Document xmlDoc)
	{
		Element root=xmlDoc.getRootElement();
		Element prediccion=root.getChild("prediccion");
		List<Element> days=prediccion.getChildren();
		for(Element day:days)
		{
			if(day.getName().equals("dia"))
			{
				InfoDia infoDia=new InfoDia(day.getAttributeValue("fecha"));
				List<Element> dayInfo=day.getChildren();
				for(Element info:dayInfo)
				{
					switch(info.getName())
					{
					case "prob_precipitacion":
						if(!info.getValue().isEmpty()){
							infoDia.addPrecipitacion(info.getAttributeValue("periodo"),info.getValue());
						}
						break;
					case "cota_nieve_prov":
						if(!info.getValue().isEmpty()){
							infoDia.addCotaNieveProv(info.getAttributeValue("periodo"),info.getValue());
						}
						break;
					case "estado_cielo":
						if(!info.getValue().isEmpty()){
							infoDia.addEstadoCielo(info.getAttributeValue("periodo"),info.getAttributeValue("descripcion"),info.getValue());
						}
						break;
					case "viento":
						if(!info.getChild("direccion").getValue().isEmpty()){
							infoDia.addViento(info.getAttributeValue("periodo"),info.getChild("direccion").getValue(),info.getChild("velocidad").getValue());
						}
						break;
					case "temperatura":
						infoDia.addTemperatura(info.getChild("maxima").getValue(),info.getChild("minima").getValue());
						break;
					case "uv_max":
						infoDia.addUvMax(info.getValue());
						break;
					}
				}
				this.infoDias.add(infoDia);
			}
		}
		
		//La lista estará ordenada por fecha
		this.infoDias=orderByDate(this.infoDias);
	}
	
	/**
	 * Parsea los datos contenidos en el json <el> usando GSON
	 */
	public AEMETParser(JsonElement el)
	{
		this.infoDias=new LinkedList<InfoDia>();
		JsonObject  jobject = el.getAsJsonObject();
	    JsonArray infoDiasArray = jobject.getAsJsonArray("InfoDias");
	    for(JsonElement jel:infoDiasArray)
	    {
	    	JsonObject jobj=jel.getAsJsonObject();
	    	InfoDia infoDia=new InfoDia(jobj.get("fecha").getAsString());
	    	infoDia.addTemperatura(parseNullableJsonString(jobj.get("temp_max")), parseNullableJsonString(jobj.get("temp_min")));
	    	infoDia.addUvMax(parseNullableJsonString(jobj.get("UV_max")));
	    	
	    	//Itera sobre todos los periodos del dia, añadiendolos a ese objeto.
	    	JsonArray periodos=jobj.getAsJsonArray("periodos");
	    	for(JsonElement jelPer:periodos)
	    	{
	    		JsonObject jobjPer=jelPer.getAsJsonObject();
	    		String periodo=parseNullableJsonString(jobjPer.get("periodo"));
	    		infoDia.addEstadoCielo(periodo, parseNullableJsonString(jobjPer.get("estadocielo_descripcion")), parseNullableJsonString(jobjPer.get("estadocielo_icono")));
	    		infoDia.addPrecipitacion(periodo, parseNullableJsonString(jobjPer.get("precipitacion_prob")));
	    		infoDia.addCotaNieveProv(periodo, parseNullableJsonString(jobjPer.get("cotanieve_valor")));
	    		infoDia.addViento(periodo, parseNullableJsonString(jobjPer.get("viento_direccion")), parseNullableJsonString(jobjPer.get("viento_velocidad")));
	    	}
	    	this.infoDias.add(infoDia);
	    }
	    
	    //La lista estará ordenada por fecha
	    this.infoDias=orderByDate(this.infoDias);
	}
	
	/**
	 * Borra la lista <infoDias> y devuelve una lista con sus dias ordenados por fecha
	 */
	private List<InfoDia> orderByDate(List<InfoDia> infoDias)
	{
		//Este metodo tiene complejidad O(n^2). Se podria hacer un Quicksort pero para un numero de elementos bajo es lo suficientemente rapido
		List<InfoDia> orderedInfoDias=new LinkedList<InfoDia>();
		while(infoDias.size()>0)
		{
			String dateMin="";
			int min=-1;
			int cont=0;
			for(InfoDia id:infoDias)
			{
				if(min==-1||UtilsManager.isLessDateThan(id.getFecha(),dateMin)){
					min=cont;
					dateMin=id.getFecha();
				}	
				cont++;
			}
			InfoDia removed=infoDias.remove(min);
			orderedInfoDias.add(removed);
		}
		return orderedInfoDias;
	}
	
	/**
	 * GSON no puede parsear un String nulo. Este metodo comprueba si un String es JSON nulo y devuelve null si corresponde, parseandolo 
	 * si no.
	 */
	private String parseNullableJsonString(JsonElement jobj){
		if(jobj.isJsonNull()) return null;
		else return jobj.getAsString();
	}
	
	/**
	 * Devuelve la lista de informacion del dia
	 */
	public List<InfoDia> getInfoDias()
	{
		return this.infoDias;
	}
	
	/**
	 * Crea un fichero JSON en <dest> conteniendo los datos de dia expresados en formato JSON
	 */
	public void createJSON(PrintWriter f) throws FileNotFoundException
	{
		//Open dest to write
		f.println("{");
		f.println("\"InfoDias\":[");
		
		Iterator<InfoDia> diaIt=this.infoDias.iterator();
		while(diaIt.hasNext())
		{
			InfoDia infoDia=diaIt.next();
			f.println("{");
			f.println("\"fecha\":\""+infoDia.getFecha()+"\""+",");
			f.println("\"temp_max\":"+(infoDia.getTempMax()==null?null:("\""+infoDia.getTempMax()+"\""))+",");
			f.println("\"temp_min\":"+(infoDia.getTempMin()==null?null:("\""+infoDia.getTempMin()+"\""))+",");
			f.println("\"periodos\":[");

			Iterator<InfoPeriodo> itPeriodo=infoDia.getPeriodos().iterator();
			while(itPeriodo.hasNext())
			{
				InfoPeriodo infoPeriodo=itPeriodo.next();
				f.println("{");
				f.println("\"periodo\":"+(infoPeriodo.getPeriodo()==null?null:("\""+infoPeriodo.getPeriodo()+"\""))+",");
				f.println("\"estadocielo_icono\":"+(infoPeriodo.getValorEstadoCielo()==null?null:("\""+infoPeriodo.getValorEstadoCielo()+"\""))+",");
				f.println("\"estadocielo_descripcion\":"+(infoPeriodo.getDescripcionEstadoCielo()==null?null:("\""+infoPeriodo.getDescripcionEstadoCielo()+"\""))+",");
				f.println("\"viento_direccion\":"+(infoPeriodo.getDireccionViento()==null?null:("\""+infoPeriodo.getDireccionViento()+"\""))+",");
				f.println("\"viento_velocidad\":"+(infoPeriodo.getVelocidadViento()==null?null:("\""+infoPeriodo.getVelocidadViento()+"\""))+",");
				f.println("\"cotanieve_valor\":"+(infoPeriodo.getValorCotaNieve()==null?null:("\""+infoPeriodo.getValorCotaNieve()+"\""))+",");
				f.println("\"precipitacion_prob\":"+(infoPeriodo.getValorPrecipitacion()==null?null:("\""+infoPeriodo.getValorPrecipitacion()+"\"")));
				f.println("}");
				if(itPeriodo.hasNext()) f.println(",");
			}
			f.println("]"+",");
			f.println("\"UV_max\":"+(infoDia.getUVMax()==null?null:("\""+infoDia.getUVMax()+"\"")));
			f.println("}");
			if(diaIt.hasNext()) f.println(",");
		}
		f.println("]");
		f.println("}");
		f.close();
	}
	
	/**
	 * Crea un fichero HTML en <dest> conteniendo los datos de dia expresados en formato JSON
	 */
	public void createHTML(PrintWriter f) throws FileNotFoundException
	{
		//Global styles, stilesheets used, etc
		f.println("<head><link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"estilosAEMET.css\">"
				+ "<style> body {"+
				"margin: 0;"+
				"font-size: .70em;"+
				  "padding: 0;"+
				  "font-family: Verdana,Arial,Helvetica,sans-serif;"+
				  "font-size: 100%;"+
				  "color: #000;"+
				  "background-color: #FFF"+
				"} table {"+
				" display: table;"+
				"border-collapse: separate;"+
				"border-spacing: 2px;"+
				"border-color: gray;"+
				"}</style>"+
				"</head>");
		
		//HTML body
		f.println("<body>");
		//font-size:.70em; because style in body is not accepted 
		//To avoid line breaks we use white-space:nowrap; as explained in http://stackoverflow.com/questions/1893751/how-to-prevent-line-break-in-a-column-of-a-table-cell-not-a-single-cell
		f.println("<table cellspacing=\"2\" class=\"tabla_datos marginbottom10px\" style=\"visibility: visible;font-size: .70em;white-space: nowrap;\">");
		f.println("<thead>");
		f.println("<tr class=\"cabecera_niv1\">");
		f.println("<th class=\"borde_rlb_th_avisos_cab borde_t_cab\" title=\"Fecha\" abbr=\"Fec.\" rowspan=\"2\"><div class=\"cabecera_celda\">Fecha</div></th>");
		List<InfoDia> temp=new LinkedList<InfoDia>();
		for(int i=0;i<infoDias.size()&&i<3;i++)
		{
			temp.add(infoDias.get(i));
		}
		List<InfoDia> infoDias=temp;
		//DIAS (TABLA SUPERIOR)
		for(InfoDia id:infoDias)
		{
			//If this day has only one period, it will not be shown and instead the day will be twice as long in the HTML
			String property=id.getPeriodsNumber()==1?"rowspan=\"2\"":"colspan=\""+id.getPeriodsNumber()+"\"";
			f.println("<th class=\"borde_rb_cab borde_t_cab\" "+property+"\">"+UtilsManager.getDayAndWeekDayFromDate(id.getFecha())+"</th>");
		}
		f.println("</tr>");
		f.println("<tr class=\"cabecera_niv2\">");
		for(InfoDia id:infoDias)
		{
			for(InfoPeriodo ip:id.getPeriodos())
			{
				if(ip.getPeriodo()!=null)f.println("<th class=\"borde_rb no_wrap\">"+ip.getPeriodo()+"</th>");
			}		
		}
		f.println("</tr>");
		f.println("</thead>");
		f.println("<tbody>");
		
		//ESTADO DEL CIELO
		f.println("<tr>");
		f.println("<th title=\"Estado del cielo\" abbr=\"Cielo\" class=\"borde_rlb_th_avisos\">Estado del cielo</th>");
		for(InfoDia id:infoDias)
		{
			Iterator<InfoPeriodo> it=id.getPeriodos().iterator();
			while(it.hasNext())
			{
				InfoPeriodo ip=it.next();
				String borde=it.hasNext()?"borde_b":"borde_rb";
				f.println("<td class=\""+borde+"\"><img src=\""+UtilsManager.getImageRouteForCode(ip.getValorEstadoCielo())+"\" title=\""+ip.getDescripcionEstadoCielo()+"\" alt=\""+ip.getDescripcionEstadoCielo()+"\"></td>");
			}		
		}
		f.println("</tr>");
		
		//PROB PRECIPITACION
		f.println("<tr>");
		f.println("<th title=\"Probabilidad de precipitación\" abbr=\"Pro.\" class=\"borde_rlb_th_avisos\">Prob. precip.</th>");
		for(InfoDia id:infoDias)
		{
			Iterator<InfoPeriodo> it=id.getPeriodos().iterator();
			while(it.hasNext())
			{
				InfoPeriodo ip=it.next();
				String borde=it.hasNext()?"borde_b":"borde_rb";
				f.println("<td class=\""+borde+"\">"+(ip.getValorPrecipitacion()==null?" -":(ip.getValorPrecipitacion()+"%"))+"&nbsp;</td>");
			}		
		}
		f.println("</tr>");
		
		//COTA DE NIEVE
		f.println("<tr>");
		f.println("<th title=\"Cota de nieve a nivel de provincia\" abbr=\"Cot.\" class=\"borde_rlb_th_avisos\">Cota nieve prov.(m)</th>");
		for(InfoDia id:infoDias)
		{
			Iterator<InfoPeriodo> it=id.getPeriodos().iterator();
			while(it.hasNext())
			{
				InfoPeriodo ip=it.next();
				String borde=it.hasNext()?"borde_b":"borde_rb";
				f.println("<td class=\""+borde+"\">"+(ip.getValorCotaNieve()!=null?ip.getValorCotaNieve():"")+"&nbsp;</td>");
			}		
		}
		f.println("</tr>");
		
		//TEMP MAX Y MIN
		f.println("<tr>");
		f.println("<th title=\"Temperatura mínima y máxima (°C)\" abbr=\"Max/Min.\" class=\"borde_rlb_th_avisos\">Temp. mín./máx. (°C)</th>");
		for(InfoDia id:infoDias)
		{
			if(id.getTempMax()!=null) f.println("<td class=\"borde_rb alinear_texto_centro no_wrap\" colspan=\""+id.getPeriodsNumber()+"\"><span class=\"texto_azul\">"+id.getTempMin()+"</span>&nbsp;/&nbsp;<span class=\"texto_rojo\">"+id.getTempMax()+"&nbsp;</span></td>	");	
			else f.println("<td class=\"borde_rb alinear_texto_centro no_wrap\" colspan=\""+id.getPeriodsNumber()+"\"> - </td>	");	
		}
		f.println("</tr>");
		
		//VIENTO (DIR)
		f.println("<tr>");
		f.println("<th title=\"Dirección del viento\" abbr=\"Vie.\" class=\"borde_rlb_th_viento\">Viento</th>");
		for(InfoDia id:infoDias)
		{
			Iterator<InfoPeriodo> it=id.getPeriodos().iterator();
			while(it.hasNext())
			{
				InfoPeriodo ip=it.next();
				String borde=it.hasNext()?"":"borde_r";
				f.println("<td class=\""+borde+" alinear_texto_centro\"><img src=\""+UtilsManager.getImageRouteForCode(ip.getDireccionViento())+"\" title=\""+ip.getDireccionViento()+"\" alt=\""+ip.getDireccionViento()+"\"></td>");
			}		
		}
		f.println("</tr>");
		
		//VIENTO (VEL)
		f.println("<tr>");
		f.println("<th title=\"Velocidad del viento en kilometros por hora\" abbr=\"km/h.\" class=\"borde_rlb_th_avisos\">(km/h)</th>");
		for(InfoDia id:infoDias)
		{
			Iterator<InfoPeriodo> it=id.getPeriodos().iterator();
			while(it.hasNext())
			{
				InfoPeriodo ip=it.next();
				String borde=it.hasNext()?"borde_b":"borde_rb";
				f.println("<td class=\""+borde+"\">"+ip.getVelocidadViento()+"&nbsp;</td>");
			}		
		}
		f.println("</tr>");
		
		//UV
		f.println("<tr>");
		f.println("<th title=\"Indice ultravioleta máximo\" abbr=\"UV\" class=\"borde_rlb_th_avisos\">Indice UV máximo</th>");
		for(InfoDia id:infoDias)
		{
			//Si no hay radiacion UV se imprime un cuadrado azul claro, vacio
			if(id.getUVMax()==null) f.println("<td class=\"borde_rb fondo_celda_azul_claro\" colspan=\""+id.getPeriodsNumber()+"\">&nbsp;</td>");
			else f.println("<td class=\"borde_rb\" colspan=\""+id.getPeriodsNumber()+"\">"+UtilsManager.getUVImportanceSpan(id.getUVMax())+"</td>");
		}
		f.println("</tr>");
		//AVISOS
		f.println("<tr>");
		f.println("<th title=\"Avisos\" abbr=\"Aviso\" class=\"borde_rlb_th_avisos\">");
		f.println("<div>Avisos</div>");
		f.println("</th>");
		int cont=4; //Solo avisos para los 4 primeros dias
		for(InfoDia id:infoDias)
		{
			if(cont<=0){
				f.println("<td class=\"borde_rb fondo_celda_azul_claro\" colspan=\""+id.getPeriodsNumber()+"\">&nbsp;</td>");
			}
			else{
				cont--;
				f.println("<td colspan=\""+id.getPeriodsNumber()+"\" class=\"borde_rb\">");
				boolean alertaViento=false;
				for(InfoPeriodo ip:id.getPeriodos())
				{
					if(ip.getVelocidadViento()!=null&&Integer.parseInt(ip.getVelocidadViento())>=30) alertaViento=true;
				}
				if(alertaViento){
					f.println("<img src=\"img/VI3.gif\" class=\"width24px\" title=\"Alerta viento\" alt=\"Alerta viento\">");
					f.println("<p class=\"marginbottom3px\">Viento&nbsp;</p></td>");
				}
				else{
					f.println("<img src=\"img/SR.gif\" class=\"width24px\" title=\"Sin Riesgo\" alt=\"Sin Riesgo\">");
					f.println("<p class=\"marginbottom3px\">Sin Riesgo&nbsp;</p></td>");
				}
			}
		}
		f.println("</tr>");
		f.println("</tbody>");
		f.println("</body>");
		f.close();
	}
	
	/**
	 * Transforma el XML AEMET standard a nuestro formato customizado XML
	 */
	public static String transformStandardAEMETXMLIntoCustomXML(int id,String standardXML)
	{
		String output=null;
		SAXBuilder constructor=new SAXBuilder(false);
		try 
		{
			Document xmlDoc=constructor.build(new StringReader(standardXML));
			Element root=xmlDoc.getRootElement();
			
			Element customRoot=new Element(root.getName());
			customRoot.addContent((new Element("ciudad")).setAttribute("id",Integer.toString(id)));
			
			Element prediccion=root.getChild("prediccion");
			customRoot.addContent(new Prediccion(prediccion));
			
			//La salida será tabulada, para poder ser visualizada en el cliente de forma agradable
			XMLOutputter salidaXml = new XMLOutputter(Format.getPrettyFormat());
			
			output=salidaXml.outputString(customRoot);
			output=XML_VERSION_TAG+XML_WEATHER_DTD+output;
		} 
		catch (JDOMException | IOException e) {
			e.printStackTrace();
			System.err.println("Error parsing XML file");
		} 
		
		return output;
	}
	
	/**
	 * Metodo de test. Devuelve todos los dias expresando sus contenidos, en un String.
	 */
	@Override
	public String toString()
	{
		String toRet="";
		for(InfoDia id:this.infoDias)
		{
			toRet+=id.toString()+"\n";
		}
		return toRet;
	}
}
