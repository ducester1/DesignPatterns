void gameWon() {
  renderGame();
  image(ruta1, 0, -8);
  scale(2);
  image(portrait1, int(scaledScrWidth*0.19), int(scaledScrHeight*0.06) );
  
  scale(0.5);
  
  scale(2);
  printShaded("CONGRATULATIONS!", int(scaledScrWidth*0.50), int(scaledScrHeight*0.50) );
  printShaded("YOU HAVE VANQUISHED YOUR ENEMIES.", int(scaledScrWidth*0.50), int(scaledScrHeight*0.60) );
  if (millis()<pauseTimer) {
    if(waitForClick){
      	pauseTimer=millis()+500;
    }else{
        loadMap=true;
      	gameState=STATE_GENERATOR;
      	waitForClick=true;
      	gameSetup();
    }
  }else{
    gameState=STATE_GAMEWON;
  }
  if(noAnims){
    gameState=STATE_GENERATOR;
    gameSetup();
  }
}

void gameOver() {
  renderGame();
  image(ruta1, 0, -8);
  scale(2);
  int winner =0;
  for(int i=0;i<players.size();i++){
      Player p = players.get(i);
      if(p.isAlive) winner=i+1;
   }
   
  if(winner==1) image(portrait1, int(scaledScrWidth*0.19), int(scaledScrHeight*0.06) );
  if(winner==2) image(portrait2, int(scaledScrWidth*0.19), int(scaledScrHeight*0.06) );
  if(winner==3) image(portrait3, int(scaledScrWidth*0.19), int(scaledScrHeight*0.06) );
  if(winner==4) image(portrait4, int(scaledScrWidth*0.19), int(scaledScrHeight*0.06) );
  
  scale(0.5);
  scale(2);
  printShaded("GAME OVER", int(scaledScrWidth*0.50), int(scaledScrHeight*0.50) );
  printShaded("PLAYER " + winner + " HAS WON.", int(scaledScrWidth*0.50), int(scaledScrHeight*0.60) );
  
  
  if (millis()<pauseTimer) {
    if(waitForClick){
          pauseTimer=millis()+500;
    }else{
        loadMap=true;
          gameState=STATE_GENERATOR;
          waitForClick=true;
          gameSetup();
    }
  }else{
    gameState=STATE_GAMEOVER;
  }
  if(noAnims){
    gameState=STATE_GENERATOR;
    gameSetup();
  }
}