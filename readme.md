
Server stores for each user:

* login name
* (hashed) password
* encrypted encryption key

Server stored for each password

* name/url etc
* password send encrypted by client

Login:

* Send login name and hashed password
* If matched, return encrypted encryption key
* Use password (with other type of hash) to decrypt encryption key
* Send encrypted passwords to server

###Changing password means reencrypting the encryption key with new password

* Must be one single operation

**If password is lost, encryption key is lost and all the data is lost.**
