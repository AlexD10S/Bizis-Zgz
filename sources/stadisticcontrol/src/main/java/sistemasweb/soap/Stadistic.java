package sistemasweb.soap;

import java.util.List;

import javax.ws.rs.FormParam;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

public class Stadistic 
{
	public String IP=null;
	public StadisticDate AccessDate=null;
	public String AccessBrowser=null;
	public String AccessOS=null;
	public String City=null;
	public String Action=null;
	 
	public Stadistic(Element root) throws JDOMException
	{
		if(!root.getName().equals("stadistic")) throw new JDOMException();
			
		List<Element> stadisticElements=root.getChildren();
		for(Element stat:stadisticElements)
		{
			switch(stat.getName())
			{
			case "IP":
				IP=stat.getValue();
				break;
			case "AccessDate":
				AccessDate=new StadisticDate(stat.getValue());
				break;
			case "AccessBrowser":
				AccessBrowser=stat.getValue();
				break;
			case "AccessOS":
				AccessOS=stat.getValue();
				break;
			case "City":
				City=stat.getValue();
				break;
			case "Action":
				Action=stat.getValue();
				break;
			}
		}
	}
	 public static String parseAsJSON(Stadistic s)
	 {
		 boolean firstIntrod=false;
		 String toRet="{";
		 if(s.IP!=null&&!s.IP.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"IP\":\""+s.IP+"\""; firstIntrod=true;
		 }
		 if(s.AccessDate!=null&&!s.AccessDate.isEmpty()) {
			 if(firstIntrod) toRet=toRet+",";
			 toRet=toRet+"\"AccessDate\":\""+s.AccessDate+"\""; firstIntrod=true;
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
	 public static String parseAsXML(Stadistic s)
	 {
		 String toRet="<stadistic>";
		 if(s.IP!=null&&!s.IP.isEmpty()) {
			 toRet=toRet+"<IP>"+s.IP+"</IP>";
		 }
		 if(s.AccessDate!=null&&!s.AccessDate.isEmpty()) {
			 toRet=toRet+"<AccessDate>"+s.AccessDate+"</AccessDate>";
		 }
		 if(s.AccessBrowser!=null&&!s.AccessBrowser.isEmpty()) {
			 toRet=toRet+"<AccessBrowser>"+s.AccessBrowser+"</AccessBrowser>";
		 }
		 if(s.AccessOS!=null&&!s.AccessOS.isEmpty()) {
			 toRet=toRet+"<AccessOS>"+s.AccessOS+"</AccessOS>";
		 }
		 if(s.City!=null&&!s.City.isEmpty()) {
			 toRet=toRet+"<City>"+s.City+"</City>";
		 }
		 if(s.Action!=null&&!s.Action.isEmpty()) {
			 toRet=toRet+"<Action>"+s.Action+"</Action>";
		 }
		 toRet=toRet+"</stadistic>";
		 return toRet;
	 }
	 public class StadisticDate
	 {
		 public String _type;
		 public String iso;
		 public StadisticDate(String date)
		 {
			 this._type="Date";
			 this.iso=date;
		 }
		 public String toString()
		 {
			 return this.iso;
		 }
		 public boolean isEmpty()
		 {
			 return iso==null||this.iso.isEmpty();
		 }
	 }
}
