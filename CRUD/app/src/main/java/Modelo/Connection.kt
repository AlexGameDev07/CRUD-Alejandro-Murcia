package Modelo

import kotlinx.coroutines.handleCoroutineException
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager

class Connection {
    fun ExecuteConnect(): Connection? {
        try {
            val url="jdbc:oracle:thin:@192.168.1.24:1521:xe"
            val user="SYSTEM"
            val password="fakedrips"

            val connection=DriverManager.getConnection(url, user, password)

            return connection
        }catch (e: Exception){
            println("este es el error:$e")
            return null
        }
    }
}