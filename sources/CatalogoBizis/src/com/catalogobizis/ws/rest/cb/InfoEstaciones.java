package com.catalogobizis.ws.rest.cb;

/**
 * Contiene toda la informacion de una estacion de bicis.
 */
public class InfoEstaciones {
	
	private String id;
	private String uri;
	private String title;
	private String estado;
	private String bicisDisponibles;
	private String anclajesDisponibles;
	private String coordenadaA;
	private String coordenadaB;


	
	public InfoEstaciones(){
		
	}
	public void addId(String id){
		this.id=id;
	}
	public void addUri(String uri){
		this.uri=uri;
	}
	public void addTitle(String title){
		this.title=title;
	}
	public void addEstado(String Estado){
		this.estado=Estado;
	}
	public void addBicisDisp(String bicisDisponibles){
		this.bicisDisponibles=bicisDisponibles;
	}
	public void addAnclajesDisp(String anclajesDisponibles){
		this.anclajesDisponibles=anclajesDisponibles;
	}
	public void addGeometry(String coordenada){
		String [] aux=coordenada.split(",");
		this.coordenadaB=aux[0];
		this.coordenadaA=aux[1];
	}
	public String getId(){
		return this.id;
	}
	public String getTitle(){
		return this.title;
	}
	public String getEstado(){
		return this.estado;
	}
	public String getBicisDisponibles(){
		return this.bicisDisponibles;
	}
	public String getAnclajesDisponibles(){
		return this.anclajesDisponibles;
	}
	public String getCoordenadaA(){
		return this.coordenadaA;
	}
	public String getCoordenadaB(){
		return this.coordenadaB;
	}
	public void presentar(){
		System.out.println("Id: "+this.id);
		System.out.println("Titulo: "+this.title);
		System.out.println("Estado: "+this.estado);
		System.out.println("BicisDisponibles: "+this.bicisDisponibles);
		System.out.println("AnclajesDisponibles: "+this.anclajesDisponibles);
		System.out.println("Coordenadas: "+this.coordenadaA +","+ this.coordenadaB);
	}

}
