void startScreen(){
    image(bg,0,0);
    playButton.update();
    settingsButton.render();
    if(playButton.mouseOver && mouseClicked){
      gameState=STATE_GENERATOR;
    }
}