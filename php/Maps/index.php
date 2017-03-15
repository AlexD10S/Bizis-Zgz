<?php

/**
 * Step 1: Require the Slim Framework
 */
require 'Slim/Slim.php';

\Slim\Slim::registerAutoloader();

/**
 * Step 2: Instantiate a Slim application
 *
 */
$app = new \Slim\Slim();

/**
 * Step 3: Define the Slim application routes
 */
   
	  
	  
// GET route
$app->get(
    '/',
    function () {
	 $coordenadaA=array();
	    $coordenadaB=array(); 
            $titulo=array();
	    $estado=array();
	    $bicisDisp=array();
	    $anclajesDisp=array();
	    $xmlD=file_get_contents("http://localhost:8080/CatalogoBizis/rest/InfoEstacionesXML");
	    $xml=simplexml_load_string($xmlD) or die("Error: Cannot create object");
			         $cuenta=0;
				 foreach ($xml->children() as $var) {					
					$coordenadaA[$cuenta]=$var->coordenadaA .'' ;
					$coordenadaB[$cuenta]=$var->coordenadaB .'';
					$titulo[$cuenta]=$var->titulo .'';
					$estado[$cuenta]=$var->estado .'';
					$bicisDisp[$cuenta]=$var->bicisDisponibles .'';
					$anclajesDisp[$cuenta]=$var->anclajesDisponibles .'';
					$cuenta=$cuenta + 1;
					
                                        
	    			}
	$coorA=json_encode($coordenadaA);
	$coorB=json_encode($coordenadaB);
	$coorT=json_encode($titulo);
	$coorE=json_encode($estado);
	$coorBi=json_encode($bicisDisp);
	$coorAn=json_encode($anclajesDisp);
        $template = <<<EOT
<!DOCTYPE html>
<html>
<head>
<title>Slim Framework for PHP 5</title>

<script src="http://maps.googleapis.com/maps/api/js"></script>
   <script>
	var myCenter=new google.maps.LatLng(41.65629,-0.8765379);
	function initialize() {
	  var mapProp = {
	    center:myCenter,
	    zoom:13,
	    mapTypeId:google.maps.MapTypeId.ROADMAP
	  };
	  var map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
          
          var arrayA=JSON.parse('$coorA');
	  var arrayB=JSON.parse('$coorB');
          var arrayTit=JSON.parse('$coorT');
	  var arrayEst=JSON.parse('$coorE');
	  var arrayBiz=JSON.parse('$coorBi');
	  var arrayAnc=JSON.parse('$coorAn');
	  var markers = [];
	  var infos = [];
	  for(var i=0;i<arrayA.length;i++)
          {       
		 
 		 var marker=new google.maps.Marker({
		  	position:new google.maps.LatLng(arrayA[i],arrayB[i]),
		  });
		  marker.setMap(map);
                  markers[i]=marker;
                  var informacion="Titulo: "+arrayTit[i]+""+
				  "Estado: "+arrayEst[i]+""+
                                  "BicisDisponibles: "+arrayBiz[i]+""+
				  "AnclajesDisponibles: "+arrayAnc[i]+"";

		  info = new google.maps.InfoWindow({
		  	content:informacion,
		  });
		  infos[i]=info;
		  

                 
				 
	  }
	  for(var x=0;x<arrayA.length;x++)
          {
	  google.maps.event.addListener(markers[x], 'click', (function(x) {
			return function() {
			  infos[x].open(map,markers[x]);
			}
		      })(x));
          }
	  google.maps.event.addListener(map, 'click', function() {
            infowindow.close();
        });
		
	}
	google.maps.event.addDomListener(window, 'load', initialize);
   </script>
</head>

<body>
	<div id="googleMap" style="width:500px;height:380px;"></div>
	

</body>
</html>

EOT;
        echo $template;
        
    }
);
$app->get(
    '/MapJson',
    function () {
	    $jsonD=file_get_contents("http://localhost:8080/CatalogoBizis/rest/InfoEstacionesJSON");
	    
	    $coorAn=json_encode($jsonD);
        $template = <<<EOT
<!DOCTYPE html>
<html>
<head>
<title>Slim Framework for PHP 5</title>

<script src="http://maps.googleapis.com/maps/api/js"></script>
   <script>
	var myCenter=new google.maps.LatLng(41.65629,-0.8765379);
	function initialize() {
	  var mapProp = {
	    center:myCenter,
	    zoom:13,
	    mapTypeId:google.maps.MapTypeId.ROADMAP
	  };
	  var map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
          
          var json=JSON.parse('$jsonD');
          var titulo;
	  var estado;
          var bicisDisponibles;
          var anclajesDisponibles;
          var coordenadaA;
          var coordenadaB;
	  var markers = [];
	  var infos = [];
	  for(var i=0;i< 50;i++)
          {       
		 coordenadaA=json.estacion[i].coordenadaA;
		 coordenadaB=json.estacion[i].coordenadaB;
		 titulo=json.estacion[i].titulo;
		 estado=json.estacion[i].estado;
		 bicisDisponibles=json.estacion[i].bicisDisponibles;
                 anclajesDisponibles=json.estacion[i].anclajesDisponibles;
 		 var marker=new google.maps.Marker({
		  	position:new google.maps.LatLng(coordenadaA,coordenadaB),
		  });
		  marker.setMap(map);
                  markers[i]=marker;
                  var informacion="Titulo: "+titulo+""+
				  "Estado: "+estado+""+
                                  "BicisDisponibles: "+bicisDisponibles+""+
				  "AnclajesDisponibles: "+anclajesDisponibles+"";

		  info = new google.maps.InfoWindow({
		  	content:informacion,
		  });
		  infos[i]=info;
		  

                 
				 
	  }
	  for(var x=0;x< 50 ;x++)
          {
	  google.maps.event.addListener(markers[x], 'click', (function(x) {
			return function() {
			  infos[x].open(map,markers[x]);
			}
		      })(x));
          }
	  google.maps.event.addListener(map, 'click', function() {
            infowindow.close();
        });
		
	}
	google.maps.event.addDomListener(window, 'load', initialize);
   </script>
</head>

<body>
	<div id="googleMap" style="width:500px;height:380px;"></div>
	

</body>
</html>

EOT;
        echo $template;
        
    }
);

