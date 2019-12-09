# CS701-Project
# Free Seas
## Annie Glassie and Lily Kuntz

Our Free Seas databse, where the reported vessel report date/time, hull number, description, longitude, latitude, country, and path to image are stored, can be found here: 
https://docs.google.com/spreadsheets/d/1zMSZi1IEveg_70rrLAxYB8NLC0luyLyAZP99PrGl-Wo/edit?usp=drive_web&ouid=112115335514804914656

Our Free Seas app scrip, where our get and post methods are, can be found here:
https://script.google.com/a/middlebury.edu/d/1pWE970n6lxuHm4zsETna7lXOeelzkBlPbfGvFbaPLaHoDX822nS_f3bM/edit

Our Firbase Storage, where the images of the vessels are stored, can be found here (Philip has access):
https://console.firebase.google.com/u/0/project/free-seas-255114/storage/free-seas-255114.appspot.com/files

## To run:

  (1) Clone github repository into Android Studio.
  
  (2) You may need to set up an Android Virtual Divice. To do so, click "Tools" --> "AVD Manager" --> "Create Virtual Device". Select "Phone" for the category and "Pixel 2" for the name. Click "Next". Click "Advanced Settings". Make sure the Back Camera setting is set to "VirtualScene". Click "Next", "Finish".
  
  (3) Now, click "Run 'app'".

## To use:

### (Screen 1) Home Screen:

![Home Screen](https://github.com/lilykuntz/CS701-Project/blob/master/homeScreen.png)

From here you can:
1. Click on "Report" to report a new vessel. --> (Screen 2)
2. Click on "List All" to see a list of all of the reported vessels.  --> (Screen 3)
3. Click on a marker, then click on marker info message to get more details on that vessel. --> (Screen 4)

### (Screen 2) Report Screen:

![Report Screen](https://github.com/lilykuntz/CS701-Project/blob/master/reportFields.png)

From here you can:
1. Click on the input fields and use the keyboard to fill out the different fields.
2. Click "take photo" and capture a photograph.
3. Click "Report" to add the inputted data to the database. Your reported vessel will then appear on both the Home Screen (Screen 1) as a marker on the map and in the list on the List All Screen (Screen 3).

### (Screen 3) List All Screen:

![List All Screen](https://github.com/lilykuntz/CS701-Project/blob/master/listAll.png)

From here you can:
1. Click on a list item to see more details about the selected vessel. --> (Screen 4). 

### (Screen 4) Vessel Details Screen:

![Vessel Details Screen](https://github.com/lilykuntz/CS701-Project/blob/master/details.png)

Displays all information about the selected vessel



* From all screens (besides the Home Screen) you can always click the back arrow in the top left corner to return to the previous page. 


