package com.example.dolpjinjunior.utils

class Config {
    companion object {
        var USER_TOKEN : String ? = null
        var STATUS_BUG : Int = 0
        var SECRET_KEY : String = "USER_TOKEN"
        var GRAPHQL_URI : String = "https://api-pkarn.azurewebsites.net/"
        //var GRAPHQL_URI : String = "https://api-for-pkarn-bunyawat37204-dev.apps.sandbox.x8i5.p1.openshiftapps.com/"
        var FORMAT_DATE : String = "dd/MM/yyyy"
    }
}