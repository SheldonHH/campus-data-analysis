
 /* 
  * TEST DATA FOUND BELOW !!!!!!
  *
  */




/* Test that the group still exists if the semantic place in the next window is the same as the  selected semantic place /previous window  */
///<--Query 2014-07-24 02:15:00-->
//	Select location as SMUSISL1LOBBY
//	Expected Result: 
//	Detected 1 groups
//	Top Location 
//	Rank 1: SMUSISL1LOBBY Count : 1


/* Test for presence of merged groups in the next location */

///<--Query 2014-07-25 02:15:00-->
//	Select location as SMUSISL1LOBBY
//	Expected Result: 0 groups found



/* Test whether 1 main group is detected in the next location */

///<--Query 2014-07-26 02:15:00-->
//	Select location as SMUSISL1LOBBY
//	Expected Result: 
//	1 Group Detected



/* Test for incorrect presence of subgroups in the next location */
Query 2014-07-27 02:15:00-->
//	Select location as SMUSISL1LOBBY
//	Expected Result: 
//	Detected 0 groups



/* 2nd Test for incorrect presence of merged groups (smaller size)  in the next location - If the previous window's 2 groups merge, results will be zero*/

///<--Query 2014-07-28 02:15:00-->
//	Select location as SMUSISL1LOBBY
//	Expected Result: 
//	Detected 0 groups





/* Test that the group does not exist if the group's last location in the previous window is not the selected window */

///<--Query 2014-07-29 02:15:00-->
//	Select location as SMUSISL1LOBBY
//	Expected Result: 
//	Detected 0 groups 




/* Test that the group's last location in the next window is >=5 minutes */

///<--Query 2014-07-30 02:15:00-->
//	Select location as SMUSISL1LOBBY
//	Expected Result: 
//	Detected 0 groups



/* Test what happens when a group receives 2 valid updates in the same location */  //Corner Case data to fill in 

//DATA NOT YET CREATED










delete from location where rowNumber>=999990000;
 
 
 
INSERT INTO LOCATION VALUES (999993341, '2014-07-24 02:00:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999993342, '2014-07-24 02:05:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999993343, '2014-07-24 02:12:05', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100018);
INSERT INTO LOCATION VALUES (999993344, '2014-07-24 02:13:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999993345, '2014-07-24 02:15:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999993346, '2014-07-24 02:19:59', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100018);
INSERT INTO LOCATION VALUES (999993347, '2014-07-24 02:00:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999993348, '2014-07-24 02:05:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999993349, '2014-07-24 02:12:05', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100018);
INSERT INTO LOCATION VALUES (999993350, '2014-07-24 02:13:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999993351, '2014-07-24 02:15:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999993352, '2014-07-24 02:19:59', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100018);


