package com.cornershop.counterstest.common

import android.content.Context
import android.content.Intent
import javax.inject.Inject

class ShareHelper @Inject constructor(){
    fun share(content: String, mimeType: String, context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = mimeType
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}