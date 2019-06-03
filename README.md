Attention
=========
This project is now defunct. No further support or updates will be provided.

RadixCore
=========

RadixCore is a library / mod core utilized by mods created by SheWolfDeadly and WildBamaBoy.

RadixCore contains the following features:

 - Rapid core mod class construction using a "fill in the blanks" point of view.
 - Automatic generation, loading, updating, and validation of configuration files, viewable and editable in-game via commands.
 - Automatically save and load data to and from NBT.
 - An automatic update checker.
 - A language system allowing for easy translation of GUIs and parsing of variables.
 - An easy to use logger that labels which side you are working with.
 - More, with your help! Feel free to make a suggestion.

##Setup for mod development
####Eclipse
 1. Set up your ForgeGradle workspace.
 2. Create a **lib** folder in your workspace folder.
 3. Download the **deobf** version of RadixCore from [RadixCore's download page](radix-shock.com/http://www.radix-shock.com/rc--download.html) and place it inside the **lib** folder.
 4. Right-click **Minecraft** in your Package Explorer.
 5. Navigate to **Properties** -> **Java Build Path** -> **Libraries** -> **Add External JARs...**.
 6. Select the deobf version of RadixCore that you placed in your **lib** folder.
 7. Add the following line to your **build.gradle** file:
`
dependencies 
{
	compile fileTree(dir: 'lib', includes: ['*.jar']) 
}
`
 8. See the Wiki for a *Getting Started* tutorial.

##Deploying a mod that uses RadixCore
If you'd like to use RadixCore in your mod, note that you should not distribute it with your mod. All users of your mod must download RadixCore separately from one of our official links on [our website](radix-shock.com) or [Curse](http://www.curse.com/mc-mods/Minecraft/radixcore).

##Contributing
Any contributions to RadixCore are welcome. Simply clone RadixCore into your workspace, set it up, make your changes, and submit a pull request for review.

##License
RadixCore utilizes a custom license, summarized below. The license is also available in-full within the repository.
#####Liability
We are not liable for any damages caused by this software.

#####Distribution
Only distribute RadixCore by linking to an official download or Curse. Do not create your own mirror links. Do not bypass our adf.ly links. Exemptions apply to modpacks.

#####Modpacks
RadixCore may be placed within a modpack without our consent, provided that the modpack is non-profit and does not generate revenue of any kind, including sales, website advertising, URL shorteners, donations, etc.

Absolutely no modifications can be made to RadixCore when it is placed in a modpack.

#####Source Code

The source code of RadixCore is made available for educational purposes only. Neither it, its source code, nor its byte code may be modified and recompiled for public use by anyone except us.

We do accept and encourage private modifications with the intent for said modifications to be added to the official public version.

Authors that submit modified code that is later added into the publically available build release all rights to the code under the terms that they are credited via in-code documentation and any other methods of credit/thanks deemed appropriate by the mod author.
