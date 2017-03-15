package p6.xml;

import java.util.List;

import org.jdom2.Element;

import p6.InfoDia;

public class Prediccion extends Element
{
	/**
	 * Genera el XML customizado de la etiqueta XML prediccion de AEMET. Contendra solo los campos no nulos y utiles para nuestro programa
	 */
	public Prediccion(Element prediccion)
	{
		super("prediccion");
		List<Element> days=prediccion.getChildren();
		for(Element day:days)
		{
			if(day.getName().equals("dia"))
			{
				Element dia=new Element("dia");
				dia.setAttribute("fecha", day.getAttributeValue("fecha"));
				List<Element> dayInfo=day.getChildren();
				for(Element info:dayInfo)
				{
					switch(info.getName())
					{
					case "prob_precipitacion":
						if(!info.getValue().isEmpty()){
							Element probprec=new Element("prob_precipitacion").setText(info.getValue());
							if(info.getAttributeValue("periodo")!=null) probprec.setAttribute("periodo",info.getAttributeValue("periodo"));
							dia.addContent(probprec);
						}
						break;
					case "cota_nieve_prov":
						if(!info.getValue().isEmpty()){
							Element cotanieve=new Element("cota_nieve_prob").setText(info.getValue());
							if(info.getAttributeValue("periodo")!=null) cotanieve.setAttribute("periodo",info.getAttributeValue("periodo"));
							dia.addContent(cotanieve);
						}
						break;
					case "estado_cielo":
						if(!info.getValue().isEmpty()){
							//infoDia.addEstadoCielo(info.getAttributeValue("periodo"),info.getAttributeValue("descripcion"),info.getValue());
							Element estadocielo=new Element("estado_cielo").setText(info.getValue()).setAttribute("descripcion",info.getAttributeValue("descripcion"));
							if(info.getAttributeValue("periodo")!=null) estadocielo.setAttribute("periodo",info.getAttributeValue("periodo"));
							dia.addContent(estadocielo);
						}
						break;
					case "viento":
						if(!info.getChild("direccion").getValue().isEmpty()){
							Element viento=new Element("viento");
							if(info.getChild("direccion")!=null) {
								viento.addContent((new Element("direccion")).setText(info.getChild("direccion").getValue()));
								viento.addContent((new Element("velocidad")).setText(info.getChild("velocidad").getValue()));
							}
							if(info.getAttributeValue("periodo")!=null) viento.setAttribute("periodo",info.getAttributeValue("periodo"));
							dia.addContent(viento);
						}
						break;
					case "temperatura":
						Element temperatura=new Element("temperatura");
						temperatura.addContent((new Element("maxima")).setText(info.getChild("maxima").getValue()));
						temperatura.addContent((new Element("minima")).setText(info.getChild("minima").getValue()));
						dia.addContent(temperatura);
						break;
					case "uv_max":
						dia.addContent((new Element("uv_max")).setText(info.getValue()));
						break;
					}
				}
				this.addContent(dia);
			}
		}
	}
}
