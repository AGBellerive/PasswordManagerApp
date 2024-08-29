# PasswordManagerApp

Welcome to my Password Manager Android App.

The purpose of this Android App was to solve the problem my [Password Manager](https://github.com/AGBellerive/PasswordManager) created 
by being so useful to me and allow me to exersize new concepts in Android Development. This app is a toned down version of my password manager app.

The current 1.0 version of this app is a companion app to work along side 
[Password Manager](https://github.com/AGBellerive/PasswordManager), so it is not a stand alone app.


## Walk Through

On first start up of the application, you will be sent to a configuration page.
This page will ask you to create a master password of length 8 or more, confirm that password, create an optional hint for that 
password, and lastly choose the location you want your to store your password file on your device. 
This process will save all this information in shared prefrences.

`todo: find a way to link to a remote db`

Once that is set up, you will be redirected to the vault. 
Now that you are in the vault, there is a search your account found in your password file, or scroll all the entries provided 
in a list below. You can click on the entries to copy the values for easy external use. 
The password file will have to be in the following [Format](https://github.com/AGBellerive/PasswordManagerApp?tab=readme-ov-file#format) also seen in [Password Manager](https://github.com/AGBellerive/PasswordManager?tab=readme-ov-file#format)

Now on any start up afterwards, you will use your devices biometrics, if toggled during configuration to enter the vault

# Format 
<pre>
 
[   
  {
    "Site": "Example Site",
    "Username": "Example Username",
    "Email": "Email@Email.com",
    "Password": "ABC123",
    "Other": "Extra information"
  },
  {
    "Site": "Github",
    "Username": "octocat",
    "Email": "Email@Email.com",
    "Password": "pa55w0rd",
    "Other": "anthropomorphized “octocat” with five octopus-like arms"
  },
  ...
]
</pre> 