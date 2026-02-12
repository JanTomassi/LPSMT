# MangaReader

## Functional
- The interface of the application is in English.
- The application need to open with a log in page if not already logged in.
- The user can open only `.cbz` file.
- The user can log in only with a personal email and password.
- Once logged the first time the user must remain logged.
- Users can log out anytime they wants.

### Home fragment
- Must be displayed the list of manga 
  + read
  + dropped
  + in progress
  + finished
- On the left of a manga title should be place a small icon showing a cover of the manga volume.
- The user can change the status of a manga and the chapter from this page.
- on the bottom right corner must be a button to open the manga reader.

### Reader fragment
- The user can move forward and backward with two icons on the bottom of the screen.
- On the bottom of the screen the user can read the number of total pages next to the number of current page.
- It's possible use the pinch to zoom (non essential)
- On the top left corner there is an arrow to return in the home fragment and close the reader fragment.

## Non functional
- Compatible with Android 9 API level 28 and above.
- The file must be decompressed and saved in the local app directory.
- Every time the user open a new `.cbz` file the old one is deleted from the app directory.
- The lists of manga available is based on the [Anilist API](https://anilist.gitbook.io/anilist-apiv2-docs/).
- The user data and manga data must be store in a proprietary DB.