// GET route
$app->post('/ruta',
    function () use($app){
		$body=$app->request()->getBody();
		$xml=simplexml_load_string($body) or die("Error: Cannot create object");
	        $origen=$xml->from;
		$destino=$xml->to;
	
	 $coordenadaA=array();
	    $coordenadaB=array(); 
            $titulo=array();
	    $estado=array();
	    $bicisDisp=array();
	    $anclajesDisp=array();
	    function limpia_espacios($cadena){
			$cadena = ereg_replace( "([ ]+)", "", $cadena );
			return $cadena;
		}
	    $xmlD=file_get_contents("http://localhost:8080/CatalogoBizis/rest/InfoEstacionesXML");
	    $xml=simplexml_load_string($xmlD) or die("Error: Cannot create object");
			         $cuenta=0;
				 $coorOA='';
				 $coorOB='';
				 foreach ($xml->children() as $var) {					
					$coordenadaA[$cuenta]=$var->coordenadaA .'' ;
					$coordenadaB[$cuenta]=$var->coordenadaB .'';
					$titulo[$cuenta]=$var->titulo;
					$titulo[$cuenta]=limpia_espacios($titulo[$cuenta]);
					$destino=limpia_espacios($destino);
					
					
					if (strcasecmp ($titulo[$cuenta] , $destino ) == 0){
					
						$coorOA=$coordenadaA[$cuenta];
						$coorOB=$coordenadaB[$cuenta];
					}
					$estado[$cuenta]=$var->estado .'';
					$bicisDisp[$cuenta]=$var->bicisDisponibles .'';
					$anclajesDisp[$cuenta]=$var->anclajesDisponibles .'';
					$cuenta=$cuenta + 1;
					
                                        
	    			}

	$xmlGoo=file_get_contents("https://maps.googleapis.com/maps/api/directions/xml?origin=".urlencode($origen)."&destination=$coorOA,$coorOB");
	$xmlG=simplexml_load_string($xmlGoo) or die("Error: Cannot create object");
	$originA= $xmlG-> route->bounds->southwest->lat .'' ;	
	$originB= $xmlG-> route->bounds->southwest->lng .'' ;
	$destA= $xmlG-> route->bounds->northeast->lat .'' ;
	$destB= $xmlG-> route->bounds->northeast->lng .'' ;
	$coordRuta=json_encode([$originA,$originB,$destA,$destB]);						
	$coorA=json_encode($coordenadaA);
	$coorB=json_encode($coordenadaB);
	$coorT=json_encode($titulo);
	$coorE=json_encode($estado);
	$coorBi=json_encode($bicisDisp);
	$coorAn=json_encode($anclajesDisp);
	
        $template = <<<EOT
<!DOCTYPE html>
<html>
<head>
<title>Slim Framework for PHP 5</title>

<script src="http://maps.googleapis.com/maps/api/js"></script>
   <script>
	var myCenter=new google.maps.LatLng(41.65629,-0.8765379);
	function initialize() {
	  var mapProp = {
	    center:myCenter,
	    zoom:13,
	    mapTypeId:google.maps.MapTypeId.ROADMAP
	  };
	  var map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
          
          var arrayA=JSON.parse('$coorA');
	  var arrayB=JSON.parse('$coorB');
          var arrayTit=JSON.parse('$coorT');
	  var arrayEst=JSON.parse('$coorE');
	  var arrayBiz=JSON.parse('$coorBi');
	  var arrayAnc=JSON.parse('$coorAn');
	  var arrayRuta=JSON.parse('$coordRuta');
	  var markers = [];
	  var infos = [];
	  for(var i=0;i<arrayA.length;i++)
          {       
		 
 		 var marker=new google.maps.Marker({
		  	position:new google.maps.LatLng(arrayA[i],arrayB[i]),
		  });
		  marker.setMap(map);
                  markers[i]=marker;
                  var informacion="Titulo: "+arrayTit[i]+""+
				  "Estado: "+arrayEst[i]+""+
                                  "BicisDisponibles: "+arrayBiz[i]+""+
				  "AnclajesDisponibles: "+arrayAnc[i]+"";

		  info = new google.maps.InfoWindow({
		  	content:informacion,
		  });
		  infos[i]=info;
		  

                 
				 
	  }
	  for(var x=0;x<arrayA.length;x++)
          {
	  google.maps.event.addListener(markers[x], 'click', (function(x) {
			return function() {
			  infos[x].open(map,markers[x]);
			}
		      })(x));
          }
	  google.maps.event.addListener(map, 'click', function() {
            infowindow.close();
        });
	 var directionsDisplay = new google.maps.DirectionsRenderer();
	 var directionsService = new google.maps.DirectionsService();
	 var request = {
		 origin: new google.maps.LatLng(arrayRuta[0],arrayRuta[1]),
		 destination: new google.maps.LatLng(arrayRuta[2],arrayRuta[3]),
		 travelMode: google.maps.DirectionsTravelMode['WALKING'],
		 unitSystem: google.maps.DirectionsUnitSystem['METRIC'],
		 provideRouteAlternatives: true
	 };
	 directionsService.route(request, function(response, status) {
	    if (status == google.maps.DirectionsStatus.OK) {
		directionsDisplay.setMap(map);
		directionsDisplay.setDirections(response);
	    } else {
		    alert("No existen rutas entre ambos puntos");
	    }
	});
		
	}
	google.maps.event.addDomListener(window, 'load', initialize);
   </script>
</head>

<body>
	<div id="googleMap" style="width:500px;height:380px;"></div>
	

</body>
</html>

EOT;
        echo $template;
        
    }
);



