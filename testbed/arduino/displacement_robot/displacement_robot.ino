//PINS

//motor up left
#define I1ul_PIN 26
#define I2ul_PIN 27
#define E12ul_PIN 5
#define ENC_Yul_PIN 19
#define ENC_Wul_PIN 15


//motor up right
#define I1ur_PIN 24
#define I2ur_PIN 25
#define E12ur_PIN 6
#define ENC_Yur_PIN 20
#define ENC_Wur_PIN 16


//motor bottom left
#define I1bl_PIN 28
#define I2bl_PIN 29
#define E12bl_PIN 4
#define ENC_Ybl_PIN 18
#define ENC_Wbl_PIN 14

//motor bottom right
#define I1br_PIN 22
#define I2br_PIN 23
#define E12br_PIN 7
#define ENC_Ybr_PIN 21
#define ENC_Wbr_PIN 17

//Limit switches
#define LS1 34
#define LS2 32
#define LS3 31
#define LS4 33
#define LS5 30
#define LS6 35

//variables to compute the different speeds

unsigned long starttime=0;
unsigned long currenttime_ul=0;
unsigned long currenttime_ur=0;
unsigned long currenttime_bl=0;
unsigned long currenttime_br=0;
unsigned long time_ul=0;
unsigned long prev_ul=0;
unsigned long time_ur=0;
unsigned long prev_ur=0;
unsigned long time_bl=0;
unsigned long prev_bl=0;
unsigned long time_br=0;
unsigned long prev_br=0;
long speed_ul=0;
long speed_ur=0;
long speed_bl=0;
long speed_br=0;

#define GUARD_GAIN 1000
#define DELTA_T 50
#define SPEED 20
#define TIME 2000

#define UP 0
#define DOWN 1
#define LEFT 2
#define RIGHT 3
#define UPRIGHT 0 //diagonal
#define DOWNRIGHT 1 //diagonal
#define DOWNLEFT 2 //diagonal
#define UPLEFT 3 //diagonal
#define CW 0
#define CCW 1
#define SHORTSIDE 0 //robot facing short side of testbed
#define LONGSIDE 1 //robot facing long side of testbed
#define CORNERDIST 200 //pullback distance from corner (unknown measurement unit)
#define SIDEDIST 125 //pullback distance from side (in inches)
#define CORNERX 150 //need to calibrate to match CORNERDIST (in inches)
#define CORNERY 150 //same as CORNERX (in inches)
#define CW90 4125 //time in milliseconds of cw rotation
#define CCW90 4125 //time in milliseconds of ccw rotation
#define RIGHTARM 0 //arn position on the left of center axis
#define LEFTARM 1 //arm position on the right of center axis

//PIDs parameters
int pTerm = 0, iTerm = 0, dTerm = 0;

//float K_ul = 1;
//float   p_ul = 2.5;                      
//float   i_ul = 0.7;                   
//float   d_ul= 0.1;  
//int last_error_ul = 0;
//int integrated_error_ul = 0;
//
//float K_bl = 1;
//float   p_bl = 2.5;                      
//float   i_bl = 0.7;                   
//float   d_bl= 0.1;  
//int last_error_bl = 0;
//int integrated_error_bl = 0;
//
//float K_br = 1;
//float   p_br = 2.5;                      
//float   i_br = 0.7;                   
//float   d_br= 0.1;  
//int last_error_br = 0;
//int integrated_error_br = 0;
//
//
//float K_ur = 1;
//float   p_ur = 2.5;                      
//float   i_ur = 0.7;                   
//float   d_ur= 0.1;  
//int last_error_ur = 0;
//int integrated_error_ur = 0;

float K_ul = 1;
float   p_ul = 1.5;                      
float   i_ul = 0.4;                   
float   d_ul= 1.5;  
int last_speed_ul = 0;
int integrated_error_ul = 0;
int i = 0;

