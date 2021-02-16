void pause(){
  if(waitForClick){
    image(bg,0,0);
    image(ruta1, 0, 0);
    scale(2);
    printShaded("PAUSED", int(scaledScrWidth*0.5), int(scaledScrHeight*0.189) );
    textAlign(LEFT);
    printShaded("SHORTCUTS:", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 1) );
    printShaded("R - RESTART MAP", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 3) );
    printShaded("M - TOGGLE MUSIC", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 4) );
    printShaded("S - TOGGLE SOUND", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 5) );
    printShaded("K - AUTO FINISH MAP", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 6) );
    printShaded("G - GENERATE MAP", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 7) );
    printShaded("RMB - AUTO ATTACK", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 8) );
    printShaded("Z,X,C,V,B - ACTIONS", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 9) );
    printShaded("SPACE - END TURN", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 10) );
    printShaded("+/-  - GAME SCREEN SIZE", int(scaledScrWidth*0.2), int(scaledScrHeight*0.22 + textHeight * 11) );
    
    textAlign(CENTER);
    scale(.5);
  }else{
    gameState=gameStateOld;
    waitForClick=true;
  }
}