void tick(){
    if(tickTimer>millis()){
        return;
    }else{
        tickTimer=millis()+tickDelay;
    }
    tickAnims();
    checkMusic();
}

void tickAnims(){
    for (int i = anims.size()-1; i >= 0; i--) { 
    Animation _anim = (Animation) anims.get(i);
    _anim.tick();
    if (_anim.finished) {
      anims.remove(i);
    }
  }
}