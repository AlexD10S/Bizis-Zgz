package p6;

/**
 * Contiene toda la informacion meteorologica referente al periodo de un dia
 */
public class InfoPeriodo 
{
	public static final String DEFAULT_PERIODO_TEXT_IF_NULL="TDIA"; //Si el periodo es nulo (A veces pasa en el XML) este es el texto
																	//por defecto que devolveremos cuando nos pidan que lo imprimamos
	private String periodo;
	
	private String direccionViento=null;
	private String velocidadViento=null;
	
	private String descripcionEstadoCielo=null;
	private String valorEstadoCielo=null;
	
	private String valorCotaNieve=null;
	
	private String valorPrecipitacion=null;
	
	public InfoPeriodo(String periodo)
	{
		this.periodo=periodo;
	}
	public void addViento(String direccion, String velocidad) {
		this.direccionViento=direccion;
		this.velocidadViento=velocidad;	
	}

	public void addEstadoCielo(String descripcion,String value) {
		this.descripcionEstadoCielo=descripcion;
		this.valorEstadoCielo=value;
		
	}

	public void addCotaNieveProv(String value) {
		this.valorCotaNieve=value;
	}

	public void addPrecipitacion(String value) {
		this.valorPrecipitacion=value;	
	}
	
	@Override
	public String toString()
	{
		String res="Periodo: "+(this.periodo==null?DEFAULT_PERIODO_TEXT_IF_NULL:this.periodo)+"\n";
		if(this.direccionViento!=null) res+="\tDireccion Viento: "+this.direccionViento+" Velocidad Viento: "+this.velocidadViento+"\n";
		if(this.descripcionEstadoCielo!=null) res+="\tEstado cielo: "+this.descripcionEstadoCielo+" IconID: "+this.valorEstadoCielo+"\n";
		if(this.valorCotaNieve!=null) res+="\tValor Cota Nieve: "+this.valorCotaNieve+"\n";
		if(this.valorPrecipitacion!=null) res+="\tValor Precipitacion: "+this.valorPrecipitacion+"\n";
		return res;
	}
	public String getPeriodo() {
		return periodo;
	}
	public String getDireccionViento() {
		return direccionViento;
	}
	public String getVelocidadViento() {
		return velocidadViento;
	}
	public String getDescripcionEstadoCielo() {
		return descripcionEstadoCielo;
	}
	public String getValorEstadoCielo() {
		return valorEstadoCielo;
	}
	public String getValorCotaNieve() {
		return valorCotaNieve;
	}
	public String getValorPrecipitacion() {
		return valorPrecipitacion;
	}
}
