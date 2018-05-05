# Baking App

This app gives the ability to the user to view different baking recipes. The data are fetched from a server in JSON format and are stored in a local database. The app uses the Exoplayer library to show videos of certain recipe steps and has a widget that allows the user to view the ingredients for the different recipes.

## How It Works

When the app starts, the user is presented with a list of recipes. When the user clicks on a recipe a new screen appears. The new screen consists of a list with two different types of information. At the top of the list there is a sublist with the ingredients for the specific recipe and then follows a sublist with the steps for the recipe. The user is allowed to click on a step. When the user clicks on a step a new screen opens. In that screen there is a detailed decription of the step and a video that accompanies the descriprion.

<p align="center">
  <img src="https://drive.google.com/uc?id=1JHIzmtMpDPfycAA9MtNG1hM7TrPNdCMt" width="300" height="450"> &nbsp<img src="https://drive.google.com/uc?id=1iH8QZClAdxm4Cddx4L9t0LV212jN7IET" width="300" height="450">
</p>

<p align="center">
  <img src="https://drive.google.com/uc?id=1RpHweh7zUF7raexbIseGLXsLuWocJAq7" width="300" height="450"> &nbsp<img src="https://drive.google.com/uc?id=1lstWW2YRHI0S78kmf8NvT9MChsix01ph" width="300" height="450">
</p>

## Technical Details

1. In order to get the recipe data the app makes a [**network request**](https://developer.android.com/training/basics/network-ops/) and then [**parses the JSON data**](https://developer.android.com/reference/org/json/package-summary) that it receives. Each network request is made on a seperate thread with the help of the [**Intent Service**](https://developer.android.com/reference/android/app/IntentService). The Intent Service makes a [**Broadcast**](https://developer.android.com/guide/components/broadcasts) in order to inform the main thread about the success or the failure of the process.
2. The app uses a [**RecyclerView**](https://developer.android.com/guide/topics/ui/layout/recyclerview) to show the list of recipes. In the second screen  .
3. When the user clicks on a recipe the detail activity opens. The detail activity contains the following information: Ingredients and Recipe Steps.In order to show the ingredients and the steps at the same list it uses a RecyclerView with two different View Types. When the user clicks on a recipe step a new activity opens.
4.  All the data is stored in an [**SQLite Database**](https://developer.android.com/training/data-storage/sqlite) and is accessed with the help of a [**Content Provider**](https://developer.android.com/guide/topics/providers/content-providers). If the user chooses to refresh the data, the ld data will be deleted if the refresh procedure was successful.
5. The app has a [**Home-Screen Widget**](https://developer.android.com/guide/topics/appwidgets/overview) that shows the ingredients for each recipe.
