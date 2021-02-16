class Button {
  PImage pic;
  int inGameState;
  int x1,y1,x2,y2;
  boolean mouseOver,selected, enabled=true;
  String info;
  
  Button(int _x1,int _y1, PImage _pic, int _state){
    pic = _pic;
    x1=_x1;
    y1=_y1;
    x2=pic.width;
    y2=pic.height;
    inGameState=_state;
  }
  
  void update(){
      if(!enabled) return;
    if(gameState!=inGameState) return;
    
    if(mouseOver()){
      if(mousePressed){
        tint(120,110,110);
        selected=true;
      }else{
        tint(255,255,255,220);
      }
    }
    image(pic,x1,y1);
    noTint();
  }
  boolean mouseOver(){
    if(enabled && mouseX*(1/myScale) > x1 && mouseX*(1/myScale) < x1+x2 && mouseY*(1/myScale) > y1 && mouseY*(1/myScale) < y1+y2){
      return true;
    }
    return false;
  }
}

class TextButton {
  int x;
  int y;
  PImage btnImage;
  PImage btnImage_Down;
  int id;
  String caption;
  boolean enabled;
  int margin = 0;
  float textScale = 1;
  
  TextButton (String caption, int x, int y, PImage btnImage, int id){
    this.x=x;
    this.y=y;
    this.btnImage=btnImage;
    this.id=id;
    this.caption=caption;
    this.enabled=true;
  }
  TextButton setTextScale(float textScale){
    this.textScale=textScale;
    return this;
  }
  boolean contains (int xx, int yy){ 
    return (xx > x + margin && xx < x + btnImage.width -margin && yy > y + margin && yy  < y + btnImage.height - margin);
  }
  
  void render(){
    textAlign(CENTER,CENTER); 
    if (enabled){
      if ( contains( sMouseX(),sMouseY() ) ){
        if (mousePressed == true) {
          renderPressed();
        } else {
          renderUnpressed();
        }
      } else {
        renderNormal();
      }
    } else {
      renderDisabled();
    }
    fill(255,255);
    tint(255,255);
    textAlign(CENTER); 
  }
  
  void renderNormal(){
    image(btnImage,x,y);
    float tx = (x + (btnImage.width*0.5))/textScale;
    float ty = (y + btnImage.height*0.5)/textScale;
    scale(textScale);
    fill(20,10,10,200);
    text(caption, tx, ty+1);
    fill(200,180,160,255);
    text(caption, tx, ty);
    scale(1/textScale);
  }
  
  void renderDisabled(){
    float tx = (x + (btnImage.width*0.5))/textScale;
    float ty = (y + btnImage.height*0.5)/textScale;
    image(btnImage,x,y);
    scale(textScale);
    fill(255,100);
    text(caption, tx, ty+1);
    tint(255, 100);
    image(blackImg,x,y,btnImage.width,btnImage.height);    
    scale(1/textScale);
  }
  
  void renderPressed(){
    float tx = (x + (btnImage.width*0.5))/textScale;
    float ty = (y + btnImage.height*0.5)/textScale;
    image(btnImage,x,y);
    scale(textScale);
    text(caption, tx, ty);
    text(caption, tx, ty);
    scale(1/textScale);
    tint(155, 100);
    image(blackImg,x,y,btnImage.width,btnImage.height);
  }
  
  void renderUnpressed(){
    float tx = (x + (btnImage.width*0.5))/textScale;
    float ty = (y + btnImage.height*0.5)/textScale;
    image(btnImage,x,y);
    tint(255,255,225, 20);
    image(whiteImg,x,y,btnImage.width,btnImage.height); 
    scale(textScale);
    fill(30,20,20,200);
    text(caption, tx, ty+1);
    fill(250,230,200,255);
    text(caption, tx, ty);
    scale(1/textScale);
  }
  
  void update(){
  }
}