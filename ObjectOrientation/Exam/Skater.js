// COMP2150 take home exam
// Question 3
// Class: Skater
// Author: Xian Mardiros, 7862786
"use strict"
import HockeyPlayer = require("./HockeyPlayer.js")

class Skater extends HockeyPlayer{

  constructor( name, number, goals ){
    super( name, number, goals )
  }

  print(){
    super.print()
    console.log("Goals: " + this.goals)
  }
}
