void splash(){
  if(waitForClick){
    image(splash,0,0);
    ticker();
  }else{
    gameState=STATE_GENERATOR;
    waitForClick=true;
  }
}

void ticker(){
  if(tickerTimer+10<millis()){
    tickerTimer=millis();
    tickerPos-=0.5; 
  }
  if(tickerPos<-850) tickerPos=scrWidth*1.35;;
    printShaded(tickerText, int(tickerPos), int(scrHeight*0.49) );
}