float K_ur = 1;
float   p_ur = 1.5;                      
float   i_ur = 0.4;                   
float   d_ur= 1.5;  
int last_speed_ur = 0;
int integrated_error_ur = 0;

float K_bl = 1;
float   p_bl = 1.5;
float   i_bl = 0.4;                   
float   d_bl= 1.5;  
int last_speed_bl = 0;
int integrated_error_bl = 0;

float K_br = 1;
float   p_br = 1.5;                      
float   i_br = 0.4;                   
float   d_br= 1.5;  
int last_speed_br = 0;
int integrated_error_br = 0;


//target speeds 
int tgt_ul=0;
int tgt_ur=0;
int tgt_bl=0;
int tgt_br=0;


float x_pos=0;
float y_pos=0;
int ori = LONGSIDE;
float x_tgt;
float y_tgt;
int ori_tgt;

void setup() {
  // set up the pins and interrupts

  //setup all pins
   Serial.begin(9600);
   pinMode(I1ul_PIN, OUTPUT);
   pinMode(I2ul_PIN, OUTPUT);
   pinMode(E12ul_PIN, OUTPUT);
   pinMode(I1ur_PIN, OUTPUT);
   pinMode(I2ur_PIN, OUTPUT);
   pinMode(E12ur_PIN, OUTPUT);
   pinMode(I1br_PIN, OUTPUT);
   pinMode(I2br_PIN, OUTPUT);
   pinMode(E12br_PIN, OUTPUT);
   pinMode(I1bl_PIN, OUTPUT);
   pinMode(I2bl_PIN, OUTPUT);
   pinMode(E12bl_PIN, OUTPUT);

   pinMode(ENC_Yul_PIN, INPUT);
   pinMode(ENC_Wul_PIN, INPUT);
   pinMode(ENC_Yur_PIN, INPUT);
   pinMode(ENC_Wur_PIN, INPUT);
   pinMode(ENC_Ybl_PIN, INPUT);
   pinMode(ENC_Wbl_PIN, INPUT);
   pinMode(ENC_Ybr_PIN, INPUT);
   pinMode(ENC_Wbr_PIN, INPUT);

   //pwm at zero- robot at rest 
   analogWrite(E12ul_PIN, 0);
   analogWrite(E12ur_PIN, 0);
   analogWrite(E12br_PIN, 0);
   analogWrite(E12bl_PIN, 0);

   //interrupts
   attachInterrupt(digitalPinToInterrupt(ENC_Yul_PIN), enc_yul, RISING);
   attachInterrupt(digitalPinToInterrupt(ENC_Yur_PIN), enc_yur, RISING);
   attachInterrupt(digitalPinToInterrupt(ENC_Ybl_PIN), enc_ybl, RISING);
   attachInterrupt(digitalPinToInterrupt(ENC_Ybr_PIN), enc_ybr, RISING);

   //start the timer. Used for calculating the speed
   starttime = micros();

}


//---------------------------------------------------------------------
//---------------------------------------------------------------------
//MAIN LOOP HERE
//---------------------------------------------------------------------
//---------------------------------------------------------------------
void loop() {
  int param;
  int x_curr;
  int y_curr;
  int ori_curr;
  int x_tgt;
  int y_tgt;
  int ori_tgt;
  
  // Wait
  while(!Serial.available())
    delay(50);
 
  // Read input
  switch(Serial.read())
  {
    case 'a':
      while(!Serial.available())
        delay(50);
        
      // Read cmd
      while(!Serial.available())
        delay(50);
      x_curr = Serial.parseInt();
      y_curr = Serial.parseInt();
      ori_curr = Serial.parseInt();
      x_tgt = Serial.parseInt();
      y_tgt = Serial.parseInt();
      ori_tgt = Serial.parseInt();
      move_xyo(x_curr, y_curr, ori_curr, x_tgt, y_tgt, ori_tgt);
      translatewall(UP);
      break;
      
    case 'l':
      while(!Serial.available())
        delay(50);
      param = Serial.parseInt();
      translaterobot(LEFT, time_from_dist(param));
      break;

    case 'r':
      while(!Serial.available())
        delay(50);
      param = Serial.parseInt();
      translaterobot(RIGHT, time_from_dist(param));
      break;

    case 'u':
      while(!Serial.available())
        delay(50);
      param = Serial.parseInt();
      translaterobot(UP, time_from_dist(param));
      break;

    case 'd':
      while(!Serial.available())
        delay(50);
      param = Serial.parseInt();
      translaterobot(DOWN, time_from_dist(param));
      break;

    case 'f':
      translatewall(UP);
      break;
  }
}

