boolean connect_ESP(String request)
{
  Serial.println("Connecting to Server...");
  Serial1.println("AT+CIPSTART=0,\"TCP\",\"" + String(HOST) + "\"," + String(PORT));
  delay(1000);
  if(read_until_ESP(keyword_ok, sizeof(keyword_ok), 5000, 0)) {
    serial_dump_ESP();
    Serial.println("Connected");
    Serial1.println("AT+CIPSEND=0,"+String(request.length()));
    Serial.println("CIPSEND=0,"+String(request.length()));
    delay(1000);
    if(read_until_ESP(keyword_carrot, sizeof(keyword_carrot), 5000, 0))
    {
      Serial.println("Ready to Send");
      for(int i=0;i<request.length();i++)
      {
        Serial1.write(request[i]);
      }
      if(read_until_ESP(keyword_sendok, sizeof(keyword_sendok), 10000, 0))
      {
        Serial.println("Sent");
        return 1;
      }
      else
      {
        Serial.println("Failed to send");
      }
    }
    else
    {
      Serial.println("Failed to connect");      
    }
  } else {
    Serial.println("Failed to connect");
    setup_ESP();
  }
}
