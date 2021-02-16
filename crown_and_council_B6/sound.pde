void playSound(String _snd){
  if(_snd.equals("song")){
      song.rewind();
      song.play();
    }
  if(!playSound) return;
  boolean _noHuman=true;
  for(int _i=0;_i<players.size();_i++){
    Player _p = players.get(_i);
    if(_p.isHuman && _p.isAlive){_noHuman=false;}
  }
  if(!_noHuman){
    
    if(_snd.equals("battle")){
      battle1.rewind();
      battle1.play();
    }
    if(_snd.equals("fail")){
      fail1.rewind();
      fail1.play();
    }
    if(_snd.equals("nogo")){
      nogo.rewind();
      nogo.play();
    }
    if(_snd.equals("fort")){
      fort1.rewind();
      fort1.play();
    }
      if(_snd.equals("castle")){
      castle1.rewind();
      castle1.play();
    }
    if(_snd.equals("fleet")){
      fleet1.rewind();
      fleet1.play();
    }
      if(_snd.equals("trade")){
      trade1.rewind();
      trade1.play();
    }
    if(_snd.equals("gold")){
      if(random(1)<0.5){
        gold1.rewind();
        gold1.play();
      }else{
        gold2.rewind();
        gold2.play();
      }
    }
    if(_snd.equals("plague")){
      plague1.rewind();
      plague1.play();
    }
    if(_snd.equals("rebellion")){
      rebellion1.rewind();
      rebellion1.play();
    }
  }
}