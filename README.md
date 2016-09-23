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




###Understanding the app usage data
To implement app usage-based features of the school system, it is very necessary to understand the characteristics of the provided app usage data and several relevant assumptions.   
1. The app usage traces are real traces that have been anonymised. Thus they have all the characteristics of real-world data -- for better or worse!  
2. The app usage of every user is updated whenever the user interacted with applications (e.g., click the button, scroll the page, texting, etc) with a timestamp. Also, it captures other system events occurred in mobile OSes such as receiving notifications, etc.  
3. Every app usage update is associated with identifiable users and their demographic information. You can assume that there will always only be one MAC address for each user.  
4. The app usage time can be approximated and calculated by the time difference between the first interaction with a specific app and the first interaction with the subsequent app. When there is no subsequent update within 2 minutes (<=120 sec), assume that the user has continued using the app for 10 more seconds from the last interaction with the app. Let’s assume the app data for a specific user is as follows.     
t=1 (second), WhatsApp  
t=7, WhatsApp  
t=21, WhatsApp   
t=30, Facebook   
t=520, GoogleMap  
t=970, Calendar  
5. For the above example, usage duration of WhatsApp is 30-1 = 29, i.e., difference between first occurrence of WhatsApp and the first occurrence of the next app, i.e., the Facebook. (Note that it is not 21-1=20!!!) The usage duration of Facebook is 10 sec as there is no subsequent update for the following 2 minutes.   
6. The smartphone access frequency is approximated and calculated based on the number of ‘phone use sessions’. A phone use session is a group of continuous interactions with apps where the two subsequent interactions are not more than 2 minutes apart (>120 sec). In the above example, there are three phone use sessions: The first session is (WhatsApp at t=1, WhatsApp at t=7, WhatsApp at t=21, Facebook at t=30), the second is (GoogleMap at t=520) and the third session is (Calendar at t=970). The smartphone access frequency is calculated as the number of phone use sessions per hour (averaged over the duration of interest).
