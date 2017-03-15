package sistemasweb.rest;

import javax.ws.rs.FormParam;

public class RestStadistic 
{
	 @FormParam("IP")
	public String IP;
	 
	 @FormParam("AccessDate")
	public String AccessDate;
	 
	 @FormParam("AccesssBrowser")
	public String AccessBrowser;
	 
	 @FormParam("AccessOS")
	public String AccessOS;
	 
	 @FormParam("City")
	public String City;
	 
	 @FormParam("Action")
	public String Action;
	 
	 public static String parseAsJSON(RestStadistic s)
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
}
