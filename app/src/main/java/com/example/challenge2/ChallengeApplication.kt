package com.example.challenge2

import android.app.Application

class ChallengeApplication : Application() {
    val noteDatabase by lazy { NoteDatabase.getDatabase(this).noteDao() }
}
