Server side Validations for Basic Location Report: Top K Companions
Procedure: Do from top to bottom and double check expected results.
	

/* 1 - Test whether two users are considered as companions if one user is updated 1 second before the query window*/
///<--Query 2014-03-01 11:15:00-->
// Select mac-address = 0000d893979225078884ae384365f2576dc26719
//	Expected Result: Status: There are no records for this timing.
// JSON http://app-2014is203g4t7.rhcloud.com/json/top-k-companions?token=eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0MTM4MDIyMTYsInN1YiI6ImFkbWluIiwiaWF0IjoxNDEzNzk4NjE2fQ.jh2h8gPSI6e68gh6ecnLmhGsgRqT9yxXTf9jiopzkc4&date=2014-03-01T11:15:00&mac-address=0000d893979225078884ae384365f2576dc26719

// Data inserted : 
insert into location(rowNumber,timestamp,mac_address,location_id) 
values 
(1,'2014-03-01 11:00:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022), 
(2,'2014-03-01 11:01:00','0000d893979225078884ae384365f2576dc26719',1010100022)

#SUCCESS 


/* 2 - Test whether a user is detected to be together with another person if both user's last update is 11:00:00 and query is @ 11:15:00*/
///<--Query 2014-03-01 11:15:00-->
// Select Email as andy.wong.2014@law.smu.edu.sg
// Expected Result: Status: There are no records for this timing.

// Data inserted : 
insert into location(rowNumber,timestamp,mac_address,location_id) 
values 
(1,'2014-03-01 11:00:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022), 
(2,'2014-03-01 11:00:00','0000d893979225078884ae384365f2576dc26719',1010100022), 


#SUCCESS 


/* 3 - Test whether functionality checks for exactly how many seconds a user is with another person within a valid time window */
///<--Query 2014-03-01 11:15:00-->
// Select Email as andy.wong.2014@law.smu.edu.sg
// Expected Result: 539 seconds returned

// Data inserted : 
insert into location(rowNumber,timestamp,mac_address,location_id) 
values (1,'2014-03-01 11:01:01','000cdde3c3c41c87802981ce44092af464bba6ee',1010100001),
(2,'2014-03-01 11:01:00','0000d893979225078884ae384365f2576dc26719',1010100001)

#SUCCESS


/* 4 - Test whether difference in milliseconds affects the result of the functionality	*/
///<--Query 2014-03-01 11:15:00-->
// Select Email as andy.wong.2014@law.smu.edu.sg
// Expected Result: 540 seconds returned

// Data inserted
insert into location(rowNumber,timestamp,mac_address,location_id) 
values (1,'2014-03-01 11:00:01:59','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022), 
(2,'2014-03-01 11:00:01:00','0000d893979225078884ae384365f2576dc26719',1010100022)

#SUCCESS  ( Milliseconds does not affect the number of seconds if the output query takes into account only seconds



/* 5 - Test how many minutes is returned if one user is updated only once @ 11:00:01 and another is updated at 11:05:01 and 11:07:01 */
///<--Query 2014-03-01 11:15:00-->
// Select Email as andy.wong.2014@law.smu.edu.sg
// Expected Result:  240 seconds returned

// Data inserted : 
insert into location(rowNumber,timestamp,mac_address,location_id) 
values (1,'2014-03-01 11:00:01','0000d893979225078884ae384365f2576dc26719',1010100022), 
(2,'2014-03-01 11:05:01','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022),
(3, '2014-03-01 11:07:01','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022)

#SUCCESS



/* 6 - Test whether user considered a companion if the data set contains conflicting timestamps  */
///<--Query 2014-03-01 11:15:00-->
// Select Email as andy.wong.2014@law.smu.edu.sg
// Expected Result: Status: There are no records for this timing.

// Data inserted : 
insert into location(rowNumber,timestamp,mac_address,location_id) 
values  
(2,'2014-03-01 11:03:01:00','0000d893979225078884ae384365f2576dc26719',1010100022),
(3, '2014-03-01 11:03:01:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022),
(4,'2014-03-01 11:03:01:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100069)

# SUCCESS


/* 7 - Test how many minutes 2 persons are together if one of them leave halfway */
///<--Query 2014-03-01 11:15:00-->
// Select Email as andy.wong.2014@law.smu.edu.sg
// Expected Result: 120 seconds returned


// Data inserted ( The second user shifts to anther location at 11:05:01:00 ) : 
insert into location(rowNumber,timestamp,mac_address,location_id) 
values (1,'2014-03-01 11:00:01','0000d893979225078884ae384365f2576dc26719',1010100022), 
(2,'2014-03-01 11:03:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022),
(3, '2014-03-01 11:05:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100005);

#FAIL : Result returns 361 seconds 
// Update 2014-10-07 23:37:00 PASS


/* 8 - Test whether time recorded is correct if user leaves the same place after a very short period of time*/
///<--Query 2014-03-01 11:15:00-->
// Select Email as andy.wong.2014@law.smu.edu.sg
// Expected Result: 1 seconds returned 
// Update 2014-10-07 23:37:00 PASS

// Data inserted : 
insert into location(rowNumber,timestamp,mac_address,location_id) 
values  
(2,'2014-03-01 11:03:01:00','0000d893979225078884ae384365f2576dc26719',1010100022),
(3,'2014-03-01 11:03:01:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100022),
(4,'2014-03-01 11:03:02:00','000cdde3c3c41c87802981ce44092af464bba6ee',1010100069);

# FAIL : Result returns 540 seconds 
// Update 2014-10-07 23:37:00 PASS





