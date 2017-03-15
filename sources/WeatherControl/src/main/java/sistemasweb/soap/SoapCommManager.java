package sistemasweb.soap;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import p6.AEMETParser;
import p6.UtilsManager;

public class SoapCommManager 
{
	/**
	 * Controla las comunicaciones via SOAP de forma transparente al resto de la implementacion (Salvo por las excepciones)
	 */
	private static final String endpointURL="http://localhost:8080/axis/services/WeatherControl";

	public String getSOAPWeatherXML(String xml) throws ServiceException, MalformedURLException, RemoteException 
	{
		Service service = new Service();
		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new java.net.URL(endpointURL));
		call.setOperationName(new QName("WeatherControl", "WeatherXML") );
		call.addParameter( "xml", XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType( org.apache.axis.encoding.XMLType.XSD_STRING );

		return (String) call.invoke( new Object[] { xml} );
	}
	
	public String getSOAPWeatherJson(String json) throws ServiceException, RemoteException, MalformedURLException
	{
		Service service = new Service();
		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new java.net.URL(endpointURL));
		call.setOperationName(new QName("WeatherControl", "WeatherJSON") );
		call.addParameter( "json", XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType( org.apache.axis.encoding.XMLType.XSD_STRING );

		return (String) call.invoke( new Object[] { json} );
	}
}
