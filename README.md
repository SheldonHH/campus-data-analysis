# Campus Data Analysis
This project is based on my previous school project to analyse the density of a venue, app usage among the campus

###Functionality Overview
1. **Login** (Blue)
A user is able to log in with their email ID and password. All the reports are only accessible after logging in.
2. **Bootstrap** (Blue)
The administrator can bootstrap the SMUA system with app usage and demographics data. The administrator can add additional data while the application is running.
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
