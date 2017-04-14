<?php  
function getSnippet($search_terms,$doc_id){
	$myfile = fopen("files/".$doc_id, "r");
	while(!feof($myfile)) {
	  $line = fgets($myfile);
	  $terms = explode(" ",$line);
	  if(sizeof(array_intersect($search_terms,$terms)) > 0){
	  	echo $line;
	  	break;
	  }
	}
	fclose($myfile);
}

getSnippet(["2018"],"32caf0f7-c248-4d05-8bdd-bab791d267a8.html");
?>