//---------------------------------------------------------
//Distance to time conversion
//---------------------------------------------------------
int time_from_dist(float dist)
{
  int res = (int) abs(dist*8.32);
  return res;
}

//---------------------------------------------------------
//PIDs functions
//---------------------------------------------------------
int pid_ul (int tgt_ul)
{
  long error_ul = tgt_ul - speed_ul; 
  pTerm = p_ul * error_ul;
  integrated_error_ul += error_ul;                           
  iTerm = i_ul* constrain(integrated_error_ul, -GUARD_GAIN, GUARD_GAIN);
  dTerm = d_ul * (speed_ul- last_speed_ul);                            
  last_speed_ul = speed_ul;
  long int input_ul = constrain(K_ul*(pTerm + iTerm + dTerm), -255, 255);

  return abs(input_ul);
}

int pid_ur (int tgt_ur)
{
  long error_ur = tgt_ur - speed_ur; 
  pTerm = p_ur * error_ur;
  integrated_error_ur += error_ur;                                       
  iTerm = i_ur* constrain(integrated_error_ur, -GUARD_GAIN, GUARD_GAIN);
  dTerm = d_ur * (speed_ur- last_speed_ur);                            
  last_speed_ur = speed_ur;
  long int input_ur = constrain(K_ur*(pTerm + iTerm + dTerm), -255, 255);

  return abs(input_ur);
}

int pid_bl ( int tgt_bl)
{
  long error_bl = tgt_bl - speed_bl; 
  pTerm = p_bl * error_bl;
  integrated_error_bl += error_bl;                                       
  iTerm = i_bl* constrain(integrated_error_bl, -GUARD_GAIN, GUARD_GAIN);
  dTerm = d_bl * (speed_bl- last_speed_bl);                            
  last_speed_bl = speed_bl;
  long int input_bl = constrain(K_bl*(pTerm + iTerm + dTerm), -255, 255);

  return abs(input_bl);
}

int pid_br ( int tgt_br)
{
  long  error_br = tgt_br - speed_br; 
  pTerm = p_br * error_br;
  integrated_error_br += error_br;                                       
  iTerm = i_br* constrain(integrated_error_br, -GUARD_GAIN, GUARD_GAIN);
  dTerm = d_br * (speed_br- last_speed_br);                            
  last_speed_br = speed_br;
  long int input_br = constrain(K_br*(pTerm + iTerm + dTerm), -255, 255);

  return abs(input_br);
}
//---------------------------------------------------------
//functions for translation and rotation
//---------------------------------------------------------

