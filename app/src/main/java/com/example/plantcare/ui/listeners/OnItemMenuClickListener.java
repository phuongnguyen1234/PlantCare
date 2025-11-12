package com.example.plantcare.ui.listeners;

public interface OnItemMenuClickListener<T> {
    void onEditClicked(T item);
    void onDeleteClicked(T item);
}
