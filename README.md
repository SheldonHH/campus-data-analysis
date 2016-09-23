# Campus Data Analysis
This project is based on my previous school project to analyse the density of a venue, app usage among the campus

###Functionality Overview
1. **Login** (Blue)
A user is able to log in with their email ID and password. All the reports are only accessible after logging in.
2. **Bootstrap** (Blue)
The administrator can bootstrap the school system with app usage and demographics data. The administrator can add additional data while the application is running.
3. **Basic App Usage Report** (Blue)
A user can see the following app usage stats for any given duration:
Breakdown by usage time category (e.g., High/Medium/Low) Breakdown by usage time category and demographics Breakdown by app category
Diurnal pattern of app usage time
4. **Top-k App Usage Report** (Blue)
A user can see the top-k users/apps for any given duration:
Top-k most used apps (given a school)
Top-k students with most app usage (given an app category) Top-k schools with most app usage (given an app category)
5. **Smartphone Overuse Report** (Blue)
A user can see a smartphone overuse index (based on smartphone usage time, gaming time, and frequency of checking smartphones) for themselves for self-feedback and potential behaviour changes.
6. **Dual Interfaces (Web UI and Web Services)** (Blue)
Web UI: Provide a user-friendly web UI for all features that your team needs to implement. The basic UI requirement is showing results in nicely formatted tables, text, etc.
Web Services: Provide JSON APIs that allow all functionalities to be queried programmatically by other machines.
7. **Loading Location Data** (Green)
The administrator can bootstrap the SMUA system with location data of students.
The administrator should also be able to add new location data while the application is running.
8. **Deletion of Data** (Green)
While loading location data for bootstrapping, the app may be required to delete a subset of data which could cause undesirable bias to the analysis.
Also, the administrator is able to delete a subset of data (for a certain user over a certain period), using a web UI while the application is running.
All the subsequent queries after the deletion need to be performed without deleted data.
9. Smartphone Usage Heatmap (Green)
A user can see the density of people using smartphones for a specified floor in the SIS building, given a particular date and time. (Output does not need to be in graphical form.)
10. Social Activeness Report (Green)
A user can calculate their own social activeness (based on social communication app usage and physical grouping).




###Web Service Requirements Overview
School uses a RESTful API using JSON. All JSON API requests result in a status value. The two status values are success and error.
Requests use a simple REST-style HTTP GET/POST. To invoke the API, include a non-empty r value in the URL. The format of a request is as follows:
`http://<host>/json/<service>?token=tokenValue&paramA=valueA&paramB=valueB`
The request queries parameter may vary across different services. For this project, all requests (except for the authenticate service) require the sending of the token obtained via the authenticate service.




###JSON Values
#####A JSON values can be a/an
#####number (integer or floating point). To indicate a floating number, always put a decimal place. For example, 12.0 instead of 12.
#####string (in double quotes)
#####Boolean (true or false)
#####array (in square brackets). array values are ordered. object (in curly brackets)
#####null


###Ordering
#####An array is an ordered collection of values.
#####An object is an unordered set of name/value pairs.


###Whitespace
#####JSON generally ignores any whitespace around or between syntactic elements (values and punctuation, but not within a string value).
#####JSON format for each functionality is specified in detail below.

###Common Validations for JSON requests
#####For all the input fields, you need to check
1. if the mandatory field is missing  
2. if the field is blank.  
3. if the token is invalid.  
Valid Request:
`http://<host>/json/heatmap?token=[tokenValue]&floor=2&date=2014-03-29T12:30:00`





####Using Libraries and External Code
1. You are welcome to use open source code libraries where it makes sense. But you must make it clear what you have used and why. When in doubt, check with your project supervisor.
2. Some libraries that you might find useful are  
[OpenCSV](http://opencsv.sourceforge.net/): For easy importing of CSV files  
[UploadBean](http://www.javazoom.net/jzservlets/servlets.html): For easy uploading of zip files to the deployment server  
[D3](http://dciarletta.github.io/d3-floorplan/): For heat map  
[NVD3](http://nvd3.org): Re-usable charts for D3  

