// FILE: Main
//
// Author: Xian Maridors, 7862786
//
// REMARKS: Main file of simulator, almost
//          entirely unchanged from provided version
//
// -------------------------------------------


#include "Simulation.h"
#include <iostream>
using namespace std;

int main(int argc, char *argv[]){
  if(argc < 2){
    cout << "Usage: " << argv[0] << " SpecificationFile InputFile \n";
    exit(1);
  }
  cout << "Simulation begins...\n";
  Simulation* sim = new Simulation();
  // run simulation
  cout << "starting simulation." << endl;
  sim->runSimulation(argv[1]);
  cout << "\n...All Assembly complete.  Final Summary:\n\n";
  sim->printStats();
  cout << "\nEnd of processing.\n";
  return 0;
}// main
