Server side Validations for Basic Location Report: Breakdown by Year, Gender and School
Procedure: Do from top to bottom and double check expected results.

/* Test for Repeated Data Entries */
// This data is a Male, 2014, Law
INSERT INTO LOCATION VALUES (999999999,'2014-03-01 01:00:01', '0000d893979225078884ae384365f2576dc26719', 1010200065);
INSERT INTO LOCATION VALUES (999999998,'2014-03-01 01:14:01', '0000d893979225078884ae384365f2576dc26719', 1010200065);

///<--Query 2014-03-01 01:15:00-->
// 	Select all 3 as criteria
//	Expected Result: 1 located at SLOCA, 2014, LAW, Male, rest empty.

/* Test for a mac_address not found in demographics.csv */
// This mac_address does not exist in demographics.csv
INSERT INTO LOCATION VALUES (999999997,'2014-03-01 01:00:01', '0000d893979225078884ae384365f2576dc26719', 1010200065);
INSERT INTO LOCATION VALUES (999999996,'2014-03-01 01:14:01', '0000d893979225078884ae384365f2576dc26719', 1010200065);

///<--Query 2014-03-01 01:15:00-->
// 	Select all 3 as criteria
//	Expected Result: 1 located at SLOCA, 2014, LAW, Male, rest empty.

/* Test for correctness of 15 minute time window */
// This data is a Female, 2011, Law
INSERT INTO LOCATION VALUES (999999995,'2014-03-01 01:00:00', '000cdde3c3c41c87802981ce44092af464bba6ee', 1010200065);
INSERT INTO LOCATION VALUES (999999994,'2014-03-01 01:15:01', '000cdde3c3c41c87802981ce44092af464bba6ee', 1010200065);

///<--Query 2014-03-01 01:15:00-->
// 	Select all 3 as criteria
//	Expected Result: 1 located at SLOCA, 2014, LAW, Male, rest empty.

/* Test Group by Gender */
// This data is a Female, 2011, Law (Same as line 14)
INSERT INTO LOCATION VALUES (999999993,'2014-03-01 01:00:01', '000cdde3c3c41c87802981ce44092af464bba6ee', 1010200065);
INSERT INTO LOCATION VALUES (999999992,'2014-03-01 01:11:01', '000cdde3c3c41c87802981ce44092af464bba6ee', 1010200065);

/* Test Group by Year */

///<--Query 2014-03-01 01:15:00-->
// 	Select Year only
//	Expected Result: 1 Counts at 2011, 2014

/* Test Group by School */

///<--Query 2014-03-01 01:15:00-->
// 	Select School only
// This data is a Female, 2012, Econs
INSERT INTO LOCATION VALUES (999999991,'2014-03-01 01:00:01', '00497f3c2eb094e8de127fcfe11703ec878792b8', 1010200065);
//	Expected Result: 2 Counts @ Law, 1 count @ Econs, rest 0.

/* Test the Order of Output based on what was selected */

///<--Query 2014-03-01 01:15:00-->
// 	Select Gender, School
//	Expected Result: 1 Count @ Male, Female, 2 Counts @ Law

//	Select School, Year
//	Expected Result: 2 Counts @ Law, 1 Count @ 2011, 2014

//	Select Gender, School, Year
// 	Expected Result: 1 Count @ Male, Female, 2 Counts @ Law, 1 Count @ 2011, 2014

//	Select School, Year, Gender
// 	Expected Result: 2 Counts @ Law, 1 Count @ 2011, 2014, 1 Count @ Male, Female

