// COMP2150 take home exam
// Question 3
// Class: HockeyPlayer
// Author: Xian Mardiros, 7862786
"use strict"

class HockeyPlayer{
  #_name
  #_number
  #_goals

  constructor( name, number, goals ){
    if( this.constructor === HockeyPlayer ){
      throw "Cannot instantiate abstract class"
    }

    if( typeof name == "string" ){
      this.#_name = name
    }else{
      throw "All players must have names"
    }

    if( typeof number == "number" ){
      this.#_number = number
    }else{
      this.#_number = -1
    }

    if( typeof goals == "number" ){
      this.#_goals = goals
    }else{
      this.#_goals = 0
    }
  }

  get number(){
    return this.#_number
  }

  get name(){
    return this.#_name
  }

  get goals(){
    return this.#_goals
  }

  incGoals(){
    this.#_goals++
  }

  print(){
    console.log("Player Name: " + this.name)
    if( this.number != -1 ){
      console.log("Number: " + this.number)
    }
    console.log("Position: " + typeof this)
  }
}

module.exports = HockeyPlayer
