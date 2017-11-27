22-Jul-14/Updated 13-Aug

Copy port.xml , dataCollectionSet.xml and commandsequence.properties to merit folder

Copy RXTXcomm.jar to the jre\lib\ext under your jdk directory.

Copy rxtxSerial.dll to the jre\bin under your jdk directory.

Copy log4j.jar and log4j.properties files to merit folder.

Change the paths in the merit.bat accordingly.

Run VSPE and Fronius sim and configure them.

Update the testmod.java for the required query.

Run merit.bat

Call the method getDeviceData("<Json input>") to integrate with your application (see Testmod.java).

log is created in log4j folder under merit folder.

Response for CommonInverterData:
Response is: {"Head":{"RequestArguments":{"Scope":"Device","DeviceId":1,"DataCollection":" CommonInverterData "},"Status":{"Code":0,"Reason":"","UserMessage":""},"Timestamp":""},"Body":{"Data":{"DAY_ENERGY":{"Value":13376,"Unit":"Wh"},"FAC":{"Value":50,"Unit":"Hz"},"IAC":{"Value":8.0,"Unit":"A"},"ID
C":{"Value":11.17,"Unit":"A"},"PAC":{"Value":18624,"Unit":"Wh"},"TOTAL_ENERGY":{"Value":12118,"Unit":"Wh"},"UAC":{"Value":230,"Unit":"V"},"UDC":{"Value":16899,"Unit":"V"},"YEAR_ENERGY":{"Value":17610,"Unit":"Wh"},"DeviceStatus":{"StatusCode":2,"MgmtTimerRemainingTime":0,"ErrorCode":0,"LEDColor":0,"L
EDState":0,"StateToReset":false}}}}

Single API call - getDeviceData supports all the following:
Data collections supported:
1. CumulativeinverterData
2. CommonInverterData
3. MinMaxInverterData
4. 3PInverterData
5. NowSensorData (not tested)
6. MinMaxSensorData (not tested)

Discovery: (For getting active inverters/sensor cards in the network)
getDeviceData("Inverter")
getDeviceData("SensorCard") - not tested