// for a linear movement,dir: 0=up, 1=down, 2=left, 3=right
// dist in cm 
void translaterobot(int dir, int dist)
{
  long unsigned looptime=0;
  long unsigned trigtime=millis()-starttime/1e3;
  Serial.println("debug: Entered the translational loop");
  //Serial.println(dir);
  
  while(looptime<dist)
  {
    if (dir==UP)
    { Serial.println("debug: Entered up");
      
      tgt_ul=-SPEED;
      tgt_ur=SPEED;
      tgt_bl=-SPEED;
      tgt_br=SPEED;
      
      digitalWrite(I1ul_PIN, HIGH);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, HIGH);
       
      digitalWrite(I1br_PIN, HIGH);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, HIGH); 
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
    }
  
    else if (dir==DOWN)
    { Serial.println("debug: Entered down");
      
      tgt_ul=SPEED;
      tgt_ur=-SPEED;
      tgt_bl=SPEED;
      tgt_br=-SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, HIGH);
       
      digitalWrite(I1ur_PIN, HIGH);
      digitalWrite(I2ur_PIN, LOW);
       
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, HIGH); 
       
      digitalWrite(I1bl_PIN, HIGH);
      digitalWrite(I2bl_PIN, LOW);
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
    }
  
    else if (dir==LEFT)
    { Serial.println("debug: Entered left");
      
      tgt_ul=SPEED;
      tgt_ur=SPEED;
      tgt_bl=-SPEED;
      tgt_br=-SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, HIGH);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, HIGH);
       
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, HIGH); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, HIGH);
  
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
    }
  
    else if (dir==RIGHT)
    { Serial.println("debug: Entered right");
      
      tgt_ul=-SPEED;
      tgt_ur=-SPEED;
      tgt_bl=SPEED;
      tgt_br=SPEED;
  
      digitalWrite(I1ul_PIN, HIGH);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, HIGH);
      digitalWrite(I2ur_PIN, LOW);
       
      digitalWrite(I1br_PIN, HIGH);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, HIGH);
      digitalWrite(I2bl_PIN, LOW);
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
    }
    
    else Serial.println("debug: Error in giving up-down-left-right direction");

    looptime=millis()-trigtime;
  }

  analogWrite(E12ul_PIN, 0);
  analogWrite(E12ur_PIN, 0);
  analogWrite(E12br_PIN, 0);
  analogWrite(E12bl_PIN, 0);
  reset_ie();
}

void translatewall(int dir)
{
  Serial.println("debug: Entered the translational wall loop");
  //Serial.println(dir);
  
  if (dir==UP) {
    while(!(digitalRead(LS3) == HIGH && digitalRead(LS4) == HIGH))
    { Serial.println("debug: Entered up");
      
      tgt_ul=-SPEED;
      tgt_ur=SPEED;
      tgt_bl=-SPEED;
      tgt_br=SPEED;
      
      digitalWrite(I1ul_PIN, HIGH);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, HIGH);
       
      digitalWrite(I1br_PIN, HIGH);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, HIGH); 
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
    }
  }
  
  else if (dir==LEFT) {
    while(!(digitalRead(LS1) == HIGH && digitalRead(LS2) == HIGH))
    { Serial.println("debug: Entered left");
      
      tgt_ul=SPEED;
      tgt_ur=SPEED;
      tgt_bl=-SPEED;
      tgt_br=-SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, HIGH);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, HIGH);
       
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, HIGH); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, HIGH);
  
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
    }
  }
  else if (dir==RIGHT) {
    while(!(digitalRead(LS5) == HIGH && digitalRead(LS6)))
    { Serial.println("debug: Entered right");
      
      tgt_ul=-SPEED;
      tgt_ur=-SPEED;
      tgt_bl=SPEED;
      tgt_br=SPEED;
  
      digitalWrite(I1ul_PIN, HIGH);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, HIGH);
      digitalWrite(I2ur_PIN, LOW);
       
      digitalWrite(I1br_PIN, HIGH);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, HIGH);
      digitalWrite(I2bl_PIN, LOW);
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
    }
  }
  else Serial.println("debug: Error in giving up-down-left-right direction");
  analogWrite(E12ul_PIN, 0);
  analogWrite(E12ur_PIN, 0);
  analogWrite(E12br_PIN, 0);
  analogWrite(E12bl_PIN, 0);
  reset_ie();
}

