package it.benche.upofood.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        var firebaseUser = FirebaseAuth.getInstance().currentUser

        var refreshToken: String? = FirebaseInstanceId.getInstance().token
        if(firebaseUser != null) {
            updateToken(refreshToken!!)
        }
    }

    private fun updateToken(refreshToken: String) {
        var firebaseUser = FirebaseAuth.getInstance().currentUser

        var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token = Token(refreshToken)
        reference.child(firebaseUser.uid).setValue(token)
    }
}