// GET route
$app->post('/rutaJSON',
    function () use($app){
           $body=$app->request()->getBody();
	   $json=json_decode($body) or die("Error: Cannot create object");

       $origen=$json->{'from'};
	   $destino=$json->{'to'};
	    $coorOA='';
	   $coorOB='';
	    $jsonD=file_get_contents("http://localhost:8080/CatalogoBizis/rest/InfoEstacionesJSON");
	   function limpia_espacios($cadena){
			$cadena = ereg_replace( "([ ]+)", "", $cadena );
			return $cadena;
		}
					
					$datos=json_decode( preg_replace('/[\x00-\x1F\x80-\xFF]/', '', $jsonD), true );
					$i=0;
					$correcto=false;
					foreach ($datos as $city){
					  foreach ($city as $c){
					    foreach ($c as $a){
					     $destino=limpia_espacios($destino);
					     $a=limpia_espacios($a);
					     if($a==$destino){
					 	$correcto=true;
                                              }
					      if($correcto==true and $i==4){
						$coorOA=$a;
					      }
					      if($correcto==true and $i==5){
						$coorOB=$a;
						$correcto=false;
					      }
						if($i==5){
						  $i=0;
						}
						else{
				   		  $i=$i+1;
						}
					       //}
		
					     }
					     
					  
					  }
					  }
		
                        
			
			
			$direccion_google = $origen;
			$resultado = file_get_contents(sprintf('https://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=%s', urlencode($direccion_google)));
			$resultado = json_decode($resultado, TRUE);
			
			$lat = $resultado['results'][0]['geometry']['location']['lat'];
			$lng = $resultado['results'][0]['geometry']['location']['lng'];

		
			$template = <<<EOT
<!DOCTYPE html>
<html>
<head>
<title>Slim Framework for PHP 5</title>

<script src="http://maps.googleapis.com/maps/api/js"></script>
   <script>
	var myCenter=new google.maps.LatLng(41.65629,-0.8765379);
	function initialize() {
	  var mapProp = {
	    center:myCenter,
	    zoom:13,
	    mapTypeId:google.maps.MapTypeId.ROADMAP
	  };
	  var map=new google.maps.Map(document.getElementById("googleMap"),mapProp);         
          var json=JSON.parse('$jsonD');
          var titulo;
	  var estado;
          var bicisDisponibles;
          var anclajesDisponibles;
          var coordenadaA;
          var coordenadaB;
	  var markers = [];
	  var infos = [];
	  for(var i=0;i< 50;i++)
          {       
		 coordenadaA=json.estacion[i].coordenadaA;
		 coordenadaB=json.estacion[i].coordenadaB;
		 titulo=json.estacion[i].titulo;
		 estado=json.estacion[i].estado;
		 bicisDisponibles=json.estacion[i].bicisDisponibles;
                 anclajesDisponibles=json.estacion[i].anclajesDisponibles;
 		 var marker=new google.maps.Marker({
		  	position:new google.maps.LatLng(coordenadaA,coordenadaB),
		  });
		  marker.setMap(map);
                  markers[i]=marker;
                  var informacion="Titulo: "+titulo+""+
				  "Estado: "+estado+""+
                                  "BicisDisponibles: "+bicisDisponibles+""+
				  "AnclajesDisponibles: "+anclajesDisponibles+"";

		  info = new google.maps.InfoWindow({
		  	content:informacion,
		  });
		  infos[i]=info;
		  

                 
				 
	  }
	  for(var x=0;x< 50 ;x++)
          {
	  google.maps.event.addListener(markers[x], 'click', (function(x) {
			return function() {
			  infos[x].open(map,markers[x]);
			}
		      })(x));
          }
	  google.maps.event.addListener(map, 'click', function() {
            infowindow.close();
        });
	 var directionsDisplay = new google.maps.DirectionsRenderer();
	 var directionsService = new google.maps.DirectionsService();
	 var request = {
		 origin: new google.maps.LatLng('$lat','$lng'),
		 destination: new google.maps.LatLng('$coorOA','$coorOB'),
		 travelMode: google.maps.DirectionsTravelMode['WALKING'],
		 unitSystem: google.maps.DirectionsUnitSystem['METRIC'],
		 provideRouteAlternatives: true
	 };
	 directionsService.route(request, function(response, status) {
	    if (status == google.maps.DirectionsStatus.OK) {
		directionsDisplay.setMap(map);
		directionsDisplay.setDirections(response);
	    } else {
		    alert("No existen rutas entre ambos puntos");
	    }
	});
		
	}
	google.maps.event.addDomListener(window, 'load', initialize);
   </script>
</head>

<body>
	<div id="googleMap" style="width:500px;height:380px;"></div>
	

</body>
</html>

EOT;
        echo $template;
        
    }
);







/**
 * Step 4: Run the Slim application
 */
$app->run();