//dir 0 == upright, 1 == downright, 2 == downleft, 3 == upleft
void diagonalrobot(int dir, int dist)
{
  long unsigned looptime=0;
  long unsigned trigtime=millis()-starttime/1e3;
  Serial.println("debug: Entered the diagonal loop");
  //Serial.println(dir);
  
  while(looptime<dist)
  {
    if (dir==UPRIGHT)
    { Serial.println("debug: Entered upright");
      
      tgt_ul=-SPEED;
      tgt_br=SPEED;
      
      digitalWrite(I1ul_PIN, HIGH);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, LOW);
       
      digitalWrite(I1br_PIN, HIGH);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, LOW); 
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, 0);
      analogWrite(E12br_PIN, pid_br(tgt_br));
      analogWrite(E12bl_PIN, 0);
    }
  
    else if (dir==DOWNRIGHT)
    { Serial.println("debug: Entered downright");
      
      tgt_ur=-SPEED;
      tgt_bl=SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, HIGH);
      digitalWrite(I2ur_PIN, LOW);
       
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, HIGH);
      digitalWrite(I2bl_PIN, LOW);
  
      analogWrite(E12ul_PIN, 0);
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12br_PIN, 0);
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
    }
  
    else if (dir==DOWNLEFT)
    { Serial.println("debug: Entered downleft");
      
      tgt_ul=SPEED;
      tgt_br=-SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, HIGH);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, LOW);
       
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, HIGH); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, LOW);
  
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, 0);
      analogWrite(E12br_PIN, pid_br(tgt_br));
      analogWrite(E12bl_PIN, 0);
    }
  
    else if (dir==UPLEFT)
    { Serial.println("debug: Entered upleft");
      
      tgt_ur=SPEED;
      tgt_bl=-SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, HIGH);
       
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, HIGH);
  
      analogWrite(E12ul_PIN, 0);
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12br_PIN, 0);
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
    }
    
    else Serial.println("debug: Error in giving diagonal direction");

    looptime=millis()-trigtime;
  }

  analogWrite(E12ul_PIN, 0);
  analogWrite(E12ur_PIN, 0);
  analogWrite(E12br_PIN, 0);
  analogWrite(E12bl_PIN, 0);
  reset_ie();
}

void diagonalwall(int dir)
{
  Serial.println("debug: Entered the diagonal wall loop");
  
  if (dir==UPRIGHT)
  {
    while(digitalRead(LS3) == LOW || digitalRead(LS4) == LOW || digitalRead(LS5) == LOW || digitalRead(LS6) == LOW)
    { Serial.println("debug: Entered upright");
      
      tgt_ul=-SPEED;
      tgt_br=SPEED;
      
      digitalWrite(I1ul_PIN, HIGH);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, LOW);
       
      digitalWrite(I1br_PIN, HIGH);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, LOW); 
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, 0);
      analogWrite(E12br_PIN, pid_br(tgt_br));
      analogWrite(E12bl_PIN, 0);
    }
  }
  
  else if (dir==UPLEFT) {
    while(digitalRead(LS3) == LOW || digitalRead(LS4) == LOW || digitalRead(LS1) == LOW || digitalRead(LS2) == LOW)
    { Serial.println("debug: Entered upleft");
      
      tgt_ur=SPEED;
      tgt_bl=-SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, LOW);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, HIGH);
       
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, HIGH);
  
      analogWrite(E12ul_PIN, 0);
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12br_PIN, 0);
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
    }
  }
  else Serial.println("debug: Error in giving diagonal direction");

  analogWrite(E12ul_PIN, 0);
  analogWrite(E12ur_PIN, 0);
  analogWrite(E12br_PIN, 0);
  analogWrite(E12bl_PIN, 0);
  reset_ie();
}

