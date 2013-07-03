<?php
// 
// THIS FILE IS NOT FOR USE IN PRODUCTION!!!
// THIS IS ONLY EXAMPLE HOW THINGS CAN BE DEONE!!!
//
// --- CONFIG -----------------------------------------------------------------

$db = array();
$db["host"] = "";
$db["port"] = 3306;
$db["name"] = "";
$db["user"] = "";
$db["pass"] = "";
$db["prefix"] = "dstat_";

// if you like you can copy above array to separate file...
@include('cfg.php');

// set timezone for php
// List of Supported Timezones http://www.php.net/manual/en/timezones.php
date_default_timezone_set('Europe/Warsaw');

// --- /CONFIG ----------------------------------------------------------------


header('Content-Type: text/html; charset=utf-8');

// Connecting, selecting database
$link = mysql_connect($db["host"].":".$db["port"], $db["user"], $db["pass"])
    or die('Could not connect: ' . mysql_error());
mysql_select_db($db["name"]) or die('Could not select database');


/**
 * Function converts time in secons to "XXXd XXh XXm XXs" format.
 * 
 * @param int $sek
 * @return string
 */
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
        return $d.'d&nbsp;'.$g.'h&nbsp'.$m.'m&nbsp'.$sek.'s';
    } else {
        return $g.'h&nbsp'.$m.'m&nbsp'.$sek.'s';
    }
}

/**
 * Player details view
 * 
 * @param string $player_name
 */
function player_view($player_name)
{
    global $db;
    
    // Performing SQL query
    $query = "SELECT * 
    		  FROM ".$db["prefix"]."players 
    		  INNER JOIN ".$db["prefix"]."morestats USING(id)
    		  WHERE player_name='".$player_name."' LIMIT 1";
    
    $result = mysql_query($query) or die('Query failed: ' . mysql_error());
    
    
    if($result && mysql_num_rows($result) > 0):
    $row = mysql_fetch_assoc($result);
    
    $first_login = strtotime($row['firstever_login']);
    $last_login = strtotime($row['last_login']);
    $this_login = strtotime($row['this_login']);
    $last_logout = strtotime($row['last_logout']);
    
    $last_seen = $row['online'] ? time() : $this_login;
    $last_seen = ($last_logout > $last_seen) ? $last_logout : $last_seen;
    
    if($row['online'])
    {
        $lastplaytime = $last_logout - $last_login;
    }
    else
    {
        $lastplaytime = $last_logout - $this_login;
    }
    
    
    $ldni = (int)floor((time() - $first_login)/(60*60*24))+1;
    $srednio = (int)round($row['num_secs_loggedon']/$ldni, 0);
?>
<div id="center">
<h1><?php echo $row['player_name'] ?></h1>   
<span>Status: <strong><?php echo $row['online'] ? 'online' : 'offline'; ?></strong></span>
<div class="clear">&nbsp;</div>
<div class="infobox">
    <h2>Time:</h2>
    <ul>
        <li>First login: <strong><?php echo date('Y-m-d H:i:s', $first_login)." (".stodgms(time() - $first_login).")"; ?></strong></li>
        <li>Last seen: <strong><?php echo date('Y-m-d H:i:s', $last_seen)." (".stodgms(time() - $last_seen).")"; ?></strong></li>
        <li>Play time: <strong><?php echo stodgms($row['num_secs_loggedon']); ?></strong></li>
        <li>Last login play time: <strong><?php echo stodgms($lastplaytime); ?></strong></li>
        <li>Daily average: <strong><?php echo stodgms($srednio); ?></strong></li>
    </ul>
</div>

<div class="infobox">
<h2>Distance traveled</h2>
<ul>
    <li>By foot: <strong><?php echo number_format($row['dist_foot']/1000.0, 3, ',', ' '); ?></strong> km</li>
    <li>When flying: <strong><?php echo number_format($row['dist_fly']/1000.0, 3, ',', ' '); ?></strong> km</li>
    <li>When swimming: <strong><?php echo number_format($row['dist_swim']/1000.0, 3, ',', ' '); ?></strong> km</li>
    <li>On pig: <strong><?php echo number_format($row['dist_pig']/1000.0, 3, ',', ' '); ?></strong> km</li>
    <li>On horse: <strong><?php echo number_format($row['dist_horse']/1000.0, 3, ',', ' '); ?></strong> km</li>
    <li>On minecart: <strong><?php echo number_format($row['dist_cart']/1000.0, 3, ',', ' '); ?></strong> km</li>
    <li>On boat: <strong><?php echo number_format($row['dist_boat']/1000.0, 3, ',', ' '); ?></strong> km</li>
</ul>
</div>

<div class="infobox">
<h2>Other:</h2>
<ul>
    <li>Number of logins: <strong><?php echo $row['num_logins']; ?></strong></li>
    <li>Bed entered: <strong><?php echo $row['bed_enter']; ?></strong></li>
    <li>Number of fish caught: <strong><?php echo $row['fish']; ?></strong></li>
</ul>
</div>

<?php
    else:
        echo "Player not found.";
    endif;
?>   
</div>
<?php
}

/**
 * Players list view
 */
function list_view()
{
    global $db;
    
    // Performing SQL query
    $query = 'SELECT `firstever_login`, `last_login`, `this_login`, `last_logout`,
              `online`, `num_secs_loggedon`, `num_logins`, `player_name`
              FROM '.$db["prefix"].'players 
              ORDER BY online DESC, this_login DESC';
    $result = mysql_query($query) or die('Query failed: ' . mysql_error());
    
    ?>
<table>
    <thead>
        <tr>
            <th>#</th>
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
$count = 0;
while ($line = mysql_fetch_array($result, MYSQL_ASSOC)):
    $count += 1;
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
        <td class="right">
            <?php echo $count; ?>
        </td>
        <td class="nazwa" title="logins: <?php echo $line['num_logins']; ?>, days: <?php echo $ldni; ?>">
            <a href="<?php echo $_SERVER['PHP_SELF']."?v=player&name=".$line['player_name']; ?>"><?php echo $line['player_name']; ?></a>
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

?>
    </tbody>
</table>    
    
<?php
}
?>

<?php
ob_start();
if(isset($_GET['v']) && $_GET['v'] != '')
{
    switch ($_GET['v']) {
        case 'player':
            player_view($_GET['name']);
           break;
        
        default:
             list_view();
            break;
    }
}
else
{
    list_view();
}
$view_output = ob_get_clean();

// Closing connection
mysql_close($link);
?>




<?php // ---- HTML TEMPLATE ------------------------------------------------- ?>

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
    #center {
        width: 960px;
        margin: 10px auto;
        border: 1px solid #666;
        padding: 10px;
        border-radius: 16px;
        text-align: left;
        height: auto;
        overflow: hidden;
    }
    #center div.infobox {
        float: left;
        width: 400px;
        margin-right: 20px;
        padding: 10px;
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
    .clear {
        clear: both;
        height: 3px;
        padding: 0;
        margin: 0;
    }
    /* ]]> */
    </style>
    
    
</head>
<body>

<div id="menu" style="padding: 5px; margin: 15px; border: 1px solid #555;">
    <a href="<?php echo $_SERVER['PHP_SELF']; ?>">index</a>
</div>

<?php echo $view_output; ?>

<div id="example" style="color: red; margin: 20px; padding: 10px; border: 1px solid red;">
    This page is just an example.<br />
    It is not safe to use it in a production environment!!!
</div>

</body>
</html>