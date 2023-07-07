package eu.tutorials.cyberwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import eu.tutorials.cyberwatch.fireStoreApi.ClientHelper
import eu.tutorials.cyberwatch.fireStoreApi.LawyerHelper
import eu.tutorials.cyberwatch.fireStoreApi.UserHelper

class FirstSignUpActivity : AppCompatActivity() {

    private val TAG = "FirstSigninActivity"
    private lateinit var fullName: EditText
    private lateinit var addres: EditText
    private lateinit var teL: EditText
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_sign_up)

        btn = findViewById(R.id.confirmBtn)
        fullName = findViewById(R.id.firstSignFullName)
        addres = findViewById(R.id.firstSignAdd)
        teL = findViewById(R.id.firstSignTel)

        val spinner = findViewById<Spinner>(R.id.spinner)
        val specialiteList = findViewById<Spinner>(R.id.specialite_spinner)
        val newAccountType = spinner.selectedItem.toString()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = spinner.selectedItem.toString()
                Log.e(TAG, "onItemSelected:$selected")
                if (selected == "Lawyer") {
                    specialiteList.visibility = View.VISIBLE
                } else {
                    specialiteList.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                specialiteList.visibility = View.GONE
            }
        }
        btn.setOnClickListener {
            val fullname: String
            val address: String
            val tel: String
            val type: String
            val specialite: String
            fullname = fullName.text.toString()
            address = addres.text.toString()
            tel = teL.text.toString()
            type = spinner.selectedItem.toString()
            specialite = specialiteList.selectedItem.toString()
            val userHelper = UserHelper()
            userHelper.addUser(fullname, address, tel, type)

            if (type == "Client") {
                val clientHelper = ClientHelper()
                clientHelper.addClient(fullname, address, tel)
                println("Add client $fullname to client collection")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                val lawyerHelper = LawyerHelper()
                lawyerHelper.addLawyer(fullname, address, tel, specialite)
                val intent = Intent(this, LawyerHome::class.java)
                startActivity(intent)
            }
            //val intent = Intent(this, HomeActivity::class.java)
            //startActivity(intent)
        }

    }
}