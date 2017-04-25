# Simple password manager

Self hosted online password manager (needs a proxy for ssl). See a demo in action 
[here](https://www.simple-password-manager.xyz)

The hashed passphrase (SHA256 + PBKDF2) is used as the key to encrypt/decrypt 
the password data on the client (with AES). There is no way for 
the server to decrypt the data. 

Use an SSL connection to make sure the login credentials are 
kept secret.

## How it works

When the user logs in the username and passphrase are send 
hashed (SHA256 + PBKDF2) to the server.

The server sends back the encrypted encryption key and the encrypted data. 

The client decrypts the encryption key with the AES algorithm 
and the hashed passphrase. Then we can decrypt the password data 
with the decrypted encryption key. The passphrase hash send to the server is 
different then the one used to encrypt/descrypt the data, so the
server has no way to decrypt the data.

Any time you make a change all the data is encrypted and send
to the server.

**If the passphrase is lost, the encryption key is lost and all the passwords are lost.**

## How to run

Download the latest zip, extract it and run the jar file:

```
  java -jar spm-1.0.0.jar
```

Take a look in your browser at the address http://localhost:3456/. You can change the port, see Settings.
The client connects with a websocket, it will only connect on http when the domain is localhost, 
otherwise it will try to connect to https (wss) without a port. **So install ssl with a proxy to use this!**

The application will create a data directory in the current directory where it will store the database. 
This is the file you need to backup (when the application is not running).

You can also use another database by changing the properties. The application will try to create the database on first use.

## Settings

To change any of the settings add a file called spm.properties to the startup directory.

Here is an example with the default settings (the application updates the jdbcConnectionUrl with the current directory if it's not overwritten in these properties):

```
    port                = 3456
    connectionTimeout   = 30000

    jdbcDriver          = "org.h2.Driver"
    jdbcConnectionUrl   = "h2:file:"
    jdbcUser            = "sa"
    jdbcPassword        = ""
```

The connection timeout can't be to short or the websocket connection will close.
