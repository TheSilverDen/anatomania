package models

import scala.collection.mutable

object loginInMemory {

  private val users = mutable.Map[String, String]("Max" -> "passXY", "admin" -> "admin")

  def validateUser(username: String, password: String): Boolean = {
    users.get(username).map(_==password).getOrElse(false)     //works even when the username isn't in the map
                                                              //if users-Map doesn't contain username key > return false
  }

  def createUser(username: String, password: String): Boolean = {
    if (users.contains(username)) false else {
      users(username) = password
      true
    }
  }
}
