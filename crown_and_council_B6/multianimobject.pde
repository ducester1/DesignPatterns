class Animation {
  PImage[] images;
  float currentFrame;
  float numFrames;
  float pace;
  int xpos;
  int ypos;
  boolean finished = false;

  Animation(PImage[] images, int mx, int my, float p) { 
    xpos = mx;
    ypos = my;
    pace = p;
    numFrames = images.length;
    this.images = images;

    currentFrame=0;
  }
  
  Animation setDelay(float delay) {
    currentFrame = -delay;
    return this;
  }
  
	void tick(){
    	currentFrame = (currentFrame+(pace*2));
        if (int(currentFrame) >= numFrames){
          finished=true;
        }
    }
    
  void display(float xpos, float ypos) {
      if (finished || currentFrame < 0) {
        return;
      }
      image(images[int(currentFrame)], xpos, ypos);
    
  }
}

PImage[] cutAnimRow(PImage spriteMap, int sprWidth, int row){
  return cutAnim(spriteMap, sprWidth)[row];
}

PImage[][] cutAnim(PImage spriteMap, int sprWidth){
  return cutAnim(spriteMap, sprWidth, sprWidth);
}
PImage[][] cutAnim(PImage spriteMap, int sprWidth, int sprHeight){
  int animation = spriteMap.height/sprHeight;
  int frame = spriteMap.width/sprWidth;
  PImage[][] images = new PImage[int(animation)][int(frame)];
  for (int a = 0; a < animation; a++) {
    for (int i = 0; i < frame; i++) {
      images[a][i] = createImage(sprWidth, sprHeight, ARGB);
      images[a][i].copy(spriteMap,i*sprWidth,a*sprHeight,sprWidth,sprHeight,0,0,sprWidth,sprHeight); 
    }
  }
  return images;
}


class PopMessage {
  String message;
  int time;
  float pace,currentTime;
  int xpos;
  int ypos;
  boolean finished = false;
  
  PopMessage(String _mes, int mx, int my, float _pace, int _time) { 
    message = _mes;

    xpos = mx;
    ypos = my;
    pace = _pace;
    time = _time;
    currentTime=0;
  }
  
  PopMessage setDelay(float delay) {
    currentTime = -delay;
    return this;
  }

  void display(float xpos, float ypos) {
    currentTime = (currentTime+pace);
    if (int(currentTime) >= time){
      finished=true;
    } else {
      if (currentTime < 0) {
        return;
      }
      printShadedFade(message,int(xpos),int(ypos-currentTime*0.5), int(time - currentTime));
    }
  }
}