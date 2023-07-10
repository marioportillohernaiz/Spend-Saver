<!doctype html>
<html lang="en">
  <head>
	<!-- Required meta tags-->
	<meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	
    <title>SpenderSaver</title>
	<link rel="icon" type="image/x-icon" href="img/logo.png">
	
	<!-- TailWind CSS -->
	<script src="https://cdn.tailwindcss.com"></script>
	
	<!-- JQuerry -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

    <!-- Personal CSS -->
	<link href="csspage.css" rel="stylesheet" >
	
	<!-- Fonts -->
	<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'>
  </head>
  
  
<body>

<main>
<div class="w-ful mainColor">
	<div class="grid grid-cols-1 md:grid-cols-3">

		<div class="px-q flex-1 col-span-2 flex my-10">
			<h1 class="max-md:text-4xl md:text-5xl font-bold tracking-wide pageTitle">SpendSaver User Guide</h1>
		</div>
	</div>
	
	<div class="flex items-center justify-center py-4 navHover">
        <button class="max-md:px-3 md:px-5 activelink" id="suButton" onclick="displayData('0')">Starting Up</button>
		<button class="max-md:px-3 md:px-5" id="homeButton" onclick="displayData('1')">Home Screen</button>
		<button class="max-md:px-3 md:px-5" id="shopButton" onclick="displayData('2')">Shopping Screen</button>
		<button class="max-md:px-3 md:px-5" id="accButton" onclick="displayData('3')">Account Screen</button>
	</div>
</div>


<div class="w-ful secColor">
	<div id="startUp" style="display:block;" class="md:p-10">
		<div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">On Start-Up</p>
			<div class="flex">
				<img class="pageImg" src="img/permissionImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>On start-up, the app will prompt you to allow storage permissions.</li>
                    <li>This data can include user account information, most recent shop input as well as 
                        the type of currency used. Without storage permissions, the app would not be able to 
                        save this data locally on the device. Additionally, storage permissions are required 
                        for the app to allow a quick access to the app by saving the most recent logged user. 
                    </li>
                </ul>
			</div>
		</div>

        <div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Log In and Sign Up</p>
			<div class="flex my-5">
				<img class="pageImg" src="img/loginImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>Authentication in the app requires a username and password.</li>
                    <li>If it is your first time on the app, you can click the "Sign Up" button
                        to create an account.
                    </li>
                </ul>
			</div>
            <div class="flex">
				<img class="pageImg" src="img/signupImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>When creating an account, you will be asked to input a username and a password. Make
                        sure the username is resonable and the password strong.
                    </li>
                    <li>You will be also asked to enter either a "Personal" or "Family" account type. 
                        Choosing a Personal account type means it will be used only for yourself. On the 
                        contrary, if you select a Family account type, it will mean you will be sharing 
                        the account with other devices.
                    </li>
                </ul>
			</div>
		</div>
	</div>

	<div id="home" style="display:none;" class="md:p-10">
		<div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Home Screen</p>
			<div class="flex my-5">
				<img class="pageImg" src="img/homeImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>Once logged in, you will have access to your account and your data. The following
                        image displays dummy data. The home screen contains a graph displaying the percentage
                        of each item, a list of each item with their respective cost, a navigation bar at the 
                        bottom of the screen, and 3 buttons which (left to right):
                    </li>
                    <li>1. Displays information regarding each item type (which will be explained in the "Add Shop"
                        screen).</li>
                    <li>2. Opens a screen displaying every shop that has been saved by the user.</li>
                    <li>3. Opens a screen which displays all data by the year.</li>
                </ul>
			</div>
            <div class="flex">
				<img class="my-5" src="img/homerotateImg.JPG" style="height:180px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>Home screen also allows a rotating screen.</li>
                </ul>
			</div>
		</div>

        <div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Info Pop-up</p>
			<div class="flex">
				<img class="pageImg" src="img/infoImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>As mentioned above, the following pop-up will contain information usefull to the 
                        user which explains how the data is stored and displayed.
                    </li>
                </ul>
			</div>
		</div>

        <div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Data Screen</p>
			<div class="flex">
				<img class="pageImg" src="img/dataImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>The following screen will display a list of all the shops saved by the user.
                        The list each item with their cost respetively along with the date they were bought in.
                    </li>
                </ul>
			</div>
		</div>

        <div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Yearly Data Screen</p>
			<div class="flex my-5">
				<img class="pageImg" src="img/yearly2Img.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>The following screen will display a bar graph containing the expenses of each 
                        montha as well as a list with the sum of each item type.
                    </li>
                    <li>The arrows at the top of the graph, to each side, allows the user to click through
                        each year to view past data. When holding down either button, it will rapidly scroll 
                        through the years.
                    </li>
                </ul>
			</div>
            <div class="flex">
				<img class="my-5" src="img/yearlyrotateImg.JPG" style="height:180px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>Yearly Data screen also allows a rotating screen.</li>
                </ul>
			</div>
		</div>
	</div>

	<div id="shop" style="display:none;" class="md:p-10">
		<div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Add Shop Screen</p>
			<div class="flex my-5">
				<img class="pageImg" src="img/addImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 pt-4">
                    <li>In the following screen, you will be able to input your shopping list. Each button 
                        (from left to right) does the following:
                    </li>
                    <li>1. "Add new grocery type" button allows the user to add a new type of grocery
                        which they can then select from when adding data. The grocery types saved in the app 
                        are very generic, for this reason this functionality was implemented.
                    </li>
                    <li>2. "Add new item" button adds a new row to the screen for the user to add items from 
                        their shopping list. As shown in the image bellow, the list of grocery types will pop up
                        when clicking on the "Item name" input. The next input will be the number of items for that 
                        one grocery type and the next will be the value of a single item in that grocery type. For 
                        example, if you buy x2 of a £5 of "Fruit and Veg" then the inputs will be as follows:
                        "Fruit and Veg", 2, 5. The button next to these inputs deletes the row.
                    </li>
                    <li>3. "Save Shop" saves the shop to a cloud database.</li>
                </ul>
			</div>
            <div class="flex my-5">
				<img class="pageImg" src="img/addItemImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>When clicking the "Item name" input, the list of grocery types pops up.</li>
                </ul>
			</div>
            <div class="flex">
				<img class="pageImg" src="img/notImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>If the user adds a shop which is over the £100 threshold, he will recieve a notification
                        to warn him from spending too much in one day.</li>
                </ul>
			</div>
		</div>

        <div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Grocery Types</p>
			<div class="flex">
				<img class="pageImg" src="img/groceryTypeImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>When clicking the "Add grocery type", the following pop-up will be displayed where you
                        can input a custom grocery type.
                    </li>
                </ul>
			</div>
		</div>
	</div>


	<div id="account" style="display:none;" class="md:p-10">
		<div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Account Screen</p>
			<div class="flex my-5">
				<img class="pageImg" src="img/accountImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>The account screen displays data from the user such as their username, account type and
                        currency. The user can change the currency from the list of different currencies available.
                    </li>
                    <li>The button "How to save money" will take you to a website wich will explain in depth
                        step to step how to save and manage money.
                    </li>
                    <li>As the name explains, the button "Log Out" will log you out and take you to the log in 
                        screen.
                    </li>
                </ul>
			</div>
            <div class="flex">
				<img class="my-5" src="img/accountrotateImg.JPG" style="height:180px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>Account Screen also allows a rotating screen.</li>
                </ul>
			</div>
		</div>

        <div class="divOutline education">	
            <p class="divTitle flex-1 font-bold pb-4">Sharing Data</p>
			<div class="flex">
				<img class="pageImg" src="img/shareImg.JPG" style="width:250px;">
            </div>
            <div class="flex">
                <ul class="pl-5 py-4">
                    <li>The Share Action Provider on the top right corner of the account screen can be used 
                        to share the app to other apps.
                    </li>
                </ul>
			</div>
		</div>
	</div>
