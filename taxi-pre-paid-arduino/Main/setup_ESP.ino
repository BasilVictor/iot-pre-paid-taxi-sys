boolean setup_ESP() 
{
  Serial1.write("AT\r\n"); // Test AT command
  if(read_until_ESP(keyword_ok, sizeof(keyword_ok), 5000, 0))
    print_on_LCD("Taxi successfully added to queue");
  else
    Serial.println("ESP CHECK FAILED");
  serial_dump_ESP();
  
  Serial1.write("AT+RST\r\n"); //Reset ESP8266
  if(read_until_ESP(keyword_ready, sizeof(keyword_ready), 5000, 0))
    Serial.println("ESP RESET OK");
  else
    Serial.println("ESP RESET FAILED"); 
  serial_dump_ESP();
  
  Serial1.write("AT+CWMODE="); //Set the CWMODE
  Serial1.write(CWMODE);
  Serial1.write("\r\n");
  if(read_until_ESP(keyword_ok, sizeof(keyword_ok), 5000, 0))
    Serial.println("ESP CWMODE SET");
  else
    Serial.println("ESP CWMODE SET FAILED");
    
  delay(1000); // Delay before connecting to WiFi
  serial_dump_ESP();  
  
  //Connecting to WiFi
  Serial1.write("AT+CWJAP=\"");
  Serial1.write(SSID_ESP);
  Serial1.write("\",\"");
  Serial1.write(PASS_ESP);
  Serial1.write("\"\r\n");
  if(read_until_ESP(keyword_ok, sizeof(keyword_ok), 10000, 0))
    Serial.println("ESP SSID SET OK");
  else
    Serial.println("ESP SSID SET FAILED");
  serial_dump_ESP();
    
  Serial1.write("AT+CIPMUX="); //Setting the CIPMUX
  Serial1.write(CIPMUX);
  Serial1.write("\r\n");
  if(read_until_ESP(keyword_ok, sizeof(keyword_ok), 5000, 0))
    Serial.println("ESP CIPMUX SET");
  else
    Serial.println("ESP CIPMUX SET FAILED"); 
  serial_dump_ESP();  
}
