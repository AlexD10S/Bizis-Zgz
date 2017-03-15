<!DOCTYPE html>
<html>
<head>
<?php
	function sendStadistic($accion,$ciudad)
	  {
	   // Function to get the client IP address (safe way), based on the answer in http://stackoverflow.com/questions/15699101/get-the-client-ip-address-using-php
	   function get_client_ip() 
	   {
		$ipaddress = '';
		if (getenv('HTTP_CLIENT_IP'))
		    $ipaddress = getenv('HTTP_CLIENT_IP');
		else if(getenv('HTTP_X_FORWARDED_FOR'))
		    $ipaddress = getenv('HTTP_X_FORWARDED_FOR');
		else if(getenv('HTTP_X_FORWARDED'))
		    $ipaddress = getenv('HTTP_X_FORWARDED');
		else if(getenv('HTTP_FORWARDED_FOR'))
		    $ipaddress = getenv('HTTP_FORWARDED_FOR');
		else if(getenv('HTTP_FORWARDED'))
		    $ipaddress = getenv('HTTP_FORWARDED');
		else if(getenv('REMOTE_ADDR'))
		    $ipaddress = getenv('REMOTE_ADDR');
		else
		    $ipaddress = 'UNKNOWN';
		return $ipaddress;
	   }
	   //echo $_SERVER['HTTP_USER_AGENT'] . "\n\n";

	   $browser = get_browser(null, true);

	   $url = 'http://localhost:8080/stadisticcontrol/rest/submitStadistic';
	   $data = <<<EOD
<?xml version="1.0" encoding="ISO-8859-15" ?>
<!DOCTYPE stadistic [
<!ELEMENT stadistic (IP?,AccessDate?,AccessBrowser?,AccessOS?,City?,Action?) >
<!ELEMENT IP (#PCDATA)>
<!ELEMENT AccessDate (#PCDATA)>
<!ELEMENT AccessBrowser (#PCDATA)>
<!ELEMENT AccessOS (#PCDATA)>
<!ELEMENT City (#PCDATA)>
<!ELEMENT Action (#PCDATA)>]>
EOD;
	   $data = $data . '<stadistic><IP>'.get_client_ip().'</IP><AccessDate>'.date('Y-m-d').'T'.date('H:i:s').'.000Z</AccessDate><AccessBrowser>'.$browser["browser"].'</AccessBrowser><AccessOS>'.$browser["platform"].'</AccessOS>';
	   if(!is_null($ciudad)) $data=$data.'<City>'.$ciudad.'</City>';
	   $data = $data . '<Action>'.$accion.'</Action></stadistic>';

	   $ch = curl_init();

		curl_setopt($ch, CURLOPT_URL,$url);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                                            'Content-Type: text/xml'
                                            ));
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_POSTFIELDS,
				$data);
		
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

		$server_output = curl_exec ($ch);

		curl_close ($ch);
	  }
	  
	  //Load all cities
	  $xmlD=file_get_contents("http://localhost:8080/WeatherControl/rest/AvaillableCitiesAsXML");
	  $xmlCities=simplexml_load_string($xmlD) or die("Error: Cannot create object");
?>



	<title>Sistema de seguimiento de estaciones de Bizis</title>
	<link rel="stylesheet" href="style1.css">
</head>
<body>
	<div id="cabecera">
	<form class="formularioDatos" action="Inicio.php"> 
		<input type="submit" value="JSON">
	</form>
	<form class="formularioStats"  action="control-estadistico"> 
		<input type="submit" value="Estadisticas" name="submitDatos">
	</form>
	<h1 class="tituloWeb">Sistema de seguimiento de estaciones de Bizis</h1>
	</div>
	<div id="bizis">	
		  <fieldset class="fiel">
			<legend style="font-weight:bold;">Llegar a una estacion Bizi</legend>
			<br><br>
			<form method="POST" action="<?php echo $_SERVER['PHP_SELF']; ?>"> 
				<input top="200px" type="text" name="nameB" size="20"><br><br> 
		
			<br><br>
			<select name="nameA">
				<option disabled>Selecciona estacion de Bizi</option>
				<?php
					$xmlD=file_get_contents("http://localhost:8080/CatalogoBizis/rest/EstacionesXML");
					$xml=simplexml_load_string($xmlD) or die("Error: Cannot create object");
					$first=true;
					 foreach ($xml->children() as $var) {
						
						echo "<option ".($first?"selected=\"selected\"":"").">" . $var['titulo'] . "</option>";
						$first=false;
					}			
				
					
				?>
			
			</select>
		
			<br><br><br><br>
			<input type="submit" value="Buscar Ruta" name="submit2">
			</form>
		  </fieldset>
	</div>
	<div id="googleMap">
	  <fieldset class="fiel">
	  <?php
		  if(isset($_POST['submit2']))
				 
			{
			 
			$url = 'http://localhost:8181/Maps/ruta';
			
			//$data='var1=a&var2=b';
			//var_dump(urlencode($data));
			//$data='var1='.urlencode($_POST['nameA']).'&var2='.urlencode($_POST['nameB']);
			$data = <<<EOD
<?xml version="1.0" encoding="ISO-8859-15" ?>
<!DOCTYPE biziroute [
<!ELEMENT biziroute (from,to) >
<!ELEMENT from (#PCDATA)>
<!ELEMENT to (#PCDATA)>]>
EOD;
			$data=$data . '<biziroute><from>'.$_POST['nameB'].'</from><to>'.$_POST['nameA'].'</to></biziroute>';

			$ch = curl_init();

			curl_setopt($ch, CURLOPT_URL,$url);
			curl_setopt($ch, CURLOPT_HTTPHEADER, array(
                                            'Content-Type: text/xml'
                                            ));
			curl_setopt($ch, CURLOPT_POST, 1);
			curl_setopt($ch, CURLOPT_POSTFIELDS,
					$data);
		
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

			$server_output = curl_exec ($ch);

			echo $server_output ;
			curl_close ($ch);
			sendStadistic('CONSULTA_RUTA_BICI',null);
			}
		   else{
			  $html=file_get_contents("http://localhost:8181/Maps/");
			  echo $html;
		  }
	  ?>
	 </fieldset>
	</div>
	<div id="Meteo">	
		  <fieldset class="fiel">
			<legend style="font-weight:bold;">Prevision meteorologica</legend>
			<div class="tabla">
			   <?php
	 
				if(isset($_POST['submit']))
				 
				{
				 
				$name = $_POST['name'];
				$result_explode = explode('|', $name);
				$html=file_get_contents("http://localhost:8080/WeatherControl/rest/WeatherHTMLusingXML/".$result_explode[0]);
				 //Prueba metodo
				 sendStadistic('CONSULTA_METEOROLOGIA',$result_explode[1]);
				echo $html;
				 
				}
			 ?>
			</div>
			<form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>"> 

			<select name="name">
				<option disabled>Seleccione un Municipio</option>
				<?php
					$first=true;
					 foreach ($xmlCities->children() as $var) {
					
						echo "<option ".($first?"selected=\"selected\"":"")."value=\"".$var['id'].'|'.$var['name']."\">" . $var['name'] . "</option>";
						$first=false;
					}
				?>
			
			</select>
			<br><br>
			<input type="submit" value="Obtener informacion meteorologica" name="submit">
			</form>
		  </fieldset>
	</div>
	

</body>
</html>
