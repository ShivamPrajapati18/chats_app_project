package com.example.friendlychattingapp.authenticate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.friendlychattingapp.databinding.ActivityVerifyOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber=intent.getStringExtra("number").toString().trim()
        val verficationId=intent.getStringExtra("verificationId")
        binding.textView3.text="Please Type the Verfication Code sent to $phoneNumber"
        auth= FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            binding.progressBar.visibility= View.VISIBLE
            val code=binding.pinview.text.toString()
            val credential = PhoneAuthProvider.getCredential(verficationId!!, code)
            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    binding.progressBar.visibility= View.GONE
                    if (it.isSuccessful){
                        Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                        val intent= Intent(this, SetupProfile::class.java)
                        intent.putExtra("number","+91$phoneNumber")
                        startActivity(intent)
                    }else
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
        }

    }
}