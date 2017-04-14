<?php  
	$dict = [];
	$mapfile = fopen("mapNYTimesDataFile.csv","r");
	while (($line = fgets($mapfile)) !== false) {
        $dict[explode(",",$line)[0]] = explode(",",$line)[1];
    }

    fclose($mapfile);
?>