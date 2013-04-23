<?php
// 
// THIS FILE IS NOT FOR USE IN PRODUCTION!!!
// THIS IS ONLY EXAMPLE HOW THINGS CAN BE DEONE!!!
//
// --- CONFIG -----------------------------------------------------------------

$db = array();
$db["host"] = "localhost";
$db["port"] = 3306;
$db["name"] = "";
$db["user"] = "";
$db["pass"] = "";
$db["prefix"] = "dstat_";

// set timezone for php
// List of Supported Timezones http://www.php.net/manual/en/timezones.php
date_default_timezone_set('Europe/Warsaw');

// --- /CONFIG ----------------------------------------------------------------


header('Content-Type: text/html; charset=utf-8');

// Connecting, selecting database
$link = mysql_connect($db["host"].":".$db["port"], $db["user"], $db["pass"])
    or die('Could not connect: ' . mysql_error());
mysql_select_db($db["name"]) or die('Could not select database');

// Performing SQL query
$query = 'SELECT * FROM '.$db["prefix"].'players ORDER BY online DESC, this_login DESC';
$result = mysql_query($query) or die('Query failed: ' . mysql_error());



function stodgms($sek) {
    $d = 0;
    $g = 0;
    $m = 0;
    
    $dd = 60*60*24;
    $gg = 60*60;
    $mm = 60;
    
    if($sek >= $dd) {
        $d = (int)floor($sek/$dd);
    }
    $sek = $sek%$dd;
    
    if($sek >= $gg) {
        $g = (int)floor($sek/$gg);
    }
    $sek = $sek%$gg;
    
    if($sek >= $mm) {
        $m = (int)floor($sek/$mm);
    }
    $sek = $sek%$mm;
    
    
    if($d > 0) {
        return $d.'d '.$g.'g '.$m.'m '.$sek.'s';
    } else {
        return $g.'g '.$m.'m '.$sek.'s';
    }
}


?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
        
<html xmlns="http://www.w3.org/1999/xhtml" lang="pl">
<head> 
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>DooBStat</title> 
    
    
    <style type="text/css">
    /* <![CDATA[ */
    
    body {
        font-size: 18px;
        text-align: center;
    }
    
    table {
        border-collapse: collapse;
        border: 1px solid #000;
        margin: 0 auto;
        text-align: left;
    }
    
    table th {
        text-align: center;
    }
    
    table th,
    table td {
        border: 1px solid #000;
        padding: 3px 5px;
    }
    
    table td.nazwa {
        font-weight: bold;
    }
    
    table td.centr {
        text-align: center;
    }
    
    table td.right {
        text-align: right;
    }
    
    
    /* ]]> */
    </style>
    
    
</head> 
<body> 

<table>
    <thead>
        <tr>
            <th>Player name</th>
            <th>Online</th>
            <th>First login</th>
            <th>Last seen</th>
            <th>Last login<br />play time</th>
            <th>Play time</th>
            <th>Daily average</th>
        </tr>
    </thead>
    <tbody>

<?php
// Printing results in HTML
while ($line = mysql_fetch_array($result, MYSQL_ASSOC)):

    $first_login = strtotime($line['firstever_login']);
    $last_login = strtotime($line['last_login']);
    $this_login = strtotime($line['this_login']);
    $last_logout = strtotime($line['last_logout']);
    
    $last_seen = $line['online'] ? time() : $this_login;
    $last_seen = ($last_logout > $last_seen) ? $last_logout : $last_seen;
    
    if($line['online'])
    {
        $lastplaytime = $last_logout - $last_login;
    }
    else
    {
        $lastplaytime = $last_logout - $this_login;
    }
    
    
    $ldni = (int)floor((time() - $first_login)/(60*60*24))+1;
    $srednio = (int)round($line['num_secs_loggedon']/$ldni, 0);

?>
    <tr>
        <td class="nazwa" title="logins: <?php echo $line['num_logins']; ?>, days: <?php echo $ldni; ?>">
            <?php echo $line['player_name']; ?>
        </td>
        <td class="centr">
            <?php echo $line['online'] ? 'ONLINE' : 'offline'; ?>
        </td>
        <td title="<?php echo date('Y-m-d H:i:s', $first_login); ?>">
            <?php echo date('Y-m-d', $first_login); ?>
        </td>
        <td class="right" title="<?php echo date('Y-m-d H:i:s', $last_seen); ?>">
            <?php echo stodgms(time() - $last_seen); ?>
        </td>
        <td class="right">
            <?php echo stodgms($lastplaytime); ?>
        </td>
        <td class="right">
            <?php echo stodgms($line['num_secs_loggedon']); ?>
        </td>
        <td class="right">
            <?php echo stodgms($srednio); ?>
        </td>
    </tr>
<?php
endwhile;

// Free resultset
mysql_free_result($result);

// Closing connection
mysql_close($link);


?>
    </tbody>
</table>



</body>
</html>