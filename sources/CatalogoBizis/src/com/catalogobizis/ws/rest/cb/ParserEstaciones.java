package com.catalogobizis.ws.rest.cb;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;





public class ParserEstaciones extends Element {
	private static List<InfoEstaciones> infoEstacion=new ArrayList<InfoEstaciones>();
	
	public ParserEstaciones(Document xmlDoc){
		Element root=xmlDoc.getRootElement();
		Element estaciones=root.getChild("result");
		List<Element> estacion=estaciones.getChildren();
		for(Element st:estacion)
		{
			
			if(st.getName().equals("estacion"))
			{
				
				InfoEstaciones infoSt=new InfoEstaciones();
				List<Element> infoS=st.getChildren();
				for(Element info:infoS)
				{
					switch(info.getName())
					{
					case "id":
						if(!info.getValue().isEmpty()){
							infoSt.addId(info.getValue());
						}
						break;
					case "uri":
						if(!info.getValue().isEmpty()){
							infoSt.addUri(info.getValue());
						}
						break;
					case "title":
						if(!info.getValue().isEmpty()){
							infoSt.addTitle(info.getValue());
						}
						break;
					case "estado":
						if(!info.getValue().isEmpty()){
							infoSt.addEstado(info.getValue());
						}
						break;
					case "bicisDisponibles":
						if(!info.getValue().isEmpty()){
							infoSt.addBicisDisp(info.getValue());
						}
						break;
					case "anclajesDisponibles":
						if(!info.getValue().isEmpty()){
							infoSt.addAnclajesDisp(info.getValue());
						}
						break;
					case "geometry":
						if(!info.getValue().isEmpty()){
							List<Element> geo=info.getChildren();
							for(Element g:geo)
							{
								if(g.getName().equals("coordinates"))
									infoSt.addGeometry(g.getValue());
							}
						}
						break;
					
					}
				}
				this.infoEstacion.add(infoSt);
			}
		}
	}
	
	public static List <InfoEstaciones> getLista(){
		return infoEstacion;
	}

}
