
#  CSE 216 AZTECS Project

## This is the Admin folder/branch

### <u>Email Account Information </u>
Username: Montezuma II \
E-mail: cse216.aztecs@gmail.com \
Password: jenniferloew123!

### <u>SendGrid Account Information </u>
Username: Montezuma II \
E-mail: cse216.aztecs@gmail.com \
Password: jenniferloew123!


## <u><b>Phase 1 Admin Developer: William Peracchio</b></u>

- Updated Stuff:

Created a CLI admin interface. CLI has different interfaces, you can switch using the different commands present, adapted heavily from the Phase 0 documentation. 

U --> User

  Create, Read, Update, and Delete Users

M --> Messages

  Create, Read, Update, and Delete Messages

L --> Likes

  Create, Read, Update, and Delete Likes

P --> Parent

  Create, Read, Update, and Delete Parent

## <u><b>Phase 2 Admin Developer: Daniel Onyemelukwe</b></u>

<u>Updates</u>
- Added Sendgrid Dependency in pom.xml and created account in order to send activation e-mails
- Added Salt and Salted Hashed Password field in User Tables
- Used Sha-256 Hashing Technique
- All changes were made in CommandLineInterface.java and User.java

<u>How to Send Activation E-mail using SendGrid</u>
- Log into your account and follow this link: https://app.sendgrid.com/guide/integrate/langs/java
- Follow Step 2: Create an API key(You can use any word. I used "Montezuma")
- Follow Step 3: Create an Environmental variabe but do not put in "echo "sendgrid.env" >> .gitignore" as I have already updated the .gitignore file
- Skip Step 4 and Step 5 and run Admin code. Once you insert a user, an e-mail will be sent(most like to their spam folder though)