INSERT INTO LOCATION VALUES (999993331, '2014-07-24 02:01:59', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999993332, '2014-07-24 02:05:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999993333, '2014-07-24 02:15:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999993334, '2014-07-24 02:20:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999993335, '2014-07-24 02:01:59', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999993336, '2014-07-24 02:05:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999993337, '2014-07-24 02:15:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999993338, '2014-07-24 02:20:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);

 
INSERT INTO LOCATION VALUES (999999898, '2014-07-25 02:00:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999999899, '2014-07-25 02:02:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100018);
INSERT INTO LOCATION VALUES (999999900, '2014-07-25 02:04:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999999901, '2014-07-25 02:10:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999999902, '2014-07-25 02:15:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999999903, '2014-07-25 02:18:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010110083);
INSERT INTO LOCATION VALUES (999999904, '2014-07-25 02:22:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010110064);
INSERT INTO LOCATION VALUES (999999905, '2014-07-25 02:24:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010110064);

INSERT INTO LOCATION VALUES (999999906, '2014-07-25 02:00:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999999907, '2014-07-25 02:02:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100018);
INSERT INTO LOCATION VALUES (999999908, '2014-07-25 02:04:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999999909, '2014-07-25 02:10:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999999910, '2014-07-25 02:15:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999999911, '2014-07-25 02:18:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010110083);
INSERT INTO LOCATION VALUES (999999912, '2014-07-25 02:22:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010110064);
INSERT INTO LOCATION VALUES (999999913, '2014-07-25 02:24:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010110064);

INSERT INTO LOCATION VALUES (999999914, '2014-07-25 02:01:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999999915, '2014-07-25 02:03:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999999916, '2014-07-25 02:05:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100006);
INSERT INTO LOCATION VALUES (999999917, '2014-07-25 02:06:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999999918, '2014-07-25 02:14:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999999919, '2014-07-25 02:18:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999999920, '2014-07-25 02:22:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010200016);
INSERT INTO LOCATION VALUES (999999921, '2014-07-25 02:24:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010200016);

INSERT INTO LOCATION VALUES (999999922, '2014-07-25 02:01:01',  'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999999923, '2014-07-25 02:03:01',  'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999999924, '2014-07-25 02:05:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100006);
INSERT INTO LOCATION VALUES (999999925, '2014-07-25 02:06:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999999926, '2014-07-25 02:14:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999999927, '2014-07-25 02:18:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999999928, '2014-07-25 02:22:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010200016);
INSERT INTO LOCATION VALUES (999999929, '2014-07-25 02:24:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010200016);





INSERT INTO LOCATION VALUES (999997600, '2014-07-26 02:00:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999997601, '2014-07-26 02:06:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999997602, '2014-07-26 02:08:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999997603, '2014-07-26 02:15:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999997604, '2014-07-26 02:18:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999997605, '2014-07-26 02:22:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999997606, '2014-07-26 02:24:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);

INSERT INTO LOCATION VALUES (999997607, '2014-07-26 02:00:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999997608, '2014-07-26 02:06:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999997609, '2014-07-26 02:08:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999997610, '2014-07-26 02:15:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999997611, '2014-07-26 02:18:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999997612, '2014-07-26 02:22:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999997613, '2014-07-26 02:24:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);


INSERT INTO LOCATION VALUES (999997614, '2014-07-26 02:00:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999997615, '2014-07-26 02:06:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999997616, '2014-07-26 02:08:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999997617, '2014-07-26 02:15:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999997618, '2014-07-26 02:18:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999997619, '2014-07-26 02:22:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999997620, '2014-07-26 02:24:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);


INSERT INTO LOCATION VALUES (999997621, '2014-07-26 02:00:01',  'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999997622, '2014-07-26 02:06:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999997623, '2014-07-26 02:08:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999997624, '2014-07-26 02:15:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999997625, '2014-07-26 02:18:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999997626, '2014-07-26 02:22:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999997627, '2014-07-26 02:24:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);

INSERT INTO LOCATION VALUES (999998600, '2014-07-27 02:00:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999998601, '2014-07-27 02:06:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999998602, '2014-07-27 02:08:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999998603, '2014-07-27 02:15:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999998604, '2014-07-27 02:18:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999998605, '2014-07-27 02:22:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100065);
INSERT INTO LOCATION VALUES (999998606, '2014-07-27 02:24:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100065);

INSERT INTO LOCATION VALUES (999998607, '2014-07-27 02:00:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999998608, '2014-07-27 02:06:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999998609, '2014-07-27 02:08:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999998610, '2014-07-27 02:15:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999998611, '2014-07-27 02:18:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999998612, '2014-07-27 02:22:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100065);
INSERT INTO LOCATION VALUES (999998613, '2014-07-27 02:24:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100065);


INSERT INTO LOCATION VALUES (999998614, '2014-07-27 02:00:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999998615, '2014-07-27 02:06:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999998616, '2014-07-27 02:08:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999998617, '2014-07-27 02:15:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999998618, '2014-07-27 02:18:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999998619, '2014-07-27 02:22:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999998620, '2014-07-27 02:24:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);


INSERT INTO LOCATION VALUES (999998621, '2014-07-27 02:00:01',  'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999998622, '2014-07-27 02:06:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999998623, '2014-07-27 02:08:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999998624, '2014-07-27 02:15:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999998625, '2014-07-27 02:18:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999998626, '2014-07-27 02:22:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999998627, '2014-07-27 02:24:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);


INSERT INTO LOCATION VALUES (999996111, '2014-07-28 02:00:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999996112, '2014-07-28 02:02:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100065);
INSERT INTO LOCATION VALUES (999996113, '2014-07-28 02:05:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999996114, '2014-07-28 02:08:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999996115, '2014-07-28 02:15:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999996116, '2014-07-28 02:18:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999996117, '2014-07-28 02:22:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999996118, '2014-07-28 02:24:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);

INSERT INTO LOCATION VALUES (999996119, '2014-07-28 02:00:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999996120, '2014-07-28 02:02:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100065);
INSERT INTO LOCATION VALUES (999996121, '2014-07-28 02:05:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999996122, '2014-07-28 02:08:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999996123, '2014-07-28 02:15:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999996124, '2014-07-28 02:18:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999996125, '2014-07-28 02:22:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999996126, '2014-07-28 02:24:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);


INSERT INTO LOCATION VALUES (999996127, '2014-07-28 02:00:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999996128, '2014-07-28 02:02:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999996129, '2014-07-28 02:06:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100090);
INSERT INTO LOCATION VALUES (999996130, '2014-07-28 02:08:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999996131, '2014-07-28 02:15:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999996132, '2014-07-28 02:18:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999996133, '2014-07-28 02:22:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);
INSERT INTO LOCATION VALUES (999996134, '2014-07-28 02:24:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010100001);


INSERT INTO LOCATION VALUES (999996135, '2014-07-28 02:00:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999996136, '2014-07-28 02:02:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999996137, '2014-07-28 02:06:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100090);
INSERT INTO LOCATION VALUES (999996138, '2014-07-28 02:08:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999996139, '2014-07-28 02:15:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999996140, '2014-07-28 02:18:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999996141, '2014-07-28 02:22:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);
INSERT INTO LOCATION VALUES (999996142, '2014-07-28 02:24:01', 'dddddddddddddddddddddddddddddddddddddddd', 1010100001);

INSERT INTO LOCATION VALUES (999995441, '2014-07-29 02:00:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010300028);
INSERT INTO LOCATION VALUES (999995442, '2014-07-29 02:05:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010300028);
INSERT INTO LOCATION VALUES (999995443, '2014-07-29 02:15:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010300028);
INSERT INTO LOCATION VALUES (999995444, '2014-07-29 02:18:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010300028);
INSERT INTO LOCATION VALUES (999995445, '2014-07-29 02:22:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010300028);
INSERT INTO LOCATION VALUES (999995446, '2014-07-29 02:26:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010300028);

INSERT INTO LOCATION VALUES (999995447, '2014-07-29 02:00:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010300028);
INSERT INTO LOCATION VALUES (999995448, '2014-07-29 02:05:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010300028);
INSERT INTO LOCATION VALUES (999995449, '2014-07-29 02:15:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010300028);
INSERT INTO LOCATION VALUES (999995450, '2014-07-29 02:18:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010300028);
INSERT INTO LOCATION VALUES (999995451, '2014-07-29 02:22:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010300028);
INSERT INTO LOCATION VALUES (999995452, '2014-07-29 02:26:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010300028);

INSERT INTO LOCATION VALUES (999995453, '2014-07-29 02:00:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010300028);
INSERT INTO LOCATION VALUES (999995454, '2014-07-29 02:05:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010300028);
INSERT INTO LOCATION VALUES (999995455, '2014-07-29 02:15:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010300028);
INSERT INTO LOCATION VALUES (999995456, '2014-07-29 02:18:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010300028);
INSERT INTO LOCATION VALUES (999995457, '2014-07-29 02:22:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010300028);
INSERT INTO LOCATION VALUES (999995458, '2014-07-29 02:26:01', 'cccccccccccccccccccccccccccccccccccccccc', 1010300028);



INSERT INTO LOCATION VALUES (999994441, '2014-07-30 02:00:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999994442, '2014-07-30 02:05:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999994443, '2014-07-30 02:15:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100001);
INSERT INTO LOCATION VALUES (999994444, '2014-07-30 02:18:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100065);
INSERT INTO LOCATION VALUES (999994445, '2014-07-30 02:22:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100006);
INSERT INTO LOCATION VALUES (999994446, '2014-07-30 02:26:01', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1010100018);

INSERT INTO LOCATION VALUES (999994447, '2014-07-30 02:00:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999994448, '2014-07-30 02:05:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999994449, '2014-07-30 02:15:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100001);
INSERT INTO LOCATION VALUES (999994450, '2014-07-30 02:18:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100065);
INSERT INTO LOCATION VALUES (999994451, '2014-07-30 02:22:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100006);
INSERT INTO LOCATION VALUES (999994452, '2014-07-30 02:26:01', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 1010100018);