// for a rotational movement,cccw 0=cw, 1=ccw
// angle in deg
void rotaterobot(int cccw, int angle)
{

  long unsigned looptime=0;
  long unsigned trigtime=millis()-starttime/1e3;

  Serial.println("debug: Entered the rotational loop");
  //Serial.println(cccw);
  while(looptime<angle)
  {
    if (cccw==0)
    {  Serial.println("debug: Entered cw");
      tgt_ul=-SPEED;
      tgt_ur=-SPEED;
      tgt_bl=-SPEED;
      tgt_br=-SPEED;
  
      digitalWrite(I1ul_PIN, HIGH);
      digitalWrite(I2ul_PIN, LOW);
     
      digitalWrite(I1ur_PIN, HIGH);
      digitalWrite(I2ur_PIN, LOW);
     
      digitalWrite(I1br_PIN, LOW);
      digitalWrite(I2br_PIN, HIGH); 
     
      digitalWrite(I1bl_PIN, LOW);
      digitalWrite(I2bl_PIN, HIGH);
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
  
      
    }
  
    else if (cccw==1)
    { Serial.println("debug: Entered ccw");
      tgt_ul=SPEED;
      tgt_ur=SPEED;
      tgt_bl=SPEED;
      tgt_br=SPEED;
  
      digitalWrite(I1ul_PIN, LOW);
      digitalWrite(I2ul_PIN, HIGH);
       
      digitalWrite(I1ur_PIN, LOW);
      digitalWrite(I2ur_PIN, HIGH);
       
      digitalWrite(I1br_PIN, HIGH);
      digitalWrite(I2br_PIN, LOW); 
       
      digitalWrite(I1bl_PIN, HIGH);
      digitalWrite(I2bl_PIN, LOW);
  
      analogWrite(E12ul_PIN, pid_ul(tgt_ul));
      analogWrite(E12ur_PIN, pid_ur(tgt_ur));
      analogWrite(E12bl_PIN, pid_bl(tgt_bl));
      analogWrite(E12br_PIN, pid_br(tgt_br));
  
       
     
    }
  
    else Serial.println("debug: Error in giving cw/cccw");

    looptime=millis()-trigtime;
  } 

  analogWrite(E12ul_PIN, 0);
  analogWrite(E12ur_PIN, 0);
  analogWrite(E12br_PIN, 0);
  analogWrite(E12bl_PIN, 0);
  reset_ie();
}


//---------------------------------------------------------
//interrupts ---> TODO: FIND A WAY (REFINE THE PIDs),  
//GET RID OF THE MIDDLE LINE IN THE INTERRUPTS
//---------------------------------------------------------

void enc_yul(void)
{
  currenttime_ul = micros();
  time_ul = currenttime_ul-starttime;
  speed_ul = 60*1e6/(400*(time_ul-prev_ul));

  if(abs(speed_ul)>100) speed_ul = 60;
  
  if(digitalRead(ENC_Wul_PIN) == HIGH) ;
  else speed_ul *= (-1); 
  prev_ul=time_ul;

  
}

void enc_yur(void)
{
  currenttime_ur = micros();
  time_ur = currenttime_ur-starttime;
  speed_ur = 60*1e6/(400*(time_ur-prev_ur));

  
  
  if(abs(speed_ur)>100) speed_ur = 60;
  
  if(digitalRead(ENC_Wur_PIN) == HIGH) ;
  else speed_ur *= (-1); 

  prev_ur=time_ur;

  
}

void enc_ybl(void)
{
  currenttime_bl = micros();
  time_bl = currenttime_bl-starttime;
  speed_bl = 60*1e6/(400*(time_bl-prev_bl));


  if(abs(speed_bl)>100) speed_bl = 60;
  
  if(digitalRead(ENC_Wbl_PIN) == HIGH) ;
  else speed_bl *= (-1);
  prev_bl=time_bl;
  
  
}

void enc_ybr(void)
{
  currenttime_br = micros();
  time_br = currenttime_br-starttime;
  speed_br = 60*1e6/(400*(time_br-prev_br));


  if(abs(speed_br)>100) speed_br = 60;
  
  if(digitalRead(ENC_Wbr_PIN) == HIGH) ;
  else speed_br *= (-1);
  prev_br=time_br;

 
}

//--------------------------------------------
//Resetting integrated error
//--------------------------------------------

