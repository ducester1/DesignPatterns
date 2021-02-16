void mapStart() {
  String _map="MAP "+level+"!";
  
  if(!campaignMode){
    genModeMapStart();
    return;
  }
  image(bg,0,0);
  image(ruta1, 0, -8);
  image(portrait1, int(scaledScrWidth*0.85), int(scaledScrHeight*0.12) );
  scale(2);
  float opponentHeight = 0.77;
  if (millis()<pauseTimer) {
    switch (level){
      case 1:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(action_army, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("ARMY, COST "+COST_ARMY+" GOLD:", int(scaledScrWidth*0.45), int(scaledScrHeight*0.30) );
        printShaded("LETS YOU ATTACK AN ADJACENT AREA.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded("EACH AREA GIVES YOU 1 GOLD, BUT\nNOT THE TURN AFTER BEING CONQUERED.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.48) );
        
        printShaded("DESTROY ALL ENEMIES TO WIN.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.64) );
        printShaded("DESPITE BEING ENEMIES, LET'S LIVE IN PEACE.", int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 2:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.33), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.53), int(scaledScrHeight*opponentHeight) );
        image(action_fort, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("FORT, COST "+COST_FORT+" GOLD:", int(scaledScrWidth*0.45), int(scaledScrHeight*0.30) );
        printShaded("INCREASES DEFENSE OF AN AREA.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded("YOU CAN'T HIDE BEHIND WALLS, BLUE!", int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 3:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        image(action_fleet, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        
        printShaded("FLEET, COST "+COST_FLEET+" GOLD:", int(scaledScrWidth*0.45), int(scaledScrHeight*0.30) );
        printShaded("LETS YOU ATTACK AN ANY AREA.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded("WAR IS THE INEVITABLE CONCLUSION OF FAILED DIPLOMACY.", int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 4:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        image(action_village, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("CITY, COST "+COST_VILLAGE+" GOLD:", int(scaledScrWidth*0.45), int(scaledScrHeight*0.30) );
        printShaded("INCREASES THE INCOME FROM AN AREA BY 1.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded("KINGS DON'T HAVE FRIENDS.", int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 5:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        image(action_uni, int(scaledScrWidth*0.68), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("UNIVERSITY, COST "+COST_UNIVERSITY+" GOLD:", int(scaledScrWidth*0.44), int(scaledScrHeight*0.30) );
        printShaded("BUILDS RESEARCH POINTS.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded(RESEARCH_LIMIT + " RESEARCH NEEDED TO UPGRADE ONE ACTION.\nAN UPGRADED ACTION DOES THE\nSAME THING, BUT BETTER.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.46) );
        printShaded("WE SEEK A PEACEFUL SOLUTION TO THIS CONFLICT.", int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 6:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        image(plague, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("PLAGUE, EVENT:", int(scaledScrWidth*0.44), int(scaledScrHeight*0.30) );
        printShaded("PLAGUE DESTROYS CITIES.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded(currentTaunt, int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 7:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        image(rebellion, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("REBELLION, EVENT:", int(scaledScrWidth*0.44), int(scaledScrHeight*0.30) );
        printShaded("TURNS AN AREA NEUTRAL.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        //printShaded(taunt[tauntIndex], int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
        printShaded(currentTaunt, int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 8:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        image(upgrade_mine, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("MINE:", int(scaledScrWidth*0.44), int(scaledScrHeight*0.30) );
        printShaded("SOMETIMES PAYS A LOT!", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded(currentTaunt, int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      case 76:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        image(rebellion, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("LAST AND FINAL MAP!", int(scaledScrWidth*0.44), int(scaledScrHeight*0.30) );
        printShaded("GENERATE NEW ONES BY\nPRESSING THE 'G' KEY.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded(currentTaunt, int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
      default:
        scale(0.5);
        image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*opponentHeight) );
        image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*opponentHeight) );
        image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*opponentHeight) );
        //image(action_castle, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("HELPFUL TIP:", int(scaledScrWidth*0.5), int(scaledScrHeight*0.30) );
        printShaded(currentTip, int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
        printShaded(currentTaunt, int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
      break;
    }
    if(waitForClick){ pauseTimer=millis()+500; }
    fill(200, 200, 150);
    scale(2);
    printShaded( _map, int(scaledScrWidth*0.25), int(scaledScrHeight*0.075) );
    scale(0.5);
  }else{
    gameState=STATE_GAME;
  }
  scale(0.5);
  if(gameState==STATE_MAPTEST) {
    for(Player player : players){
      player.isHuman=false;
    }
  }
}

void genModeMapStart(){
    if(!loadMap){
        gameState=STATE_GAME;
        return;
    }
  if (millis()<pauseTimer) {
    if(waitForClick){ pauseTimer=millis()+500; }
    image(bg,0,0);
    image(ruta1, 0, -8);
    image(portrait1, int(scaledScrWidth*0.85), int(scaledScrHeight*0.12) );
    scale(2);
    printShaded("PRESS 'G' TO GENERATE NEW MAP.", int(scaledScrWidth*0.5), int(scaledScrHeight*0.30) );
    printShaded("RESTART GAME TO RETURN TO CAMPAIGN MODE", int(scaledScrWidth*0.5), int(scaledScrHeight*0.38) );
    printShaded(currentTaunt, int(scaledScrWidth*0.5), int(scaledScrHeight*(0.77+0.20)) );
    scale(0.5);
    image(portrait2, int(scaledScrWidth*0.28), int(scaledScrHeight*0.77) );
    image(portrait3, int(scaledScrWidth*0.43), int(scaledScrHeight*0.77) );
    image(portrait4, int(scaledScrWidth*0.58), int(scaledScrHeight*0.77) );
    scale(2);
    scale(2);
    printShaded( "GENERATOR MODE", int(scaledScrWidth*0.25), int(scaledScrHeight*0.075) );
    scale(0.5);
  }else{
    gameState=STATE_GAME;
  }
}