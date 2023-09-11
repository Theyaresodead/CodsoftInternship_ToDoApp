package com.example.todolist.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog.show
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.databinding.FragmentHomeBinding
import com.example.todolist.utils.ToDoData
import com.example.todolist.utils.TodoAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HomeFragment: Fragment(), AddTodoPopupFragment.DialogNextBtnClickListener,
    TodoAdapter.TodoAdapterClicksInterface {

  lateinit var auth:FirebaseAuth
  lateinit var databaseRef: DatabaseReference
  lateinit var navControl:NavController
  lateinit var binding:FragmentHomeBinding
  private var popupFragment: AddTodoPopupFragment?=null
  private lateinit var adapter:TodoAdapter
  private lateinit var list:MutableList<ToDoData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFireBase()
        registerEvents()
    }

    private fun getDataFromFireBase() {
        databaseRef.addValueEventListener(object:ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(tasksnapshot in snapshot.children) {
                    val todoTask = tasksnapshot.key?.let {
                         ToDoData(it,tasksnapshot.value.toString())
                    }
                    if(todoTask!=null) {
                        list.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun registerEvents() {
        binding.addBtnHome.setOnClickListener{
            if(popupFragment!=null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
     popupFragment= AddTodoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                AddTodoPopupFragment.TAG
            )
        }
    }

    private fun init(view: View) {
      navControl=Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        databaseRef= FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager=LinearLayoutManager(context)
        list= mutableListOf()
        adapter= TodoAdapter(list)
        adapter.setListener(this)
        binding.recyclerview.adapter=adapter
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
    databaseRef.push().setValue(todo).addOnCompleteListener{
        if(it.isSuccessful)
        {
            Toast.makeText(context,"Todo Saved Successfully",Toast.LENGTH_SHORT).show()

        }
        else{
            Toast.makeText(context,it.exception.toString(),Toast.LENGTH_SHORT).show()
        }
        todoEt.text=null
        popupFragment!!.dismiss()
    }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
                val map=HashMap<String,Any>()
        map[toDoData.taskId]=toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener{
            if(it.isSuccessful)
            {
                Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(context,it.exception.toString(),Toast.LENGTH_SHORT).show()
            }
            todoEt.text=null
            popupFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
      databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
          if(it.isSuccessful)
          {
              Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show()
          }
          else{
              Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
          }
      }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
         if(popupFragment!=null)
             childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
        popupFragment=AddTodoPopupFragment.newInstance(toDoData.taskId,toDoData.task)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager,AddTodoPopupFragment.TAG)
    }
}