package dev.amin.curvybar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        curvyBar.init()

        curvyBar.setFabClickListener {
            Toast.makeText(this, "Hey Fella", Toast.LENGTH_LONG).show()
        }

        curvyBar.setFabIcon(R.drawable.ic_add_white_24dp)

        curvyBar.setMenu(R.menu.nav_menu)
    }
}
