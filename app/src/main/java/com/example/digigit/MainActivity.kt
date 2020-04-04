package com.example.digigit

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    internal lateinit var myAuth:FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration

    //var LOGIN_CODE:String = "LOGGED_OUT"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)



        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_diary, R.id.nav_progress, R.id.nav_achievements,
                R.id.nav_profile, R.id.nav_settings, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)





    }

    fun checkLoginStatus(menu: Menu){
        var loggedInMenuItem = menu.findItem(R.id.action_login)
        var loggedOutMenuItem = menu.findItem(R.id.action_logout)


        if (myAuth.currentUser == null){
            loggedOutMenuItem.setVisible(false)
            loggedInMenuItem.setVisible(true)

        }else{
            loggedOutMenuItem.setVisible(true)
            loggedInMenuItem.setVisible(false)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        checkLoginStatus(menu)





        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun  onOptionsItemSelected(menuItem: MenuItem): Boolean{

       return when(menuItem.itemId){
           R.id.action_login->{

               //LOGIN_CODE = "LOGGED_IN"
               val homeIntent = Intent(this@MainActivity, LoginScreenActivity::class.java)
               startActivity(homeIntent)

               return false
           }
           R.id.action_logout->{
              // LOGIN_CODE = "LOGGED_OUT"
               FirebaseAuth.getInstance().signOut()
               val homeIntent = Intent(this@MainActivity, LoginScreenActivity::class.java)
               startActivity(homeIntent)
               Toast.makeText(applicationContext,"User logged out", Toast.LENGTH_LONG).show()
               return false
           }else->return super.onOptionsItemSelected(menuItem)
       } // val id = menuItem.getItemId()

        /*if (id == R.id.action_login){

            val homeIntent = Intent(this@MainActivity, LoginScreenActivity::class.java)
            startActivity(homeIntent)
            return false


        }
        if (id == R.id.action_logout){
            AuthUI.getInstance().signOut(this)
        }*/
        return super.onOptionsItemSelected(menuItem)
    }
}
