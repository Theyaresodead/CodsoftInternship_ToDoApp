package com.example.todolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todolist.R
import com.example.todolist.databinding.FragmentSignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


class SignUpFragment : Fragment() {

   private lateinit var auth: FirebaseAuth
   private lateinit var  navControl: NavController
   private lateinit var binding : FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSignUpBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {

        binding.authtextView.setOnClickListener{
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
      binding.nextbtn.setOnClickListener{
          val email=binding.emailET.text.toString().trim()
          val pass=binding.passwordET.text.toString().trim()
          val verifypass=binding.retypepassword.text.toString().trim()
          if(email.isNotEmpty() && pass.isNotEmpty() && verifypass.isNotEmpty())
          {
              if(pass.equals(verifypass))
              {
                  auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                      OnCompleteListener {
                          if(it.isSuccessful)
                          {
                              Toast.makeText(context,"Registered successfully", Toast.LENGTH_SHORT).show()
                              navControl.navigate(R.id.action_signUpFragment_to_homeFragment2)
                          }
                          else
                          {
                              Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
                          }
                      }
                  )
              }
              else
              {
                  Toast.makeText(context,"Password doesn't match",Toast.LENGTH_SHORT).show()
              }
          }
      }
    }

    private fun init(view: View) {
        navControl=Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()

    }
}