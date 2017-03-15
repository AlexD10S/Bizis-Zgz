package p6;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Contiene toda la informacion meteorologica de un dia
 */
public class InfoDia 
{
	private List<InfoPeriodo> listPeriodos=null; //Auto refreshes when more data are added
	private String fecha;
	private HashMap<String,InfoPeriodo> hashDia=new HashMap<String,InfoPeriodo>(); //Contiene los periodos del dia
	private String UVMax=null;
	private String tempMax=null;
	private String tempMin=null;
	
	public InfoDia(String fecha)
	{
		this.fecha=fecha;
	}

	public void addUvMax(String UVValue) {
		this.listPeriodos=null;
		
		this.UVMax=UVValue;
	}

	public void addTemperatura(String maxima, String minima) {
		this.listPeriodos=null;
		
		this.tempMax=maxima;
		this.tempMin=minima;
	}

	public void addViento(String periodo, String direccion, String velocidad)
	{
		this.listPeriodos=null;
		
		InfoPeriodo info=this.hashDia.get(periodo);
		if(info==null) {
			info=new InfoPeriodo(periodo);
			this.hashDia.put(periodo,info);
		}
		
		info.addViento(direccion, velocidad);
	}

	public void addEstadoCielo(String periodo, String descripcion,
			String value) 
	{
		this.listPeriodos=null;
		
		InfoPeriodo info=this.hashDia.get(periodo);
		if(info==null) {
			info=new InfoPeriodo(periodo);
			this.hashDia.put(periodo,info);
		}
		
		info.addEstadoCielo(descripcion, value);
	}

	public void addCotaNieveProv(String periodo, String value) 
	{
		this.listPeriodos=null;
		
		InfoPeriodo info=this.hashDia.get(periodo);
		if(info==null) {
			info=new InfoPeriodo(periodo);
			this.hashDia.put(periodo,info);
		}
		
		info.addCotaNieveProv(value);
	}

	public void addPrecipitacion(String periodo, String value) 
	{
		this.listPeriodos=null;
		
		InfoPeriodo info=this.hashDia.get(periodo);
		if(info==null) {
			info=new InfoPeriodo(periodo);
			this.hashDia.put(periodo,info);
		}
		
		info.addPrecipitacion(value);
	}
	
	public int getPeriodsNumber()
	{
		return this.getPeriodos().size();
	}
	
	@Override
	public String toString()
	{
		String toRet="Dia: "+this.fecha+" "+UtilsManager.getDayAndWeekDayFromDate(this.fecha)+"\n";
		if(UVMax!=null) toRet+="UV Max: "+this.UVMax+"\n";
		if(this.tempMax!=null) toRet+="Temp Max="+this.tempMax+" - Min="+this.tempMin+"\n";
		for(String key:this.hashDia.keySet())
		{
			toRet+=this.hashDia.get(key).toString()+"\n";
			
		}
		return toRet;
	}
	
	//GETTERS
	public String getFecha() {
		return fecha;
	}

	/**
	 * Obtiene todos los periodos contenidos en este dia, en forma de lista
	 */
	public List<InfoPeriodo> getPeriodos() {
		if(this.listPeriodos!=null) return this.listPeriodos;
		
		List<InfoPeriodo> periodos=new LinkedList<InfoPeriodo>();
		for(String key:this.hashDia.keySet())
		{
			periodos.add(this.hashDia.get(key));
		}
		
		this.listPeriodos=orderPeriodos(periodos);
		return this.listPeriodos;
	}
	
	/**
	 * Ordena los periodos de mayor a menor considerando las dos primeras cifras, para que se muestren en el HTML cuando sea necesario
	 * de forma ordenada y correcta
	 */
	private List<InfoPeriodo> orderPeriodos(List<InfoPeriodo> infoPeriodos)
	{
		List<InfoPeriodo> orderedInfoPeriodos=new LinkedList<InfoPeriodo>();
		
		//Como hay mas periodos de los necesarios, nos quedamos con el conjunto de periodos que abarquen un intervalo de tiempo más pequeño
		//el resto, los borramos antes de ordenar.
		int minDif=Integer.MAX_VALUE;
		//Hallamos el valor menor de diferencia de tiempo entre periodo (00-08=8, 12-24=12, etc)
		for(InfoPeriodo ip:infoPeriodos)
		{
			if(ip.getPeriodo()==null){
				minDif=0;
				break;
			}	
			else{
				int dif=Integer.parseInt(ip.getPeriodo().substring(3,5))-Integer.parseInt(ip.getPeriodo().substring(0,2));
				if(dif<minDif) minDif=dif;
			}
		}
		//Borramos todos los periodos con diferencia de tiempo mayor que la menor
		Iterator<InfoPeriodo> itPer=infoPeriodos.iterator();
		while(itPer.hasNext())
		{
			InfoPeriodo ip=itPer.next();
			if(minDif==0){
				if(ip.getPeriodo()!=null) itPer.remove();
			}
			else if(Integer.parseInt(ip.getPeriodo().substring(3,5))-Integer.parseInt(ip.getPeriodo().substring(0,2))>minDif) itPer.remove();
		}
		
		//Ordenamos los periodos restantes de mayor a menor
		while(infoPeriodos.size()>0)
		{
			int dateMin=-1;
			int min=-1;
			int cont=0;
			for(InfoPeriodo ip:infoPeriodos)
			{
				if(min==-1||ip.getPeriodo()==null||Integer.parseInt(ip.getPeriodo().substring(0,2))<dateMin){
					min=cont;
					dateMin=ip.getPeriodo()==null?0:Integer.parseInt(ip.getPeriodo().substring(0,2));
				}	
				cont++;
			}
			InfoPeriodo removed=infoPeriodos.remove(min);
			orderedInfoPeriodos.add(removed);
		}
		return orderedInfoPeriodos;
	}

	public String getUVMax() {
		return UVMax;
	}

	public String getTempMax() {
		return tempMax;
	}

	public String getTempMin() {
		return tempMin;
	}
}