</div>

<script>
	function displayData(table){
		shopButton.classList.remove("activelink");
		if (table === "1") {
            startUp.style.display = "none";
			home.style.display = "block";
			shop.style.display = "none";
			account.style.display = "none";
			
            suButton.classList.remove("activelink");
			homeButton.classList.add("activelink");
			shopButton.classList.remove("activelink");
			accButton.classList.remove("activelink");
		} else if (table === "2") {
            startUp.style.display = "none";
			home.style.display = "none";
			shop.style.display = "block";
			account.style.display = "none";
			
            suButton.classList.remove("activelink");
			homeButton.classList.remove("activelink");
			shopButton.classList.add("activelink");
			accButton.classList.remove("activelink");
		} else if (table === "3") {
            startUp.style.display = "none";
			home.style.display = "none";
			shop.style.display = "none";
			account.style.display = "block";
			
            suButton.classList.remove("activelink");
			homeButton.classList.remove("activelink");
			shopButton.classList.remove("activelink");
			accButton.classList.add("activelink");
		} else {
            startUp.style.display = "block";
            home.style.display = "none";
			shop.style.display = "none";
			account.style.display = "none";
			
            suButton.classList.add("activelink");
			homeButton.classList.remove("activelink");
			shopButton.classList.remove("activelink");
			accButton.classList.remove("activelink");
        }
	}
</script>
</main>

<div class="w-ful mainColor">
	<footer class="flex justify-between p-5 navHover">
		<a href="#" class="activelink navHover md:px-10">Back to top</a>
	</footer>
</div>
</body>
</html>
