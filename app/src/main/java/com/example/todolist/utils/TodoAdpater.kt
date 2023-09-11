package com.example.todolist.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.EachItemTodoBinding

class TodoAdapter(private val list:MutableList<ToDoData>) :
RecyclerView.Adapter<TodoAdapter.ToDoViewHolder>(){

    private var listener:TodoAdapterClicksInterface?=null
    fun setListener(listener:TodoAdapterClicksInterface){
        this.listener=listener
    }
    inner class ToDoViewHolder(val binding: EachItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding=EachItemTodoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
       with(holder){
           with(list[position])
           {
              binding.todoTask.text=this.task
               binding.deleteTask.setOnClickListener{
                   listener?.onDeleteTaskBtnClicked(this)
               }
               binding.editTask.setOnClickListener {
                   listener?.onEditTaskBtnClicked(this)
               }
           }
       }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface TodoAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)
    }

}