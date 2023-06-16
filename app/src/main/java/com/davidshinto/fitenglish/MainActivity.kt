package com.davidshinto.fitenglish

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.davidshinto.fitenglish.databinding.ActivityMainBinding
import com.davidshinto.fitenglish.utils.CategoryList
import com.davidshinto.fitenglish.utils.WidthProvider
import com.davidshinto.fitenglish.utils.Word
import com.davidshinto.fitenglish.utils.WordList
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await


class MainActivity : AppCompatActivity(), WidthProvider {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNav()
        FirebaseApp.initializeApp(this)

        loadDataFromDB()
    }

    private fun setupNav() {
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_finder,
//                R.id.navigation_cards,
                R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        navView.setupWithNavController(navController)
        setupSelectedFragmentIcon(navController)
    }

    private fun setupSelectedFragmentIcon(navController: NavController) {
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id != navView.selectedItemId) {
                controller.backQueue.asReversed().drop(1).forEach { entry ->
                    navView.menu.forEach { item ->
                        if (entry.destination.id == item.itemId) {
                            item.isChecked = true
                            return@addOnDestinationChangedListener
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    }

    /**
     * Returns the screen width in pixels
     * @return <int> screen width
     */

    override fun getWidth(): Int {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsetsCompat.Type.systemBars()
            )
            displayMetrics.widthPixels = windowMetrics.bounds.width() - insets.left - insets.right
        } else { //earlier version of Android
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics.widthPixels
    }


    /**
     * Loads data from Firebase database
     */

    fun loadDataFromDB()
    {
        val database =
            FirebaseDatabase.getInstance("https://fitenglishdb-default-rtdb.europe-west1.firebasedatabase.app/")
        val dbRefCategory = database.getReference("wordList/")

        dbRefCategory.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val data = snapshot.key
                    data?.let { dataList.add(it) }
                }
                CategoryList.categoryList = dataList

                CategoryList.categoryList.forEach {category ->
                    val dbRefWord = database.getReference("wordList/${category}/")

                    dbRefWord.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val dataList = mutableListOf<Word>()
                            for (snapshot in dataSnapshot.children) {
                                val data = snapshot.getValue(Word::class.java)
                                data?.let { dataList.add(it) }
                            }
                            dataList.sortBy { it.engName }
                            WordList.wordList += dataList
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(applicationContext,"An error happened while getting database data: $databaseError",
                                Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,"$databaseError",
                    Toast.LENGTH_SHORT).show()
                Toast.makeText(
                    applicationContext,
                    "An error happened while getting database data: $databaseError",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Loads data from file and saves it to database
     */

    fun SaveDataToDBFromFile(filePath : String) {
        var categoryName: String = ""
        var wordList: String = "Numbers:\n" +
                "jeden-one\n" +
                "dwa-two\n" +
                "trzy-three\n" +
                "cztery-four\n" +
                "pięć-five\n" +
                "sześć-six\n" +
                "siedem-seven\n" +
                "osiem-eight\n" +
                "dziewięć-nine\n" +
                "dziesięć-ten\n" +
                "jedenaście-eleven\n" +
                "dwanaście-twelve\n" +
                "trzynaście-thirteen\n" +
                "czternaście-fourteen\n" +
                "piętnaście-fifteen\n" +
                "szesnaście-sixteen\n" +
                "siedemnaście-seventeen\n" +
                "osiemnaście-eighteen\n" +
                "dziewiętnaście-nineteen\n" +
                "dwadzieścia-twenty\n" +
                "Colors:\n" +
                "czerwony-red\n" +
                "niebieski-blue\n" +
                "zielony-green\n" +
                "żółty-yellow\n" +
                "czarny-black\n" +
                "biały-white\n" +
                "szary-gray\n" +
                "brązowy-brown\n" +
                "różowy-pink\n" +
                "fioletowy-purple\n" +
                "pomarańczowy-orange\n" +
                "złoty-gold\n" +
                "srebrny-silver\n" +
                "jasny-light\n" +
                "ciemny-dark\n" +
                "pastelowy-pastel\n" +
                "neonowy-neon\n" +
                "metaliczny-metallic\n" +
                "turkusowy-turquoise\n" +
                "oliwkowy-olive\n" +
                "Animals:\n" +
                "pies-dog\n" +
                "kot-cat\n" +
                "ptak-bird\n" +
                "ryba-fish\n" +
                "kon-horse\n" +
                "królik-rabbit\n" +
                "małpa-monkey\n" +
                "lew-lion\n" +
                "tygrys-tiger\n" +
                "niedźwiedź-bear\n" +
                "zebra-zebra\n" +
                "słon-elephant\n" +
                "kangur-kangaroo\n" +
                "żyrafa-giraffe\n" +
                "wąż-snake\n" +
                "jeleń-deer\n" +
                "lis-fox\n" +
                "owca-sheep\n" +
                "kura-chicken\n" +
                "krowa-cow\n" +
                "Fruits:\n" +
                "jabłko-apple\n" +
                "banan-banana\n" +
                "pomarańcza-orange\n" +
                "truskawka-strawberry\n" +
                "winogrono-grape\n" +
                "ananas-pineapple\n" +
                "cytryna-lemon\n" +
                "arbuz-watermelon\n" +
                "malina-raspberry\n" +
                "brzoskwinia-peach\n" +
                "gruszka-pear\n" +
                "kiwi-kiwi\n" +
                "jagoda-blueberry\n" +
                "mandarynka-mandarin\n" +
                "czereśnia-cherry\n" +
                "mango-mango\n" +
                "śliwka-plum\n" +
                "agrest-currant\n" +
                "figa-fig\n" +
                "granat-pomegranate\n" +
                "Vegetables:\n" +
                "marchewka-carrot\n" +
                "pomidor-tomato\n" +
                "ogórek-cucumber\n" +
                "kapusta-cabbage\n" +
                "ziemniak-potato\n" +
                "cebula-onion\n" +
                "papryka-bell pepper\n" +
                "brokuł-broccoli\n" +
                "fasola-beans\n" +
                "kalafior-cauliflower\n" +
                "szpinak-spinach\n" +
                "marchew-parsnip\n" +
                "dynia-pumpkin\n" +
                "cukinia-zucchini\n" +
                "sałata-lettuce\n" +
                "rzodkiewka-radish\n" +
                "burak-beetroot\n" +
                "por-leek\n" +
                "seler-celery\n" +
                "kapusta brukselska-brussels sprouts\n" +
                "Common phrases:\n" +
                "dzień dobry-good morning/afternoon\n" +
                "do widzenia-goodbye\n" +
                "proszę-please\n" +
                "dziękuję-thank you\n" +
                "przepraszam-excuse me, sorry\n" +
                "witam-hello\n" +
                "cześć-hi\n" +
                "miło mi cię poznać-nice to meet you\n" +
                "na zdrowie-bless you\n" +
                "dobranoc-goodnight\n" +
                "witaj-welcome\n" +
                "wszystkiego najlepszego-happy birthday\n" +
                "powodzenia-good luck\n" +
                "nie ma sprawy-you're welcome\n" +
                "gratulacje-congratulations\n" +
                "przepraszam za spóźnienie-sorry for being late\n" +
                "żegnaj-farewell\n" +
                "do zobaczenia-see you later\n" +
                "na razie-bye for now\n" +
                "powitanie-greeting\n" +
                "Family:\n" +
                "matka-mother\n" +
                "ojciec-father\n" +
                "siostra-sister\n" +
                "brat-brother\n" +
                "dziadek-grandfather\n" +
                "babcia-grandmother\n" +
                "córka-daughter\n" +
                "syn-son\n" +
                "wnuk-grandson\n" +
                "wnuczka-granddaughter\n" +
                "wujek-uncle\n" +
                "ciocia-aunt\n" +
                "kuzyn-cousin\n" +
                "rodzina-family\n" +
                "mąż-husband\n" +
                "żona-wife\n" +
                "rodzice-parents\n" +
                "dzieci-children\n" +
                "bratanek-nephew\n" +
                "siostrzeniec-nephew\n" +
                "kuzynka-cousin\n" +
                "Food:\n" +
                "chleb-bread\n" +
                "mleko-milk\n" +
                "kawa-coffee\n" +
                "herbata-tea\n" +
                "woda-water\n" +
                "sok-juice\n" +
                "wino-wine\n" +
                "piwo-beer\n" +
                "jajko-egg\n" +
                "mięso-meat\n" +
                "ryż-rice\n" +
                "ziemniaki-potatoes\n" +
                "makaron-pasta\n" +
                "deser-dessert\n" +
                "cukier-sugar\n" +
                "mąka-flour\n" +
                "oliwa-olive oil\n" +
                "ser-cheese\n" +
                "warzywa-vegetables\n" +
                "owoce-fruits\n" +
                "Places:\n" +
                "dom-house\n" +
                "szkoła-school\n" +
                "sklep-shop\n" +
                "restauracja-restaurant\n" +
                "park-park\n" +
                "biblioteka-library\n" +
                "szpital-hospital\n" +
                "kino-cinema\n" +
                "plaża-beach\n" +
                "muzeum-museum\n" +
                "teatr-theater\n" +
                "lotnisko-airport\n" +
                "stacja kolejowa-train station\n" +
                "ratusz-town hall\n" +
                "kościół-church\n" +
                "kawiarnia-cafe\n" +
                "stadion-stadium\n" +
                "gospoda-pub\n" +
                "zoo-zoo\n" +
                "basen-swimming pool\n" +
                "Jobs:\n" +
                "lekarz-doctor\n" +
                "nauczyciel-teacher\n" +
                "inżynier-engineer\n" +
                "prawnik-lawyer\n" +
                "programista-programmer\n" +
                "pielęgniarka-nurse\n" +
                "kierowca-driver\n" +
                "sprzedawca-salesperson\n" +
                "kelner-waiter\n" +
                "mechanik-mechanic\n" +
                "projektant-designer\n" +
                "architekt-architect\n" +
                "pilot-pilot\n" +
                "artysta-artist\n" +
                "pilot samolotu-airline pilot\n" +
                "weterynarz-veterinarian\n" +
                "fryzjer-hairdresser\n" +
                "dentysta-dentist\n" +
                "listonosz-postman\n" +
                "policjant-policeman\n" +
                "Travel:\n" +
                "lotnisko-airport\n" +
                "hotel-hotel\n" +
                "plaża-beach\n" +
                "zwiedzanie-sightseeing\n" +
                "bagaż-luggage\n" +
                "paszport-passport\n" +
                "bilet-ticket\n" +
                "przewodnik-guide\n" +
                "podróż-journey\n" +
                "wycieczka-excursion\n" +
                "samolot-airplane\n" +
                "pociąg-train\n" +
                "autostrada-highway\n" +
                "podróżnik-traveler\n" +
                "bagażnik-trunk\n" +
                "kemping-camping\n" +
                "zwiedzać-to explore\n" +
                "rezerwacja-reservation\n" +
                "bagażowy-baggage handler\n" +
                "wiza-visa\n" +
                "Hobby:\n" +
                "czytanie-reading\n" +
                "kino-cinema\n" +
                "muzyka-music\n" +
                "sport-sports\n" +
                "gotowanie-cooking\n" +
                "pisanie-writing\n" +
                "malowanie-painting\n" +
                "fotografia-photography\n" +
                "ogród-garden\n" +
                "podróżowanie-traveling\n" +
                "tańczenie-dancing\n" +
                "gra w golfa-playing golf\n" +
                "wspinaczka-rock climbing\n" +
                "wędkarstwo-fishing\n" +
                "teatr-theater\n" +
                "gra na instrumentach-playing musical instruments\n" +
                "jazda na rowerze-cycling\n" +
                "grzybobranie-mushroom picking\n" +
                "astronomia-astronomy\n" +
                "kolarstwo-cycling\n" +
                "Common Verbs:\n" +
                "być-to be\n" +
                "mieć-to have\n" +
                "robić-to do, to make\n" +
                "iść-to go\n" +
                "widzieć-to see\n" +
                "słuchać-to listen\n" +
                "mówić-to speak\n" +
                "czytać-to read\n" +
                "pisać-to write\n" +
                "myśleć-to think\n" +
                "rozumieć-to understand\n" +
                "uczyć się-to learn\n" +
                "pomagać-to help\n" +
                "pracować-to work\n" +
                "podróżować-to travel\n" +
                "kupować-to buy\n" +
                "sprzedawać-to sell\n" +
                "jeść-to eat\n" +
                "pić-to drink\n" +
                "spać-to sleep"
        val database =
            FirebaseDatabase.getInstance("https://fitenglishdb-default-rtdb.europe-west1.firebasedatabase.app/")

        wordList.split("\n").forEach {
            if (it.contains(":")) categoryName = it.replace(":", "")
            else {
                val word = it.split("-")
                val dbRef = database.getReference("wordList/${categoryName}/${word[1]}")
                if (!word[0].isEmpty()) dbRef.setValue(Word(word[1], word[0], categoryName))
            }
        }
    }

    /**
     * Gets current user location
     *
     * @return <Location> current user location
     */

    suspend fun getLocationFromApi(): Location? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (isLocationPermissionGranted()) {
            return getLastKnownLocation()
        } else {
            requestLocationPermission()
        }
        return null
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }

    private suspend fun getLastKnownLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        try {
            return fusedLocationClient.lastLocation.await()
        } catch (exception: Exception) {
            Log.e("GG", "Error getting last known location: ${exception.message}")
        }
        return null
    }
}