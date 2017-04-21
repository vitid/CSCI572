<?php  
function getSnippet($search_terms,$doc_id,$expand=100){
	$content = file_get_contents("files/".$doc_id,true);
	$content = strip_tags($content);
	$content = preg_replace("!\s+!"," ",$content);

	$snippet = "";
	$pattern = "/[^a-zA-Z]" . implode("[^a-zA-Z]|[^a-zA-Z]",$search_terms) . "[^a-zA-Z]/i";
	$index = -1;
	if(preg_match($pattern, $content, $matches, PREG_OFFSET_CAPTURE)) {
	    $index = $matches[0][1];
	    $index += 1;//adjust for one whitespace
	    $start_index = max(0,$index - $expand);
	    $snippet = substr($content,$start_index,2*$expand);
	}
	return $snippet;
}

function decorateSnippet($search_terms,$content){
	foreach($search_terms as $term){
		$content = preg_replace('/'.$term.'/i',"<span class='snippet'>\$0</span>",$content);
	}
	return $content;
}
#echo getSnippet(["visualizing"],"32caf0f7-c248-4d05-8bdd-bab791d267a8.html");
#echo decorateSnippet(["hello"]," hello world hellox Hello ");
?>