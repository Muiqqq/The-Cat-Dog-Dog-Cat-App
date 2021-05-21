# The-Cat-Dog-Dog-Cat-App

Small mobile app for a school course.

## Author

Aleksi Muikku

## Screencast
[![Project Screencast](https://img.youtube.com/vi/We936RqpJ9c/0.jpg)](https://www.youtube.com/watch?v=We936RqpJ9c "Native Mobile Development - Projektin Screencast")

## Known bugs

- Shown images are sometimes cropped poorly. This is due to the APIs providing wildly differently sized images, and a compromise had to be made.
- The view showing a single favorite image has it's area extend a bit beyond the textview on top and the button at bottom, making dismissing the view by clicking the background awkward if user clicks near those elements. This is more apparent on smaller screens.

## Release 2: 2021-05-21 features

- A third mode was added: Prefless, shows images of cats or dogs at random. Favorite view in this mode shows both favorite dog and cat images.
- User is able to change between all 3 modes with a dialog that opens when the swap button is pressed.
- User is able to swipe to get new random image in the main view.
- User is able to view favorite images in a larger form, by clicking any image in the Favorites view.
- When viewing images in the larger form, user is able to swipe to move between favorite images.
- User is able to delete images from favorites
- Favorites are now unique on each installation of the app. Before, íf the app was used on multiple devices, it would show the same images.

## Release 1: 2021-05-12 features

- User is able to swap between Cat/Dog mode
- The UI changes depending on which mode is in use.
- User is able to get a random image for the mode they're in.
- User is able to add an image to favorites
- A snackbar appears to show if the favorite was added successfully or not.
- User is able to view their favorites.

## Topic

An Android application written in Kotlin, with which the user can view random pictures of cats or dogs, depending on their preference. Preference is chosen by the user when the app is launched for the first time, and affects which animal's pictures are shown by default. This preference can be changed at any time. The user can switch the animal they are viewing at any time with the click of a button.

While the main function of the app is to just see cat or dog images at random, the user will be able to favorite any image and look at their favorites later. Plans for other features include showing random facts about whichever animal is being viewed, and the ability to browse the images in some other fashion than just randomly.

## Used APIs

- https://thecatapi.com/
- https://thedogapi.com/
- ... and maybe some others for additional features

## Target

Android devices, written in Kotlin.
