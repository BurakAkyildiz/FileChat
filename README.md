# FileChat
File transfer and Chat application for local or remote networks.

Simply create a room and share your server ip and port with your friends. They can enter your room... 
You can chat with them and share any file directly from your pc to your friends pc.
Because of the simplicity there is no user limit or file limit. If you create a server the limit is your pc and users network.

-----> You can create a local server or remote server. 

The server's role is just notifying users. Redirecting download and upload requests to clients, publish chat messages, post system information messages.

If you want to create a remote server you need to open a port and redirect incoming messages to your local ip. File chat will check your port if its open on remoote connection.
If you want to create a local server you just need to select a free port on your pc.
(If you create a remoote server and is someone try to enter your room on local network with local ip he will have better connection with server but he can have problem with other remoote users when transferring files.)
When you create a room you can see your ip on aplicatons windows title so you can share it with you friends.
---------------------------------------------------------------

-----> You can Connect a server.

When you connect a server you can do same things like server.
You can share any files, send messages, download and upload files.
If you connect a remoote server you need to open a file port and redirect int to your local ip on your router to send files to clients.
-----------------------------------------------------------------

-->Features
-You can chat with all users on chat room over server.
-Send private messages to any user. Server will not store it just redirect it to client. (Stored chat messages on server will not shared with new users.)
-When you download a file you can watch the progress and see which file is downloaded dynamically. Also you can open them on from list with double click.
-Edit shared files and reshare them.
-Delete your share.
-Set automatic download and download files when shared automaticly.
-Set override or not override state and when you automaticly download file just download new files.
-Set upload & download speed limit.
-Set download directory. (By default it is C:/Users/user/Desktop/Download/ directory.)
-You can search shared files from table dynamicly.
------------------------------------------------------------------

--> Behavior
The main attribute is, file chat will upload files one by one to clients. If downloader disconnects passes next upload on queue.
There is no download limit you can download any shared file at the same time. But you need to wait your turn to download.
Chat messages will publish on server but files will transfer from your pc directly.
Editing shared file will edit all of your requests too. But your turn will not change on download queue.
If you delete your shared file when you uploading it to a client the current upload will be not effected but other things will be all removed.
File chat will check your all network interfaces and will select first successful one which can connect your host to detect your local ip. ( If you use multihomed localhost be sure your server ip is correct.)
-------------------------------------------------------------------
