<?php
ini_set('memory_limit', '-1');
include 'spellcorrector/SpellCorrector.php';

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;

if ($query)
{
  include('load_dict.php');
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
  require_once('Apache/Solr/Service.php');

  // create a new solr service instance - host, port, and webapp
  // path (all defaults in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/');

  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }

  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
  try
  {
    $results = $solr->search($query, 0, $limit);
  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}

?>
<html>
  <head>
    <title>PHP Solr Client Example</title>
    <script type="text/javascript">
    	function getSuggestion(term,e){
    		//detect KeyUp and KeyDown
    		if(e.keyCode == 38 || e.keyCode == 40){
    			return;
    		}

    		var candidate_obj = document.getElementById("candidate_words");
    		candidate_obj.innerHTML = "";
    		term = term.trim();

    		if(term.length > 0){
    			terms = term.split(" ").filter(function(x){return x.length > 0;});
    			search_term = terms[terms.length-1];
    			var xmlhttp = new XMLHttpRequest();
        		xmlhttp.onreadystatechange = function() {
	            if (this.readyState == 4 && this.status == 200) {
	            		//query is changed
	            		if(document.getElementById("q").value.trim() != term){
	            			return;
	            		}
	            		var response = JSON.parse(this.responseText);
	            		var suggestions = response["suggest"]["suggest"][search_term]["suggestions"];
	            		var limit = Math.min(10,suggestions.length);
	            		if(search_term.length >= 2){
	            			limit = Math.min(7,limit)
	            		}
	            		for(var i = 0;i<limit;++i){

	            			terms[terms.length-1] = suggestions[i]["term"];
	            			var option_element = document.createElement("option");
    						option_element.value = terms.join(" ");
    						candidate_obj.appendChild(option_element);
	            		}
		            }
		        };
		        xmlhttp.open("GET", "suggest_service.php?q=" + search_term, true);
		        xmlhttp.send();
    		}

    	}
    </script>
  </head>
  <body>
    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" list="candidate_words" onkeyup="getSuggestion(this.value,event)"/>
      <input type="submit"/>
    </form>
    <datalist id="candidate_words">
      <option value="airplane"/>
      <option value="airplne"/>
      <option value="hello"/>
    </datalist>
    <?php 
      if($query){
        
        $terms = explode(" ",$query);
        $correct_terms = array();
        $is_spell_error = false;
        for($i=0;$i<sizeof($terms);++$i){
        	$term = $terms[$i];
        	$correct_term = SpellCorrector::correct($term);
        	if($term != $correct_term){
        		$is_spell_error = true;
        	}
        	array_push($correct_terms,$correct_term);
        }
        if($is_spell_error){
        	$correct_terms = implode(" ",$correct_terms);
        	?>
        	You may refer to:<a href="query.php?q=<?=$correct_terms?>"><?= $correct_terms; ?></a>
        	<?php
        }
      }
    ?>
<?php

// display results
if ($results)
{

?>
<ol>
<?php
  $docs0 = $results->response->docs;
  // iterate result documents
  for($i = 0;$i < sizeof($docs0); ++$i)
  {
?>
      <li>
        <table width="100%">
        <tr>
        <td width="50%" valign="top">
          <table width="100%" style="border: 1px solid black; text-align: left">
      <?php
      // iterate document fields / values
        
            $doc = $docs0[$i];
            $real_id = substr($doc->id,strripos($doc->id,"/")+1);
            $url = $dict[$real_id];
            ?>
            <tr>
                <th>ID</th>
                <td>
                <?php echo substr($doc->id,0,strripos($doc->id,"/")+1) . "<b>" . $real_id . "</b>" ?>
                </td>
            </tr>
            <tr>
                <th>Title</th>
                <td><a href='<?php echo $url ?>'><?php echo htmlspecialchars($doc->title, ENT_NOQUOTES, 'utf-8'); ?></a></td>
            </tr>
            <tr>
                <th>Description</th>
                <td><?php 
                  $description = "";
                  if(is_array($doc->description)){
                    $description = $doc->description[0];
                  }else{
                    $description = $doc->description;
                  }
                  echo htmlspecialchars($description, ENT_NOQUOTES, 'utf-8'); 
                ?></td>
            </tr>
            <?php
        
            ?>
          </table>
        </td>
        </tr>
        </table>
      </li>
<?php
  }
?>
    </ol>
<?php
}
?>
  </body>
</html>
