#include <ArduinoJson.h>
#include <SPI.h>
#include <MFRC522.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

#define RST_PIN 8
#define SS_PIN 9

MFRC522 mfrc522(SS_PIN, RST_PIN);
LiquidCrystal_I2C lcd(0x27, 16, 2);

const char SSID_ESP[] = "DominicVictor";
const char PASS_ESP[] = "Domi@5836";
const char USER_ID_API[] = "100";
const char PASSWORD_API[] = "password";
const char HOST[] = "192.168.0.145";
const int PORT = 3000;

const char CWMODE = '1';
const char CIPMUX = '1';

//DEFINE KEYWORDS HERE
const char keyword_ok[] = "OK";
const char keyword_ready[] = "ready";
const char keyword_rn[] = "\r\n";;
const char keyword_carrot[] = ">";
const char keyword_sendok[] = "SEND OK";
const char keyword_linkdisc[] = "Unlink";
const char keyword_closed[] = "0,CLOSED";
const char keyword_keepalive[] = "Keep-Alive";
const char keyword_braceopen[] = "{";

unsigned long timeout_start_val = 0;
char scratch_data_from_ESP[1000];//first byte is the length of bytes
char ip_address[16];

String scannedRFID = "";
String authHeader = "";
byte lcd_status = 0;
unsigned long lcd_timeout = 0;

boolean setup_ESP();
boolean read_until_ESP(const char keyword1[], int key_size, int timeout_val, byte mode);
boolean connect_ESP(String request);
void serial_dump_ESP();
void booth_login();
String read_RFID();
void add_to_queue(String scannedRFID);
void print_on_LCD(char string[]);

void setup() {
  Serial.begin(115200);
  Serial1.begin(115200);
  while(!Serial);
  SPI.begin();
  mfrc522.PCD_Init();
  lcd.begin();
  // Initail delay
  delay(5000);
  setup_ESP();
  delay(1000);

  // Pre paid booth login
  booth_login();
}

void loop() {
  if (mfrc522.PICC_IsNewCardPresent()) {
    scannedRFID = read_RFID();
    if(scannedRFID.length() > 0) {
      print_on_LCD("WELCOME");
      add_to_queue(scannedRFID);
    }
  }

  if(lcd_status == 1 && millis()>= lcd_timeout) {
    lcd_status = 0;
    lcd.clear();
  }
  
}
