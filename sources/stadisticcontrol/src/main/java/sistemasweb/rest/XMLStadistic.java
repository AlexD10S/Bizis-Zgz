package sistemasweb.rest;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="stadistic")
public class XMLStadistic 
{
	@XmlElement(nillable=true)
	public String IP;
	 
	@XmlElement(nillable=true)
	public String AccessDate;
	 
	@XmlElement(nillable=true)
	public String AccessBrowser;
	 
	@XmlElement(nillable=true)
	public String AccessOS;
	 
	@XmlElement(nillable=true)
	public String City;
	 
	@XmlElement(nillable=true)
	public String Action;
	 
	 public static String parseAsJSON(XMLStadistic s)
	 {
		 boolean firstIntrod=false;
		 String toRet="{";
		 if(s.IP!=null&&!s.IP.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"IP\":\""+s.IP+"\""; firstIntrod=true;
		 }
		 if(s.AccessDate!=null&&!s.AccessDate.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"AccessDate\":{\"__type\": \"Date\",\"iso\": \""+s.AccessDate+"\"}"; firstIntrod=true;
		 }
		 if(s.AccessBrowser!=null&&!s.AccessBrowser.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"AccessBrowser\":\""+s.AccessBrowser+"\""; firstIntrod=true;
		 }
		 if(s.AccessOS!=null&&!s.AccessOS.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"AccessOS\":\""+s.AccessOS+"\""; firstIntrod=true;
		 }
		 if(s.City!=null&&!s.City.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"City\":\""+s.City+"\""; firstIntrod=true;
		 }
		 if(s.Action!=null&&!s.Action.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"Action\":\""+s.Action+"\""; firstIntrod=true;
		 }
		 toRet=toRet+"}";
		 return toRet;
	 }
}
