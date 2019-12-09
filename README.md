# Free Seas

**Free Seas** is an Android application that serves as a platform to empower the Filipino fishing community and help them take action in regards to violence in the South China Sea by documenting identifying features of the perpetrators of violence at sea. [Click here to see what is happening in the South China Sea. ](https://www.vox.com/videos/2017/2/17/14642818/china-south-china-sea-us-islands
) Chinese quasi-military vessels are attempting to intimidate the filipino fishing community by committing violent acts against filipino fishing boats that are not easily documentable and the Chinese deny. The Filipino fishing community currently lacks sufficient safety measures to avert disaster and a comprehensive method of evidence collection against the perpetrators. Thus, Free Seas' key features include a database of reported vessels in the South China Sea that the user can either see on the map, or in a list of all reported vessels, and the ability to report a new vessel by documenting the hull number, description, longitude, latitude, country, and image evidence of a dangerous vessel. 

## Additional files:

Our Free Seas [Google Sheets Database](https://docs.google.com/spreadsheets/d/1zMSZi1IEveg_70rrLAxYB8NLC0luyLyAZP99PrGl-Wo/edit?usp=drive_web&ouid=112115335514804914656) is where the reported vessel report date/time, hull number, description, longitude, latitude, country, and path to image are stored.

Our Free Seas [Google App Script](https://script.google.com/a/middlebury.edu/d/1pWE970n6lxuHm4zsETna7lXOeelzkBlPbfGvFbaPLaHoDX822nS_f3bM/edit) is where our get and post methods are.

Our [Firebase Storage](https://console.firebase.google.com/u/0/project/free-seas-255114/storage/free-seas-255114.appspot.com/files) is where the images of the vessels are stored (Philip has access)

## To run:

  (1) Clone github repository into Android Studio.
  
  (2) You may need to set up an Android Virtual Divice that the emulator can use to install and run the app. To do so: 
  1. Click **Tools > AVD Manager**.  
  2. Click **Create Virtual Device**, at the bottom of the AVD Manager dialog. The **Select Hardware** page appears. 
  3. Select **Phone** for the category and **Pixel 2** for the name. 
  4. Click **Next**. 
  5. Click **Advanced Settings**. Make sure the Back Camera setting is set to **VirtualScene**. (for the purpose of testing)
  6. Click **Next**.
  7. Click **Finish**.
  
  (3) From the target device drop-down menu, select the AVD that you just created.
  
  (4) Now, in the toolbar click **Run > Run 'app'**.

## To use:

**(Screen 1) Home Screen:**

![Home Screen](https://github.com/lilykuntz/CS701-Project/blob/master/screenshots/homeScreen.png)

From here you can:
1. Click on "Report" to report a new vessel. --> (Screen 2)
2. Click on "List All" to see a list of all of the reported vessels.  --> (Screen 3)
3. Click on a marker, then click on marker info message to get more details on that vessel. --> (Screen 4)

**(Screen 2) Report Screen:**

![Report Screen](https://github.com/lilykuntz/CS701-Project/blob/master/screenshots/reportFields.png)

From here you can:
1. Click on the input fields and use the keyboard to fill out the different fields.
2. Click "take photo" and capture a photograph.
3. Click "Report" to add the inputted data to the database. Your reported vessel will then appear on both the Home Screen (Screen 1) as a marker on the map and in the list on the List All Screen (Screen 3).

An example of how a user might fill out the Report Screen can be seen below:

![Report Example](https://github.com/lilykuntz/CS701-Project/blob/master/screenshots/filledOut.png)

**(Screen 3) List All Screen:**

![List All Screen](https://github.com/lilykuntz/CS701-Project/blob/master/screenshots/listAll.png)

From here you can:
1. Click on a list item to see more details about the selected vessel. --> (Screen 4). 

**(Screen 4) Vessel Details Screen:**

![Vessel Details Screen](https://github.com/lilykuntz/CS701-Project/blob/master/screenshots/details.png)

Displays all information about the selected vessel


* From all screens (besides the Home Screen) you can always click the back arrow in the top left corner to return to the previous page. 


