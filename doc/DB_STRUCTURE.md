
# DB Structure


## Tables

### [prefix]players

| Column            | Type                             | Description
| ----------------- | -------------------------------- | -----------
| id                | int(11); PRIMARY; AUTO_INCREMENT | Player ID
| player_name       | varchar(20); UNIQUE | Player Name
| player_ip         | varchar(15) | Last known player IP
| online            | tinyint(1)  | Online/Offline status (0 - offline)
| firstever_login   | datetime    | First login date and time
| last_login        | datetime    | Last login date and time
| num_logins        | int(11)     | Number of logins
| this_login        | datetime    | This login date and time - if player is still online "last_login" is his previous login date and "this_login" is actual
| last_logout       | datetime    | Last logout date and time
| num_secs_loggedon | int(11)     | Time spend on the server in seconds


### [prefix]morestats

| Column      | Type             | Description
| ----------- | ---------------- | -----------
| id          | int(11); PRIMARY | Player ID
| dist_foot   | int(11) | Distance traveled by foot (in meters/blocks)
| dist_fly    | int(11) | Distance traveled when flying (in meters/blocks)
| dist_swim   | int(11) | Distance traveled when swimming (in meters/blocks)
| dist_pig    | int(11) | Distance traveled on pig (in meters/blocks)
| dist_cart   | int(11) | Distance traveled on minecart (in meters/blocks)
| dist_boat   | int(11) | Distance traveled on boat (in meters/blocks)
| dist_horse  | int(11) | Distance traveled on horse (in meters/blocks)
| bed_enter   | int(11) | Number of beds used
| fish        | int(11) | Number of fish caught
| block_place | int(11) | Number of placed blocks 
| block_break | int(11) | Number of destroyed blocks
| death_count | int(11) | Number of death
| pvp_deaths  | int(11) | Number of death in PVP
| pvp_killer  | varchar(16) | Name of last killer
| pvp_kills   | int(11) | Number of kills in PVP
| pvp_victim  | varchar(16) | Name of last victim


