# ApkCenterAndroid (app)
- install apk applications on device
- display website of given application/website name

# TODOS
//////////////////////////////////////////////////////////////////////////////////////////////////////////
// todos:
- [Search Activity]
    (working on) load suggestion apps on app request

- [Section Activity]
    make possible to load data through api (apps section)

- [Search Activity]
    (working on) add icon on search list to api results

- [Search Activity]
    make the content for the contact page

- [ALL]
    add dagger to project

//////////////////////////////////////////////////////////////////////////////////////////////////////////
// bugs:

- [Main Activity, Section Activity, App Activity]
    main thread freeze when on glide loading images (~40 frames)

- [App Activity]
    webView contains deprecated stuff and some Errors

- [App Activity]
    installing apk with other program than this apk,
    that he will do it in the background and the user can close the app
    (Now you need to stay on the app page else it will crash the application)

- [Main Activity, Section Activity]
    need to load more data based on scroll position (small apps)

- [DbOpenHelper]
    remove Log: W/SQLiteConnectionPool:
        `A SQLiteConnection object for database '/data/user/0/com.pukkol.apkcenter/databases/ApkCenter.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.`

