package com.unipi.chrisakar.mycalculatorapp

import android.os.Bundle
import android.security.NetworkSecurityPolicy
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*
import java.io.IOException

data class ApiResponse(
        val success: Boolean,
        val symbols: Map<String, String>
)


class MainActivity : AppCompatActivity() {
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // running a call to initialize sumbols
        val urlForCall: String = "http://data.fixer.io/api/symbols?access_key=" + getString(R.string.api_key)
        run(urlForCall)

        // url to get currencies
        val urlForConvert: String = "http://data.fixer.io/api/latest?access_key=" + getString(R.string.api_key)

        // myInstance will be used each time a button is pressed on the calculator
        val myInstance = CalculatorFunction()

        val myText: TextView = super.findViewById(R.id.textViewResult)

        val delButton: ImageButton = super.findViewById(R.id.delButton)
        val button1: Button = super.findViewById(R.id.button1)
        val button2: Button = super.findViewById(R.id.button2)
        val button3: Button = super.findViewById(R.id.button3)
        val button4: Button = super.findViewById(R.id.button4)
        val button5: Button = super.findViewById(R.id.button5)
        val button6: Button = super.findViewById(R.id.button6)
        val button7: Button = super.findViewById(R.id.button7)
        val button8: Button = super.findViewById(R.id.button8)
        val button9: Button = super.findViewById(R.id.button9)
        val button0: Button = super.findViewById(R.id.button0)
        val plus: Button = super.findViewById(R.id.plus)
        val minus: Button = super.findViewById(R.id.minus)
        val divide: Button = super.findViewById(R.id.divide)
        val eq: Button = super.findViewById(R.id.eq)
        val mult: Button = super.findViewById(R.id.mult)
        val decimalPoint: Button = super.findViewById(R.id.decimalPoint)
        val convertButton: Button = super.findViewById(R.id.convertButton)

        //setting up on click listeners for all the buttons

        convertButton.setOnClickListener {
            val maybeDouble = myText.text.toString().toDoubleOrNull()
            if (maybeDouble != null) {
                runCurrenciesCall(urlForConvert, maybeDouble)
            } else {
                Toast.makeText(this, "Incorrect number format.", Toast.LENGTH_SHORT).show()

            }

        }


        delButton.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('~'))
        }

        button1.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('1'))
        }

        button2.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('2'))
        }

        button3.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('3'))
        }

        button4.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('4'))
        }

        button5.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('5'))
        }

        button6.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('6'))
        }

        button7.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('7'))
        }

        button8.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('8'))
        }

        button9.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('9'))
        }

        button0.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('0'))
        }

        plus.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('+'))
        }

        minus.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('-'))
        }

        divide.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('/'))
        }

        eq.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('='))
        }

        mult.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('*'))
        }

        decimalPoint.setOnClickListener {
            myText.setText(myInstance.CheckLastInput('.'))
        }
    }

    fun run(url: String) {

        val request = Request.Builder().url(url).build()
        val mSpinner: Spinner = super.findViewById(R.id.simple_spinner_item)
        val mSpinner2: Spinner = super.findViewById(R.id.simple_spinner_item2)

        val options = ArrayList<String>()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@MainActivity, "Call Failed.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {

                        val gson = Gson()
                        val apiResponse = gson.fromJson(response.body?.string(), ApiResponse::class.java)

                        // Checking if the response was successful
                        if (apiResponse.success) {

                            // symbols map
                            val symbolsMap = apiResponse.symbols

                            // iterating over the symbols map to access each symbol
                            for ((key, value) in symbolsMap) {

                                options.add("$key - $value")
                            }

                        } else {
                            // unsuccessful response
                            Toast.makeText(this@MainActivity, "Unsuccessful response.", Toast.LENGTH_SHORT).show()
                        }


                    } else {
                        // unsuccessful response
                        Toast.makeText(this@MainActivity, "Unsuccessful response: ${response.code}", Toast.LENGTH_SHORT).show()

                    }
                } catch (e: IOException) {

                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()

                }

                runOnUiThread {
                    //updating ui based on the response
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, options)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    mSpinner.setAdapter(adapter)
                    mSpinner2.setAdapter(adapter)
                }
            }
            //end
        })
    }

    fun runCurrenciesCall(url: String, numToConvert: Double) {

        val myText2: TextView = super.findViewById(R.id.textView2)
        val request = Request.Builder().url(url).build()

        //selected currencies to convert
        val mSpinner: Spinner = super.findViewById(R.id.simple_spinner_item)
        val text: String = mSpinner.getSelectedItem().toString()
        val splittedText = text.split(" - ")

        val mSpinner2: Spinner = super.findViewById(R.id.simple_spinner_item2)
        val text2: String = mSpinner2.getSelectedItem().toString()
        val splittedText2 = text2.split(" - ")

        if (text != null && text2 != null) {
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                myText2.setText("failure")
                Toast.makeText(this@MainActivity, "Request failed.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body

                        // Checking if the response body is not null
                        if (responseBody != null) {
                            val jsonString = responseBody.string()

                            // processing json String
                            val jsonElement = JsonParser.parseString(jsonString)
                            if (jsonElement.isJsonObject) {

                                val jsonObject = jsonElement.asJsonObject

                                // Check if the "rates" field exists
                                if (jsonObject.has("rates") && jsonObject["rates"].isJsonObject) {
                                    val ratesObject = jsonObject.getAsJsonObject("rates")

                                    // Accessing the values for base and result
                                    val baseRate = ratesObject.getAsJsonPrimitive(splittedText[0]).asDouble
                                    val ResultRate = ratesObject.getAsJsonPrimitive(splittedText2[0]).asDouble
                                    myText2.setText(convertAmount(baseRate, ResultRate, numToConvert).toString())

                                } else {
                                    // missing or invalid "rates"
                                    Toast.makeText(this@MainActivity, "Error retrieving rates.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // unexpected JSON format
                                Toast.makeText(this@MainActivity, "Error retrieving json.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // null response body
                            Toast.makeText(this@MainActivity, "Error no rates found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // unsuccessful response
                        Toast.makeText(this@MainActivity, "Error unsuccessful response.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // exceptions
                    Toast.makeText(this@MainActivity, "An error has occurred.", Toast.LENGTH_SHORT).show()
                } finally {
                    // closing the response body in a finally block
                    response.body?.close()
                }
            }
        })} else {
            Toast.makeText(this@MainActivity, "Please select currencies.", Toast.LENGTH_SHORT).show()
        }

    }

    fun convertAmount(baseToEuro: Double, resultRateToEuro: Double, baseAmount: Double): Double {

        // Function to convert from base to result
        fun convertBaseToCurrency(baseAmount: Double): Double {
            return baseAmount * (resultRateToEuro / baseToEuro)
        }

        val resultAmount = convertBaseToCurrency(baseAmount)

        return resultAmount
    }




}