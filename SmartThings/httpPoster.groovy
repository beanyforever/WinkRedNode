/**
 *  HTTP POSTer for WNR
 *
 *  Copyright 2016 Timur Fatykhov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "HTTP POST for Wink Node Red",
    namespace: "winknodered",
    author: "Timur Fatykhov",
    description: "Sends POST messages from ST devices to WNR URL.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png") {
}


preferences {
    section ("Sensors that should go to WNR") {
//        input "powers", "capability.powerMeter", title: "Power Meters", required: false, multiple: true
        input "temperatures", "capability.temperatureMeasurement", title: "Temperatures", required: false, multiple: true
        input "humidities", "capability.relativeHumidityMeasurement", title: "Humidities", required: false, multiple: true
        input "contacts", "capability.contactSensor", title: "Doors open/close", required: false, multiple: true
        input "accelerations", "capability.accelerationSensor", title: "Accelerations", required: false, multiple: true
        input "motions", "capability.motionSensor", title: "Motions", required: false, multiple: true
        input "presence", "capability.presenceSensor", title: "Presence", required: false, multiple: true
//        input "switches", "capability.switch", title: "Switches", required: false, multiple: true
        input "waterSensors", "capability.waterSensor", title: "Water sensors", required: false, multiple: true
        input "batteries", "capability.battery", title: "Batteries", required: false, multiple: true
//        input "energies", "capability.energyMeter", title: "Energy Meters", required: false, multiple: true
        input "illuminances" ,"capability.illuminanceMeasurement", title: "Illuminance Meters" , required: false, multiple: true
    }
    section ("YOUR WNR URL") {
        input "url", "text", title: "Your node-red app URL", required: true
    }
    section ("YOUR WNR ST Key") {
        input "key", "text", title: "Enter same secret ST key you set in WNR", required: true
    }    
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
    subscribe(powers, "power", handlePowerEvent)
    subscribe(temperatures, "temperature", handleTemperatureEvent)
    subscribe(waterSensors, "water", handleWaterEvent)
    subscribe(humidities, "humidity", handleHumidityEvent)
    subscribe(contacts, "contact", handleContactEvent)
    subscribe(accelerations, "acceleration", handleAccelerationEvent)
    subscribe(motions, "motion", handleMotionEvent)
    subscribe(presence, "presence", handlePresenceEvent)
//    subscribe(switches, "switch", handleSwitchEvent)
    subscribe(batteries, "battery", handleBatteryEvent)
//    subscribe(energies, "energy", handleEnergyEvent)
    subscribe(illuminances, "illuminance", handleIlluminanceEvent)
}

def handlePowerEvent(evt) {
    sendValue(evt) { it.toFloat() }
}

def handleTemperatureEvent(evt) {
    sendValue(evt) { it.toFloat() }
}
 
def handleWaterEvent(evt) {
    sendValue(evt) { it == "wet" ? true : false }
}
 
def handleHumidityEvent(evt) {
    sendValue(evt) { it.toFloat() }
}
 
def handleContactEvent(evt) {
    sendValue(evt) { it == "open" ? true : false }
}
 
def handleAccelerationEvent(evt) {
    sendValue(evt) { it == "active" ? true : false }
}
 
def handleMotionEvent(evt) {
    sendValue(evt) { it == "active" ? true : false }
}
 
def handlePresenceEvent(evt) {
    sendValue(evt) { it == "present" ? true : false }
}
 
def handleSwitchEvent(evt) {
    sendValue(evt) { it == "on" ? true : false }
}
 
def handleBatteryEvent(evt) {
    sendValue(evt) { it.toFloat() }
}
 
def handleEnergyEvent(evt) {
    sendValue(evt) { it.toFloat() }
}

def handleIlluminanceEvent(evt) {
    sendValue(evt) { it.toFloat() }
}

private sendValue(evt, Closure convert) {
    def compId = URLEncoder.encode(evt.displayName.trim())
    def devId = evt.device.id
    def streamId = evt.name
    def value = convert(evt.value)
    def type = (value == true || value == false ? "boolean" : "float"); 

    log.debug "Sending ${compId}/${streamId} data to ${url}..."

	def payload = [
        component: compId,
        devid: devId,
        stream: streamId,
        value: value,
        type: type
    ]

    def params = [
        uri:  url + '/red/smartthings/',
        headers: [
        'Authorization': 'Bearer '+key
        ],
        body: payload
    ]

    try {
    	log.debug params;
        httpPostJson(params) { resp ->
        	log.debug resp.status
        }
    } catch (e) {
        log.debug "something went wrong: $e"
    }

}
