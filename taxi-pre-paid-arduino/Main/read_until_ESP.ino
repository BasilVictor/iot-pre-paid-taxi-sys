boolean read_until_ESP(const char keyword1[], int key_size, int timeout_val, byte mode)
{
  timeout_start_val = millis();
  char data_in[20];
  int scratch_length = 1;
  key_size--;
  for(byte i=0; i<key_size; i++)
  {
    while(!Serial1.available())
    {
      if((millis()-timeout_start_val)>timeout_val)
      {
        Serial.println("timeout 1");
        return 0;
      }
    }
    
    data_in[i] = Serial1.read();
//    Serial.write(data_in[i]);
    if(mode==1)
    {
      //this will save all of the data to the scratch_data_from
      scratch_data_from_ESP[scratch_length] = data_in[i];//starts at 1
      scratch_data_from_ESP[0] = scratch_length;// [0] is used to hold the length of the array
      scratch_length++;//increment the length
    }
  }
  while(1)
  {
    for(byte i=0; i<key_size; i++)
    {
      if(keyword1[i]!=data_in[i])
        break;
      if(i==(key_size-1))
      {
        return 1;
      }
     }
     

    //start rolling the buffer
    for(byte i=0; i<(key_size-1); i++)
    {
      // keysize-1 because everthing is shifted over - see next line
      data_in[i]=data_in[i+1];// so the data at 0 becomes the data at 1, and so on.... the last value is where we'll put the new data
    }
    
    while(!Serial1.available())
    {
      if((millis()-timeout_start_val)>timeout_val)
      {
        Serial.println("timeout 2");
        return 0;
      }
    }
    data_in[key_size-1]=Serial1.read();//save the new data in the last position in the buffer
//    Serial.write(data_in[key_size-1]);
    if(mode==1)
    {
      //continue to save everything if this is set
      scratch_data_from_ESP[scratch_length] = data_in[key_size-1];
      scratch_data_from_ESP[0] = scratch_length;
      scratch_length++;
    }
  }
}
/*boolean read_until_ESP(const char keyword1[], int key_size, int timeout_val, byte mode)
{
  timeout_start_val = millis();
  Serial.println(timeout_start_val);
  char data_in[20];
  int scratch_length = 1;
  key_size--;
  for(byte i=0; i<key_size; i++)
  {
    while(!Serial1.available())
    {
      if((millis()-timeout_start_val)>timeout_val)
      {
        Serial.println("timeout 1");
        return 0;
      }
    }
    
    data_in[i] = Serial1.read();
    if(mode==1)
    {
      //this will save all of the data to the scratch_data_from
      scratch_data_from_ESP[scratch_length] = data_in[i];//starts at 1
      scratch_data_from_ESP[0] = scratch_length;// [0] is used to hold the length of the array
      scratch_length++;//increment the length
    }
  }
  while(1)
  {
    for(byte i=0; i<key_size; i++)
    {
      if(keyword1[i]!=data_in[i])
        break;
      if(i==(key_size-1))
      {
        return 1;
      }
    }
     
    //start rolling the buffer
    for(byte i=0; i<(key_size-1); i++)
    {
      // keysize-1 because everthing is shifted over - see next line
      data_in[i]=data_in[i+1];// so the data at 0 becomes the data at 1, and so on.... the last value is where we'll put the new data
    }
    
    while(!Serial1.available())
    {
      if((millis()-timeout_start_val)>timeout_val)
      {
        Serial.println(timeout_start_val);
        Serial.println(millis());
        Serial.println(millis()-timeout_start_val);
        Serial.println("timeout 2");
        return 0;
      }
    }
    data_in[key_size-1]=Serial1.read();//save the new data in the last position in the buffer
    Serial.write(data_in[key_size-1]); 
    if(mode==1)
    {
      //continue to save everything if this is set
      scratch_data_from_ESP[scratch_length] = data_in[key_size-1];
      scratch_data_from_ESP[0] = scratch_length;
      scratch_length++;
    }
  }
}*/