void reset_ie(void) {
  integrated_error_ur = 0;
  integrated_error_ul = 0;
  integrated_error_bl = 0;
  integrated_error_br = 0;
}

//--------------------------------------------
//Station to station movement
//--------------------------------------------
float get_x(char station)
{
  if (station == 'A') {
    return 45.5;
  }
  else if (station == 'B') {
    return 33.5;
  }
  else if (station == 'C') {
    return 21.5;
  }
  else if(station == 'D') {
    return 9.5;
  }
  else{
    return 0;
  }
}

float get_y(char station)
{
  if (station == 'G') {
    return 9.5;
  }
  else if (station == 'H') {
    return 21.5;
  }
  else {
    return 0;
  }
}

int get_orientation(char station)
{
  if (station == 'F' || station == 'G' || station == 'H') {
    return SHORTSIDE;
  }
  else {
    return LONGSIDE;
  }
}

int get_armpos(char station)
{
  if (station == 'D' || station == 'E' || station == 'H') {
    return RIGHTARM;
  }
  else {
    return LEFTARM;
  }
}

void move_s_d(char station_s, char station_d) {
  float diff_x = get_x(station_d) - get_x(station_s);
  float diff_y = get_y(station_d) - get_y(station_s);
  Serial.println("debug 1");
  if (get_orientation(station_s) == get_orientation(station_d)) { //source and destination on same side, just hug wall
    Serial.println("debug 2");
    if (diff_x < 0) {
      translaterobot(RIGHT, time_from_dist(diff_x));
    }
    else if (diff_x > 0) {
      translaterobot(LEFT, time_from_dist(diff_x));
    }
    else if (diff_y < 0) {
      translaterobot(LEFT, time_from_dist(diff_y));
    }
    else if (diff_y > 0) {
      translaterobot(RIGHT, time_from_dist(diff_y));
    }
    else {}
  }
  else { //next station on other side, will need to rotate
    int finalorientation = get_orientation(station_d);
    Serial.println("debug 3");
    if (get_x(station_s) == 0 && get_y(station_s) == 0) { //robot in the corner
      Serial.println("debug 4");
      if (finalorientation == SHORTSIDE) { //robot at station E, destination is short side
        diagonalrobot(DOWNLEFT, time_from_dist(CORNERDIST)); //robot pulling diagonally away from corner
        rotaterobot(CW, CW90); //robot now facing short side
        if (station_d == 'F') { //destination station is F (short side corner station)
          Serial.println("debug 5");
          diagonalrobot(UPLEFT, time_from_dist(CORNERDIST));
        }
        else { //destination is station G or H on short side (not corner)
          Serial.println("debug 6");
          diff_x = get_x(station_d) - CORNERX; //CORNERX is distance travelled by robot decided by CORNERDIST
          diff_y = get_y(station_d) - CORNERY; //CORNERY is distance travelled by robot decided by CORNERDIST
          if (diff_y > 0) {
            translaterobot(RIGHT, time_from_dist(diff_y));
          }
          else {
            translaterobot(LEFT, time_from_dist(diff_y));
          }
          translaterobot(UP, time_from_dist(diff_x));
        }
      }
      else { //robot at station F, destination is long side
        Serial.println("debug 7");
        diagonalrobot(DOWNRIGHT, time_from_dist(CORNERDIST)); //robot pulling diagonally away from corner
        rotaterobot(CCW, CCW90); //robot now facing long side
        if (station_d == 'E') { //destination station is E (long side corner station)
          diagonalrobot(UPRIGHT, time_from_dist(CORNERDIST));
        }
        else { //destination is station A-D on long side (not corner)
          diff_x = get_x(station_d) - CORNERX;
          diff_y = get_y(station_d) - CORNERY;
          if (diff_x > 0) {
            translaterobot(LEFT, time_from_dist(diff_x));
          }
          else {
            translaterobot(RIGHT, time_from_dist(diff_x));
          }
          //Serial.println(diff_y);
          translaterobot(UP, time_from_dist(diff_y));
        }
      }
    }
    else if (get_orientation(station_s) == SHORTSIDE){//robot currently facing short side, final destination is long side
      if (station_d == 'E') {
        Serial.println("debug 8");
        translaterobot(DOWN, time_from_dist(SIDEDIST));
        rotaterobot(CCW, CCW90);
        translaterobot(UP, time_from_dist(diff_y));
        translaterobot(RIGHT, time_from_dist(SIDEDIST));
      }
      else {
        translaterobot(DOWN, time_from_dist(diff_x));
        rotaterobot(CCW, CCW90);
        translaterobot(UP, time_from_dist(diff_y));
      }
    }
    else { //robot currently facing long side, not in the corner, final destination on short side
      if (station_d == 'F') {
        translaterobot(DOWN, time_from_dist(SIDEDIST));
        rotaterobot(CW, CW90);
        translaterobot(UP, time_from_dist(diff_x));
        translaterobot(LEFT, time_from_dist(SIDEDIST));
      }
      else {
        translaterobot(DOWN,time_from_dist(diff_y));
        rotaterobot(CW, CW90);
        translaterobot(UP, time_from_dist(diff_x));
      }
    }
  }
}

