<?php
ini_set('memory_limit', '-1');
include 'spellcorrector/SpellCorrector.php';
include 'functions.php';

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');
?>
<head>
  <link rel="stylesheet"
  href="https://code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css">
  <script
  src="https://code.jquery.com/jquery-1.12.4.js"
  integrity="sha256-Qw82+bXyGq6MydymqBxNPYTaUXXq7c8v3CwiYwLLNXU="
  crossorigin="anonymous"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js" 
  integrity="sha256-T0Vest3yCU7pafRw9r+settMBX6JkKN06dqBnpQ8d30=" 
  crossorigin="anonymous"></script>
  <style>
  .snippet{
    color: red;
  }
  </style>
  <script type="text/javascript">
  function getSuggestion(term,e){
    //detect KeyUp and KeyDown
    if(e.keyCode == 38 || e.keyCode == 40){
      return;
    }

    if(term.length == 0 || term[term.length-1] == " "){
      return;
    }

    terms = term.split(" ");
    search_term = terms[terms.length-1].toLowerCase();
    var xmlhttp = new XMLHttpRequest();
      xmlhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            //query is changed
            if(document.getElementById("q").value != term){
              return;
            }
            var response = JSON.parse(this.responseText);
            var suggestions = response["suggest"]["suggest"][search_term]["suggestions"];
            var limit = Math.min(10,suggestions.length);
            if(search_term.length >= 2){
              limit = Math.min(7,limit)
            }
            var suggestion_list = [];
            for(var i = 0;i<limit;++i){
              suggestion_list.push(term.substr(0,term.lastIndexOf(" ")+1) + suggestions[i]["term"]);
            }

            $("#q").autocomplete({
              source: function( request, response ) {
                      response(suggestion_list);
                  }
            });
          }
      };
      xmlhttp.open("GET", "suggest_service.php?q=" + search_term, true);
      xmlhttp.send();

  }
  </script>
</head>
<?php
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
  <body class="ui-widget">
    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" onkeyup="getSuggestion(this.value,event)" autocomplete="off"/>
      <input type="submit"/>
    </form>
    <?php 
      if($query){
        
        $query = strtolower($query);

        $terms = explode(" ",$query);
        $correct_terms = array();
        $is_spell_error = false;
        for($i=0;$i<sizeof($terms);++$i){
        	$term = $terms[$i];
        	$correct_term = strtolower(SpellCorrector::correct($term));
        	if($term != $correct_term){
        		$is_spell_error = true;
        	}
        	array_push($correct_terms,$correct_term);
        }
        if($is_spell_error){
        	$correct_terms = implode(" ",$correct_terms);
        	?>
        	Did you mean: <a href="query.php?q=<?=$correct_terms?>"><?= $correct_terms; ?></a>
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
        <table width="70%">
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
                <td><a href='<?php echo $url ?>'><?php echo htmlspecialchars($doc->title, ENT_NOQUOTES, 'utf-8'); ?></a></td>
            </tr>
            <tr>
                <td><?php 
                  $content = getSnippet($terms,$real_id);
                  $content = decorateSnippet($terms,$content);
                  echo $content;
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
