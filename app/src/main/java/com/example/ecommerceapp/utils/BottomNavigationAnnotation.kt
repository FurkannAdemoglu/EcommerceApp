package com.example.netflixcloneapp.utils

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
annotation class BottomNavigationAnnotation(
    val menuItemId: Int = -1,
)