void move_xyo(int x_pos, int y_pos, int ori, int x, int y, int ori_tgt)
{
  Serial.println("debug E");
  int xmove = time_from_dist(x-x_pos);
  int ymove = time_from_dist(y-y_pos);
  if (x_pos == 0 && y_pos == 0) {
    if (ori == LONGSIDE && ori_tgt == SHORTSIDE) {
      diagonalrobot(DOWNLEFT, time_from_dist(CORNERDIST)); //robot pulling diagonally away from corner
      rotaterobot(CW, CW90); //robot now facing short side
      if (x == 0 && y == 0) {
        diagonalrobot(UPLEFT, time_from_dist(CORNERDIST));
      }
      else {
        translaterobot(RIGHT, ymove);
        translaterobot(UP, CORNERX);
      }
    }
    else if (ori == SHORTSIDE && ori_tgt == LONGSIDE) {
      diagonalrobot(DOWNRIGHT, time_from_dist(CORNERDIST)); //robot pulling diagonally away from corner
      rotaterobot(CCW, CCW90); //robot now facing short side
      if (x == 0 && y == 0) {
        diagonalrobot(UPRIGHT, time_from_dist(CORNERDIST));
      }
      else {
        translaterobot(LEFT, xmove);
        translaterobot(UP, CORNERY);
      }
    }
    else if (ori == LONGSIDE && ori_tgt == LONGSIDE) {
      translaterobot(LEFT, xmove);
    }
    else {
      translaterobot(RIGHT,ymove);
    }
  }
  else {
    //Serial.println("debug D");
    if (ori == LONGSIDE) { //current orientation longside
      if (ori_tgt == LONGSIDE) { //target orientation longside
        if (x > x_pos) {
          translaterobot(LEFT, xmove); //slide left, hugging the long wall
        }
        else {
          translaterobot(RIGHT, xmove); //slide right, hugging the long wall
        }
      }
      else { //target orientation shortside (current orientation longside)
        translaterobot(DOWN, ymove);
        rotaterobot(CW, CW90);
        translaterobot(UP, xmove);
      }
    }
    else { //current orientation shortside
      if (ori_tgt == SHORTSIDE) { //target orientation shortside
        if (y > y_pos) {
          translaterobot(RIGHT, ymove); //slide right, hugging short wall
        }
        else {
          translaterobot(LEFT, ymove); //slide left, hugging short wall
        }
      }
      else { //target orientation long side (current orientation shortside)
        translaterobot(DOWN, xmove);
        rotaterobot(CCW,CCW90);
        translaterobot(UP, ymove);
      }
    }
  }
}

