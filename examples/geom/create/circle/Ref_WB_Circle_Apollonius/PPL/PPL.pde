import wblut.processing.*;
import wblut.geom.*;
import java.util.List;

WB_GeometryFactory factory;
WB_Render2D render;

WB_Line L;
WB_Point p;
WB_Point q;
List<WB_Circle> circles;

void setup() {
  size(800, 800);
  factory=new WB_GeometryFactory();
  render=new WB_Render2D(this); 
}

void create() {
  L= factory.createLineWithDirection2D( width/2,height/2+200, 1,0);
  p=factory.createPoint(400,400);
  q= factory.createPoint( mouseX,mouseY);
  circles=factory.createCirclePPL(p,q,L);
}

void draw() {
  background(255);
  create();
  noFill();
  stroke(0, 120);
  render.drawLine2D(L,width);
  render.drawPoint2D(p,10);
  render.drawPoint2D(q,10);
  stroke(255,0,0, 120);
  for(WB_Circle C:circles){
   render.drawCircle2D(C); 
  }

}