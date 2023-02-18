// COMP2150 take home exam
// Question 3
// Class: Goalie
// Author: Xian Mardiros, 7862786
"use strict"
import HockeyPlayer = require("./HockeyPlayer.js")

class Goalie extends HockeyPlayer{
  #_saves

  constructor( name, number, goals, saves ){
    super( name, number, goals )
    if( typeof saves == "number" ){
      this.#_saves = saves
    }
  }

  get saves(){
    return this.#_saves
  }

  incSaves(){
    this.#_saves++
  }

  savePercentage(){
    return 100*this.saves/(this.saves + this.goals)
  }
  
  print(){
    super.print()
    console.log("Save Percentage: " + this.savePercentage() + "%")
  }
}


