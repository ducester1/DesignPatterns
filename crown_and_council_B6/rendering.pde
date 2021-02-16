

void renderGame(){
  startRender();
  renderBG();
  renderAreas();  
  renderAnims();
  renderMessages();
  renderEffect();
  
  playerActions();
  playerPane();
  hasUpgrade();  
}

void renderEffect(){
    tint(250,250,200,25);
  image(glow,0,0);
  noTint(); 
  image(gui_bg,0,2);
}

void renderMessages(){
    for (int i = popMessages.size()-1; i >= 0; i--) {
    PopMessage _mes = (PopMessage) popMessages.get(i);
    _mes.display(_mes.xpos,_mes.ypos);
    if (_mes.finished) {
      popMessages.remove(i);
    }
  }
}

void renderBG(){
    image(water,0,0);
  image(info_pane,0,scaledScrHeight-info_pane.height);
}

void startRender(){
    if(gameState==STATE_MAPTEST) screenShake=0;
  if(screenShake>0.01){
    translate(random(-screenShake,screenShake),random(-screenShake,screenShake));
    screenShake-=0.1;
  }
  noTint();
  noMouseOver = true;
}

void renderAreas(){
    for(int i=0;i<areas.size();i++){
    Area a = areas.get(i);
    a.drawArea(!currentPlayer().hasUpgrade());
    noMouseOver = (a.mouseOver) ? false:noMouseOver;
  }
  
  for(int i=0;i<areas.size();i++){
    Area a = areas.get(i);
    a.drawUpgrades();
  }
}
void renderAnims(){
    for (int i = anims.size()-1; i >= 0; i--) { 
    Animation _anim = (Animation) anims.get(i);
    _anim.display(_anim.xpos,_anim.ypos);
  }
}
void checkMusic(){
  if(song.isPlaying() == false && playMusic==true) {
   playSound("song");
  }
  if(playMusic==false){
    song.pause();
  }else{
    musicAt=song.position();
  }
}

void playerActions(){
    
  for(int i=0;i<players.size();i++){
    Player _p = players.get(i);
    //_p.showGold();
    if(_p.id==playerTurn){_p.showActions(!currentPlayer().hasUpgrade());}
    
    if(_p.id==playerTurn){
      if(gameState==STATE_GAMEOVER){
        printShaded("EXIT",2*scaledScrWidth-42,2*scaledScrHeight-12);
      }else{
        if(_p.isHuman){
          endTurnButton.render();
        }else{
          printShaded("ENEMY TURN "+gameTurn,2*scaledScrWidth-42,2*scaledScrHeight-12);
        }
      }
    }
  }
}

void playerPane(){
  int _placer=0;
  int _atY=32;
  for(int i=0;i<players.size();i++){
    _placer=i;    
    image(player_pane,0,_atY+(_placer*16));
    tint(tintByOwner2(i+1,color(10)));
    image(player_pane_colorplate,0,_atY+(_placer*16));
    if(i+1==playerTurn){
      tint(220,210,200);
      image(player_pane_current,0,_atY+(_placer*16));
    }
    Player _p = players.get(i);
    if(_p.isAlive){
      printShadedDark(getPlayerDesc(_p.id),32,_atY*2+15+(_placer*32));
      printShadedGold("GOLD: "+_p.gold+"/"+_p.income,32,_atY*2+24+(_placer*32));
    }else{
      printShadedDark(getPlayerDesc(_p.id),32,_atY*2+15+(_placer*32));
      printShadedDark(" - DEAD -",32,_atY*2+24+(_placer*32));
    }
    if(_p.isHuman) {
        noTint();
        scale(0.5);
        image(iconHuman,3,_atY*2+19+(_placer*32));
        scale(2);
    }
    if(_p.id==1 && level == 1 && campaignMode){
      printShaded("SELECT",32,14*12);
      printShaded("ACTION",32,15*12);
      printShaded("BELOW,",32,16*12);
      printShaded("THEN",32,17*12);
      printShaded("ATTACK",32,18*12);
      printShaded("ADJACENT",32,19*12);
      printShaded("AREA",32,20*12);
      printShaded("|",32,22*12);
      printShaded("|",32,23*12);
      printShaded("\\/",32,24*12);
    }
    noTint();
  }
  if(sMouseX()<32){
    if(sMouseY()>32 && sMouseY()<48) showUpgradedActions(1);
    if(sMouseY()>48 && sMouseY()<64) showUpgradedActions(2);
    if(sMouseY()>64 && sMouseY()<80) showUpgradedActions(3);
    if(sMouseY()>80 && sMouseY()<94) showUpgradedActions(4);
  }
}

String getPlayerDesc(int id){
  String s="";
  if(id>players.size()) return s;
  if(id==1) s="BLUE KING";
  if(id==2) s="RED QUEEN";
  if(id==3) s="GREEN QUEEN";
  if(id==4) s="PURPLE KING";
  return s;
}

void showUpgradedActions(int playerId){
  if(playerId > players.size()) return;
  Player p = players.get(playerId-1);
  int upgradeCount=0;
  for(int i=0;i<p.actions.size();i++){
    if(p.actions.get(i).upgraded){
      image(p.actions.get(i).pic2, 0, scaledScrHeight-32-i*16);
      upgradeCount++;
    }
  }
  image(p.portrait,0,0);
  if(p.research>0) printShaded("RESEARCH: " + p.research, 32, 58);
}
void hasUpgrade(){
  if(currentPlayer().hasUpgrade()){
    deselectAreas();
    image(upgrade_pane,0,scaledScrHeight-info_pane.height);
    printShaded("CHOOSE UPGRADE:",int(scaledScrWidth*0.18),2*scaledScrHeight-12);
    int _btnCount=0;
    for(int _a=0;_a<currentPlayer().actions.size()-1;_a++){
      Action _action = currentPlayer().actions.get(_a);
      if(!_action.upgraded){
        _btnCount++;

        Button upgradeButton = getUpgradeButton(_action.type);
        upgradeButton.x1 = int( (scaledScrWidth*0.1) + (_btnCount*action_army.width*1.2) );
        upgradeButton.update();
       
      }
    }
  }
}